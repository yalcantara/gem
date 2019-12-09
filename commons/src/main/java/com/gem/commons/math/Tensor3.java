package com.gem.commons.math;

import com.gem.commons.Grid;

import static com.gem.commons.Checker.*;

public class Tensor3 implements Tensor {

	private final Shape shape;

	private final float[] data;



	public Tensor3(int d1, int d2, int d3){
		checkParamGreatherThanOrEquals("d1", 1, d1);
		checkParamGreatherThanOrEquals("d2", 1, d2);
		checkParamGreatherThanOrEquals("d2", 1, d3);
		shape = new Shape(d1, d2, d3);


		data = new float[d1 * d2 * d3];
	}


	@Override
	public Shape shape() {
		return shape;
	}


	public float get(int x, int y, int z){

		var d1 = shape.get(1);
		var d2 = shape.get(2);

		int idx;

		idx = x;
		idx += y * d1;
		idx += z * d1 * d2;

		var ans = data[idx];

		return ans;
	}

	public void set(int x, int y, int z, float val){

		int idx = absIdx(x, y , z);

		data[idx] = val;
	}

	public float getAbs(int idx){
		return data[idx];
	}

	private int absIdx(int x, int y, int z){
		var d1 = shape.get(1);
		var d2 = shape.get(2);

		int idx;

		idx = x;
		idx += y * d1;
		idx += z * d1 * d2;

		return idx;
	}

	@Override
	public float getAt(int... idx) {
		checkParamNotNull("idx", idx);
		checkParamEquals("idx", 3, idx.length);
		return get(idx[0], idx[1], idx[2]);
	}


	public void print(){

		var d1 = shape.get(1);
		var d2 = shape.get(2);
		var d3 = shape.get(3);

		var g = new Grid(d2, d1);

		for(var k = 0; k < d3; k++) {
			for (var i = 0; i < d2; i++) {
				for (var j = 0; j < d1; j++) {
					var v1 = get(i, j, 1);

					g.set(i, j, String.valueOf(v1));

				}
			}

			g.print();
		}
	}


	public String toString(){
		return "Tensor3 " + shape();
	}
}
