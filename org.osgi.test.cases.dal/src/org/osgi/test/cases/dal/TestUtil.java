/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.osgi.service.dal.DeviceFunction;

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
	 * @return <code>true</code> if the arguments are equivalent,
	 *         <code>false</code> otherwise.
	 */
	public static boolean areEqual(Object obj1, Object obj2) {
		if (null == obj1) {
			return (null == obj2);
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
	 * @return <code>true</code> if the element can be found in the array,
	 *         <code>false</code> otherwise.
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
		final StringBuffer getterName = new StringBuffer();
		getterName.append(prefix);
		getterName.append(Character.toUpperCase(propertyName.charAt(0)));
		if (propertyName.length() > 1) {
			getterName.append(propertyName.substring(1));
		}
		return getterName.toString();
	}

	/**
	 * Based on the accessor name, returns the properyt name.
	 * 
	 * @param beanAccessorMethod The accessor name.
	 * 
	 * @return The property name.
	 */
	public static String getPropertyName(String beanAccessorMethod) {
		if (beanAccessorMethod.length() < 4) {
			return null;
		}
		char firstChar = beanAccessorMethod.charAt(0);
		if ((('g' == firstChar) || ('s' == firstChar)) &&
				('e' == beanAccessorMethod.charAt(1)) &&
				('t' == beanAccessorMethod.charAt(2))) {
			return beanAccessorMethod.substring(3).toLowerCase();
		}
		return null;
	}

	/**
	 * Returns all class methods with the given name.
	 * 
	 * @param classObj The class to search in.
	 * @param methodName The method name.
	 * 
	 * @return All methods or <code>null</code> if there are no such methods.
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
	 * Returns the device function class instance.
	 * 
	 * @param deviceFunction The device function.
	 * @param bc The bundle context.
	 * 
	 * @return The function class.
	 * 
	 * @throws ClassNotFoundException If the class cannot be found.
	 */
	public static Class getDeviceFunctionClass(DeviceFunction deviceFunction, BundleContext bc) throws ClassNotFoundException {
		try {
			Long serviceId = (Long) deviceFunction.getServiceProperty(Constants.SERVICE_ID);
			ServiceReference[] sRefs = bc.getServiceReferences(
					null, '(' + Constants.SERVICE_ID + '=' + serviceId + ')');
			if (1 != sRefs.length) {
				throw new IllegalArgumentException(
						"Cannot find the device function service with id: " + serviceId);
			}
			return sRefs[0].getBundle().loadClass(
					(((String[]) deviceFunction.getServiceProperty(Constants.OBJECTCLASS))[0]));
		} catch (InvalidSyntaxException e) {
			// the filter is valid, it's not possible
			return null;
		}
	}

}
