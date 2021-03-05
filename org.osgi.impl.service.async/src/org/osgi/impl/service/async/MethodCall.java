/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
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
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
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
	
	public <V> Promise<V> invokeAsynchronously(
			@SuppressWarnings("hiding") Bundle clientBundle,
			ExecutorService executor) {
		
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
		promise.onResolve(() -> releaseService());
		
		return promise;
	}

	public Promise<Void> fireAndForget(
			@SuppressWarnings("hiding") Bundle clientBundle,
			ExecutorService executor) {
		Object svc;
		try {
			svc = getService();
		} catch (Exception e) {
			logError("Unable to obtain the service object", e);
			return Promises.failed(e);
		}
		
		if(svc instanceof AsyncDelegate) {
			try {
				if(((AsyncDelegate) svc).execute(method, arguments)) {
					releaseService();
					return Promises.resolved(null);
				}
			} catch (Exception e) {
				releaseService();
				logError("The AsyncDelegate rejected the fire-and-forget invocation with an exception", e);
				return Promises.failed(e);
			}
		}
		//If we get here then svc is either not an async delegate, or it rejected the call
		
		Deferred<Void> cleanup = new Deferred<Void>();
		Deferred<Void> started = new Deferred<Void>();
		try {
			executor.execute(new FireAndForgetWork(this, cleanup, started));
			cleanup.getPromise()
					.onResolve(() -> releaseService())
					.then(null,
							resolved -> logError(
									"The fire-and-forget invocation failed",
									resolved.getFailure()));
			return started.getPromise();
		} catch (RejectedExecutionException ree) {
			logError("The Async Service threadpool rejected the fire-and-forget invocation", ree);
			return Promises.failed(new ServiceException("Unable to enqueue the fire-and forget task", 7, ree));
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
