package com.gem.commons;

public class Checker {
	
	private Checker() {

	}

	
	public static void checkParamNotNull(String name, Object value) {
		checkParamNotNull(value, "The parameter '" + name + "' can not be null.");
	}
	
	private static void checkParamNotNull(Object value, String message) {
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
