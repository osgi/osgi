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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Reflector to create pojos from JSON Object representations and vice versa.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 * @param <B> The pojo base class for which the reflector does the conversion.
 */
public class PojoReflector<B> {

	private static HashMap<Class<?>, PojoReflector<?>>	reflectorCache	= new HashMap<Class<?>, PojoReflector<?>>();

	private final Class<B>								clazz;

	private final HashMap<String, Method>				setterMethodTable;

	private final HashMap<String, Method>				getterMethodTable;

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
		final Field[] fields = clazz.getDeclaredFields();

		System.err.println("FIELDS ARE " + Arrays.toString(fields));

		setterMethodTable = new HashMap<String, Method>(fields.length);
		getterMethodTable = new HashMap<String, Method>(fields.length);
		for (int i = 0; i < fields.length; i++) {
			final Field field = fields[i];
			final String fieldName = field.getName();
			try {
				final Method setter = clazz.getMethod(getSetterName(fieldName), field.getType());
				setterMethodTable.put(fieldName, setter);
				final Method getter = clazz.getMethod(getGetterName(fieldName));
				getterMethodTable.put(fieldName, getter);
			} catch (final NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}

	private static String getSetterName(final String fieldName) {
		return "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
	}

	private static String getGetterName(final String fieldName) {
		return "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
	}

	public B beanFromJSONObject(final JSONObject obj) throws Exception {
		final String[] names = JSONObject.getNames(obj);
		final B instance = clazz.newInstance();
		for (int i = 0; i < names.length; i++) {
			final String key = names[i];
			final Method setter = setterMethodTable.get(key);
			if (setter == null) {
				// silently ignore, it's JSON after all
				continue;
			}
			final Object o = obj.get(key);
			// check for empty object from JS...
			if (!(o instanceof JSONObject)) {
				setter.invoke(instance, o);
			}
		}

		return instance;
	}

	public Document xmlFromBean(final B bean) throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.newDocument();

		final Element rootNode = doc.createElement(bean.getClass().getAnnotation(RootNode.class).name());
		doc.appendChild(rootNode);

		for (final Map.Entry<String, Method> entry : getterMethodTable.entrySet()) {
			final String field = entry.getKey();
			final Object o = entry.getValue().invoke(bean);
			rootNode.appendChild(toXml(o, field, doc));

		}

		System.err.println("DOCUMENT " + doc.toString());

		return doc;
	}

	private Element toXml(final Object o, final String name, final Document doc) {
		final Element e = doc.createElement(name);

		if ("usingBundles".equals(name)) {
			for (final String bundle : (String[]) o) {
				e.appendChild(toXml(bundle, "bundle", doc));
			}
		} else if (o instanceof Map) {
			@SuppressWarnings("unchecked")
			final Map<Object, Object> map = (Map<Object, Object>) o;
			for (final Map.Entry<Object, Object> entry : map.entrySet()) {
				final Element elem = doc.createElement("entry");
				elem.setAttribute("key", entry.getKey().toString());
				final Object val = entry.getValue();
				if (val instanceof String) {
					elem.setAttribute("value", val.toString());
				} else {
					final Element valElement = toXml(val, "value", doc);
					valElement.setAttribute("type", val.getClass().getName());
					elem.appendChild(valElement);
				}
				e.appendChild(elem);
			}
		} else if (o.getClass().isArray()) {
			final Element elem = doc.createElement("array");
			elem.setAttribute("value-type", o.getClass().getComponentType().getName());
			for (int i = 0; i < Array.getLength(o); i++) {
				elem.appendChild(toXml(Array.get(o, i), "value", doc));
			}
			e.appendChild(elem);
		} else {
			e.setTextContent(o.toString());
		}

		return e;
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface RootNode {
		String name();
	}

}
