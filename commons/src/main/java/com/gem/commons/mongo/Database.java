package com.gem.commons.mongo;

import java.util.Set;

import com.gem.commons.Grid;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class Database {
	
	private final DB db;

	public Database(DB db) {
		this.db = db;
	}

	public Collection getCollection(String name) {
		DBCollection col = db.getCollection(name);

		return new Collection(col);
	}
	
	public void printCollections() {

		Set<String> names = db.getCollectionNames();
		
		Grid g = new Grid(names);
		g.header(0, "Collections");
		g.print();
	}
}
