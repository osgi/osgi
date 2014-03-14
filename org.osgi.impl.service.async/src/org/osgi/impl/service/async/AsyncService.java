package org.osgi.impl.service.async;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.async.Async;
import org.osgi.util.promise.Promise;


public class AsyncService implements Async {

	private final Bundle clientBundle;
	
	private final ConcurrentMap<Thread, MethodCall> invocations = new ConcurrentHashMap<Thread, MethodCall>();
	
	private final ExecutorService executor;
	
	public AsyncService(Bundle clientBundle, ExecutorService executor) {
		super();
		this.clientBundle = clientBundle;
		this.executor = executor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T mediate(T service) {
		
		Class<?> cls = service.getClass();
		Set<Class<?>> interfaces = new HashSet<Class<?>>();
		do {
			for(Class<?> iface : cls.getInterfaces()) {
				try {
					if(iface.equals(clientBundle.loadClass(iface.getName()))) {
						interfaces.add(cls);
					}
				} catch (ClassNotFoundException cnfe) {
					//Just don't proxy this interface
				}
			}
		} while ((cls = cls.getSuperclass()) != null);
		
		return (T) Proxy.newProxyInstance(clientBundle.adapt(BundleWiring.class).getClassLoader(), 
				interfaces.toArray(new Class[interfaces.size()]), new TrackingInvocationHandler(this, service));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T mediate(ServiceReference<T> ref) {
		Object o = ref.getProperty(Constants.OBJECTCLASS);
		
		List<String> ifaceNames;
		
		if(o == null) {
			throw new IllegalArgumentException("This service reference has no objectclass " + ref);
		} else if (o instanceof String[]) {
			ifaceNames = Arrays.asList((String[]) o); 
		} else {
			ifaceNames = Collections.singletonList(o.toString());
		}
		
		Collection<Class<?>> ifaces = new ArrayList<Class<?>>(ifaceNames.size());
		for(String s : ifaceNames) {
			try {
				Class<?> c = clientBundle.loadClass(s);
				if(c.isInterface()) {
					ifaces.add(c);
				}
			} catch (ClassNotFoundException e) {
				//Just don't proxy this interface
			}
		}
		
		return (T) Proxy.newProxyInstance(clientBundle.adapt(BundleWiring.class).getClassLoader(), 
				ifaces.toArray(new Class[ifaces.size()]), new TrackingInvocationHandler(this, ref));
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
