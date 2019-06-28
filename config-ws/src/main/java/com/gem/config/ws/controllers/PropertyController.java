package com.gem.config.ws.controllers;

import com.gem.commons.TxResult;
import com.gem.commons.rest.AbstractController;
import com.gem.config.ws.entities.Property;
import com.gem.config.ws.services.PropertyService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Component
@Path("/apps/{app}/properties")
public class PropertyController extends AbstractController {
	
	@Inject
	private PropertyService srv;

	
	private void check(String app) {
		checkPathParam(app, "The app's name was not specified in the URL path.");
	}

	private void check(String app, String name) {
		check(app);
		checkPathParam(name, "The property's name was not specified in the URL path.");
	}

	private void checkId(String app, String id){
		check(app);
		checkPathParam(id, "The property's id was not specified in the URL path.");
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
		
		return Response.created(locationForPost(name)).build();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("app") String app, @PathParam("id") String id,
			Property data) {
		checkId(app, id);
		ObjectId oid = new ObjectId(id);
		TxResult<Property> tx = srv.put(app, oid, data);

		String ans = tx.getResult().getName();
		return putResponse(tx, ans);
	}

	@DELETE
	@Path("/{name}")
	public Response delete(@PathParam("app") String app, @PathParam("name") String name) {
		check(app, name);
		boolean ans = srv.delete(app, name);
		return Response.ok(ans).build();
	}
}
