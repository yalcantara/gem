package com.gem.commons.mongo;

import com.gem.commons.Json;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import static java.util.Map.Entry;

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


	private void _match(String field, Object val){
		Document doc = new Document();

		Document filter = new Document();
		filter.put(field, val);
		doc.put("$match", filter);
		list.add(doc);
	}

	public void match(String field, String val) {
		_match(field, val);
	}

	public void match(String field, ObjectId val) {
		_match(field, val);
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
		return new ArrayList<>(list);
	}

	public PipeLine clone(){
		return new PipeLine(list);
	}

	public String toString(){

		if(list.isEmpty()){
			return "[]";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("[\n");
		for(int i =0; i < list.size(); i++){
			Document d = list.get(i);

			Entry<String, Object> entry = d.entrySet().iterator().next();

			String key = entry.getKey();

			Object v = entry.getValue();

			sb.append("{");
			sb.append(key);
			sb.append(": ");

			if(v instanceof Document){
				sb.append(((Document) v).toJson());
			}else{
				sb.append("'");
				sb.append(v);
				sb.append("'");
			}

			sb.append("}");

			if(i + 1 < list.size()){
				sb.append(",\n");
			}
		}

		sb.append("\n]");
		return sb.toString();
	}
}
