package com.gem.commons.rest;

import java.net.URI;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.gem.commons.Lazy;
import com.gem.commons.Retriever;

public class RestClient {

	private static final Lazy<Client> __client = build();

	private static Lazy<Client> build() {
		Retriever<Client> r = () -> ClientBuilder.newClient();
		Lazy<Client> l = Lazy.wrap(r);

		return l;
	}

	public Response get(String url) {
		Response ans = client().target(url).request().accept(MediaType.WILDCARD).buildGet()
				.invoke();

		return ans;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Response get(GetRequest req) {

		URI uri = req.toUri();
		MultivaluedMap query = req.getParams();
		MultivaluedMap headers = req.getHeaders();

		WebTarget target = client().target(uri);

		for (Object obj : query.entrySet()) {
			Entry e = (Entry) obj;
			String key = (String) e.getKey();
			List value = (List) e.getValue();

			// It is important to know queryParam method won't update
			// current WebTarget object, but return a new one.
			Object[] arr = value.toArray();
			target = target.queryParam(key, arr);
		}

		Response res = target.request().headers(headers).get();

		return res;
	}

	public String getText(GetRequest req) {
		String ans = get(req).readEntity(String.class);

		return ans;
	}

	public String getText(String url) {
		String ans = get(url).readEntity(String.class);

		return ans;
	}

	private Client client() {
		return __client.get();
	}
}
