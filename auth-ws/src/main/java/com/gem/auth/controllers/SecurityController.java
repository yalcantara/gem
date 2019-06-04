package com.gem.auth.controllers;

import com.gem.auth.entities.InternalUser;
import com.gem.auth.services.SecurityService;
import com.gem.commons.Json;
import com.gem.commons.rest.AbstractController;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import static com.gem.commons.Utils.strip;
import static javax.ws.rs.core.Response.*;

@Path("/sec")
public class SecurityController extends AbstractController {


    @Inject
    private SecurityService srv;

    private Json convert(InternalUser ent){
        Json json = new Json();
        json.put("id", ent.getId());
        json.put("name", ent.getName());
        json.put("role", ent.getRole());

        return json;
    }

    @Path("/users/view/{name}")
    public Response getUser(@PathParam("name") String name){
        name = strip(name);
        checkPathParam("name", name);

        Json json = convert(srv.findByName(name));

        return Response.ok(json).build();
    }

    @POST
    @Path("/match")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response match(Json json) {

        String user = json.getString("user");
        String pass = json.getString("pass");

        if(srv.match(user, pass)){
            return Response.ok().build();
        }

        String ent = "Username and password does not match.";
        return Response.status(Status.UNAUTHORIZED).entity(ent).build();
    }

}
