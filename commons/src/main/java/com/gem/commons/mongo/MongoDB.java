package com.gem.commons.mongo;

import javax.swing.text.Document;

public interface MongoDB {
	
	public Collection<Document> getCollection(String name);

	public <E> Collection<E> getCollection(String name, Class<E> documentClass);

	public void printCollections();
}
