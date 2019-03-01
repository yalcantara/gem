package com.gem.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonArray {
	
	private final List<Object> list;

	public JsonArray() {
		list = new ArrayList<>();
	}

	@SuppressWarnings("rawtypes")
	public void add(Map json) {
		list.add(json);
	}

	public List<Object> toList() {
		return new ArrayList<Object>(list);
	}
}
