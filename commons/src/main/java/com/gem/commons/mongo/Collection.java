package com.gem.commons.mongo;

import com.gem.commons.Json;
import com.mongodb.client.AggregateIterable;
import org.bson.types.ObjectId;

import java.util.List;

public interface Collection {
	
	int DEFAULT_LIMIT = 1000;
	int DEFAULT_ROW_PRINT = 50;
	
	@SuppressWarnings("rawtypes")
	List find();
	
	@SuppressWarnings("rawtypes")
	List find(int max);
	
	@SuppressWarnings("rawtypes")
	List find(Query query);
	
	<T> List<T> find(Query query, Class<T> resultClass);
	
	Object findOne(ObjectId id);
	
	Object findOne(String filterKey, Object filterValue);

	
	<T> T findOne(Query query, Class<T> resultClass);
	

	
	long count(Json query);
	
	void insert(Json json);
	
	void insert(Object entity);
	
	long update(Query query);
	
	boolean deleteOne(String filterKey, Object filterValue);
	
	boolean deleteOne(Query query);

	
	<T> AggregateIterable<T> aggregate(PipeLine pipeline, Class<T> resultClass);

	<T> List<T> aggregateAndCollect(PipeLine pipeline, Class<T> resultClass);
	
	void print();
	
}