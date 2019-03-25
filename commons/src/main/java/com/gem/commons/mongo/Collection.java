package com.gem.commons.mongo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.gem.commons.Json;
import com.mongodb.client.AggregateIterable;

public interface Collection {
	
	int DEFAULT_LIMIT = 1000;
	int DEFAULT_ROW_PRINT = 50;
	
	@SuppressWarnings("rawtypes")
	public List find();
	
	@SuppressWarnings("rawtypes")
	List find(int max);
	
	@SuppressWarnings("rawtypes")
	List find(Query query);
	
	<T> List<T> find(Query query, Class<T> resultClass);
	
	Object findOne(ObjectId id);
	
	Object findOne(String filterKey, Object filterValue);
	
	Object findOne(Query query);
	
	<T> T findOne(Query query, Class<T> resultClass);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	default <T> List<T> findOneAndConvertList(Query query, String field, Class<T> resultClass) {
		Document doc = findOne(query, Document.class);
		if (doc == null) {
			return Collections.emptyList();
		}
		
		List arr = (List) doc.get(field);
		if (arr == null) {
			return Collections.emptyList();
		}
		return Converter.convertList(arr, resultClass);
	}
	
	long count(Json query);
	
	void insert(Json json);
	
	void insert(Object entity);
	
	long update(String filterKey, Object filterValue, Json query);
	
	long update(String filterKey, Object filterValue, Document query);
	
	long update(Query query);
	
	boolean deleteOne(String filterKey, Object filterValue);
	
	boolean deleteOne(Query query);
	
	AggregateIterable<Document> agregate(Json pipeline);
	
	<T> AggregateIterable<T> agregate(PipeLine pipeline, Class<T> resultClass);
	
	List<Document> agregateAndCollect(Json pipeline);
	
	<T> List<T> agregateAndCollect(PipeLine pipeline, Class<T> resultClass);
	
	default <T> List<T> aggregateAndConvertObject(PipeLine pipeline, String field,
			Class<T> resultClass) {
		List<Document> list = agregateAndCollect(pipeline, Document.class);
		if (list.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<T> ans = new ArrayList<T>();
		
		for (Document doc : list) {
			Document obj = (Document) doc.get(field);
			if (obj == null) {
				ans.add(null);
			} else {
				T inner = Converter.convert(obj, resultClass);
				
				ans.add(inner);
			}
			
		}
		
		return ans;
	}
	
	void print();
	
}