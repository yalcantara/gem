package com.gem.commons.mongo;

import java.net.UnknownHostException;
import java.util.List;

import com.gem.commons.Grid;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class Mongo {

	public static void main(String[] args) {
		Mongo m = new Mongo(12717);
		
		m.printDatabases();

		Database db = m.getDatabase("carmarket");
		Collection col = db.getCollection("sp_posts");
		
		col.print();
	}

	private MongoClient client;

	public Mongo() {
		try {
			client = new MongoClient();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public Mongo(int port) {
		try {
			client = new MongoClient("localhost", port);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public Database getDatabase(String name) {
		DB db = client.getDB(name);
		return new Database(db);
	}

	public void printDatabases() {
		
		List<String> names = client.getDatabaseNames();
		
		Grid g = new Grid(names);
		g.header(0, "Databases");
		g.print();
	}
}
