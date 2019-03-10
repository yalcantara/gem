package com.gem.commons.mongo;

import static com.gem.commons.Checker.checkParamHigherThan;
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
import com.mongodb.client.result.DeleteResult;

public class CollectionImpl implements Collection {
	
	@SuppressWarnings("rawtypes")
	private final Lazy<MongoCollection> col;
	
	@SuppressWarnings("rawtypes")
	public CollectionImpl(Lazy<MongoCollection> col) {
		checkParamNotNull("col", col);
		this.col = col;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List find(int max) {
		checkParamHigherThan("max", 0, max);

		FindIterable iter = col.get().find();
		iter.limit(max);
		
		List arr = new ArrayList<>();
		
		try (MongoCursor cur = iter.iterator()) {
			int count = 0;

			// let us add another layer of safety by having a counter.
			while (cur.hasNext() && count < max) {
				count++;
				Object doc = cur.next();

				arr.add(doc);
			}
		}
		
		return arr;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object findOne(String key, String val) {
		checkParamNotNull("key", key);
		checkParamNotNull("val", val);

		Document q = new Document();
		q.put(key, val);

		FindIterable iter = col.get().find(q);
		iter.limit(1);
		
		try (MongoCursor cur = iter.iterator()) {
			// let us add another layer of safety by having a counter.
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
	public Object update(String key, Object id, Json query) {
		checkParamNotNull("query", query);
		return update(key, id, query.toBson());
	}
	
	@Override
	public Object update(String key, Object id, Query query) {
		checkParamNotNull("query", query);
		return update(key, id, query.toBson());
	}

	@Override
	public Object update(String key, Object id, Document query) {
		checkParamNotNull("key", key);
		checkParamNotNull("id", id);
		checkParamNotNull("query", query);
		Document f = new Document();
		f.put(key, id);

		return col.get().findOneAndUpdate(f, query);
	}
	
	@Override
	public boolean deleteOne(String key, Object id) {
		checkParamNotNull("key", key);
		checkParamNotNull("id", id);

		Document f = new Document();
		f.put(key, id);
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
