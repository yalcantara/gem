package com.gem.commons.mongo;

import java.util.List;
import java.util.Map;

import com.gem.commons.Grid;
import com.gem.commons.JsonArray;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Collection {

	public static final int DEFAULT_ROW_PRINT = 50;

	private final DBCollection col;

	public Collection(DBCollection col) {
		this.col = col;
	}
	
	@SuppressWarnings("rawtypes")
	public JsonArray find(int max) {

		DBCursor cur = col.find();

		JsonArray arr = new JsonArray();
		int count = 0;
		while (cur.hasNext() && count < max) {
			count++;
			DBObject obj = cur.next();

			Map map = obj.toMap();

			arr.add(map);
		}

		return arr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void print() {
		
		JsonArray arr = find(DEFAULT_ROW_PRINT);

		List list = arr.toList();
		
		Grid g = Grid.wrap(list);
		g.print();
	}
}
