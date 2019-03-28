package com.gem.commons.mongo;

import com.gem.commons.Json;
import com.mongodb.annotations.NotThreadSafe;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.Serializable;

import static com.gem.commons.Checker.checkParamNotNull;

@NotThreadSafe
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

	private Document fil() {
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

	public void _id(ObjectId id) {
		filter("_id", id);
	}

	public void filter(String field, Object val) {
		fil().put(field, val);
	}

	public void update(String field, Object val) {
		
		Document d = (Document) up().get("$set");

		if (d == null) {
			d = new Document();
			up().put("$set", d);
		}

		d.put(field, val);
	}
	
	public void push(String field, Object val) {
		
		Document d = (Document) up().get("$push");

		if (d == null) {
			d = new Document();
			up().put("$push", d);
		}

		d.put(field, val);
	}

	public void include(String field) {
		checkParamNotNull("field", field);
		fields().put(field, 1);
	}

	public void includes(String... fields) {
		if (fields == null || fields.length == 0) {
			return;
		}
		
		for (String k : fields) {
			include(k);
		}
	}
	
	public void exclude(String field) {
		checkParamNotNull("field", field);
		fields().put(field, 0);
	}

	public void pull(String field, Json criteria){
		Document c = new Document();
		c.put(field, criteria.toBson());
		up().put("$pull", c);
	}

	public void checkUpdate() {
		if (_update == null) {
			throw new RuntimeException("No update actions have been made to this query.");
		}
	}

	public void checkFilter() {
		if (_filter == null) {
			throw new RuntimeException("No filter actions have been made to this query.");
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
