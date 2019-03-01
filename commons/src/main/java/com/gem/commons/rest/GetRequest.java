package com.gem.commons.rest;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.core.AbstractMultivaluedMap;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class GetRequest {

	private UriComponents comp;
	
	private MultivaluedMap<String, String> headers = new MultivaluedLinkedHashMap();
	private MultivaluedMap<String, String> params = new MultivaluedLinkedHashMap();

	public GetRequest() {
		super();
		headers.add("Accept", MediaType.WILDCARD);
	}

	public URI toUri() {
		if (comp == null) {
			throw new NullPointerException("The URL is null.");
		}
		
		return comp.toUri();
	}

	public void setUrl(String url) {
		URI uri;
		try {
			uri = new URI(url);
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
		
		this.comp = UriComponentsBuilder.fromUri(uri).build();
		String query = comp.getQuery();
		if (query == null) {
			return;
		}

		MultiValueMap<String, String> params = comp.getQueryParams();
		
		this.params.putAll(params);
	}

	public void putHeader(String key, String val) {
		headers.putSingle(key, val);
	}

	public void putParam(String key, String val) {
		params.putSingle(key, val);
	}

	public MultivaluedMap<String, String> getParams() {
		MultivaluedLinkedHashMap dup = new MultivaluedLinkedHashMap();
		dup.localPutAll(params);
		
		return dup;
	}

	public MultivaluedMap<String, String> getHeaders() {
		MultivaluedMap<String, String> dup = new MultivaluedLinkedHashMap();
		dup.putAll(headers);

		return dup;
	}
	
	private static final class MultivaluedLinkedHashMap
			extends AbstractMultivaluedMap<String, String> implements Serializable {

		private static final long serialVersionUID = 3712848416039042918L;
		
		private MultivaluedLinkedHashMap() {
			super(new LinkedHashMap<String, List<String>>());
		}

		private MultivaluedLinkedHashMap(MultivaluedMap<String, String> map) {
			this();
			localPutAll(map);
		}

		/**
		 * This private method is used by the copy constructor to avoid exposing
		 * additional generic parameters through the public API documentation.
		 *
		 * @param map the map
		 */
		private void localPutAll(MultivaluedMap<String, String> map) {
			for (Entry<String, List<String>> e : map.entrySet()) {
				store.put(e.getKey(), new ArrayList<String>(e.getValue()));
			}
		}
	}
}
