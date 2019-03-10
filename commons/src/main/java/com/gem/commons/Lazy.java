package com.gem.commons;

import static com.gem.commons.Checker.checkParamNotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public final class Lazy<T> {
	
	private volatile T obj;
	private final Object lock = new Object();
	private final Retriever<T> ret;

	public static <T> Lazy<T> wrap(Retriever<T> ret) {
		return new Lazy<T>(ret);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T proxy(Class<T> inter, Retriever<T> ret) {
		
		Class[] classes = { inter };
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		return (T) Proxy.newProxyInstance(cl, classes, new InvocationHandler() {
			
			private final Object hash = new Object();
			private final Lazy<T> ref = Lazy.wrap(ret);
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

				final String name = method.getName();
				if (name.equals("hashCode") || name.equals("equals")) {
					return method.invoke(hash, args);
				}
				
				T target = ref.get();
				try {
					return method.invoke(target, args);
				} catch (InvocationTargetException ex) {
					Throwable original = ex.getCause();
					throw original;
				}
			}
		});
	}
	
	private Lazy(Retriever<T> ret) {
		checkParamNotNull("ret", ret);
		this.ret = ret;
	}

	public T get() {

		T o = obj;
		if (o == null) {

			synchronized (lock) {
				o = obj;
				if (o == null) {
					o = doRetrieve();
					obj = o;
				}
			}
		}

		return o;
	}
	
	private T doRetrieve() {
		try {
			return ret.retrieve();
		} catch (RuntimeException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
