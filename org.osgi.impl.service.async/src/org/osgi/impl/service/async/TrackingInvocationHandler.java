package org.osgi.impl.service.async;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.ServiceReference;

class TrackingInvocationHandler implements InvocationHandler {

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
	private final ServiceReference<?> ref;
	private final Object delegate;
	
	public TrackingInvocationHandler(AsyncService asyncService, ServiceReference<?> ref) {
		super();
		this.asyncService = asyncService;
		this.ref = ref;
		this.delegate = null;
	}
	public TrackingInvocationHandler(AsyncService asyncService, Object service) {
		super();
		this.asyncService = asyncService;
		this.delegate = service;
		this.ref = null;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		asyncService.registerInvocation(new MethodCall(ref, delegate, method, args));
		Class<?> returnType = method.getReturnType();
		return RETURN_VALUES.get(returnType);
	}
	
}