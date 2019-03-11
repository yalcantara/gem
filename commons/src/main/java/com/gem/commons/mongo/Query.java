package com.gem.commons.mongo;

import java.io.Serializable;

import org.bson.Document;

public class Query implements Serializable {

	private static final long serialVersionUID = 7641162061309489773L;
	
	private Integer limit;
	private Document _update;
	private Document _filter;
	private Document _fields;

	public Query() {
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	private Document up() {
		if (_update == null) {
			_update = new Document();
		}

		return _update;
	}
	
	private Document f() {
		if (_filter == null) {
			_filter = new Document();
		}

		return _filter;
	}
	
	private Document fields() {
		if (_fields == null) {
			_fields = new Document();
		}

		return _fields;
	}
	
	public void filter(String field, Object val) {
		f().put(field, val);
	}
	
	public void update(String field, Object val) {

		Document d = (Document) up().get("$set");
		
		if (d == null) {
			d = new Document();
			up().put("$set", d);
		}
		
		d.put(field, val);
	}
	
	public void includes(String... fields) {
		if (fields == null || fields.length == 0) {
			return;
		}

		for (String k : fields) {
			fields().put(k, 1);
		}
	}
	
	public void checkUpdate() {
		if (_update == null) {
			throw new RuntimeException("No update actions have been made to this query.");
		}
	}
	
	public Document getUpdate() {
		if (_update == null) {
			return null;
		}
		return new Document(_update);
	}
	
	public Document getFilter() {
		if (_filter == null) {
			return null;
		}
		return new Document(_filter);
	}
	
	public Document getFields() {
		if (_fields == null) {
			return null;
		}
		
		return new Document(_fields);
	}
}
