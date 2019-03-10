package com.gem.commons.mongo;

public interface MongoDB {
	
	public Collection getCollection(String name);

	public Collection getCollection(String name, Class<?> documentClass);

	public void printCollections();
}
