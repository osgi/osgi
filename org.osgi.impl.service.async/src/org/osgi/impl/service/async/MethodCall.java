package org.osgi.impl.service.async;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;


public class MethodCall {

	private final ServiceReference<?> reference;
	private final Object service;

	private final Method method;
	private final Object[] arguments;
	
	public MethodCall(ServiceReference<?> reference, Object service,
			Method method, Object[] arguments) {
		super();
		this.reference = reference;
		this.service = service;
		this.method = method;
		this.arguments = arguments;
	}

	public <V> Promise<V> invokeAsynchronously(Bundle clientBundle, ExecutorService executor) {
		Deferred<V> deferred = new Deferred<V>();
		try {
			executor.execute(new Work<>(reference, clientBundle, service, method, arguments, deferred));
		} catch (RejectedExecutionException ree) {
			deferred.fail(new ServiceException("The Async service is unable to accept new requests", 7, ree));
		}
		return deferred.getPromise();
	}
}
