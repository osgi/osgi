/*
 * $Header$
 * 
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

package org.eclipse.osgi.component;

import java.lang.reflect.*;

/**
 * Base class which provides reflection support methods.
 * 
 * @version $Revision$
 */
public abstract class AbstractReflector {

	/**
	 * Locate and invoke the specified method.
	 * 
	 * @param object Object or Class upon which to call the method.
	 * @param methodName The name of the method.
	 * @param params The formal parameter types of the method.
	 * @param args The arguments for the method call.
	 * @return The return value of the method.
	 */
	protected Object invokeMethod(Object object, String methodName,
			Class[] params, Object[] args) {
		Class clazz;
		if (object instanceof Class) {
			clazz = (Class) object;
			object = null;
		}
		else {
			clazz = object.getClass();
		}

		Method method = getMethod(clazz, methodName, params);
		try {
			return method.invoke(object, args);
		}
		catch (IllegalAccessException e) {
			reflectionException(e);
		}
		catch (InvocationTargetException e) {
			reflectionException(e);
		}

		return null;
	}

	/**
	 * Locate the specified method.
	 * 
	 * @param clazz Class upon which to locate the method.
	 * @param methodName The name of the method.
	 * @param params The formal parameter types of the method.
	 * @return The method.
	 */
	protected Method getMethod(Class clazz, String methodName, Class[] params) {
		Exception exception = null;
		for (; clazz != null; clazz = clazz.getSuperclass()) {
			try {
				Method method = clazz.getDeclaredMethod(methodName, params);
				// enable us to access the method if not public
				method.setAccessible(true);
				return method;
			}
			catch (NoSuchMethodException e) {
				exception = e;
				continue;
			}
		}

		reflectionException(exception);
		return null;
	}

	/**
	 * Locate and return the value of the specified field.
	 * 
	 * @param object Object or Class upon which to locate the field.
	 * @param fieldName The name of the field.
	 * @return The value of the field.
	 */
	protected Object getFieldValue(Object object, String fieldName) {
		Class clazz;
		if (object instanceof Class) {
			clazz = (Class) object;
			object = null;
		}
		else {
			clazz = object.getClass();
		}

		Field field = getField(clazz, fieldName);
		try {
			return field.get(object);
		}
		catch (IllegalAccessException e) {
			reflectionException(e);
		}

		return null;
	}

	/**
	 * Locate the specified field.
	 * 
	 * @param clazz Class upon which to locate the field.
	 * @param fieldName The name of the field.
	 * @return The field.
	 */
	protected Field getField(Class clazz, String fieldName) {
		Exception exception = null;
		for (; clazz != null; clazz = clazz.getSuperclass()) {
			try {
				Field field = clazz.getDeclaredField(fieldName);
				// enable us to access the field if not public
				field.setAccessible(true);
				return field;
			}
			catch (NoSuchFieldException e) {
				exception = e;
				continue;
			}
		}

		reflectionException(exception);
		return null;
	}

	/**
	 * Abstract method called when a reflection exception occurs.
	 * 
	 * @param e Exception which indicates the reflection logic is confused.
	 */
	protected abstract void reflectionException(Exception e);

}
