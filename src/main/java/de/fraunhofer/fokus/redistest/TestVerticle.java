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

package de.fraunhofer.fokus.redistest;



import de.fraunhofer.fokus.adep.Constants;
import de.fraunhofer.fokus.adep.LoggerHelper;
import de.fraunhofer.fokus.adep.VertxHelper;
import de.fraunhofer.fokus.adep.model.json.SearchResult;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.TimeoutHandler;
/**
 * Search micro service 
 * 
 * @author ulrich.kriegel@fokus.fraunhofer.de
 *
 */
public class TestVerticle extends AbstractVerticle {


	private static final Logger logger = LoggerFactory.getLogger("SearchVerticle.logger");
	private static final Logger audit = LoggerFactory.getLogger("SearchVerticle.audit");


	private static JsonObject redisConfig;
	private static JsonObject serverConfig;


	
	private long timeout;


	public static void main(String[] args) {
		VertxHelper.runVertx(TestVerticle.class.getName(), new VertxOptions().setClustered(false), new DeploymentOptions());
	}

	@Override
	public void start(Future<Void> startFuture) {
		JsonObject config = config();
		LoggerHelper.configLogger(logger, config.getJsonObject(Constants.KEY_LOGGER));
		LoggerHelper.configLogger(audit, config.getJsonObject(Constants.KEY_AUDIT));

	
			LoggerHelper.configLogger(logger, config.getJsonObject(Constants.KEY_LOGGER));
			serverConfig = config.getJsonObject(Constants.KEY_SERVER);
			redisConfig = config.getJsonObject(Constants.KEY_REDIS);
			System.out.println(redisConfig);

	
			Router router = Router.router(vertx);

			router.route().produces(Constants.APPLICATION_JSON).handler(BodyHandler.create());
			router.post("/adep/search").produces(Constants.APPLICATION_JSON).handler(this::search );
			router.post("/adep/search").produces(Constants.APPLICATION_JSON).handler(TimeoutHandler.create(timeout, HttpResponseStatus.REQUEST_TIMEOUT.code()));
			vertx.createHttpServer(new HttpServerOptions()).requestHandler(router::accept).listen(serverConfig.getInteger(Constants.KEY_PORT, Constants.DEFAULT_HTTP_SERVER_PORT));	
			startFuture.complete();

	
	}

	@Override
	public void stop(Future<Void> stopFuture){
		stopFuture.complete();
	}




	/**
	 * Search service
	 * @param routingContext
	 */
	private void search(RoutingContext routingContext) {
		// start time
		long t0 = System.currentTimeMillis();
		HttpServerResponse response = routingContext.response();
		response.putHeader(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
		JsonObject body = null;
		try{
			// get body as json
			body =  routingContext.getBodyAsJson();
		}catch(io.vertx.core.json.DecodeException e){}

		final String uuid = body.getString(Constants.KEY_UUID);

		JsonObject bodyQueryPart = body.getJsonObject(Constants.KEY_QUERY);
		final JsonObject _body = bodyQueryPart;
		// validation using drools can take a few millis --> execute blocking
		vertx.executeBlocking(future ->{
			vertx.setTimer(2, res->{
				future.complete();
			});

		}, res0->{
			final Query query = new Query(_body, "DE");
			final SearchResult result = new SearchResult("XX", uuid);

			searchN(query, redisConfig, uuid, res1 ->{
				long t1 = System.currentTimeMillis() - t0;
				if(res1.succeeded()){
					result.setContent(res1.result());
					response.setStatusCode(HttpResponseStatus.OK.code()).end(result.setElapsedTime(t1).encode());
				}else{}
			});

		});
	}





	/**
	 * Deep search first
	 * @param query
	 * @param diceThreshold
	 * @param uuid
	 * @param handler
	 */
	private void searchN(Query query, JsonObject redisConfig,  String uuid, Handler<AsyncResult<JsonArray>> handler){
		searchNAux(query, redisConfig, uuid, res0->{
			if(res0.failed()){
				handler.handle(Future.failedFuture(res0.cause()));
				return;
			}
			JsonArray  _result = res0.result();
			if(_result.isEmpty()){
				// try search with switched names
				Query _query = query.cloneAndSwitchNames();
				searchNAux(_query, redisConfig, uuid, res1 ->{
					if(res1.failed()){
						handler.handle(Future.failedFuture(res1.cause()));
						return;
					}
					handler.handle(Future.succeededFuture(res1.result()));
					return;
				});
			}else{
				// there is a result
				handler.handle(Future.succeededFuture(_result));
				return;
			}
		});

	}
	private void searchNAux(Query query, JsonObject redisConfig, String uuid, Handler<AsyncResult<JsonArray>> handler){
		new Dummy().dummy(vertx, query, logger, res0 ->{
			if(res0.failed()){
				handler.handle(Future.failedFuture(res0.cause()));
				return;
			}else{
				JsonArray _result =  res0.result();
				if( _result.isEmpty()){
					new DoInterestingThings().doIt(vertx, redisConfig, uuid, logger, res1 ->{
						if(res1.succeeded()){
							handler.handle(Future.succeededFuture(res1.result()));
						}else{	
							handler.handle(Future.failedFuture(res1.cause()));
							return;
						}
					});
				}else{
					/*
					 * exact search got a result
					 */
					handler.handle(Future.succeededFuture(res0.result()));
					return;
				}
			}
		});
	}






}
