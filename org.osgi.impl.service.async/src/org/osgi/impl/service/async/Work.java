package org.osgi.impl.service.async;

import java.lang.reflect.InvocationTargetException;

import org.osgi.util.promise.Deferred;

public class Work<T> implements Runnable {

	private final MethodCall methodCall;
	
	private final Deferred<T> deferred;
	
	public Work(MethodCall methodCall, Deferred<T> deferred) {
		this.methodCall = methodCall;
		this.deferred = deferred;
	}


	public void run() {
		try {
			Object service = methodCall.getService();
			// This is necessary for non public methods. The original mediator call must
			// have been allowed to happen, so this should always be safe.
			methodCall.method.setAccessible(true);
			
			@SuppressWarnings("unchecked")
			T returnValue = (T) methodCall.method.invoke(service, methodCall.arguments);
			if(deferred != null) {
				deferred.resolve(returnValue);
			}
			
		} catch (InvocationTargetException ite) {
			if(deferred != null) {
				deferred.fail(ite.getTargetException());
			}
		} catch (Exception e) {
			if(deferred != null) {
				deferred.fail(e);
			}
		} finally {
			methodCall.releaseService();
		}
	}
}
