package com.gem.commons;

import static com.gem.commons.Checker.checkParamNotNull;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("gem")
public class GemConfig {
	
	@Inject
	private MongoConfig mongo;

	public MongoConfig mongo() {
		return mongo;
	}

	public void check(String key) {
		checkParamNotNull("key", key);
		String val = Application.get(key);
		if (val == null) {
			throw new ConfigurationException("The property " + key + " is not defined.");
		}

	}

	@Component
	public class MongoConfig {

		@Value("${gem.mongo.database}")
		private String database;

		public String getDatabase() {
			return database;
		}
	}
}
