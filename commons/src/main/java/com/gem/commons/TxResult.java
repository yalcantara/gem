package com.gem.commons;

public class TxResult<T> {

	private final boolean created;
	private final T result;

	public TxResult(boolean created, T result) {
		super();
		this.created = created;
		this.result = result;
	}

	public boolean isCreated() {
		return created;
	}

	public T getResult() {
		return result;
	}
	
}
