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
import io.vertx.core.json.JsonObject;
/**
 * Model class for Entry
 * 
 * @author ulrich.kriegel@fokus.fraunhofer.de
 *
 */
public class Entry extends JsonObject {
	public Entry(){
		super();
		put(Constants.KEY_TYPE, Constants.TYPE_ENTRY);
	}
	
	public Entry setHandle(String handle){
		return (Entry) put(Constants.KEY_HANDLE, handle);
	}
	
	
	public Entry setDice(Object dice){
		return (Entry) put(Constants.KEY_DICE_VALUE, dice);
	}
	
	
	public Entry setDiceJson(JsonObject json){
		return (Entry) put(Constants.KEY_DICE_OBJECT, json);
	}
	
	public Entry setNamesSwitched(boolean flag){
		return (Entry) put(Constants.KEY_NAMES_SWITCHED, flag);
	}

}
