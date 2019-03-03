package com.gem.commons.mongo;

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
}
