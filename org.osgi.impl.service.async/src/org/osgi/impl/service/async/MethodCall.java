package org.osgi.impl.service.async;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.async.delegate.AsyncDelegate;
import org.osgi.service.log.LogService;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;


public class MethodCall {

	private final Bundle clientBundle;
	private final ServiceTracker<LogService, LogService> logServiceTracker;
	
	private final ServiceReference<?> reference;
	private final Object service;

	final Method method;
	final Object[] arguments;
	
	public MethodCall(Bundle clientBundle, ServiceTracker<LogService, LogService> logServiceTracker, 
			ServiceReference<?> reference, Object service, Method method, Object[] arguments) {
		this.clientBundle = clientBundle;
		this.logServiceTracker = logServiceTracker;
		this.reference = reference;
		this.service = service;
		this.method = method;
		this.arguments = arguments;
	}

	Object getService() {
		if(reference != null) {
			BundleContext bc = clientBundle.getBundleContext();
			if(bc != null) {
				try {
					Object svc = bc.getService(reference);
					if(svc == null) {
						throw new ServiceException("Unable to retrieve the mediated service because it has been unregistered", 7);
					} else {
						return svc;
					}
				} catch (Exception e) {
					throw new ServiceException("Unable to retrieve the mediated service", 7, e);
				}
			} else {
				throw new ServiceException("Unable to retrieve the mediated service because the client bundle has been stopped", 7);
			}
		} else {
			return service;
		}
	}
	
	void releaseService() {
		if(reference != null) {
			BundleContext bc = clientBundle.getBundleContext();
			if(bc != null) {
				bc.ungetService(reference);
			}
		}
	}
	
	public <V> Promise<V> invokeAsynchronously(Bundle clientBundle, ExecutorService executor) {
		
		Deferred<V> deferred = new Deferred<V>();

		Object svc;
		try {
			svc = getService();
		} catch (Exception e) {
			deferred.fail(e);
			return deferred.getPromise();
		}
		
		if(svc instanceof AsyncDelegate) {
			try {
				@SuppressWarnings("unchecked")
				Promise<V> p = (Promise<V>) ((AsyncDelegate) svc).async(method, arguments);
				if(p != null) {
					try {
						deferred.resolveWith(p);
						return deferred.getPromise();
					} finally {
						releaseService();
					}
				}
			} catch (Exception e) {
				try {
					deferred.fail(e);
					return deferred.getPromise();
				} finally {
					releaseService();
				}
			}
		}
		//If we get here then svc is either not an async delegate, or it rejected the call
		
		try {
			executor.execute(new Work<V>(this, deferred));
		} catch (RejectedExecutionException ree) {
			deferred.fail(new ServiceException("The Async service is unable to accept new requests", 7, ree));
		}
		Promise<V> promise = deferred.getPromise();
		
		//Release the service we got at the start of this method
		promise.onResolve(new Runnable() {
			public void run() {
				releaseService();
			}
		});
		
		return promise;
	}

	public void fireAndForget(Bundle clientBundle, ExecutorService executor) {
		Object svc;
		try {
			svc = getService();
		} catch (Exception e) {
			logError("Unable to obtain the service object", e);
			return;
		}
		
		if(svc instanceof AsyncDelegate) {
			try {
				if(((AsyncDelegate) svc).execute(method, arguments)) {
					releaseService();
					return;
				}
			} catch (Exception e) {
				releaseService();
				logError("The AsyncDelegate rejected the fire-and-forget invocation with an exception", e);
				return;
			}
		}
		//If we get here then svc is either not an async delegate, or it rejected the call
		
		Deferred<Object> deferred = new Deferred<Object>();
		try {
			executor.execute(new Work<Object>(this, deferred));
			deferred.getPromise().onResolve(new Runnable() {
				public void run() {
					releaseService();
				}
			}).then(null, new Failure(){
				public void fail(Promise<?> resolved) throws Exception {
					logError("The fire-and-forget invocation failed", resolved.getFailure());
				}
			});
		} catch (RejectedExecutionException ree) {
			logError("The Async Service threadpool rejected the fire-and-forget invocation", ree);
			return;
		}
	}

	void logError(String message, Throwable e) {
		for(LogService log : logServiceTracker.getServices(new LogService[0])) {
			if(reference == null) {
				log.log(LogService.LOG_ERROR, message, e);
			} else {
				log.log(reference,  LogService.LOG_ERROR, message, e);
			}
		}
	}
}
