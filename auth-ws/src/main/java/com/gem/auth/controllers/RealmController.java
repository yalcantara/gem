package com.gem.auth.controllers;

import com.gem.auth.entities.Realm;
import com.gem.auth.services.RealmService;
import com.gem.commons.Json;
import com.gem.commons.rest.AbstractController;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/realms")
public class RealmController extends AbstractController {

    @Inject
    private RealmService srv;

    private Json convert(Realm ent){
        Json json = new Json();
        json.put("id", ent.getId());
        json.put("name", ent.getName());

        return json;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        List<Json> ans = Json.toListOfJson(srv.list(), this::convert);

        return Response.ok(ans).build();
    }

    @GET
    @Path("/ent/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") long id) {
        checkPathParamIsPositive("id", id);

        Json json = convert(srv.get(id));

        return Response.ok(json).build();
    }

    @POST
    @Path("/ent")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Json data) {

        String name = data.getString("name");


        Realm ent = new Realm();
        ent.setName(name);
        srv.post(ent);

        long id = ent.getId();

        return Response.created(locationForPost(id)).build();
    }
}
