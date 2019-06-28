package com.gem.commons;

import static com.gem.commons.Utils.strip;

public class Checker {
	
	private Checker() {

	}

	
	public static void checkParamNotNull(String name, Object value) {
		_checkParamNotNull(value, "The parameter '" + name + "' can not be null.");
	}

	public static void checkParamNotEmpty(String name, String value) {
		checkParamNotNull(name, value);
		value = strip(value);

		if(value == null){
			throw new IllegalArgumentException("The parameter '" + name + "' can not be empty.");
		}
	}
	
	private static void _checkParamNotNull(Object value, String message) {
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
