package com.gem.commons.mongo;

import static com.gem.commons.Checker.checkParamNotNull;

import com.gem.commons.Grid;
import com.gem.commons.Lazy;
import com.mongodb.client.MongoDatabase;

public class MongoDBImpl implements MongoDB {

	private final Lazy<MongoDatabase> db;
	
	public MongoDBImpl(Lazy<MongoDatabase> db) {
		checkParamNotNull("db", db);
		this.db = db;
	}
	
	@Override
	public Collection getCollection(String name) {
		checkParamNotNull("name", name);
		
		return new CollectionImpl(Lazy.wrap(() -> db.get().getCollection(name)));
	}
	
	@Override
	public Collection getCollection(String name, Class<?> documentClass) {
		return new CollectionImpl(Lazy.wrap(() -> db.get().getCollection(name, documentClass)));
	}

	@Override
	public void printCollections() {
		
		Iterable<String> names = db.get().listCollectionNames();

		Grid g = new Grid(names);
		g.header(0, "Collections");
		g.print();
	}

}
