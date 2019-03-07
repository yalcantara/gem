package com.gem.express.sample.controllers;

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

import com.gem.commons.Json;
import com.gem.commons.TxResult;
import com.gem.express.sample.entities.Staff;
import com.gem.express.sample.services.StaffService;

@Component
@Path("/staff")
public class StaffController {
	
	@Inject
	private StaffService srv;
	
	@Context
	private UriInfo info;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response list() {
		
		List<Staff> list = srv.list();
		
		String json = Json.write(list);
		
		return Response.ok(json).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(String json) {

		Staff data = Json.parse(json, Staff.class);

		Staff ent = srv.create(data);
		long id = ent.getId();
		
		return Response.created(location(id)).build();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") long id, String json) {

		Staff data = Json.parse(json, Staff.class);

		TxResult<Staff> tx = srv.put(id, data);

		if (tx.isCreated()) {
			long ans = tx.getResult().getId();
			return Response.created(location(ans)).build();
		}
		
		return Response.ok().build();
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") long id) {

		boolean ans = srv.delete(id);
		return Response.ok(String.valueOf(ans)).build();
	}

	private URI location(long id) {
		UriBuilder b = info.getAbsolutePathBuilder();
		b.path(String.valueOf(id));
		return b.build();
	}
	
}
