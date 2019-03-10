package com.gem.commons.mongo;

import java.io.Serializable;

import org.bson.Document;

public class Query implements Serializable {
	
	private static final long serialVersionUID = 7641162061309489773L;

	private final Document doc;
	
	public Query() {
		doc = new Document();
	}

	public void set(String field, Object val) {
		
		Document d = (Document) doc.get("$set");

		if (d == null) {
			d = new Document();
		}

		doc.put(field, val);
		doc.put("$set", d);
	}

	public Document toBson() {
		return new Document(doc);
	}
}
