package com.gem.commons;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Json implements Iterable<String>, Serializable {
	
	private static final long serialVersionUID = -2237588925474667391L;
	
	private static final ObjectMapper mapper;
	private static final ObjectWriter writter;

	static {
		mapper = new ObjectMapper();
		writter = mapper.writerWithDefaultPrettyPrinter();
	}
	
	@SuppressWarnings("rawtypes")
	public static Json parse(String json) {

		try {

			LinkedHashMap map = mapper.readValue(json, LinkedHashMap.class);
			return new Json(map);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T parse(String json, Class<T> clazz) {

		try {

			T obj = mapper.readValue(json, clazz);
			return obj;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static Map parseAsMap(String json) {
		
		try {
			return mapper.readValue(json, LinkedHashMap.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static String write(List list) {

		if (list == null) {
			return "null";
		}
		
		if (list.size() == 0) {
			return "[]";
		}

		try {
			return writter.writeValueAsString(list);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String write(Object obj) {

		try {
			return writter.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static String write(Map map) {

		if (map == null) {
			return "null";
		}
		
		if (map.isEmpty()) {
			return "{}";
		}

		try {
			return writter.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static List<Map<String, Object>> convert(List<Json> json) {
		List<Map<String, Object>> list = new ArrayList<>();
		
		json.stream().forEach(e -> list.add(e.toMap()));
		
		return list;
	}
	
	private final Map<String, Object> map;
	
	// private constructor, only for internal use.
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Json(LinkedHashMap map) {
		this.map = map;
	}
	
	public Json() {
		map = new LinkedHashMap<>();
	}

	@SuppressWarnings("rawtypes")
	public Json(Map map) {
		this.map = new LinkedHashMap<>();
		
		for (Object raw : map.entrySet()) {
			Entry e = (Entry) raw;
			
			String key = String.valueOf(e.getKey());
			Object val = e.getValue();
			
			set(key, val);
		}
	}
	
	public void set(String key, Object val) {
		if (val == null) {
			set(key, (String) null);
			return;
		}
		
		if (val instanceof Integer) {
			set(key, (Integer) val);
			return;
		}
		
		if (val instanceof Long) {
			set(key, (Long) val);
			return;
		}
		
		throw new IllegalArgumentException("Unsupported value type: " + val.getClass().getName());
	}
	
	public void set(String key, Integer val) {
		checkParamNotNull("key", key);
		map.put(key, val);
	}

	public void set(String key, Long val) {
		checkParamNotNull("key", key);
		map.put(key, val);
	}
	
	public void set(String key, String val) {
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
	
	public Object getObject(String key) {
		checkParamNotNull("key", key);
		Object val = map.get(key);
		return val;
	}
	
	public Set<String> keys() {
		return map.keySet();
	}
	
	private void checkParamNotNull(String name, Object val) {
		if (val == null) {
			throw new NullPointerException("The parameter '" + name + "' can not be null.");
		}
	}
	
	public Map<String, Object> toMap() {
		return new LinkedHashMap<String, Object>(map);
	}

	public Document toBson() {
		
		Document b = new Document();

		for (Entry<String, Object> ent : entrySet()) {
			b.put(ent.getKey(), ent.getValue());
		}
		
		return b;
	}
	
	@Override
	public Iterator<String> iterator() {
		return map.keySet().iterator();
	}
	
	public Set<Entry<String, Object>> entrySet() {
		return map.entrySet();
	}

	@Override
	public String toString() {
		return Json.write(map);
	}
}
