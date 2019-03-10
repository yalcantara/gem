package com.gem.commons.mongo;

import static com.gem.commons.Checker.checkParamNotNull;

import org.springframework.stereotype.Service;

import com.gem.commons.Application;
import com.gem.commons.Lazy;

@Service
public class MongoService {
	
	private final Lazy<Mongo> _client = Lazy.wrap(() -> {

		String host = Application.get("gem.mongo.host", "localhost");
		int port = Application.getInteger("gem.mongo.port", 27017);

		return new Mongo(host, port);
	});

	public MongoDB getDatabase(String name) {
		checkParamNotNull("name", name);
		return client().getDatabase(name);
	}
	
	public Mongo client() {
		return _client.get();
	}
}
