package com.gem.commons.mongo;

import static com.gem.commons.Checker.checkParamHigherThan;
import static com.gem.commons.Checker.checkParamNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.gem.commons.Grid;
import com.gem.commons.Json;
import com.mongodb.AggregationOptions;
import com.mongodb.AggregationOptions.Builder;
import com.mongodb.AggregationOptions.OutputMode;
import com.mongodb.Cursor;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Collection {

	public static final int DEFAULT_ROW_PRINT = 50;

	private final DBCollection col;

	public Collection(DBCollection col) {
		checkParamNotNull("col", col);
		this.col = col;
	}
	
	@SuppressWarnings("rawtypes")
	public List<Json> find(int max) {
		checkParamHigherThan("max", 0, max);
		
		DBCursor cur = col.find();
		cur.limit(max);
		
		List<Json> arr = new ArrayList<>();
		int count = 0;
		while (cur.hasNext() && count < max) {
			count++;
			DBObject obj = cur.next();

			Map map = obj.toMap();

			Json json = new Json(map);
			arr.add(json);
		}

		return arr;
	}

	public long count(Json query) {
		checkParamNotNull("query", query);

		DBObject obj = Converter.toMongoObject(query);
		return col.count(obj);
	}
	
	public void insert(Json json) {
		checkParamNotNull("json", json);
		
		DBObject obj = Converter.toMongoObject(json);
		col.insert(obj);
	}

	public Cursor agregate(Json pipeline) {
		checkParamNotNull("pipeline", pipeline);
		
		Builder b = AggregationOptions.builder();
		b.outputMode(OutputMode.INLINE);

		AggregationOptions options = b.build();
		DBObject obj = Converter.toMongoObject(pipeline);
		
		Cursor ans = col.aggregate(Arrays.asList(obj), options);
		return ans;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void print() {
		
		List<Json> json = find(DEFAULT_ROW_PRINT);
		
		List list = Json.convert(json);
		
		Grid g = Grid.wrap(list);
		g.print();
	}
}
