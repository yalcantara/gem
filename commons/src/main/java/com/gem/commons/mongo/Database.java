package com.gem.commons.mongo;

import static com.gem.commons.Checker.checkParamNotNull;

import org.bson.Document;

import com.gem.commons.Grid;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Database {
	
	private final MongoDatabase db;

	public Database(MongoDatabase db) {
		checkParamNotNull("db", db);
		this.db = db;
	}

	public Collection getCollection(String name) {
		checkParamNotNull("name", name);
		MongoCollection<Document> col = db.getCollection(name);

		return new Collection(col);
	}
	
	public void printCollections() {

		Iterable<String> names = db.listCollectionNames();
		
		Grid g = new Grid(names);
		g.header(0, "Collections");
		g.print();
	}
}
