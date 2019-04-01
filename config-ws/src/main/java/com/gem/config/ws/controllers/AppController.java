package com.gem.config.ws.controllers;

import com.gem.commons.TxResult;
import com.gem.commons.rest.AbstractController;
import com.gem.config.ws.entities.App;
import com.gem.config.ws.services.AppService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Component
@Path("/apps")
public class AppController extends AbstractController {

	@Inject
	private AppService srv;

	
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
		return putResponse(tx, ans);
	}

	@DELETE
	@Path("/{name}")
	public Response delete(@PathParam("name") String name) {

		boolean ans = srv.delete(name);
		return Response.ok(ans).build();
	}


}
