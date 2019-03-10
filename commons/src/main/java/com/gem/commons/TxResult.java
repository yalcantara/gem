package com.gem.commons;

public class TxResult<T> {
	
	private final boolean created;
	private final boolean miss;
	private final T result;
	
	public TxResult(boolean created, boolean miss, T result) {
		super();
		this.created = created;
		this.miss = miss;
		this.result = result;
	}

	public TxResult(boolean created, T result) {
		super();
		this.created = created;
		this.miss = false;
		this.result = result;
	}
	
	public boolean isCreated() {
		return created;
	}

	public boolean isMiss() {
		return miss;
	}
	
	public T getResult() {
		return result;
	}

}
