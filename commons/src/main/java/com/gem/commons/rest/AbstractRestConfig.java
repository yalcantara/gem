package com.gem.commons.rest;

import com.gem.commons.Json;
import com.gem.commons.Utils;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

import static javax.ws.rs.core.Response.*;

public abstract class AbstractRestConfig extends ResourceConfig {



    private static final Logger log = LoggerFactory.getLogger(AbstractRestConfig.class);

    private static final String[] APP_PKGS = {"com.gem"};

    public AbstractRestConfig(){
        this(null);
    }

    public AbstractRestConfig(String packageName) {
        //The keyword 'this' returns the sub-class
        if(packageName == null){
            packages(this.getClass().getPackageName());
        }else{
            packages(packageName);
        }

        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(Json.getMapper());
        register(provider);

        register(new RestExceptionHandler());
    }


    protected String[] mapAppExceptionPackages(){
        return APP_PKGS;
    }

    protected ResponseBuilder mapException(Throwable ex){


        if (ex instanceof BadRequestException) {
            return Response.status(Status.BAD_REQUEST).entity(ex.getMessage());
        }

        if (ex instanceof NotFoundException) {
            return Response.status(Status.NOT_FOUND).entity(ex.getMessage());
        }

        if (ex instanceof ConflictException) {
            return Response.status(Status.CONFLICT).entity(ex.getMessage());
        }

        if (ex instanceof UnauthorizedException) {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage());
        }

        if (ex instanceof ForbiddenException) {
            return Response.status(Status.FORBIDDEN).entity(ex.getMessage());
        }

        log.error(Utils.getStackTrace(ex));
        return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity("An error has occurred. Please try again later.");

    }

    class RestExceptionHandler implements ExceptionMapper<Throwable> {



        @Override
        public Response toResponse(Throwable ex) {
            ResponseBuilder b = mapException(ex);


            StackTraceElement[] stack = ex.getStackTrace();

            StackTraceElement first = stack[0];
            String className  = first.getClassName();

            String[] packages = mapAppExceptionPackages();

            if(packages != null){
                for(String pkg:APP_PKGS){
                    if(className.startsWith(pkg)){
                        // A header indicating that the message was created by this
                        // application and thus can be used to show to the end user.
                        return b.header("X-Application-Message", true).build();
                    }
                }
            }



            return b.build();
        }
    }


}
