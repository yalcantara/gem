package com.gem.config.ws.controllers;

import static com.gem.commons.Checker.checkPathParam;

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
import com.gem.config.ws.entities.Property;
import com.gem.config.ws.services.PropertyService;

@Component
@Path("/apps/{app}/properties")
public class PropertiesController {
	
	@Inject
	private PropertyService srv;
	
	@Context
	private UriInfo info;
	
	private void check(String app) {
		checkPathParam("app", app, "The app's name was not specified in the URL path.");
	}

	private void check(String app, String name) {
		check(app);
		checkPathParam("name", name, "The property's name was not specified in the URL path.");
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@PathParam("app") String app) {
		check(app);
		List<Property> list = srv.list(app);
		
		return Response.ok(list).build();
	}
	
	@GET
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("app") String app, @PathParam("name") String name) {
		check(app, name);
		Property dto = srv.get(app, name);
		
		return Response.ok(dto).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("app") String app, Property data) {
		check(app);
		Property dto = srv.create(app, data);
		String name = dto.getName();
		
		return Response.created(location(name)).build();
	}
	
	@PUT
	@Path("/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("app") String app, @PathParam("name") String name,
			Property data) {
		check(app, name);
		TxResult<Property> tx = srv.put(app, name, data);

		if (tx.isCreated()) {
			String ans = tx.getResult().getName();
			return Response.created(location(ans)).build();
		}
		
		return Response.ok().build();
	}

	@DELETE
	@Path("/{name}")
	public Response delete(@PathParam("app") String app, @PathParam("name") String name) {
		check(app, name);
		boolean ans = srv.delete(app, name);
		return Response.ok(ans).build();
	}

	private URI location(String name) {
		UriBuilder b = info.getAbsolutePathBuilder();
		b.path(name);
		return b.build();
	}
	
}
