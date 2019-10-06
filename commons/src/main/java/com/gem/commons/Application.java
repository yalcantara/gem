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
	private static final Lazy<Props> _props = Lazy.wrap(() -> {
		return Props.fromClassPathResource("/application.properties");
	});

	private static Props props(){
		return _props.get();
	}

	public static void check(String key) {
		props().check(key);
	}

	public static String get(String key) {
		return props().get(key);
	}

	public static String checkAndGet(String key) {
		return props().checkAndGet(key);
	}

	public static String get(String key, String defaultValue) {
		return props().get(key, defaultValue);
	}

	public static Integer getInteger(String key, Integer defaultValue) {
		return props().getInteger(key, defaultValue);
	}
}
