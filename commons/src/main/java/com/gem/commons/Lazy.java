package com.gem.commons;

public abstract class Lazy<T> {
	
	private volatile T obj;
	private final Object lock = new Object();

	public static <T> Lazy<T> wrap(Retriever<T> ret) {
		return new Lazy<T>() {

			@Override
			protected T retrieve() throws Exception {
				return ret.retrieve();
			}
		};
	}
	
	private Lazy() {
		super();
	}

	public T get() {

		T o = obj;
		if (o == null) {

			synchronized (lock) {
				o = obj;
				if (o == null) {
					o = doRetrieve();
				}
			}
		}

		return o;
	}
	
	private T doRetrieve() {
		try {
			return retrieve();
		} catch (RuntimeException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	protected abstract T retrieve() throws Exception;
}
