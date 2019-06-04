package com.gem.commons.rest;

import com.gem.commons.TxResult;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class AbstractController {

    @Context
    protected UriInfo info;


    protected void checkPathParam(String name, Object value) {

        if (value == null) {
            String msg = "The segment '" + name + "' was not specified in the URL path.";
            throw new BadRequestException(msg);
        }
    }

    protected void checkPathParamIsPositive(String name, long value) {
        if (value < 0) {
            throw new IllegalArgumentException(
                    "The segment '" + name + "' must be positive. Got: " + value + ".");
        }
    }

    protected URI locationForPost(String id) {
        UriBuilder b = info.getAbsolutePathBuilder();
        b.path(id);
        return b.build();
    }

    protected URI locationForPost(long id) {
        UriBuilder b = info.getAbsolutePathBuilder();
        b.path(String.valueOf(id));
        return b.build();
    }

    protected URI locationForPut(String name) {
        UriBuilder b = info.getAbsolutePathBuilder();
        String uri = b.build().toString();
        int idx = uri.lastIndexOf('/');

        String newUri = uri.substring(0, idx + 1);

        String full;
        try {
            full = newUri + URLEncoder.encode(name, "UTF-8");
            return new URI(full);
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    protected Response putResponse(TxResult tx, String ans){
        if (tx.isCreated()) {
            return Response.created(locationForPut(ans)).build();
        }

        return Response.noContent().location(locationForPut(ans)).build();
    }
}
