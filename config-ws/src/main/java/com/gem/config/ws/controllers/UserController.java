package com.gem.config.ws.controllers;

import com.gem.commons.Json;
import com.gem.commons.rest.AbstractController;
import com.gem.config.ws.services.UserService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status;

@Component
@Path("/sec/users")
public class UserController extends AbstractController {


    @Inject
    private UserService srv;

    @POST
    @Path("/match")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response match(String data) {

        Json cred = Json.parse(data);
        String user = cred.getString("user");
        String pass = cred.getString("pass");

        if(srv.match(user, pass)){
            return Response.ok().build();
        }

        String ent = "Username and password does not match.";
        return Response.status(Status.UNAUTHORIZED).entity(ent).build();
    }

}
