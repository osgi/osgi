/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Function;

/**
 * Test utility class.
 */
public final class TestUtil {

	private TestUtil() {
		// prevent object instantiation
	}

	/**
	 * Compares both objects. If arrays are compared, the array values are
	 * checked.
	 * 
	 * @param obj1 The first object to compare.
	 * @param obj2 The second object to compare.
	 * 
	 * @return {@code true} if the arguments are equivalent, {@code false}
	 *         otherwise.
	 */
	public static boolean areEqual(Object obj1, Object obj2) {
		if (null == obj1) {
			return null == obj2;
		}
		if (!obj1.getClass().isArray()) {
			return obj1.equals(obj2);
		}
		if (!obj2.getClass().isArray()) {
			return false;
		}
		int array1Length = Array.getLength(obj1);
		int array2Length = Array.getLength(obj2);
		if (array1Length != array2Length) {
			return false;
		}
		for (int i = 0; i < array1Length; i++) {
			if (!Array.get(obj1, i).equals(Array.get(obj2, i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks that the array contains the element.
	 * 
	 * @param array The array to search in.
	 * @param elem THe element to search.
	 * 
	 * @return {@code true} if the element can be found in the array,
	 *         {@code false} otherwise.
	 */
	public static boolean contains(Object array, Object elem) {
		if ((null == array) ||
				(null == elem)) {
			return false;
		}
		if (!array.getClass().isArray()) {
			return false;
		}
		for (int i = 0, length = Array.getLength(array); i < length; i++) {
			if (elem.equals(Array.get(array, i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return the bean accessor name for this property. For example, getData and
	 * setData.
	 * 
	 * @param propertyName The property name.
	 * @param prefix The prefix, get or set.
	 * 
	 * @return The accessor name.
	 */
	public static String getBeanAccessor(String propertyName, String prefix) {
		StringBuffer getterName = new StringBuffer();
		getterName.append(prefix);
		getterName.append(Character.toUpperCase(propertyName.charAt(0)));
		if (propertyName.length() > 1) {
			getterName.append(propertyName.substring(1));
		}
		return getterName.toString();
	}

	/**
	 * Returns all class methods with the given name.
	 * 
	 * @param classObj The class to search in.
	 * @param methodName The method name.
	 * 
	 * @return All methods or {@code null} if there are no such methods.
	 */
	public static Method[] getMethods(Class classObj, String methodName) {
		Method[] methods = classObj.getMethods();
		List result = new ArrayList();
		for (int i = 0; i < methods.length; i++) {
			if (methodName.equals(methods[i].getName())) {
				result.add(methods[i]);
			}
		}
		return (result.isEmpty()) ? null :
				(Method[]) result.toArray(new Method[result.size()]);
	}

	/**
	 * Returns the function class instance.
	 * 
	 * @param function The function.
	 * @param bc The bundle context.
	 * 
	 * @return The function class.
	 * 
	 * @throws ClassNotFoundException If the class cannot be found.
	 */
	public static Class getFunctionClass(Function function, BundleContext bc) throws ClassNotFoundException {
		try {
			Long serviceId = (Long) function.getServiceProperty(Constants.SERVICE_ID);
			ServiceReference[] sRefs = bc.getServiceReferences(
					null, '(' + Constants.SERVICE_ID + '=' + serviceId + ')');
			if (1 != sRefs.length) {
				throw new IllegalArgumentException(
						"Cannot find the function service with id: " + serviceId);
			}
			return sRefs[0].getBundle().loadClass(
					((String[]) function.getServiceProperty(Constants.OBJECTCLASS))[0]);
		} catch (InvalidSyntaxException e) {
			// the filter is valid, it's not possible
			return null;
		}
	}
}
