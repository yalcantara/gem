package com.gem.commons.mongo;

import java.util.Map.Entry;

import org.bson.Document;

import com.gem.commons.Json;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Converter {

	private Converter() {
		// empty
	}

	public static DBObject toMongoObject(Json json) {
		BasicDBObject db = new BasicDBObject();

		for (String k : json) {

			Object val = json.getObject(k);
			db.put(k, val);
		}
		
		return db;
	}
	
	public static Document toBson(Json json) {
		
		Document b = new Document();

		for (Entry<String, Object> ent : json.entrySet()) {
			b.put(ent.getKey(), ent.getValue());
		}
		
		return b;
	}
}
