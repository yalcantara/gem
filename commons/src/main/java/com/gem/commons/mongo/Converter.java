package com.gem.commons.mongo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.Document;

import com.gem.commons.Json;

public class Converter {

	private Converter() {
	}
	
	public static <T> List<T> convertList(List<Document> list, Class<T> resultClass) {

		if (list == null) {
			return Collections.emptyList();
		}
		
		List<T> ans = new ArrayList<>();

		for (Document doc : list) {
			
			T t = convert(doc, resultClass);

			ans.add(t);
		}
		
		return ans;
	}
	
	public static <T> T convert(Document doc, Class<T> resultClass) {
		if (doc == null) {
			return null;
		}
		String json = Json.write(doc);
		T t = Json.parse(json, resultClass);
		return t;
	}
}
