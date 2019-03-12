package com.gem.commons.mongo;

import static com.gem.commons.Checker.checkParamNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.gem.commons.Grid;
import com.gem.commons.Json;
import com.gem.commons.Lazy;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;

public class CollectionImpl implements Collection {
	
	@SuppressWarnings("rawtypes")
	private final Lazy<MongoCollection> col;
	
	private final Class<?> _resultClass;
	
	@SuppressWarnings("rawtypes")
	public CollectionImpl(Lazy<MongoCollection> col) {
		this(col, Document.class);
	}
	
	@SuppressWarnings("rawtypes")
	public CollectionImpl(Lazy<MongoCollection> col, Class<?> resultClass) {
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
			iter = col.get().find(resultClass);
		} else {
			iter = col.get().find(filter, resultClass);
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
	@SuppressWarnings("unchecked")
	public <T> T findOne(Query query, Class<T> resultClass) {
		checkParamNotNull("query", query);
		checkParamNotNull("resultClass", resultClass);

		query.checkFilter();
		FindIterable<T> iter = col.get().find(query.getFilter(), resultClass);
		iter.limit(1);
		
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
		long count = col.get().countDocuments(bson);
		return count;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void insert(Json json) {
		checkParamNotNull("json", json);
		
		Document bson = json.toBson();
		col.get().insertOne(bson);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void insert(Object entity) {
		checkParamNotNull("entity", entity);
		if (entity instanceof Json) {
			insert((Json) entity);
			return;
		}
		
		col.get().insertOne(entity);
	}
	
	@Override
	public long update(String filterKey, Object filterValue, Json query) {
		checkParamNotNull("filterKey", filterKey);
		checkParamNotNull("filterValue", filterValue);
		checkParamNotNull("query", query);
		return update(filterKey, filterValue, query.toBson());
	}
	
	@Override
	public long update(String filterKey, Object filterValue, Document query) {
		checkParamNotNull("filterKey", filterKey);
		checkParamNotNull("filterValue", filterValue);
		checkParamNotNull("query", query);
		Document f = new Document();
		f.put(filterKey, filterValue);
		
		return col.get().updateOne(f, query).getModifiedCount();
	}
	
	@Override
	public long update(Query query) {
		checkParamNotNull("query", query);
		query.checkUpdate();
		return col.get().updateOne(query.getFilter(), query.getUpdate()).getModifiedCount();
	}
	
	@Override
	public boolean deleteOne(Query query) {
		checkParamNotNull("query", query);
		query.checkFilter();
		DeleteResult res = col.get().deleteOne(query.getFilter());
		return res.getDeletedCount() > 0;
	}
	
	@Override
	public boolean deleteOne(String filterKey, Object filterValue) {
		checkParamNotNull("filterKey", filterKey);
		checkParamNotNull("filterValue", filterValue);
		
		Document f = new Document();
		f.put(filterKey, filterValue);
		DeleteResult res = col.get().deleteOne(f);
		return res.getDeletedCount() > 0;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public AggregateIterable<Document> agregate(Json pipeline) {
		checkParamNotNull("pipeline", pipeline);
		
		Document bson = pipeline.toBson();
		
		AggregateIterable<Document> ans = col.get().aggregate(Arrays.asList(bson), Document.class);
		return ans;
	}
	
	@Override
	public List<Document> agregateAndCollect(Json pipeline) {
		
		AggregateIterable<Document> agg = agregate(pipeline);
		
		List<Document> list = new ArrayList<>();
		try (MongoCursor<Document> cur = agg.iterator()) {
			while (cur.hasNext()) {
				Document doc = cur.next();
				list.add(doc);
			}
		}
		
		return list;
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
