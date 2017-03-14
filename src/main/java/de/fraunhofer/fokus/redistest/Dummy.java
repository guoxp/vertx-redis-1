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

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.rxjava.core.Vertx;



/**
 * Find Attributes in Database
 * 
 * @author ulrich.kriegel@fokus.fraunhofer.de
 *
 */
public class Dummy {


	

	public void dummy(Vertx vertx, Query query, Logger logger, Handler<AsyncResult<JsonArray>> handler) {
		vertx.setTimer(4, res->{
			handler.handle(Future.succeededFuture(new JsonArray()));
		});

	}
}
