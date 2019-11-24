package com.gem.commons.mongo;

import com.gem.commons.Json;
import org.bson.Document;

import java.io.Serializable;
import java.util.Map;

import static com.gem.commons.Checker.checkParamNotEmpty;

public class Group implements Serializable {


	private final Json json;


	public Group(){
		json = new Json();
	}

	public void id(String field, String path){
		checkParamNotEmpty("field", field);

		if(json.isJsonObject("_id") == false){
			json.put("_id", new Json());
		}

		json.getJson("_id").put(field, path);
	}

	public void sum(String alias){
		checkParamNotEmpty("alias", alias);
		var sum = new Json();
		sum.put("$sum", 1);

		json.put(alias, sum);
	}


	public Json toJson(){
		return new Json(json);
	}


	public Document toBson(){
		return json.toBson();
	}
}
