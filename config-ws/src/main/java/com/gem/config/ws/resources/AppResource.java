package com.gem.config.ws.resources;

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
		
		String json = Json.write(list);
		
		return Response.ok(json).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(String json) {

		App data = Json.parse(json, App.class);

		App app = srv.create(data);
		long id = app.getId();
		
		return Response.created(location(id)).build();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") long id, String json) {

		App data = Json.parse(json, App.class);

		TxResult<App> tx = srv.put(id, data);

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
