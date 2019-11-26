package com.gem.commons.math;

import static com.gem.commons.Checker.checkParamGreatherThanOrEquals;

public class Shape {

	private final int[] dimensions;

	public Shape(int... dimensions){
		checkParamGreatherThanOrEquals("dimensions.length", 1, dimensions.length);
		this.dimensions = new int[dimensions.length];

		System.arraycopy(dimensions, 0, this.dimensions, 0, dimensions.length);
	}

	public int get(int dim){
		return dimensions[dim -1];
	}
}
