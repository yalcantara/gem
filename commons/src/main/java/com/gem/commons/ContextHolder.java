package com.gem.commons;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ContextHolder implements ApplicationContextAware {

	private static volatile ApplicationContext __ctx;

	public static boolean isInitilized() {
		return __ctx != null;
	}
	
	public static ApplicationContext get() {
		if (__ctx == null) {
			throw new RuntimeException(
					"The application context is not set. Maybe this method was called way before the Spring context initialized.");
		}
		
		return __ctx;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		__ctx = applicationContext;
	}

}
