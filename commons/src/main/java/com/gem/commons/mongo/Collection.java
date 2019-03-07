package com.gem.commons.mongo;

import static com.gem.commons.Checker.checkParamHigherThan;
import static com.gem.commons.Checker.checkParamNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.gem.commons.Grid;
import com.gem.commons.Json;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class Collection {

	public static final int DEFAULT_ROW_PRINT = 50;

	private final MongoCollection<Document> col;

	public Collection(MongoCollection<Document> col) {
		checkParamNotNull("col", col);
		this.col = col;
	}
	
	public List<Document> find(int max) {
		checkParamHigherThan("max", 0, max);
		
		FindIterable<Document> iter = col.find();
		iter.limit(max);

		List<Document> arr = new ArrayList<>();

		try (MongoCursor<Document> cur = iter.iterator()) {
			int count = 0;
			
			// let us add another layer of safety by having a counter.
			while (cur.hasNext() && count < max) {
				count++;
				Document bson = cur.next();
				
				arr.add(bson);
			}
		}

		return arr;
	}

	public long count(Json query) {
		checkParamNotNull("query", query);

		Document bson = Converter.toBson(query);
		long count = col.countDocuments(bson);
		return count;
	}
	
	public void insert(Json json) {
		checkParamNotNull("json", json);
		
		Document bson = Converter.toBson(json);
		col.insertOne(bson);
	}

	public AggregateIterable<Document> agregate(Json pipeline) {
		checkParamNotNull("pipeline", pipeline);
		
		Document bson = Converter.toBson(pipeline);

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

	public void print() {
		
		List<Document> list = find(DEFAULT_ROW_PRINT);
		
		Grid g = Grid.wrap(list);
		g.print();
	}
}
