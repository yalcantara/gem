package com.gem.auth.controllers;

import com.gem.auth.entities.User;
import com.gem.auth.services.UserService;
import com.gem.commons.Json;
import com.gem.commons.rest.AbstractController;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/realms/ent/{realmId}/users")
public class UserController extends AbstractController {

    @Inject
    private UserService srv;

    private Json convert(User user){
        Json json = new Json();
        json.put("id", user.getId());

        //Realm
        //---------------------------------------------------------------------
        Json realm = new Json();
        realm.put("id", user.getRealmId());
        realm.put("name", user.getRealmName());

        json.put("realm", realm);
        //---------------------------------------------------------------------

        json.put("name", user.getName());
        json.put("createdDate", user.getCreatedDate());
        json.put("lastModifiedDate", user.getLastModifiedDate());

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
    public Response get(@PathParam("realmId") long realmId, @PathParam("id") long id) {
        checkPathParamIsPositive("realmId", realmId);
        checkPathParamIsPositive("id", id);

        Json json = convert(srv.get(realmId, id));

        return Response.ok(json).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Json data) {

        Long realmId = data.getLong("realmId");
        String name = data.getString("name");
        String pass = data.getString("pass");
        String createdBy = data.getString("createdBy");

        User user = new User();
        user.setName(name);
        user.setPass(pass);
        user.setCreatedBy(createdBy);

        srv.post(realmId, user);

        long id = user.getId();

        return Response.created(locationForPost(id)).build();
    }
}
