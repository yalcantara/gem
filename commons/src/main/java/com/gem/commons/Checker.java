package com.gem.commons;

import javax.ws.rs.BadRequestException;

public class Checker {
	
	private Checker() {

	}

	public static void checkPathParam(String name, Object value) {
		String msg = "The segment '" + name + "' was not specified in the URL path.";

		if (value == null) {
			throw new BadRequestException(msg);
		}
	}
	
	public static void checkParamNotNull(String name, Object value) {
		checkParamNotNull(name, value, "The parameter '" + name + "' can not be null.");
	}
	
	public static void checkParamNotNull(String name, Object value, String message) {
		if (value == null) {
			throw new NullPointerException(message);
		}
	}

	public static void checkParamHigherThan(String name, int threshold, int value) {
		if (value <= threshold) {
			throw new IllegalArgumentException("The parameter '" + name + "' must be higher than "
					+ threshold + ", but got " + value + " instead.");
		}
	}
	
	public static void checkParamIsPositive(String name, long value) {
		if (value < 0) {
			throw new IllegalArgumentException(
					"The parameter '" + name + "' must be positive. Got: " + value + ".");
		}
	}
}
