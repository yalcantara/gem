package com.gem.commons.mongo;

import static com.gem.commons.Checker.checkParamNotNull;

import org.bson.Document;

import com.gem.commons.Grid;
import com.gem.commons.Lazy;
import com.mongodb.client.MongoDatabase;

public class MongoDBImpl implements MongoDB {

	private final MongoDatabase db;
	
	public MongoDBImpl(MongoDatabase db) {
		checkParamNotNull("db", db);
		this.db = db;
	}
	
	@Override
	public Collection getCollection(String name) {
		return getCollection(name, Document.class);
	}
	
	@Override
	public <E> Collection<E> getCollection(String name, Class<E> documentClass) {
		Collection proxy = Lazy.proxy(Collection.class,
			() -> new CollectionImpl(db.getCollection(name, documentClass), documentClass));
		return proxy;
	}

	@Override
	public void printCollections() {
		
		Iterable<String> names = db.listCollectionNames();

		Grid g = new Grid(names);
		g.header(0, "Collections");
		g.print();
	}

}
