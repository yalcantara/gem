package com.gem.commons.rest.exceptions;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response.Status;

public class ConflictException extends ClientErrorException {
	
	private static final long serialVersionUID = -4468323039768380350L;
	
	public ConflictException(String message, Throwable cause) {
		super(message, Status.CONFLICT.getStatusCode(), cause);
	}
	
	public ConflictException(String message) {
		super(message, Status.CONFLICT.getStatusCode());
	}

}
