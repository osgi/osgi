/*
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */
 
package org.eclipse.osgi.component.instance;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.osgi.service.component.*;


/**
 *
 * Invoke a method on the Service Component: 
 * 		activate, deactivate, bind or unbind
 * 
 * @version $Revision$
 */
public class InvokeMethod {

	/* set this to true to compile in debug messages */
	static final boolean DEBUG = false;

	/**
	 * Invoke the activate method of the Service Component
	 * 
	 * @param instance The instance of the component 
	 * @param context The componenet context 
	 */
	public void activateComponent(Object instance, ComponentContext context) throws IllegalAccessException, InvocationTargetException {
		// Create an array of parameters to pass to the method 
		// The activate method requires the ComponentContext 
		Object[] parameterTypes = new Object[] {context};
		invokeMethod("activate", instance, parameterTypes);
	}

	/**
	 * Invoke the deactivate method of the Service Component
	 * 
	 * @param instance The instance of the component 
	 * @param context The componenet context 
	 */
	public void deactivateComponent(Object instance, ComponentContext context) throws IllegalAccessException, InvocationTargetException {
		// Create an array of parameters to pass to the method 
		// The deactivate method requires the ComponentContext 
		Object[] parameterTypes = new Object[] {context};
		invokeMethod("deactivate", instance, parameterTypes);
	}

	/** 
	 * bindComponent method of the Service Component
	 * 
	 * @param bind
	 * @param instance
	 * @param serviceObject
	 */

	public void bindComponent(String bind, Object instance, Object serviceObject) throws IllegalAccessException, InvocationTargetException {
		// Create an array of parameters to pass to the method 
		Object[] parameterTypes = new Object[] {serviceObject};
		invokeMethod(bind, instance, parameterTypes);
	}

	/**
	 * unbind method of the Service Component
	 * 
	 * @param unbind
	 * @param instance
	 * @param serviceObject
	 */

	public void unbindComponent(String unbind, Object instance, Object serviceObject) throws IllegalAccessException, InvocationTargetException {
		// Create an array of parameters to pass to the method 
		Object[] parameterTypes = new Object[] {serviceObject};
		invokeMethod(unbind, instance, parameterTypes);
	}

	/**
	 * invokeMethod - invoke a Method on the Service Compoent via reflection
	 * 
	 * @param methodName - the method name to invoke
	 * @param instance - instance used to get Class
	 * @param parameterTypes - array of parameters to pass to the method
	 */

	public void invokeMethod(String methodName, Object instance, Object[] parameterTypes) throws IllegalAccessException, InvocationTargetException {

		// Get the runtime class of the Service Component object
		Class c = instance.getClass();
		// Get all the methods for this class
		Method method = getMethod(methodName, c.getDeclaredMethods());
		if (method == null)
			//check the inherited methods
			method = getMethod(methodName, c.getMethods());
		if (method == null)
			return;
		//If the method is declared protected or public, SCR will call the method
		int mod = method.getModifiers();
		if ((Modifier.isProtected(mod)) || (Modifier.isPublic(mod))) {
			//if the method is protected must set accessibility(true) to invoke it
			if(Modifier.isProtected(mod))
				method.setAccessible(true);
			//invoke the method
			method.invoke(instance, parameterTypes);
		}
	}

	private Method getMethod(String methodName, Method[] methods) {
		for (int i = 0; i < methods.length; i++) {
			if (methodName.equals(methods[i].getName())) {
				return methods[i];
			}
		}
		return null;
	}
}

