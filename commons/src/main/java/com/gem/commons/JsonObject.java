package com.gem.commons;

import java.util.LinkedHashMap;
import java.util.Map;

public class JsonObject {
	
	private final Map<String, Object> map;
	
	public JsonObject() {
		map = new LinkedHashMap<>();
	}
	
	public void set(String key, Integer val) {
		checkParamNotNull("key", key);
		map.put(key, val);
	}
	
	public Integer getInt(String key) {
		checkParamNotNull("key", key);
		Object val = map.get(key);
		return (Integer) val;
	}
	
	public String getString(String key) {
		checkParamNotNull("key", key);
		Object val = map.get(key);
		return (String) val;
	}

	private void checkParamNotNull(String name, Object val) {
		if (val == null) {
			throw new NullPointerException("The parameter '" + name + "' can not be null.");
		}
	}
}
