package com.gem.auth.controllers;

import com.gem.auth.entities.Tenant;
import com.gem.auth.services.TenantService;
import com.gem.commons.Json;
import com.gem.commons.rest.AbstractController;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/tenants")
public class TenantController extends AbstractController {

    @Inject
    private TenantService srv;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        List<Tenant> ans = srv.list();

        return Response.ok(ans).build();
    }

    @GET
    @Path("/by-name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByName(@PathParam("name") String name){
        Tenant ent = srv.getByName(name);
        return Response.ok(ent).build();
    }

    @GET
    @Path("/ent/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") long id) {
        checkPathParamIsPositive("id", id);

        Tenant ans = srv.get(id);

        return Response.ok(ans).build();
    }

    @POST
    @Path("/ent")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Json data) {

        String name = data.getString("name");
        String label = data.getString("label");


        Tenant ent = new Tenant();
        ent.setName(name);
        ent.setLabel(label);
        srv.post(ent);

        long id = ent.getId();

        return Response.created(locationForPost(id)).build();
    }

    @PUT
    @Path("/ent/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") long id, Tenant data) {

        //let's make sure the id is the one defined in the URL
        data.setId(id);
        srv.put(data);

        return putResponse(id);
    }

    @DELETE
    @Path("/ent/{id}")
    public Response delete(@PathParam("id") long id) {

        boolean ans = srv.delete(id);
        return Response.ok(ans).build();
    }
}
