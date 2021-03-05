/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.impl.service.rest;

import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.json.JSONObject;
import org.osgi.impl.service.rest.pojos.BundleExceptionPojo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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

	private static final DocumentBuilderFactory			factory;

	private static final Map<Class<?>, String>			typeCache		= new HashMap<Class<?>, String>();

	private static final String							SCHEMA_LOCATION	= "http://www.osgi.org/xmlns/rest/v1.0.0 rest.xsd";

	private static final String							REST_NS			= "rest";

	static {
		final SchemaFactory sfact = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		factory = DocumentBuilderFactory.newInstance();
		try {
			factory.setSchema(sfact.newSchema(new StreamSource(PojoReflector.class.getResourceAsStream("/rest.xsd"))));
			factory.setNamespaceAware(true);
			factory.setValidating(true);
		} catch (SAXException e) {
			e.printStackTrace();
		}

		typeCache.put(String.class, "String");
		typeCache.put(Long.class, "Long");
		typeCache.put(Double.class, "Double");
		typeCache.put(Float.class, "Float");
		typeCache.put(Integer.class, "Integer");
		typeCache.put(Byte.class, "Byte");
		typeCache.put(Character.class, "Character");
		typeCache.put(Boolean.class, "Boolean");
		typeCache.put(Short.class, "Short");
	}

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
		final B instance = clazz.getConstructor().newInstance();
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

	public B beanFromXml(final Document doc) throws Exception {
		final B instance = clazz.getConstructor().newInstance();
		final Node rootNode = doc.getFirstChild();
		final NodeList elems = rootNode.getChildNodes();
		for (int i = 0; i < elems.getLength(); i++) {
			final String key = elems.item(i).getNodeName();
			final Method setter = setterMethodTable.get(key);
			if (setter == null) {
				System.err.println("Warning: unknown value " + key);
				continue;
			}
			final String value = elems.item(i).getTextContent();
			final Object o;
			final Class<?> type = setter.getParameterTypes()[0];
			if (int.class == type) {
				o = Integer.valueOf(value);
			} else if (long.class == type) {
				o = Long.valueOf(value);
			} else if (boolean.class == type) {
				o = Boolean.valueOf(value);
			} else {
				o = value;
			}

			setter.invoke(instance, o);
		}

		return instance;
	}

	public Document xmlFromBean(final B bean) throws Exception {
		final DocumentBuilder builder = factory.newDocumentBuilder();
		final Document doc = builder.newDocument();

		final Element rootNode = xmlFromBean(bean, doc);
		rootNode.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
				"xsi:schemaLocation", SCHEMA_LOCATION);
		doc.appendChild(rootNode);

		return doc;
	}

	public Element xmlFromBean(final Object bean, final Document doc) throws Exception {
		final Element rootNode = doc.createElementNS(REST_NS, bean.getClass().getAnnotation(RootNode.class).name());
		if (bean instanceof Collection) {
			String elemName = null;
			boolean complex = false;

			final ElementNode a = bean.getClass().getAnnotation(ElementNode.class);
			if (a != null) {
				elemName = a.name();
			}

			for (final Object o : (Collection<?>) bean) {
				if (elemName == null) {
					elemName = o.getClass().getAnnotation(RootNode.class).name();
					complex = true;
				}

				if (complex) {
					rootNode.appendChild(getReflector(o.getClass()).xmlFromBean(o, doc));
				} else {
					rootNode.appendChild(toXml(o, elemName, doc));
				}
			}
		} else if (bean instanceof BundleExceptionPojo) {
			final BundleExceptionPojo p = (BundleExceptionPojo) bean;

			final Element tc = doc.createElementNS(REST_NS, "typecode");
			tc.setTextContent(Integer.toString(p.getTypecode()));
			rootNode.appendChild(tc);
			final Element msg = doc.createElementNS(REST_NS, "message");
			msg.setTextContent(p.getMessage());
			rootNode.appendChild(msg);
		} else {
			for (final Map.Entry<String, Method> entry : getterMethodTable.entrySet()) {
				final String field = entry.getKey();
				final Object o = entry.getValue().invoke(bean);
				rootNode.appendChild(toXml(o, field, doc));
			}
		}

		return rootNode;
	}

	// for debugging only
	@SuppressWarnings("unused")
	private final String printDoc(final Document doc) throws Exception {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		return writer.getBuffer().toString();
	}

	private static Element toXml(final Object o, final String name, final Document doc) {
		final Element e = doc.createElementNS(REST_NS, name);

		if ("usingBundles".equals(name)) {
			for (final String bundle : (String[]) o) {
				e.appendChild(toXml(bundle, "bundle", doc));
			}
		} else if (o instanceof Map) {
			@SuppressWarnings("unchecked")
			final Map<Object, Object> map = (Map<Object, Object>) o;
			for (final Map.Entry<Object, Object> entry : map.entrySet()) {
				final Element elem = doc.createElementNS(REST_NS, "property");
				elem.setAttribute("name", entry.getKey().toString());
				final Object val = entry.getValue();
				if (val.getClass().isArray()) {
					final String type = getType(o.getClass().getComponentType());
					if (type != null) {
						e.setAttribute("type", type);
					}
					final int len = Array.getLength(val);
					final StringBuilder sb = new StringBuilder();
					for (int i = 0; i < len; i++) {
						sb.append(Array.get(val, i).toString());
						if (i < len) {
							sb.append('\n');
						}
					}
					elem.setTextContent(sb.toString());
				} else {
					final String type = getType(val.getClass());
					if (type != null) {
						elem.setAttribute("type", type);
					}
					elem.setAttribute("value", val.toString());
				}
				e.appendChild(elem);
			}
		} else {
			e.setTextContent(o.toString());
		}

		return e;
	}

	private static String getType(Class<? extends Object> cls) {
		return typeCache.get(cls);
	}

	public static Document mapToXml(final Map<String, String> map) throws Exception {
		final DocumentBuilder builder = factory.newDocumentBuilder();
		final Document doc = builder.newDocument();

		final Element rootNode = doc.createElementNS(REST_NS, "bundleHeader");
		doc.appendChild(rootNode);

		for (final String key : map.keySet()) {
			final Element entry = doc.createElementNS(REST_NS, "entry");
			entry.setAttribute("key", key);
			entry.setAttribute("value", map.get(key));
			rootNode.appendChild(entry);
		}

		return doc;
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface RootNode {
		String name();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface ElementNode {
		String name();
	}

}
