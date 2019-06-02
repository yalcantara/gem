package com.gem.commons.mongo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.Document;

import com.gem.commons.Json;
import org.bson.codecs.DocumentCodec;

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

		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 8); // 8kb
		
		T t;
		try {
			Json.plainWrite(baos, doc);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			t = Json.parse(bais, resultClass);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return t;
	}


	public static Document convert(Object obj){
		if (obj == null) {
			return null;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 8); // 8kb

		Document doc;

			String str = Json.plainWrite(obj);
			doc = Document.parse(str);

		return doc;
	}
}
