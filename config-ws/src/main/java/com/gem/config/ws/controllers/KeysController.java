package com.gem.config.ws.controllers;


import com.gem.config.ws.entities.Key;
import com.gem.config.ws.services.KeyService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import static com.gem.commons.Checker.checkPathParam;

@Component
@Path("/apps/{app}/properties/{prop}/keys")
public class KeysController {


    @Inject
    private KeyService srv;

    @Context
    private UriInfo info;

    private void check(String app) {
        checkPathParam("app", app, "The app's name was not specified in the URL path.");
    }

    private void check(String app, String prop) {
        check(app);
        checkPathParam("prop", prop, "The property's name was not specified in the URL path.");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@PathParam("app") String app, @PathParam("prop") String prop) {
        check(app, prop);
        List<Key> list = srv.list(app, prop);

        return Response.ok(list).build();
    }
}
