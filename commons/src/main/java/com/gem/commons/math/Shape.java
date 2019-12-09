package com.gem.commons.math;

import org.bson.types.ObjectId;

import java.io.Serializable;

import static com.gem.commons.Checker.checkParamGreatherThanOrEquals;
import static com.gem.commons.Checker.checkParamIsPositive;

public class Shape implements Serializable {

	private final int[] dimensions;

	public Shape(int... dimensions){
		checkParamGreatherThanOrEquals("dimensions.length", 1, dimensions.length);

		for(var i =0; i < dimensions.length; i++){
			var d = dimensions[i];
			checkParamIsPositive("dimension[" +i + "]", d);
		}


		this.dimensions = new int[dimensions.length];

		System.arraycopy(dimensions, 0, this.dimensions, 0, dimensions.length);
	}

	public int get(int dim){
		return dimensions[dim -1];
	}

	public int size(){
		var ans = 1;

		for(var i=0; i < dimensions.length; i++){
			ans *= dimensions[i];
		}

		return ans;
	}

	@Override
	public boolean equals(Object other){
		if(other == null || other instanceof Shape == false){
			return false;
		}

		return equals((Shape)other);
	}


	public boolean equals(Shape other){
		if(other == null || other.dimensions.length != dimensions.length){
			return false;
		}

		for(var i=0; i < dimensions.length; i++){
			var d1 = dimensions[i];
			var d2 = other.dimensions[i];

			if(d1 != d2){
				return false;
			}
		}

		return true;
	}


	public String toString(){

		var ans = new StringBuilder();

		ans.append("[");
		for(var i=0; i < dimensions.length; i++){
			var d = dimensions[i];
			ans.append(d);

			if(i + 1 < dimensions.length){
				ans.append(", ");
			}
		}

		ans.append("]");

		return ans.toString();
	}
}
