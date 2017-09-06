/*
 * Copyright (c) OSGi Alliance (2013, 2017). All Rights Reserved.
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

package org.osgi.impl.service.rest.client;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.dto.DTO;
import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A reflector for turning DTOs into JSON representations and vice versa.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
public final class DTOReflector {

	private static final MediaType	XML_BASED	= MediaType.valueOf("application/*+xml");
	private static final MediaType	JSON_BASED	= MediaType.valueOf("application/*+json");

	public static <T extends DTO> T getDTO(final Class<T> clazz,
			final Representation repr) throws Exception {
		if (JSON_BASED.includes(repr.getMediaType())) {
			final JSONObject data = new JsonRepresentation(repr).getJsonObject();
			return getDTOfromJson(clazz, data, repr.getLocationRef().getPath());
		} else if (XML_BASED.includes(repr.getMediaType())) {
			final Document doc = new DomRepresentation(repr).getDocument();
			return getDTOfromXml(clazz, (Element) doc.getFirstChild(), repr.getLocationRef().getPath());
		} else {
			throw new UnsupportedOperationException(repr.getMediaType().toString());
		}
	}

	public static <T extends DTO> Collection<T> getDTOs(Class<T> clazz, Representation repr) throws Exception {
		if (JSON_BASED.includes(repr.getMediaType())) {
			return getDTOsFromJson(clazz, new JsonRepresentation(repr).getJsonArray());
		} else if (XML_BASED.includes(repr.getMediaType())) {
			System.out.println("content: " + repr.getText());
			return getDTOsFromXml(clazz, new DomRepresentation(repr).getDocument());
		} else {
			throw new UnsupportedOperationException(repr.getMediaType().toString());
		}
	}

	public static Map<String, String> getMap(final Representation repr) throws Exception {
		if (JSON_BASED.includes(repr.getMediaType())) {
			final JSONObject data = new JsonRepresentation(repr).getJsonObject();
			return getMapfromJsonObject(data);
		} else {
			throw new UnsupportedOperationException(repr.getMediaType().toString());
		}
	}

	public static Collection<String> getStrings(final Representation repr) throws Exception {
		final Collection<String> result = new ArrayList<String>();
		if (JSON_BASED.includes(repr.getMediaType())) {
			final JSONArray array = new JsonRepresentation(repr).getJsonArray();
			for (int i = 0; i < array.length(); i++) {
				result.add(array.getString(i));
			}
		} else if (XML_BASED.includes(repr.getMediaType())) {
			final Document doc = new DomRepresentation(repr).getDocument();
			final Node rootNode = doc.getFirstChild();
			final NodeList nodes = rootNode.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				result.add(nodes.item(i).getTextContent());
			}
		} else {
			throw new UnsupportedOperationException(repr.getMediaType().toString());
		}

		return result;
	}

	private static <T extends DTO> T getDTOfromJson(final Class<T> clazz, final JSONObject data, final String path) throws Exception {
		final Field[] fields = clazz.getFields();
		final T dto = clazz.getConstructor().newInstance();
		for (final Field field : fields) {
			if ("bundle".equals(field.getName())) {
				if (data.has("bundle")) {
					field.set(dto, Long.valueOf(getBundleIdFromPath(data.getString("bundle"))));
				} else {
					field.set(dto, Long.valueOf(getBundleIdFromPath(path)));
				}
			} else if ("usingBundles".equals(field.getName())) {
				field.set(
						dto,
						getBundleIdsFromPaths(data.getJSONArray("usingBundles")));
			} else if (field.getType().equals(Map.class)) {
				field.set(dto, getMapfromJsonObject(data.getJSONObject(field.getName())));
			} else {
				field.set(dto, data.get(field.getName()));
			}
		}
		return dto;
	}

	private static <T extends DTO> T getDTOfromXml(final Class<T> clazz, final Element elem, final String path) throws Exception {
		final Field[] fields = clazz.getFields();
		final T dto = clazz.getConstructor().newInstance();

		for (final Field field : fields) {
			if ("bundle".equals(field.getName())) {
				if (elem.getElementsByTagName("bundle") != null) {
					field.set(dto, Long.valueOf(getBundleIdFromPath(elem.getElementsByTagName("bundle").item(0).getTextContent())));
				} else {
					field.set(dto, Long.valueOf(getBundleIdFromPath(path)));
				}
			} else if ("usingBundles".equals(field.getName())) {
				final Node node = elem.getElementsByTagName("usingBundles").item(0);
				final NodeList nodes = node.getChildNodes();

				if (nodes.getLength() > 0) {
					final long[] using = new long[nodes.getLength()];
					for (int i = 0; i < nodes.getLength(); i++) {
						using[i] = getBundleIdFromPath(nodes.item(i).getTextContent());
						field.set(dto, using);
					}
				}
			} else if ("id".equals(field.getName()) || "lastModified".equals(field.getName())) {
				field.set(dto, Long.valueOf(elem.getElementsByTagName(field.getName()).item(0).getTextContent()));
			} else if ("state".equals(field.getName())) {
				field.set(dto, Integer.valueOf(elem.getElementsByTagName(field.getName()).item(0).getTextContent()));
			} else if (field.getType().equals(Map.class)) {
				final Node props = elem.getElementsByTagName("properties").item(0);

				final Map<String, Object> properties = new HashMap<String, Object>();
				final NodeList prop = props.getChildNodes();
				for (int i = 0; i < prop.getLength(); i++) {
					propertyFromXml(properties, prop.item(i));
				}

				field.set(dto, properties);
			} else {
				// default is string
				field.set(dto, elem.getElementsByTagName(field.getName()).item(0).getTextContent());
			}
		}
		return dto;
	}

	private static void propertyFromXml(final Map<String, Object> map, final Node node) {
		final String name = node.getAttributes().getNamedItem("name").getTextContent();
		final Node tNode = node.getAttributes().getNamedItem("type");
		final String type = tNode == null ? null : tNode.getTextContent();
		final Node vNode = node.getAttributes().getNamedItem("value");

		final Object value;
		if (vNode != null) {
			value = fromXml(type, vNode.getTextContent());
		} else {
			final String[] vals = node.getTextContent().split("\n");
			final int len = vals.length;
			value = getArray(type, len);
			for (int i = 0; i < len; i++) {
				Array.set(value, i, fromXml(type, vals[i]));
			}
		}

		map.put(name, value);
	}

	private static Object fromXml(final String type, final String value) {
		if (type == null || "String".equals(type)) {
			return value;
		} else if ("Long".equals(type)) {
			return Long.valueOf(value);
		} else if ("Double".equals(type)) {
			return Double.valueOf(value);
		} else if ("Float".equals(type)) {
			return Float.valueOf(value);
		} else if ("Integer".equals(type)) {
			return Integer.valueOf(value);
		} else if ("Byte".equals(type)) {
			return Byte.valueOf(value);
		} else if ("Character".equals(type)) {
			return Character.valueOf(value.trim().charAt(0));
		} else if ("Boolean".equals(type)) {
			return Boolean.valueOf(value);
		} else if ("Short".equals(type)) {
			return Short.valueOf(value);
		} else {
			return value;
		}
	}

	private static Object getArray(final String type, final int len) {
		if (type == null || "String".equals(type)) {
			return new String[len];
		} else if ("Long".equals(type)) {
			return new long[len];
		} else if ("Double".equals(type)) {
			return new double[len];
		} else if ("Float".equals(type)) {
			return new float[len];
		} else if ("Integer".equals(type)) {
			return new int[len];
		} else if ("Byte".equals(type)) {
			return new byte[len];
		} else if ("Character".equals(type)) {
			return new char[len];
		} else if ("Boolean".equals(type)) {
			return new boolean[len];
		} else if ("Short".equals(type)) {
			return new short[len];
		} else {
			return new Object[len];
		}
	}

	private static final Pattern	p	= Pattern.compile("\\/(\\d+)\\/*");

	private static long getBundleIdFromPath(final String path) {
		final Matcher m = p.matcher(path);
		if (m.find()) {
			final String s = m.group(1);
			return Long.parseLong(s);
		} else {
			throw new IllegalArgumentException(path);
		}
	}

	private static long[] getBundleIdsFromPaths(JSONArray array)
			throws JSONException {
		if (array.length() == 0) {
			return null;
		}

		final long[] result = new long[array.length()];

		for (int i = 0; i < array.length(); i++) {
			result[i] = getBundleIdFromPath(array.getString(i));
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private static <K, V> Map<K, V> getMapfromJsonObject(final JSONObject obj)
			throws JSONException {
		final Map<K, V> result = new HashMap<K, V>();

		final String[] keys = JSONObject.getNames(obj);

		for (int i = 0; i < keys.length; i++) {
			final Object o = keys[i].equals("service.id") || keys[i].equals("service.bundleid") ? Long.valueOf(obj.getLong(keys[i])) : obj.get(keys[i]);
			if (o instanceof JSONArray) {
				result.put((K) keys[i], (V) getStringArrayFromJSONArray((JSONArray) o));
			} else {
				result.put((K) keys[i], (V) o);
			}
		}
		return result;
	}

	private static String[] getStringArrayFromJSONArray(final JSONArray a) throws JSONException {
		final String[] result = new String[a.length()];
		for (int i = 0; i < result.length; i++) {
			result[i] = a.getString(i);
		}
		return result;
	}

	private static <T extends DTO> Collection<T> getDTOsFromJson(final Class<T> clazz, final JSONArray array) throws Exception {
		final Collection<T> result = new ArrayList<T>();
		for (int i = 0; i < array.length(); i++) {
			result.add(DTOReflector.getDTOfromJson(clazz, array.getJSONObject(i), null));
		}
		return result;
	}

	private static <T extends DTO> Collection<T> getDTOsFromXml(final Class<T> clazz, final Node rootNode) throws Exception {
		final Collection<T> result = new ArrayList<T>();
		final NodeList nodes = rootNode.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			result.add(DTOReflector.getDTOfromXml(clazz, (Element) nodes.item(i), null));
		}
		return result;
	}

	public static <T extends DTO> JSONObject getJson(final Class<T> clazz,
			final T dto) throws Exception {

		final Field[] fields = clazz.getFields();

		final JSONObject obj = new JSONObject();
		for (final Field field : fields) {
			if (field.getName().equals("bundle")) {
				obj.put("bundle", getBundlePathFromId((Long) field.get(dto)));
			} else {
				obj.put(field.getName(), field.get(dto));
			}
		}

		return obj;
	}

	private static String getBundlePathFromId(final Long id) {
		return "framework/bundle/" + id.toString();
	}

}
