package org.osgi.impl.service.async;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.async.AsyncDelegate;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

public class Work<T> implements Runnable {

	private final ServiceReference<?> ref;
	private final Bundle client;
	private final Object object;
	private final Method method;
	private final Object[] args;
	
	private final Deferred<T> deferred;
	
	public Work(ServiceReference<?> ref, Bundle client, Object object,
			Method method, Object[] args, Deferred<T> deferred) {
		this.ref = ref;
		this.client = client;
		this.object = object;
		this.method = method;
		this.args = args;
		this.deferred = deferred;
	}


	public void run() {
		Object service;
		if(ref != null) {
			service = getService();
			if(service == null) {
				return;
			}
		} else {
			service = object;
			if(service == null) {
				deferred.fail(new ServiceException("Unable to locate the mediated object", 7));
				return;
			}
		}

		if(service instanceof AsyncDelegate) {
			try {
				@SuppressWarnings("unchecked")
				Promise<T> p = (Promise<T>) ((AsyncDelegate)service).async(method, args);
				if(p != null) {
					deferred.resolveWith(p);
					return;
				}
			} catch (Exception e) {
				deferred.fail(new ServiceException("The AsyncDelegate threw an exception", 7, e));
			}
		}
		
		try {
			// This is necessary for non public methods. The original mediator call must
			// have been allowed to happen, so this should always be safe.
			method.setAccessible(true);
			
			@SuppressWarnings("unchecked")
			T returnValue = (T) method.invoke(service, args);
			
			deferred.resolve(returnValue);
		} catch (InvocationTargetException ite) {
			deferred.fail(ite.getTargetException());
		} catch (Exception e) {
			deferred.fail(new ServiceException("There was a failure invoking the service", 7, e));
		}
	}


	private Object getService() {
		BundleContext context = client.getBundleContext();
		if(context == null) {
			deferred.fail(new ServiceException("The client bundle context for " + client + 
					" is no longer valid", 7));
			return null;
		}
		
		Object o = context.getService(ref);
		
		if(o == null) {
			deferred.fail(new ServiceException("The service " + ref + 
					" is no longer valid", 7));
		}
		
		return o;
	}
}
