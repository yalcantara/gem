package com.gem.commons.math;

public interface Tensor {

	public Shape shape();

	public float getAt(int... idx);
}
