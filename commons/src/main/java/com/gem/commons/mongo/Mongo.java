package com.gem.commons.mongo;

import static com.gem.commons.Checker.checkParamIsPositive;
import static com.gem.commons.Checker.checkParamNotNull;

import java.io.Closeable;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.gem.commons.Grid;
import com.gem.commons.Lazy;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientSettings.Builder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class Mongo implements Closeable {
	
	private final Lazy<MongoClient> client;

	public static Collection proxyCollection(MongoDB db, String name, Class<?> documentClass) {
		checkParamNotNull("db", db);
		checkParamNotNull("name", name);
		checkParamNotNull("documentClass", documentClass);
		return Lazy.proxy(Collection.class, () -> db.getCollection(name, documentClass));
	}

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

		client = Lazy.wrap(() -> MongoClients.create(settings()));
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
		client = Lazy.wrap(() -> MongoClients.create(settings(str)));
	}

	public Mongo(String connectionString) {
		checkParamNotNull("connectionString", connectionString);
		client = Lazy.wrap(() -> MongoClients.create(settings(connectionString)));
	}

	public MongoDB getDatabase(String name) {
		checkParamNotNull("name", name);
		return new MongoDBImpl(Lazy.wrap(() -> client.get().getDatabase(name)));
	}

	public void printDatabases() {

		Iterable<String> names = client.get().listDatabaseNames();

		Grid g = new Grid(names);
		g.header(0, "Databases");
		g.print();
	}

	public MongoClient client() {
		return client.get();
	}

	@Override
	public void close() {
		client.get().close();
	}
}
