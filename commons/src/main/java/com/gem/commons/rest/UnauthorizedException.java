package com.gem.commons.rest;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response.Status;

public class UnauthorizedException extends ClientErrorException {
	
	private static final long serialVersionUID = 6864953282770386069L;

	public UnauthorizedException() {
		super(Status.UNAUTHORIZED);
	}

	public UnauthorizedException(String message) {
		super(message, Status.UNAUTHORIZED);
	}
	
	public UnauthorizedException(Throwable cause) {
		super(Status.UNAUTHORIZED, cause);
	}
}
