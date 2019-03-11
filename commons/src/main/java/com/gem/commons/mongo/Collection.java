package com.gem.commons.mongo;

import java.util.List;

import org.bson.Document;

import com.gem.commons.Json;
import com.mongodb.client.AggregateIterable;

public interface Collection {
	
	int DEFAULT_LIMIT = 1000;
	int DEFAULT_ROW_PRINT = 50;
	
	@SuppressWarnings("rawtypes")
	List find(int max);

	@SuppressWarnings("rawtypes")
	List find(Query query);
	
	Object findOne(String key, String val);
	
	long count(Json query);
	
	void insert(Json json);
	
	void insert(Object entity);
	
	Object update(String key, Object id, Json query);
	
	Object update(String key, Object id, Query query);
	
	Object update(String key, Object id, Document query);
	
	boolean deleteOne(String key, Object id);
	
	AggregateIterable<Document> agregate(Json pipeline);
	
	List<Document> agregateAndCollect(Json pipeline);
	
	void print();
	
}