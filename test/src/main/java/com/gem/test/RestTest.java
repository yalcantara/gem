package com.gem.test;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import com.mongodb.util.JSON;


/**
 * Hello world!
 *
 */
public class RestTest {
	public static void main(String[] args) {


		String[] names = {"911", "wot", "ndmt", "netasssist", "ncct", "admin", "edx"};

		List<Document> list = new ArrayList<Document>();
		
		for(int i =0; i < names.length; i++){
			String name = names[i];
	
			Object id = ObjectId.get();
			
			Document doc = new Document();
			doc.put("_id", id);
			doc.put("name", name);
			doc.put("label", null);

			Instant d = Instant.now();
			d = d.minus(5 * 365, ChronoUnit.DAYS);
			int days = (int)(Math.random()*900);
			long creation = d.plus(days, ChronoUnit.DAYS).toEpochMilli();


			int plus = (int)(Math.random()*360);
			long update = d.plus(plus, ChronoUnit.DAYS).toEpochMilli();
			
			doc.put("lastUpdate", update);
			doc.put("creationDate", creation);
	
			
			list.add(doc);
		}
		
		Document holder = new Document();
		holder.put("arr", list);
		JsonWriterSettings settings = new JsonWriterSettings(JsonMode.RELAXED, true);
		System.out.print(holder.toJson(settings));

	}
}
