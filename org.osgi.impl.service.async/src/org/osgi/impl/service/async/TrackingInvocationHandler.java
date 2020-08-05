package org.osgi.impl.service.async;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

class TrackingInvocationHandler implements InvocationHandler, net.sf.cglib.proxy.InvocationHandler {

	private static final Map<Class<?>, Object> RETURN_VALUES;
	
	static {
		Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
		
		map.put(boolean.class, Boolean.FALSE);
		map.put(byte.class, Byte.valueOf((byte)0));
		map.put(short.class, Short.valueOf((short)0));
		map.put(char.class, Character.valueOf((char)0));
		map.put(int.class, Integer.valueOf(0));
		map.put(float.class, Float.valueOf(0));
		map.put(long.class, Long.valueOf(0));
		map.put(double.class, Double.valueOf(0));
		
		RETURN_VALUES = Collections.unmodifiableMap(map);
	}
	
	/**
	 * 
	 */
	private final AsyncService asyncService;
	private final ServiceTracker<LogService, LogService> logServiceTracker;
	private final Bundle clientBundle;
	private final ServiceReference<?> ref;
	private final Object delegate;
	
	public TrackingInvocationHandler(AsyncService asyncService, 
			Bundle clientBundle, ServiceTracker<LogService, LogService> logServiceTracker, 
			ServiceReference<?> ref) {
		this.asyncService = asyncService;
		this.logServiceTracker = logServiceTracker;
		this.clientBundle = clientBundle;
		this.ref = ref;
		this.delegate = null;
	}
	public TrackingInvocationHandler(AsyncService asyncService, 
			Bundle clientBundle,ServiceTracker<LogService, LogService> logServiceTracker, 
			Object service) {
		this.asyncService = asyncService;
		this.logServiceTracker = logServiceTracker;
		this.clientBundle = clientBundle;
		this.delegate = service;
		this.ref = null;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		asyncService.registerInvocation(new MethodCall(clientBundle, logServiceTracker, 
				ref, delegate, method, args));
		Class<?> returnType = method.getReturnType();
		return RETURN_VALUES.get(returnType);
	}
	
}
