package com.gem.commons.mongo;

import com.gem.commons.Json;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class PipeLine {
	
	private final List<Document> list;

	private PipeLine(List<Document> list){
		this.list = new ArrayList<>();

		for(Document d:list){
			this.list.add(d);
		}
	}
	
	public PipeLine() {
		list = new ArrayList<>();
	}
	
	public void match(String field, String val) {
		Document doc = new Document();
		
		Document filter = new Document();
		filter.put(field, val);
		doc.put("$match", filter);
		list.add(doc);
	}


	public void project(Json fields){
		Document doc = new Document();

		Document fd = new Document();

		fd.putAll(fields.toMap());

		doc.put("$project", fd);
		list.add(doc);
	}
	
	public void project(String field) {
		Document doc = new Document();
		
		Document fd = new Document();

		fd.put(field, 1);

		doc.put("$project", fd);
		list.add(doc);
	}
	
	public void projects(String... fields) {
		Document doc = new Document();
		
		Document fd = new Document();
		for (String f : fields) {
			fd.put(f, 1);
		}
		
		doc.put("$project", fd);
		list.add(doc);
	}
	
	public void unwind(String field) {
		Document doc = new Document();

		doc.put("$unwind", field);
		list.add(doc);
	}

	public void count() {
		count("count");
	}

	public void count(String out) {
		Document doc = new Document();

		doc.put("$count", out);
		list.add(doc);
	}


	
	public List<Document> toList() {
		return new ArrayList<Document>(list);
	}

	public PipeLine clone(){
		return new PipeLine(list);
	}
}
