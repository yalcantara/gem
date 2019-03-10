package com.gem.commons;

public class Locator {

	public static <T> T get(Class<T> clazz) {
		return ContextHolder.get().getBean(clazz);
	}

	public static <T> Lazy<T> lazy(Class<T> clazz) {
		return Lazy.wrap(() -> get(clazz));
	}
}
