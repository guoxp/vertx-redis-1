package de.fraunhofer.fokus.redistest;
/**
 * This code was developed in the cooperation project "Vernetzte Sicherheit" between Fraunhofer FOKUS and the German Federal Ministry of Interior
 * under the project code ÖS I 3 - 43002/1#2
 * Copyright 2014-2017 Fraunhofer FOKUS
 * This source code is licensed under Creative Commons Attribution-NonCommercial 4.0 International license. 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * http://creativecommons.org/licenses/by-nc/4.0/
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */


import static de.fraunhofer.fokus.adep.Constants.HASH_NAME_FORMAT;
import static de.fraunhofer.fokus.adep.Constants.ZSET_NAME_FORMAT;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import de.fraunhofer.fokus.adep.Constants;
import de.fraunhofer.fokus.adep.DisposableRedisConnection;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.redis.op.RangeLimitOptions;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;
import rx.Single;
/**
 * Attribute finder for search with prefilter
 * 
 * @author ulrich.kriegel@fokus.fraunhofer.de
 *
 */
public class DoInterestingThings {




	public DoInterestingThings(){
	}


	public  void doIt( final Vertx vertx, final JsonObject redisConfig, String uuid,  Logger logger, Handler<AsyncResult<JsonArray>> handler){
		String zSetName = String.format(ZSET_NAME_FORMAT, uuid);
		final RangeLimitOptions rangeLimitOptions = (RangeLimitOptions) new RangeLimitOptions().setLimit(0, Constants.MAX_SEARCH_RESULTS);

		/*
		 * To prevent errors in streams due to empty result set and to enable subscription
		 * a dummy element will be prepended to the result set and removed in the subscription
		 */
		final List<JsonArray> secureResultList  = new ArrayList<JsonArray>();
		secureResultList.add(new JsonArray().add("default"));

		Single.using(DisposableRedisConnection::new, f -> f.create(vertx, redisConfig, logger), f -> f.dispose()).subscribe(
				redisClient->{
					// emulate searech time in database
//					vertx.setTimer(40,  xx->{
						Observable.range(1, 40)
						.map(x -> new JsonObject().put(Constants.KEY_TYPE, Constants.KEY_DICE_OBJECT).put(Constants.KEY_RECORD_ID, String.valueOf(x))
								.put(Constants.KEY_DICE_VALUE, x*0.1).put(Constants.KEY_FAMILYNAME, x*0.1).put(Constants.KEY_FIRSTNAME,1))
						.filter(x-> x != null) //remove results where comparison has been stopped due to bad dice values
						// side effect - store objects in redis	
						.subscribe(res->{
							String handle = res.getString(Constants.KEY_RECORD_ID);
							String hashName = String.format(HASH_NAME_FORMAT,handle);
							Single.zip(redisClient.rxZadd(zSetName, res.getDouble(Constants.KEY_DICE_VALUE), handle),
									redisClient.rxExpire(hashName, Constants.EXPIRE_AFTER),
									redisClient.rxHmset(hashName, res), (a, b, c) ->{return res;})
							.subscribe(r->{
								// do nothing
							}, t-> handler.handle(Future.failedFuture(t))
									);
						}, t-> handler.handle(Future.failedFuture(t)),
								()->{ //set expiration and retrieve record_ids
									Observable.zip(redisClient.rxExpire(zSetName, Constants.EXPIRE_AFTER).toObservable(), // set expiration
											redisClient.rxZrevrangebyscore(zSetName, "1", "0", rangeLimitOptions).toObservable(),  // list of record_ids as JsonArray
											(a,b) -> Observable.from(b))
									.flatMap(x->x)
									.map(handle -> 	redisClient.rxHgetall(String.format(HASH_NAME_FORMAT, handle)).toObservable()) // retrieve hash from redis
									.flatMap(x->x)
									.map(json -> toEntry(json))
									.collect(()-> new JsonArray(), (eList,e)-> eList.add(e))
									.subscribe(collectedJson->  
									handler.handle(Future.succeededFuture(collectedJson)) ,
									t-> {	
										System.out.println("XXXX: "+t);
										logger.error("XXX",t);
										handler.handle(Future.failedFuture(t));});
//								});
					});
				},
				t->{
					CharArrayWriter cw = new CharArrayWriter();
					PrintWriter w = new PrintWriter(cw);
					t.printStackTrace(w);
					w.close();
					logger.error("trace",cw.toString());
					logger.error("YYY",t);
					handler.handle(Future.failedFuture(t));});


				

	}


	/**
	 * transfer hash table from redis into Entry
	 * @param json - JsonObject stored in redis, all fields are of type string
	 * @return Entry
	 */


	private JsonObject toEntry(JsonObject json){
		Entry entry = new Entry().setHandle(json.getString(Constants.KEY_RECORD_ID));
		json.remove(Constants.KEY_RECORD_ID);
		for(String fn :json.fieldNames()){
			switch(fn){
			case Constants.KEY_DICE_VALUE:
			case Constants.KEY_FAMILYNAME:
			case Constants.KEY_FIRSTNAME:
			case Constants.KEY_DAY_OF_BIRTH:
			case Constants.KEY_MONTH_OF_BIRTH:
			case Constants.KEY_YEAR_OF_BIRTH:
			case Constants.KEY_PLACE_OF_BIRTH:
			case Constants.KEY_GENDER:
				json.put(fn, Double.parseDouble(json.getString(fn)));
				break;
			}

		}
		return entry.setDiceJson(json);
	}




}
