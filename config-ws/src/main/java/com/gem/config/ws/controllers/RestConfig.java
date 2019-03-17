package com.gem.config.ws.controllers;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.bson.types.ObjectId;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gem.commons.rest.ConflictException;
import com.gem.commons.rest.UnauthorizedException;

@Component
@ApplicationPath("/rest/config")
public class RestConfig extends ResourceConfig {
	
	private static final ObjectMapper mapper;
	
	static {
		mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		SimpleModule mongo = new SimpleModule("Custom Mongo Module");
		mongo.addSerializer(ObjectId.class, ToStringSerializer.instance);
		mapper.registerModule(mongo);
	}
	
	public RestConfig() {
		packages(RestConfig.class.getPackageName());

		JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
		provider.setMapper(mapper);
		register(provider);
		
		register(new RestExceptionHandler());
	}

	static class RestExceptionHandler implements ExceptionMapper<Throwable> {
		
		@Override
		public Response toResponse(Throwable ex) {
			Response.ResponseBuilder b = create(ex);

			if (b == null) {
				return null;
			}
			// A header indicating that the message was created by this
			// application and thus can be used to show to the end user.
			return b.header("X-Application-Message", true).build();
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

			return null;
		}
		
	}
}
