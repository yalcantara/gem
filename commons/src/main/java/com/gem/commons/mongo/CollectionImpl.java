package com.gem.commons.mongo;

import com.gem.commons.Grid;
import com.gem.commons.Json;
import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.gem.commons.Checker.checkParamNotNull;

public class CollectionImpl<E> implements Collection<E> {
	
	@SuppressWarnings("rawtypes")
	private final MongoCollection col;
	
	private final Class<E> _resultClass;

	
	@SuppressWarnings("rawtypes")
	public CollectionImpl(MongoCollection col, Class<E> resultClass) {
		checkParamNotNull("col", col);
		checkParamNotNull("resultClass", resultClass);
		this.col = col;
		_resultClass = resultClass;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public FindIterable find() {
		return find(DEFAULT_LIMIT);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public FindIterable find(int limit) {
		Query q = new Query();
		q.setLimit(limit);
		return find(q);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public FindIterable find(Query query) {
		checkParamNotNull("query", query);
		return find(query, _resultClass);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> FindIterable<T> find(Query query, Class<T> resultClass) {
		checkParamNotNull("query", query);
		checkParamNotNull("resultClass", resultClass);
		
		Document filter = query.getFilter();
		FindIterable<T> iter;
		if (filter == null) {
			iter = col.find(resultClass);
		} else {
			iter = col.find(filter, resultClass);
		}
		
		Document fields = query.getFields();
		
		if (fields != null) {
			iter.projection(fields);
		}
		
		Integer max = query.getLimit();
		if (max == null) {
			max = DEFAULT_LIMIT;
		}
		iter.limit(max);


		Document sort = query.getSort();
		if(sort != null){
			iter.sort(sort);
		}

		return iter;
	}
	
	@Override
	public E findOne(String filterKey, Object filterValue) {
		checkParamNotNull("filterKey", filterKey);
		checkParamNotNull("filterValue", filterValue);
		Query q = new Query();
		q.filter(filterKey, filterValue);
		return findOne(q, _resultClass);
	}
	

	
	@Override
	public E findOne(ObjectId id) {
		checkParamNotNull("id", id);
		Query q = new Query();
		q.filter("_id", id);
		return findOne(q, _resultClass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public E findOne(Query query){
		checkParamNotNull("query", query);
		return findOne(query, _resultClass);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T findOne(Query query, Class<T> resultClass) {
		checkParamNotNull("query", query);
		checkParamNotNull("resultClass", resultClass);

		query.checkFilter();
		Document filter = query.getFilter();
		FindIterable<T> iter = col.find(filter, resultClass);
		iter.limit(1);
		
		Document fields = query.getFields();

		if (fields != null) {
			iter.projection(fields);
		}

		try (MongoCursor<T> cur = iter.iterator()) {
			if (cur.hasNext()) {
				return cur.next();
			}
		}
		
		return null;
	}
	
	@Override
	public long count(Json query) {
		checkParamNotNull("query", query);

		Document bson = query.toBson();
		long count = col.countDocuments(bson);
		return count;
	}

	@Override
	public long count(Query query) {
		checkParamNotNull("query", query);

		Document bson = query.getFilter();
		long count = col.countDocuments(bson);
		return count;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void insert(Json json) {
		checkParamNotNull("json", json);
		
		Document bson = json.toBson();
		col.insertOne(bson);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void insert(Object entity) {
		checkParamNotNull("entity", entity);
		if (entity instanceof Json) {
			insert((Json) entity);
			return;
		}
		
		col.insertOne(entity);
	}
	
	@Override
	public long updateOne(Query query) {
		checkParamNotNull("query", query);
		query.checkUpdate();

		Document filter = query.getFilter();
		if(filter == null){
			filter = new Document();
		}
		Document update = query.getUpdate();
		List<Document> list = query.getArrayFilters();
		if(list == null){
			return col.updateOne(filter, update).getModifiedCount();
		}

		UpdateOptions uo = new UpdateOptions();
		uo.arrayFilters(list);

		return col.updateOne(filter, update, uo).getModifiedCount();
	}


	
	@Override
	public long deleteMany(String filterKey, Object filterValue) {
		checkParamNotNull("filterKey", filterKey);
		checkParamNotNull("filterValue", filterValue);

		Document f = new Document();
		f.put(filterKey, filterValue);
		DeleteResult res = col.deleteMany(f);
		return res.getDeletedCount();
	}

	@Override
	public long deleteMany(Json filter) {
		checkParamNotNull("filter", filter);

		DeleteResult res = col.deleteMany(filter.toBson());
		return res.getDeletedCount();
	}
	

	@Override
	@SuppressWarnings("unchecked")
	public <T> AggregateIterable<T> aggregate(PipeLine pipeline, Class<T> resultClass) {
		checkParamNotNull("pipeline", pipeline);
		
		AggregateIterable<T> ans = col.aggregate(pipeline.toList(), resultClass);
		return ans;
	}


	public List aggregateAndCollect(PipeLine pipeline){
		return aggregateAndCollect(pipeline, _resultClass);
	}

	@Override
	public List aggregateAndCollect(PipeLine pipeline, Integer limit) {
		return aggregateAndCollect(pipeline, _resultClass, limit);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> aggregateAndCollect(PipeLine pipeline, Class<T> resultClass, Integer limit) {
		AggregateIterable<T> agg = aggregate(pipeline, resultClass);
		return MongoUtils.collect(agg, limit);
	}


	public <T> DistinctIterable<T> distinct(String fieldName, Class<T> resultClass){
		return col.distinct(fieldName, resultClass);
	}


	public <T> DistinctIterable<T> distinct(String fieldName, Document filter, Class<T> resultClass){
		return col.distinct(fieldName, filter, resultClass);
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void print() {
		
		List list = list(DEFAULT_ROW_PRINT);
		
		List arr = new ArrayList<>();
		
		for (Object e : list) {
			if (e == null) {
				arr.add(null);
			} else if (e instanceof Map) {
				arr.add(e);
			} else if (e instanceof Json) {
				arr.add(((Json) e).toMap());
			} else {
				arr.add(Json.parseAsMap(Json.write(e)));
			}
		}
		
		Grid g = Grid.wrap(arr);
		g.print();
	}

}
