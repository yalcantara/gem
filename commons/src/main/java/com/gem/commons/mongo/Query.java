package com.gem.commons.mongo;

import com.gem.commons.Json;
import com.mongodb.annotations.NotThreadSafe;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.gem.commons.Checker.checkParamNotNull;

@NotThreadSafe
public class Query implements Serializable {
	
	private static final long serialVersionUID = 7641162061309489773L;

	private Integer limit;
	private Document _update;
	private Document _filter;
	private Document _fields;
	private Document _sort;

	private List<Document> _arrayFilters;
	
	public Query() {
	}
	
	public Integer getLimit() {
		return limit;
	}
	
	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public void setMaxLimit(){
		setLimit(Integer.MAX_VALUE);
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

	private Document srt(){
		if(_sort == null){
			_sort = new Document();
		}

		return _sort;
	}

	private List<Document> af() {
		if (_arrayFilters == null) {
			_arrayFilters = new ArrayList<>();
		}

		return _arrayFilters;
	}

	public void _id(ObjectId id) {
		filter("_id", id);
	}

	public void filter(String field, Object val) {
		fil().put(field, val);
	}

	public void filter(Json filter) {
		fil().putAll(filter.toMap());
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

	public void sort(String field){
		srt().put(field, 1);
	}

	public void addArrayFilter(Json json){

		Document f = json.toBson();
		af().add(f);
	}

	public void addArrayFilter(Document json){

		af().add(json);
	}

	public void addArrayFilter(String field, String val){

		Document f = new Document();
		f.put(field, val);
		af().add(f);
	}

	public void addArrayFilter(String field, ObjectId val){

		Document f = new Document();
		f.put(field, val);
		af().add(f);
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

	public Document getSort(){
		if(_sort == null){
			return null;
		}

		return new Document(_sort);
	}

	public List<Document> getArrayFilters(){
		if(_arrayFilters == null){
			return null;
		}

		return _arrayFilters;
	}
}
