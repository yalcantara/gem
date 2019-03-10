package com.gem.commons.mongo;

import com.gem.commons.ConfigurationException;
import com.gem.commons.GemConfig;
import com.gem.commons.Lazy;
import com.gem.commons.Locator;

public class ConfigMongoDBImpl implements MongoDB {

	private static final Lazy<MongoService> srv = Locator.lazy(MongoService.class);
	
	private final Lazy<MongoDB> ref;

	public ConfigMongoDBImpl(GemConfig config) {
		ref = Lazy.wrap(() -> {
			String name = config.mongo().getDatabase();
			if (name == null) {
				throw new ConfigurationException("The property gem.mongo.database is not defined.");
			}
			
			return srv.get().client().getDatabase(name);
		});
	}

	private MongoDB get() {
		return ref.get();
	}

	@Override
	public Collection getCollection(String name) {
		return get().getCollection(name);
	}

	@Override
	public Collection getCollection(String name, Class<?> documentClass) {
		return getCollection(name, documentClass);
	}

	@Override
	public void printCollections() {
		get().printCollections();
	}
	
}
