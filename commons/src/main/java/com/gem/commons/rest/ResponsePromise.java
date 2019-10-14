package com.gem.commons.rest;

import com.gem.commons.Callback;
import com.gem.commons.PlainCallback;

import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;

import static com.gem.commons.Checker.checkParamNotNull;

public class ResponsePromise {

	private static final Object FOO_INIT_VALUE = new Object();


	private volatile Callback<Response> _then = null;
	private volatile Callback<Throwable> _error = null;
	private volatile PlainCallback _always = null;

	public ResponsePromise(){
		super();
	}


	public class Delegator implements InvocationCallback<Response> {

		private void callFinally(){
			final var a = _always;
			if(a != null){
				try{
					a.apply();
				}catch (RuntimeException ex){
					throw ex;
				}catch(Exception ex){
					throw new RuntimeException(ex);
				}
			}
		}


		private <T2> void  doCall(final Callback<T2> c, T2 param){
			if(c == null){
				callFinally();
				return;
			}

			try{
				c.apply(param);
			}catch(RuntimeException ex){
				throw ex;
			}catch(Exception ex){
				throw new RuntimeException(ex);
			}finally{
				callFinally();
			}
		}

		@Override
		public void completed(Response res) {
			doCall(_then, res);
		}

		@Override
		public void failed(Throwable error) {
			doCall(_error, error);
		}
	}


	public ResponsePromise then(Callback<Response> callback){
		checkParamNotNull("callback", callback);
		_then = callback;
		return this;
	}

	public ResponsePromise error(Callback<Throwable> callback){
		checkParamNotNull("callback", callback);
		_error = callback;
		return this;
	}

	public ResponsePromise always(PlainCallback callback){
		checkParamNotNull("callback", callback);
		_always = callback;
		return this;
	}
}
