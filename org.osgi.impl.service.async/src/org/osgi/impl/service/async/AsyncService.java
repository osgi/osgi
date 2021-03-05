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
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.async.Async;
import org.osgi.service.log.LogService;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;

import net.sf.cglib.proxy.Enhancer;


public class AsyncService implements Async {

	private static final class CGLibAwareClassLoader extends ClassLoader {
		private final ClassLoader serviceTypeLoader;

		CGLibAwareClassLoader(Bundle registeringBundle) {
			this.serviceTypeLoader = registeringBundle.adapt(BundleWiring.class).getClassLoader();
		}

		CGLibAwareClassLoader(ClassLoader loader) {
			this.serviceTypeLoader = loader;
		}

		@Override
		protected Class<?> findClass(String var0)
				throws ClassNotFoundException {
			if(var0.startsWith("net.sf.cglib")) {
				return AsyncService.class.getClassLoader().loadClass(var0);
			} else {
				return serviceTypeLoader.loadClass(var0);
			}
		}
	}

	private final Bundle clientBundle;
	
	private final ConcurrentMap<Thread, MethodCall> invocations = new ConcurrentHashMap<Thread, MethodCall>();
	
	private final ExecutorService executor;
	
	private final ServiceTracker<LogService, LogService> logServiceTracker;
	
	public AsyncService(Bundle clientBundle, ExecutorService executor, ServiceTracker<LogService, LogService> logServiceTracker) {
		super();
		this.clientBundle = clientBundle;
		this.executor = executor;
		this.logServiceTracker = logServiceTracker;
	}

	@Override
	public <T> T mediate(final T service, final Class<T> iface) {
		return AccessController.doPrivileged(
				(PrivilegedAction<T>) () -> privMediate(service, iface));
	}

	@SuppressWarnings("unchecked")
	<T> T privMediate(T service, Class<T> iface) {
		
		TrackingInvocationHandler handler = new TrackingInvocationHandler(this, 
				clientBundle, logServiceTracker, service);
		
		if(iface.isInterface()) {
			return (T) Proxy.newProxyInstance(
					new ClassLoader(service.getClass().getClassLoader()) {
						// empty
					}, 
					new Class[] {iface}, handler);
		} else {
			return (T) proxyClass(iface, handler, 
					new CGLibAwareClassLoader(service.getClass().getClassLoader()));
		}
	}

	@Override
	public <T> T mediate(final ServiceReference<? extends T> ref, final Class<T> iface) {
		return AccessController.doPrivileged(
				(PrivilegedAction<T>) () -> privMediate(ref, iface));
	}
	
	@SuppressWarnings("unchecked")
	<T> T privMediate(ServiceReference< ? extends T> ref, Class<T> iface) {

		TrackingInvocationHandler handler = new TrackingInvocationHandler(this, 
				clientBundle, logServiceTracker, ref);
		
		Bundle registeringBundle = ref.getBundle();

		if(iface.isInterface()) {
			return (T) Proxy.newProxyInstance(
					new ClassLoader(registeringBundle.adapt(BundleWiring.class)
							.getClassLoader()) {
						// empty
					}, 
					new Class[] {iface}, handler);
		} else {
			return (T) proxyClass(iface, handler, 
					new CGLibAwareClassLoader(registeringBundle));
		}
	}

	private Object proxyClass(Class<?> mostSpecificClass, 
			TrackingInvocationHandler handler, ClassLoader classLoader) {
		
		acceptClassType(mostSpecificClass);
		
		Enhancer enhancer = new Enhancer();
		enhancer.setClassLoader(classLoader);
		enhancer.setSuperclass(mostSpecificClass);
		enhancer.setCallback(handler);
		
		return enhancer.create();
	}

	private void acceptClassType(Class<?> type) {
		
		if(Modifier.isFinal(type.getModifiers())) {
			throw new IllegalArgumentException("The type " + type.getName() + " is final");
		}
		try {
			type.getConstructor();
		} catch (NoSuchMethodException nsme) {
			throw new IllegalArgumentException("The type " + type.getName() + " has no zero-argument constructor", nsme);
		}
		
		Class<?> toCheck = type;
		while(toCheck != Object.class) {
			for(Method m : toCheck.getDeclaredMethods()) {
				if(Modifier.isFinal(m.getModifiers())) {
					throw new IllegalArgumentException("The type hierarchy for " + type.getName() + 
							" has a final method " + m.getName() + " defined on " + toCheck.getName());
				}
			}
			toCheck = toCheck.getSuperclass();
		}
	}

	@Override
	public <T> Promise<T> call(T call) throws IllegalStateException {
		MethodCall currentInvocation = consumeCurrentInvocation();
		if(currentInvocation == null) throw new IllegalStateException("Incorrect API usage - this thread has no pending method calls");
		return currentInvocation.invokeAsynchronously(clientBundle, executor);
	}

	@Override
	public Promise<?> call() throws IllegalStateException {
		return call(null);
	}

	@Override
	public Promise<Void> execute() throws IllegalStateException {
		MethodCall currentInvocation = consumeCurrentInvocation();
		if(currentInvocation == null) throw new IllegalStateException("Incorrect API usage - this thread has no pending method calls");
		return currentInvocation.fireAndForget(clientBundle, executor);
	}

	void registerInvocation(MethodCall invocation) {
		if(invocations.putIfAbsent(Thread.currentThread(), invocation) != null) {
			invocations.remove(Thread.currentThread());
			throw new IllegalStateException("Incorrect API usage - this thread already has a pending method call");
		}
	}

	MethodCall consumeCurrentInvocation() {
		return invocations.remove(Thread.currentThread());
	}

}
