package com.gem.commons.mongo;

import static com.gem.commons.Checker.checkParamIsPositive;
import static com.gem.commons.Checker.checkParamNotNull;

import java.io.Closeable;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.gem.commons.Grid;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientSettings.Builder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class Mongo implements Closeable {
	
	private final MongoClient client;

	private static MongoClientSettings settings() {
		return settings(null);
	}

	private static MongoClientSettings settings(String connectionString) {
		CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
			MongoClientSettings.getDefaultCodecRegistry(),
			CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		Builder b = MongoClientSettings.builder();
		if (connectionString != null) {
			b.applyConnectionString(new ConnectionString(connectionString));
		}
		
		MongoClientSettings settings = b.codecRegistry(pojoCodecRegistry).build();
		
		return settings;
	}
	
	public Mongo() {

		client = MongoClients.create(settings());
	}

	public Mongo(int port) {
		this("localhost", port);
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
		client = MongoClients.create(settings(connectionString));
	}

	public MongoDB getDatabase(String name) {
		checkParamNotNull("name", name);
		MongoDatabase db = client.getDatabase(name);
		return new MongoDBImpl(db);
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
