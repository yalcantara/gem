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
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class Collection {
	
	public static final int DEFAULT_ROW_PRINT = 50;
	
	@SuppressWarnings("rawtypes")
	private final MongoCollection col;
	
	@SuppressWarnings("rawtypes")
	public Collection(MongoCollection col) {
		checkParamNotNull("col", col);
		this.col = col;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List find(int max) {
		checkParamHigherThan("max", 0, max);

		FindIterable iter = col.find();
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
	
	public long count(Json query) {
		checkParamNotNull("query", query);
		
		Document bson = query.toBson();
		long count = col.countDocuments(bson);
		return count;
	}

	@SuppressWarnings("unchecked")
	public void insert(Json json) {
		checkParamNotNull("json", json);

		Document bson = json.toBson();
		col.insertOne(bson);
	}

	@SuppressWarnings("unchecked")
	public void insert(Object entity) {
		checkParamNotNull("entity", entity);
		if (entity instanceof Json) {
			insert((Json) entity);
			return;
		}

		col.insertOne(entity);
	}
	
	@SuppressWarnings("unchecked")
	public AggregateIterable<Document> agregate(Json pipeline) {
		checkParamNotNull("pipeline", pipeline);

		Document bson = pipeline.toBson();
		
		AggregateIterable<Document> ans = col.aggregate(Arrays.asList(bson), Document.class);
		return ans;
	}

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
