package com.gem.commons;

import javax.ws.rs.core.Response;

@FunctionalInterface
public interface Callback<T> {

	public void apply(T ans)throws Exception;
}
