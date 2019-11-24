package com.gem.commons.mongo;

import com.gem.commons.Json;
import org.bson.Document;

import java.io.Serializable;
import java.util.ArrayList;

import static com.gem.commons.Checker.checkParamNotEmpty;
import static com.gem.commons.Checker.checkParamNotNull;

public class Project implements Serializable {


	private final Json json;

	public Project(){
		json = new Json();
	}


	public void add(String field){
		checkParamNotEmpty("field", field);
		json.put(field, 1);
	}

	public void add(String field, String path){
		checkParamNotEmpty("field", field);
		json.put(field, path);
	}

	public void hide(String field){
		checkParamNotEmpty("field", field);
		json.put(field, 0);
	}

	public void ifNull(String field, Object thenObj, Object elseObj){
		checkParamNotEmpty("field", field);
		var ifNull = new Json();
		var list = new ArrayList<>();
		list.add(thenObj);
		list.add(elseObj);

		ifNull.put("$ifNull", list);

		json.put(field, ifNull);
	}

	public Json toJson(){
		return new Json(json);
	}

	public Document toBson(){
		return json.toBson();
	}
}
