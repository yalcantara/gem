package com.gem.commons.rest;

import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status;
public class RestUtils {



	public static boolean isOkFamily(Response res){
		return Status.OK.getFamily().equals(res.getStatusInfo().getFamily());
	}
}
