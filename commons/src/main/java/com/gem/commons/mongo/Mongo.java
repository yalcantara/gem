package com.gem.commons.mongo;

import static com.gem.commons.Checker.checkParamIsPositive;
import static com.gem.commons.Checker.checkParamNotNull;

import java.io.Closeable;

import com.gem.commons.Grid;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class Mongo implements Closeable {
	
	private final MongoClient client;

	public Mongo() {
		client = MongoClients.create();
	}

	public Mongo(int port) {
		checkParamIsPositive("port", port);

		StringBuilder sb = new StringBuilder("mongodb://localhost:");
		sb.append(port);

		String str = sb.toString();
		client = MongoClients.create(str);
	}

	public Mongo(String host, int port) {
		checkParamNotNull("host", host);
		checkParamIsPositive("port", port);

		StringBuilder sb = new StringBuilder("mongodb://");
		sb.append(host);
		sb.append(":");
		sb.append(port);

		String str = sb.toString();
		client = MongoClients.create(str);
	}

	public Mongo(String connectionString) {
		checkParamNotNull("connectionString", connectionString);
		client = MongoClients.create(connectionString);
	}

	public Database getDatabase(String name) {
		checkParamNotNull("name", name);

		MongoDatabase db = client.getDatabase(name);
		return new Database(db);
	}

	public void printDatabases() {

		Iterable<String> names = client.listDatabaseNames();

		Grid g = new Grid(names);
		g.header(0, "Databases");
		g.print();
	}

	@Override
	public void close() {
		client.close();
	}
}
