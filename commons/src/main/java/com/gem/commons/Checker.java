package com.gem.commons;

public class Checker {
	
	private Checker() {

	}
	
	public static void checkParamNotNull(String name, Object value) {
		if (value == null) {
			throw new NullPointerException("The parameter '" + name + "' can not be null.");
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
