package com.gem.commons.mongo;

import com.gem.commons.Json;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;

public interface Collection {
	
	int DEFAULT_LIMIT = 1000;
	int DEFAULT_ROW_PRINT = 50;


	default List list(){
		return MongoUtils.collect(find());
	}

	default List list(int max){
		return MongoUtils.collect(find(max), max);
	}

	default List list(Query query){
		return MongoUtils.collect(find(query), query.getLimit());
	}

	default <T> List<T> list(Query query, Class<T> resultClass){
		return MongoUtils.collect(find(query, resultClass), query.getLimit());
	}

	@SuppressWarnings("rawtypes")
	FindIterable find();
	
	@SuppressWarnings("rawtypes")
	FindIterable find(int max);
	
	@SuppressWarnings("rawtypes")
	FindIterable find(Query query);
	
	<T> FindIterable<T> find(Query query, Class<T> resultClass);
	
	Object findOne(ObjectId id);
	
	Object findOne(String filterKey, Object filterValue);

	
	<T> T findOne(Query query, Class<T> resultClass);
	

	
	long count(Json query);

	long count(Query query);
	
	void insert(Json json);
	
	void insert(Object entity);
	
	long updateOne(Query query);

	
	long deleteMany(String filterKey, Object filterValue);

	long deleteMany(Json filter);

	
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


	List aggregateAndCollect(PipeLine pipeline);

	List aggregateAndCollect(PipeLine pipeline, Integer maxValue);

	default <T> List<T> aggregateAndCollect(PipeLine pipeline, Class<T> resultClass){
		return aggregateAndCollect(pipeline, resultClass, DEFAULT_LIMIT);
	}

	<T> List<T> aggregateAndCollect(PipeLine pipeline, Class<T> resultClass, Integer limit);

	<T> DistinctIterable<T> distinct(String fieldName, Class<T> resultClass);

	<T> DistinctIterable<T> distinct(String fieldName, Document filter, Class<T> resultClass);

	void print();
	
}