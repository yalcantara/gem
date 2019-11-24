package com.gem.commons.mongo;

import com.gem.commons.Json;
import org.bson.Document;

import java.io.Serializable;

import static com.gem.commons.Checker.checkParamNotEmpty;

public class Match implements Serializable {


	private final Json json;

	public Match(){
		json = new Json();
	}

	public void ne(String field, Object val){
		checkParamNotEmpty("field", field);
		var cont = container(field);

		cont.put("$ne", val);
	}

	public void gte(String field, Object val){
		checkParamNotEmpty("field", field);
		var cont = container(field);

		cont.put("$gte", val);
	}

	private Json container(String field){

		if(json.isJsonObject(field) == false){
			json.put(field, new Json());
		}

		return json.getJson(field);
	}

	public Json toJson(){
		return new Json(json);
	}

	public Document toBson(){
		return json.toBson();
	}
}
