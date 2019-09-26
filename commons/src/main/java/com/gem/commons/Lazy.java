package com.gem.commons;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static com.gem.commons.Checker.checkParamNotNull;

public final class Lazy<T> {
	
	private volatile Tuple<T, Long> obj = null;

	private final Object lock = new Object();
	private final Retriever<T> ret;

	public static <T> Lazy<T> wrap(Retriever<T> ret) {
		return new Lazy<>(ret);
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

	public Long getRetrievalDate(){
		final var crt = obj;
		return (crt == null)? null : crt.second();
	}

	public Tuple<T, Long> peek(){
		return obj;
	}

	public T get(){
		return get(false);
	}

	public T get(boolean force){
		return getTuple(force).first();
	}

	public Tuple<T, Long> getTuple(){
		return getTuple(false);
	}

	public Tuple<T, Long> getTuple(boolean force){
		return __internal_get(force);
	}


	private Tuple<T, Long> __internal_get(boolean force) {

		Tuple<T, Long> o;
		if(force){
			synchronized (lock){
				o = doRetrieve();
				obj = o;
			}
		}else{
			o = obj;
			if (o == null) {
				synchronized (lock) {
					o = obj;
					if (o == null) {
						o = doRetrieve();
						obj = o;
					}
				}
			}
		}

		return o;
	}

	public void reset(){
		synchronized (lock){
			obj = null;
		}
	}
	
	private Tuple<T, Long> doRetrieve() {
		try {
			T obj = ret.retrieve();
			long retrievalDate = System.currentTimeMillis();
			return new Tuple<T, Long>(obj, retrievalDate);
		} catch (RuntimeException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
