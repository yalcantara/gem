package com.gem.commons;

@FunctionalInterface
public interface Retriever<T> {
	
	public T retrieve() throws Exception;
}
