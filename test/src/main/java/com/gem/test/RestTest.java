package com.gem.test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.gem.commons.Json;
import com.gem.commons.rest.RestClient;

/**
 * Hello world!
 *
 */
public class RestTest {
	public static void main(String[] args) {
		RestClient c = new RestClient();
		String url = "http://localhost:8080/rest/config/apps/5c8e96277aa7f6ea9c1a342f";
		Json json = new Json();
		json.put("name", "ddde");
		Response r = c.put(url, json);
		System.out.println(r);
	}

	static void test1() {
		String[] names = { "911", "wot", "ndmt", "netasssist", "ncct", "admin", "edx" };

		List<Document> list = new ArrayList<Document>();
		
		for (int i = 0; i < names.length; i++) {
			String name = names[i];

			Object id = ObjectId.get();
			
			Document doc = new Document();
			doc.put("_id", id);
			doc.put("name", name);
			doc.put("label", null);

			Instant d = Instant.now();
			d = d.minus(5 * 365, ChronoUnit.DAYS);
			int days = (int) (Math.random() * 900);
			long creation = d.plus(days, ChronoUnit.DAYS).toEpochMilli();

			int plus = (int) (Math.random() * 360);
			long update = d.plus(plus, ChronoUnit.DAYS).toEpochMilli();
			
			doc.put("lastUpdate", update);
			doc.put("creationDate", creation);

			list.add(doc);
		}
		
		Document holder = new Document();
		holder.put("arr", list);
		System.out.print(holder.toJson());
	}
}
