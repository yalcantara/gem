package com.gem.commons.rest;

import com.sun.research.ws.wadl.Param;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.gem.commons.Checker.*;

public class Params {

	private final MultivaluedHashMap<String, String> map;


	public static Params of(String key, String value){
		checkParamNotNull("key", key);
		checkParamNotNull("value", value);
		var map = new MultivaluedHashMap<String, String>();
		map.put(key, Arrays.asList(value));
		return new Params(map);
	}

	public Params(){
		this.map = new MultivaluedHashMap<>();
	}

	public Params(MultivaluedMap<String, String> map){
		this.map = new MultivaluedHashMap<>();


		for(String key:map.keySet()){
			List<String> values = map.get(key);
			this.map.addAll(key, values);
		}
	}

	public Params add(String key, String value){
		checkParamNotNull("key", key);
		map.add(key, value);
		return this;
	}

	public WebTarget toTarget(final WebTarget target){

		WebTarget ans = target;
		for (var e : map.entrySet()) {

			var key =  e.getKey();
			var value = e.getValue();

			// It is important to know queryParam method won't update
			// current WebTarget object, but return a new one.
			Object[] arr = value.toArray();
			ans = ans.queryParam(key, arr);
		}

		return ans;
	}


	public Params clone(){
		return new Params(map);
	}


	@Override
	public String toString() {
		return map.toString();
	}
}
