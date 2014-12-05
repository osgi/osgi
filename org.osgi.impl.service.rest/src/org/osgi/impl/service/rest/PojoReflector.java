/*
 * Copyright (c) OSGi Alliance (2013, 2014). All Rights Reserved.
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

package org.osgi.impl.service.rest;

import java.lang.reflect.Method;
import java.util.HashMap;
import org.json.JSONObject;

/**
 * Reflector to create pojos from JSON Object representations and vice versa.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 * @param <B> The pojo base class for which the reflector does the conversion.
 */
public class PojoReflector<B> {

	private static HashMap<Class<?>, PojoReflector<?>>	reflectorCache	= new HashMap<Class<?>, PojoReflector<?>>();

	private final Class<B>								clazz;

	private final HashMap<String, Method>				methodTable;

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
		return methodName.startsWith("set") && methodName.length() > 3 ? Character.toLowerCase(methodName.charAt(3)) + methodName
				.substring(4) : null;
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
