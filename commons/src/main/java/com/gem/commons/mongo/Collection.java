package com.gem.commons.mongo;

import com.gem.commons.Json;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
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

	long count(Query query);
	
	void insert(Json json);
	
	void insert(Object entity);
	
	long updateOne(Query query);

	
	boolean deleteMany(String filterKey, Object filterValue);


	
	<T> AggregateIterable<T> aggregate(PipeLine pipeline, Class<T> resultClass);

	default long count(PipeLine pipeline){

		PipeLine p = pipeline.clone();
		p.count();

		AggregateIterable<Document> a = aggregate(p, Document.class);

		try (MongoCursor<Document> iter = a.iterator()) {
			if (iter.hasNext()) {
				Document ans = iter.next();

				long val = ((Number) ans.get("count")).longValue();

				return val;
			}

			return 0;
		}
	}

	<T> List<T> aggregateAndCollect(PipeLine pipeline, Class<T> resultClass);
	
	void print();
	
}