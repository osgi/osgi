package org.osgi.impl.service.async;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import net.sf.cglib.proxy.Enhancer;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.async.Async;
import org.osgi.service.log.LogService;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;


public class AsyncService implements Async {

	private static final class CGLibAwareClassLoader extends ClassLoader {
		private final ClassLoader serviceTypeLoader;

		private CGLibAwareClassLoader(Bundle registeringBundle) {
			this.serviceTypeLoader = registeringBundle.adapt(BundleWiring.class).getClassLoader();
		}

		private CGLibAwareClassLoader(ClassLoader loader) {
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

	@SuppressWarnings("unchecked")
	public <T> T mediate(T service) {
		
		TrackingInvocationHandler handler = new TrackingInvocationHandler(this, 
				clientBundle, logServiceTracker, service);
		
		return (T) proxyClass(Collections.<Class<?>>emptyList(), service.getClass(), handler, 
				new CGLibAwareClassLoader(service.getClass().getClassLoader()));
	}

	@SuppressWarnings("unchecked")
	public <T> T mediate(ServiceReference<T> ref) {
		Object o = ref.getProperty(Constants.OBJECTCLASS);
		
		List<String> ifaceNames;
		final Bundle registeringBundle = ref.getBundle();
		
		if(o == null) {
			throw new IllegalArgumentException("This service reference has no objectclass " + ref);
		} else if (o instanceof String[]) {
			ifaceNames = Arrays.asList((String[]) o); 
		} else {
			ifaceNames = Collections.singletonList(o.toString());
		}
		
		Collection<Class<?>> ifaces = new ArrayList<Class<?>>(ifaceNames.size());
		
		Class<?> mostSpecificClass = Object.class;
		for(String s : ifaceNames) {
			try {
				Class<?> c = registeringBundle.loadClass(s);
				if(c.isInterface()) {
					ifaces.add(c);
				} else if (mostSpecificClass.isAssignableFrom(c)) {
					mostSpecificClass = c;
				}
			} catch (ClassNotFoundException e) {
				//Just don't proxy this interface
			}
		}
		
		TrackingInvocationHandler handler = new TrackingInvocationHandler(this, 
				clientBundle, logServiceTracker, ref);

		if(mostSpecificClass == Object.class) {
			return (T) Proxy.newProxyInstance(registeringBundle.adapt(BundleWiring.class).getClassLoader(), 
				ifaces.toArray(new Class[ifaces.size()]), handler);
		} else {
			return (T) proxyClass(ifaces, mostSpecificClass, handler, 
					new CGLibAwareClassLoader(registeringBundle));
		}
	}

	private Object proxyClass(Collection<Class<?>> ifaces,
			Class<?> mostSpecificClass, TrackingInvocationHandler handler,
			ClassLoader classLoader) {
		
		mostSpecificClass = acceptClass(mostSpecificClass);
		
		Enhancer enhancer = new Enhancer();
		enhancer.setClassLoader(classLoader);
		enhancer.setSuperclass(mostSpecificClass);
		enhancer.setInterfaces(ifaces.toArray(new Class[ifaces.size()]));
		enhancer.setCallback(handler);
		
		return enhancer.create();
	}

	private Class<?> acceptClass(Class<?> mostSpecificClass) {
		if(Modifier.isFinal(mostSpecificClass.getModifiers())) {
			mostSpecificClass = mostSpecificClass.getSuperclass();
		}
		
		outer: while(mostSpecificClass != null) {
			
			Class<?> top = mostSpecificClass;
			while(top != Object.class) {
				for(Method m : top.getDeclaredMethods()) {
					if(Modifier.isFinal(m.getModifiers())) {
						mostSpecificClass = mostSpecificClass.getSuperclass();
						continue outer;
					}
				}
				top = top.getSuperclass();
			}
			
			for(Constructor<?> c : mostSpecificClass.getDeclaredConstructors()) {
				if(c.getParameterTypes().length == 0 && !Modifier.isPrivate(c.getModifiers())) {
					break outer;
				}
			}
			mostSpecificClass = mostSpecificClass.getSuperclass();
		}
		return mostSpecificClass != null ? mostSpecificClass : Object.class;
	}

	public <T> Promise<T> call(T call) throws IllegalStateException {
		MethodCall currentInvocation = consumeCurrentInvocation();
		if(currentInvocation == null) throw new IllegalStateException("Incorrect API usage - this thread has no pending method calls");
		return currentInvocation.invokeAsynchronously(clientBundle, executor);
	}

	public Promise<?> call() throws IllegalStateException {
		return call(null);
	}

	public void execute() throws IllegalStateException {
		MethodCall currentInvocation = consumeCurrentInvocation();
		if(currentInvocation == null) throw new IllegalStateException("Incorrect API usage - this thread has no pending method calls");
		currentInvocation.fireAndForget(clientBundle, executor);
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
