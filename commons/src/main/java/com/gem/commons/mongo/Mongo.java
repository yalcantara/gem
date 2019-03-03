package com.gem.commons.mongo;

import static com.gem.commons.Checker.checkParamIsPositive;
import static com.gem.commons.Checker.checkParamNotNull;

import java.net.UnknownHostException;
import java.util.List;

import com.gem.commons.Grid;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class Mongo {

	public static void main(String[] args) {

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
		checkParamIsPositive("port", port);
		
		try {
			client = new MongoClient("localhost", port);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public Database getDatabase(String name) {
		checkParamNotNull("name", name);
		
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
