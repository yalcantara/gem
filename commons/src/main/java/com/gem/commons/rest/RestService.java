package com.gem.commons.rest;

import com.gem.commons.Json;
import com.gem.commons.Lazy;
import com.gem.commons.Retriever;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status;

import static com.gem.commons.Checker.*;

@Service
@Scope("singleton")
public class RestService {

	public static final MediaType DEFAULT_MEDIA_TYPE = MediaType.APPLICATION_JSON_TYPE;

	private final Lazy<Client> __client = build();

	private Lazy<Client> build() {
		Retriever<Client> r = () -> ClientBuilder.newClient();
		Lazy<Client> l = Lazy.wrap(r);

		return l;
	}

	public RestService(){
		super();
	}

	private Client client() {
		return __client.get();
	}

	public Response get(String url){
		checkParamNotEmpty("url", url);
		return request(url, null).get();
	}


	public Response get(String url, Params queryParams){
		checkParamNotEmpty("url", url);
		checkParamNotNull("queryParams", queryParams);
		return request(url, queryParams).get();
	}

	public String getText(String url){
		Response res = get(url);
		String txt = res.readEntity(String.class);
		return txt;
	}

	public String getText(String url, Params queryParams){
		Response res = get(url, queryParams);
		String txt = res.readEntity(String.class);
		return txt;
	}

	public Json getJson(String url){
		String txt = getText(url);
		return Json.parse(txt);
	}

	public Json getJson(String url, Params queryParams){
		String txt = getText(url, queryParams);
		return Json.parse(txt);
	}

	public ResponsePromise getAsync(String url, Params queryParams){
		checkParamNotEmpty("url", url);
		checkParamNotNull("queryParams", queryParams);
		var p = new ResponsePromise();
		var del = p.new Delegator();




		request(url, queryParams).async().get(del);
		return p;
	}

	private Invocation.Builder request(String url, Params queryParams){
		if(queryParams == null){
			return client().target(url).request(DEFAULT_MEDIA_TYPE);
		}
		return queryParams.toTarget(client().target(url)).request(DEFAULT_MEDIA_TYPE);
	}
}
