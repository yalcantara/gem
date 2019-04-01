package com.gem.config.ws.controllers;


import com.gem.commons.rest.AbstractController;
import com.gem.config.ws.entities.Key;
import com.gem.config.ws.services.KeyService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.gem.commons.Checker.checkPathParam;

@Component
@Path("/apps/{app}/properties/{prop}/keys")
public class KeysController extends AbstractController {


    @Inject
    private KeyService srv;


    private void check(String app) {
        checkPathParam(app, "The app's name was not specified in the URL path.");
    }

    private void check(String app, String prop) {
        check(app);
        checkPathParam(prop, "The property's name was not specified in the URL path.");
    }

    private void check(String app, String prop, String name) {
        check(app, prop);
        checkPathParam(name, "The property's name was not specified in the URL path.");
    }

    private void checkId(String app, String prop, String id){
        check(app, prop);
        checkPathParam(id, "The key's id was not specified in the URL path.");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@PathParam("app") String app, @PathParam("prop") String prop) {
        check(app, prop);
        List<Key> list = srv.list(app, prop);

        return Response.ok(list).build();
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("app") String app,
                        @PathParam("prop") String prop,
                        @PathParam("name") String name) {
        check(app, prop, name);
        Key dto = srv.get(app, prop, name);

        return Response.ok(dto).build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("app") String app,
                           @PathParam("prop") String prop,
                           Key data) {
        check(app, prop);
        Key dto = srv.create(app, prop, data);
        String name = dto.getName();

        return Response.created(locationForPost(name)).build();
    }
}
