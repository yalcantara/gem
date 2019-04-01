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

public class CollectionImpl implements Collection {
	
	@SuppressWarnings("rawtypes")
	private final MongoCollection col;
	
	private final Class<?> _resultClass;
	
	@SuppressWarnings("rawtypes")
	public CollectionImpl(MongoCollection col) {
		this(col, Document.class);
	}
	
	@SuppressWarnings("rawtypes")
	public CollectionImpl(MongoCollection col, Class<?> resultClass) {
		checkParamNotNull("col", col);
		checkParamNotNull("resultClass", resultClass);
		this.col = col;
		_resultClass = resultClass;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public List find() {
		return find(DEFAULT_LIMIT);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public List find(int limit) {
		Query q = new Query();
		q.setLimit(limit);
		return find(q);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public List find(Query query) {
		checkParamNotNull("query", query);
		return find(query, _resultClass);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> find(Query query, Class<T> resultClass) {
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
		
		return collect(iter, max);
	}
	
	@Override
	public Object findOne(String filterKey, Object filterValue) {
		checkParamNotNull("filterKey", filterKey);
		checkParamNotNull("filterValue", filterValue);
		Query q = new Query();
		q.filter(filterKey, filterValue);
		return findOne(q, _resultClass);
	}
	

	
	@Override
	public Object findOne(ObjectId id) {
		checkParamNotNull("id", id);
		Query q = new Query();
		q.filter("_id", id);
		return findOne(q, _resultClass);
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
	public boolean deleteMany(String filterKey, Object filterValue) {
		checkParamNotNull("filterKey", filterKey);
		checkParamNotNull("filterValue", filterValue);
		
		Document f = new Document();
		f.put(filterKey, filterValue);
		DeleteResult res = col.deleteMany(f);
		return res.getDeletedCount() > 0;
	}
	

	@Override
	@SuppressWarnings("unchecked")
	public <T> AggregateIterable<T> aggregate(PipeLine pipeline, Class<T> resultClass) {
		checkParamNotNull("pipeline", pipeline);
		
		AggregateIterable<T> ans = col.aggregate(pipeline.toList(), resultClass);
		return ans;
	}


	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> aggregateAndCollect(PipeLine pipeline, Class<T> resultClass) {
		AggregateIterable<T> agg = aggregate(pipeline, resultClass);
		
		return collect(agg, DEFAULT_LIMIT);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List collect(MongoIterable iter, Integer limit) {
		List arr = new ArrayList<>();
		
		int capedLimit = (limit == null) ? DEFAULT_LIMIT : limit;
		try (MongoCursor cur = iter.iterator()) {
			int count = 0;
			
			// let us add another layer of safety by having a counter.
			while (cur.hasNext() && count < capedLimit) {
				count++;
				Object doc = cur.next();
				
				arr.add(doc);
			}
		}
		
		return arr;
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void print() {
		
		List list = find(DEFAULT_ROW_PRINT);
		
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
