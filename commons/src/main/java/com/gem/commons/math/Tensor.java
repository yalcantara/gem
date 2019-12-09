package com.gem.commons.math;

import static com.gem.commons.Checker.checkParamNotNull;

public interface Tensor {

	public Shape shape();

	public float getAt(int... idx);

	public default int size(){
		return shape().size();
	}

	public float getAbs(int idx);

	public default float sqrError(Tensor other){
		checkParamNotNull("other", other);

		var s1 = shape();
		var s2 = other.shape();
		if(s1.equals(s2) == false){
			throw new IllegalArgumentException("The shape of the tensors does not match. This tensor's shape: " + s1 + ", other tensor's shape: " + s2 +".");
		}


		var sum = 0.0f;

		var size = s1.size();
		for(var i = 0; i < size; i++){
			var v1 = getAbs(i);
			var v2 = other.getAbs(i);

			sum += Math.pow(v1 - v2, 2.0);
		}

		var ans = sum / size;

		return ans;
	}



}
