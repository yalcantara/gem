package com.gem.config.ws.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import com.gem.commons.TxResult;
import com.gem.config.ws.entities.App;
import com.gem.config.ws.services.AppService;

@Component
@Path("/apps")
public class AppController {

	@Inject
	private AppService srv;
	
	@Context
	private UriInfo info;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response list() {
		
		List<App> list = srv.list();
		
		return Response.ok(list).build();
	}
	
	@GET
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("name") String name) {
		
		App dto = srv.get(name);
		
		return Response.ok(dto).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(App data) {

		App dto = srv.create(data);
		String name = dto.getName();
		
		return Response.created(locationForPost(name)).build();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") String id, App data) {
		
		ObjectId oid = new ObjectId(id);
		TxResult<App> tx = srv.put(oid, data);

		String ans = tx.getResult().getName();
		if (tx.isCreated()) {
			return Response.created(locationForPut(ans)).build();
		}
		
		return Response.noContent().location(locationForPut(ans)).build();
	}

	@DELETE
	@Path("/{name}")
	public Response delete(@PathParam("name") String name) {

		boolean ans = srv.delete(name);
		return Response.ok(ans).build();
	}

	private URI locationForPost(String name) {
		UriBuilder b = info.getAbsolutePathBuilder();
		b.path(name);
		return b.build();
	}
	
	private URI locationForPut(String name) {
		UriBuilder b = info.getAbsolutePathBuilder();
		String uri = b.build().toString();
		int idx = uri.lastIndexOf('/');
		
		String newUri = uri.substring(0, idx + 1);
		
		String full;
		try {
			full = newUri + URLEncoder.encode(name, "UTF-8");
			return new URI(full);
		} catch (UnsupportedEncodingException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
