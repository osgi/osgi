/*
 *	Licensed Materials - Property of IBM.
 *	(C) Copyright IBM Corporation 2011
 *	All Rights Reserved.
 *
 *	US Government Users Restricted Rights -
 *	Use, duplication or disclosure restricted by
 *	GSA ADP Schedule Contract with IBM Corporation.
 *
 *  Created by Jan S. Rellermeyer
 *  Copyright 2011 ibm.com. All rights reserved.
 */
package org.osgi.impl.service.rest;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.json.JSONObject;

public class PojoReflector<B> {

	private static HashMap<Class<?>, PojoReflector<?>> reflectorCache = new HashMap<Class<?>, PojoReflector<?>>();

	private final Class<B> clazz;

	private final HashMap<String, Method> methodTable;

	public static <T> PojoReflector<T> getReflector(final Class<T> clazz) {
		@SuppressWarnings("unchecked")
		PojoReflector<T> r = (PojoReflector<T>) reflectorCache.get(clazz);
		if (r == null) {
			r = new PojoReflector<T>(clazz);
			reflectorCache.put(clazz, r);
		}
		return r;
	}

	private PojoReflector(final Class<B> clazz) {
		this.clazz = clazz;
		final Method[] methods = clazz.getMethods();
		methodTable = new HashMap<String, Method>(methods.length);
		for (int i = 0; i < methods.length; i++) {
			final Method method = methods[i];
			final String field = getFieldName(method.getName());
			if (field != null) {
				methodTable.put(field, method);
			}
		}
	}

	private static String getFieldName(final String methodName) {
		return methodName.startsWith("set") && methodName.length() > 3 ? methodName
				.substring(3).toLowerCase() : null;
	}

	public B beanFromJSONObject(final JSONObject obj) throws Exception {
		final String[] names = JSONObject.getNames(obj);
		final B instance = clazz.newInstance();
		for (int i = 0; i < names.length; i++) {
			final String key = names[i];
			final Method setter = methodTable.get(key);
			if (setter == null) {
				throw new IllegalArgumentException("no field " + key + " in " + clazz.getSimpleName() + ": " + methodTable);
			}
			setter.invoke(instance, obj.get(key));
		}
		return instance;
	}

}
