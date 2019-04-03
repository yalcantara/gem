package com.gem.config.ws.controllers;

import com.gem.commons.Json;
import com.gem.commons.Utils;
import com.gem.commons.rest.ConflictException;
import com.gem.commons.rest.UnauthorizedException;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

@Component
@ApplicationPath("/rest/config")
public class RestConfig extends ResourceConfig {

	private static final Logger log = LoggerFactory.getLogger(RestConfig.class);

	private static final String[] APP_PKGS = {"com.gem"};

	public RestConfig() {
		packages(RestConfig.class.getPackageName());

		JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
		provider.setMapper(Json.getMapper());
		register(provider);
		
		register(new RestExceptionHandler());
	}

	static class RestExceptionHandler implements ExceptionMapper<Throwable> {
		
		@Override
		public Response toResponse(Throwable ex) {
			Response.ResponseBuilder b = create(ex);


			StackTraceElement[] stack = ex.getStackTrace();

			StackTraceElement first = stack[0];
			String className  = first.getClassName();

			for(String pkg:APP_PKGS){
				if(className.startsWith(pkg)){
					// A header indicating that the message was created by this
					// application and thus can be used to show to the end user.
					return b.header("X-Application-Message", true).build();
				}
			}



			return b.build();
		}
		
		private Response.ResponseBuilder create(Throwable ex) {



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
		
	}
}
