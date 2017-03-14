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

import java.io.Serializable;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import de.fraunhofer.fokus.adep.Constants;
import de.fraunhofer.fokus.adep.NameHelper;
import de.fraunhofer.fokus.adep.model.DateQuery;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Class to encode a query
 * @author ulrich.kriegel@fokus.fraunhofer.de
 *
 */
@XmlRootElement 
@XmlAccessorType(XmlAccessType.FIELD)

public class Query implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String []  familyName;
	private String [] firstName;
	private DateQuery dayOfBirth = null;
	private DateQuery monthOfBirth = null;
	private DateQuery yearOfBirth = null;
	private String placeOfBirth;
	private String gender;
	private String issuer;
	private String [] familyNameFltr;
	private String [] firstNameFltr;
	private String placeOfBirthFltr;
	private boolean pseudonymized = false;
	private boolean validated = false;
	private int code = 0;
	private boolean namesSwitched = false;
	
	public Query(){
		super();
	}
	/**
	 * Constructor for a Query object
	 * @param json - JosonObject, a JSON representation of a Query object
	 * @param issuer
	 */
	public Query(JsonObject json, String issuer){
		familyName = NameHelper.toStringArray(json.getJsonArray("familyName", null));
		firstName = NameHelper.toStringArray(json.getJsonArray("firstName", null));
		placeOfBirth = json.getString("placeOfBirth",null);
		yearOfBirth = DateQuery.toDateQuery(json.getJsonObject("yearOfBirth", null));
		monthOfBirth = DateQuery.toDateQuery(json.getJsonObject("monthOfBirth", null));
		dayOfBirth = DateQuery.toDateQuery(json.getJsonObject("dayOfBirth", null));
		gender = json.getString("gender", null);
		JsonArray tmp = json.getJsonArray("familyNameFltr");
		if(tmp != null){
			familyNameFltr = NameHelper.toStringArray(tmp);
		}
		tmp = json.getJsonArray("firstNameFltr");
		if(tmp != null){
			firstNameFltr = NameHelper.toStringArray(tmp);
		}
		String stmp = json.getString("placeOfBirthFltr");
		if(stmp != null){
			placeOfBirthFltr = stmp;
		}
		this.issuer = issuer;
		pseudonymized = json.getBoolean("pseudonymized", false);
		

		
	}
	

	
	/**
	 * Clone a Query Object and switch first and family name in the clone
	 * @return - Query, a clone of the query with all first- and family name attributes switched
	 */
	public Query cloneAndSwitchNames(){
		Query query = new Query();
		query.familyName = firstName;
		query.firstName = familyName;
		query.dayOfBirth = dayOfBirth;
		query.monthOfBirth = monthOfBirth;
		query.yearOfBirth = yearOfBirth;
		query.placeOfBirth = placeOfBirth;
		query.gender = gender;
		query.issuer = issuer;
		query.pseudonymized = pseudonymized;
		if(familyNameFltr != null){
			query.firstNameFltr = familyNameFltr;
		}
		if(firstNameFltr != null){
			query.familyNameFltr = firstNameFltr;
		}
		if(placeOfBirthFltr != null){
			query.placeOfBirthFltr = placeOfBirthFltr;
		}
		query.namesSwitched = true;
		query.code = 0; //needs to be recalculated
		return query;
	}
	
	
	public String [] getFamilyName() {
		return familyName;
	}
	
	
	public String getFamilyNameAsSqlArray(){
		StringBuilder sb = new StringBuilder("{");
		if(familyName == null){
			sb.append(Constants.NULL_STRING);
			sb.append(Constants.ARRAY_SEP);
		}else{
			for(int i = 0; i < familyName.length; ++i){
				sb.append(familyName[i]);
				sb.append(Constants.ARRAY_SEP);
			}
		}
		sb.setCharAt(sb.length()-1,'}');

		return sb.toString();
	}

	
	
	
	public String getFirstNameAsSqlArray(){
		StringBuilder sb = new StringBuilder("{");
		if(firstName == null){
			sb.append(Constants.NULL_STRING);
			sb.append(Constants.ARRAY_SEP);
		}else{
			for(int i = 0; i < firstName.length; ++i){
				sb.append(firstName[i]);
				sb.append(Constants.ARRAY_SEP);
			}
		}
		sb.setCharAt(sb.length()-1,'}');
		return sb.toString();
	}

	public Query setFamilyName(String [] familyName) {
		this.familyName = familyName;
		return this;
	}


	public String [] getFirstName() {
		return firstName;
	}

	public Query setFirstName(String [] firstName) {
		this.firstName = firstName;
		return this;
	}


	public DateQuery getDayOfBirth() {
		return dayOfBirth;
	}

	public Query setDayOfBirth(DateQuery dayOfBirth) {
		this.dayOfBirth = dayOfBirth;
		return this;
	}

	public DateQuery getMonthOfBirth() {
		return monthOfBirth;
	}

	public Query setMonthOfBirth(DateQuery monthOfBirth) {
		this.monthOfBirth = monthOfBirth;
		return this;
	}

	public DateQuery getYearOfBirth() {
		return yearOfBirth;
	}

	public Query setYearOfBirth(DateQuery yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
		return this;
	}

	public String getPlaceOfBirth() {
		return placeOfBirth;
	}

	public Query setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
		return this;
	}



	public String getGender() {
		return gender;
	}

	public Query setGender(String gender) {
		this.gender = gender;
		return this;
	}


	public String getIssuer() {
		return issuer;
	}

	public Query setIssuer(String issuer) {
		this.issuer = issuer;
		return this;
	}


	public boolean isPseudonymized() {
		return pseudonymized;
	}

	public Query setPseudonymized(boolean pseudonymized) {
		this.pseudonymized = pseudonymized;
		return this;
	}

	

	public boolean isNamesSwitched() {
		return namesSwitched;
	}
	@Override
	public String toString() {
		return "Query [familyName=" + Arrays.toString(familyName) + ", firstName=" + Arrays.toString(firstName)
				+ ", dayOfBirth=" + dayOfBirth + ", monthOfBirth=" + monthOfBirth + ", yearOfBirth=" + yearOfBirth
				+ ", placeOfBirth=" + placeOfBirth + ", gender=" + gender + ", issuer=" + issuer + ", familyNameFltr="
				+ Arrays.toString(familyNameFltr) + ", firstNameFltr=" + Arrays.toString(firstNameFltr)
				+ ", placeOfBirthFltr=" + placeOfBirthFltr + ", pseudonymized=" + pseudonymized + ", validated="
				+ validated + ", code=" + getCode() + "]";
	}
	public boolean isValidated() {
		return validated;
	}

	public Query setValidated(boolean validated) {
		this.validated = validated;
		return this;
	}


	public String [] getFamilyNameFltr() {
		return familyNameFltr;
	}

	public Query setFamilyNameFltr(String [] familyNameFltr) {
		this.familyNameFltr = familyNameFltr;
		return this;
	}
	public String getFamilyNameFltrAsSqlArray(){
		StringBuilder sb = new StringBuilder("{");
		if(familyNameFltr == null){
			sb.append(Constants.NULL_STRING);
			sb.append(Constants.ARRAY_SEP);
		}else{
			for(int i = 0; i < familyNameFltr.length; ++i){
				sb.append(familyNameFltr[i]);
				sb.append(Constants.ARRAY_SEP);
			}
		}
		sb.setCharAt(sb.length()-1, '}');
		return sb.toString();
	}
	public String [] getFirstNameFltr() {
		return firstNameFltr;
	}

	public Query setFirstNameFltr(String [] firstNameFltr) {
		this.firstNameFltr = firstNameFltr;
		return this;
	}
	public String getFirstNameFltrAsSqlArray(){
		StringBuilder sb = new StringBuilder("{");
		if(firstNameFltr == null){
			sb.append(Constants.NULL_STRING);
			sb.append(Constants.ARRAY_SEP);
		}else{
			for(int i = 0; i < firstNameFltr.length; ++i){
				sb.append(firstNameFltr[i]);
				sb.append(Constants.ARRAY_SEP);
			}
		}
		sb.setCharAt(sb.length()-1,'}');
		return sb.toString();
	}

	public String getPlaceOfBirthFltr() {
		return placeOfBirthFltr;
	}

	public Query setPlaceOfBirthFltr(String placeOfBirthFltr) {
		this.placeOfBirthFltr = placeOfBirthFltr;
		return this;
	}

	public int getCode(){
		if(code == 0){
			if (familyName != null) {
				code |= Constants.CODE_FAMILY_NAME;
			}
			if (firstName != null) {
				code |= Constants.CODE_FIRST_NAME;
			}
			if (yearOfBirth != null) {
				code |= Constants.CODE_YEAR_OF_BIRTH;
			}

			if (monthOfBirth != null) {
				code |= Constants.CODE_MONTH_OF_BIRTH;
			}
			if (dayOfBirth != null) {
				code |= Constants.CODE_DAY_OF_BIRTH;
			}
			if (placeOfBirth != null) {
				code |= Constants.CODE_PLACE_OF_BIRTH;
			}
			if (gender != null) {
				code |= Constants.CODE_GENDER;
			}
		}
		return code;
	}

}
