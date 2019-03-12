package com.gem.config.ws.controllers;

import java.net.URI;
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

import org.springframework.stereotype.Component;

import com.gem.commons.TxResult;
import com.gem.config.ws.entities.App;
import com.gem.config.ws.services.AppService;

@Component
@Path("/apps")
public class AppResource {
	
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

		App app = srv.get(name);

		return Response.ok(app).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(App data) {
		
		App app = srv.create(data);
		String name = app.getName();

		return Response.created(location(name)).build();
	}

	@PUT
	@Path("/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("name") String name, App data) {

		TxResult<App> tx = srv.put(name, data);
		
		if (tx.isCreated()) {
			String ans = tx.getResult().getName();
			return Response.created(location(ans)).build();
		}

		return Response.ok().build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response delete(@PathParam("name") String name) {
		
		boolean ans = srv.delete(name);
		return Response.ok(ans).build();
	}
	
	private URI location(String name) {
		UriBuilder b = info.getAbsolutePathBuilder();
		b.path(name);
		return b.build();
	}
}
