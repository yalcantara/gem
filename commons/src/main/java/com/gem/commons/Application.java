package com.gem.commons;

import static com.gem.commons.Checker.checkParamNotNull;

import java.util.Map.Entry;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class Application {

	private Application() {
	}

	@SuppressWarnings("rawtypes")
	private static final Lazy<Properties> props = Lazy.wrap(() -> {

		Resource resource = new ClassPathResource("/application.properties");
		Properties p = PropertiesLoaderUtils.loadProperties(resource);

		Properties ans = new Properties();
		for (Entry e : p.entrySet()) {

			Object k = e.getKey();
			Object v = e.getValue();

			if (v instanceof String) {
				v = ((String) v).strip();
			}

			ans.put(k, v);
		}

		return p;
	});
	
	public static void check(String key) {
		checkParamNotNull("key", key);
		String val = get(key);
		if (val == null) {
			throw new ConfigurationException("The property " + key + " is not defined.");
		}
	}
	
	public static Properties get() {
		return props.get();
	}
	
	public static String get(String key) {
		return get().getProperty(key);
	}

	public static String checkAndGet(String key) {
		check(key);
		return get(key);
	}
	
	public static String get(String key, String defaultValue) {
		String val = get().getProperty(key);
		if (val == null) {
			return defaultValue;
		}
		
		return val;
	}

	public static Integer getInteger(String key, Integer defaultValue) {
		String val = get().getProperty(key);
		if (val == null) {
			return defaultValue;
		}
		
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException ex) {
			throw new ConfigurationException(
					"Could not parse property '" + val + "' with key '" + key + "' to an integer.", ex);
		}
	}
}
