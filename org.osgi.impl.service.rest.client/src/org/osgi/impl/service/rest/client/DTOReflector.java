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

package org.osgi.impl.service.rest.client;

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
import org.w3c.dom.TypeInfo;

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
			return getDTOfromXml(clazz, doc, repr.getLocationRef().getPath());
		} else {
			throw new UnsupportedOperationException(repr.getMediaType().toString());
		}
	}

	public static <T extends DTO> Collection<T> getDTOs(Class<T> clazz, Representation repr) throws Exception {
		if (JSON_BASED.includes(repr.getMediaType())) {
			final JSONArray data = new JsonRepresentation(repr).getJsonArray();
			return getDTOsFromJson(clazz, data);
		} else {
			throw new UnsupportedOperationException(repr.getMediaType().toString());
		}
	}

	public static Map<String, Object> getMap(Representation repr) throws Exception {
		if (JSON_BASED.includes(repr.getMediaType())) {
			final JSONObject data = new JsonRepresentation(repr).getJsonObject();
			return getMapfromJsonObject(data);
		} else {
			throw new UnsupportedOperationException(repr.getMediaType().toString());
		}
	}

	private static <T extends DTO> T getDTOfromJson(final Class<T> clazz, final JSONObject data, final String path) throws Exception {
		final Field[] fields = clazz.getFields();
		final T dto = clazz.newInstance();
		for (final Field field : fields) {
			if ("bundle".equals(field.getName())) {
				if (data.has("bundle")) {
					field.set(dto, new Long(getBundleIdFromPath(data.getString("bundle"))));
				} else {
					field.set(dto, new Long(getBundleIdFromPath(path)));
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

	private static <T extends DTO> T getDTOfromXml(final Class<T> clazz, final Document doc, final String path) throws Exception {
		final Field[] fields = clazz.getFields();
		final T dto = clazz.newInstance();

		for (final Field field : fields) {
			if ("bundle".equals(field.getName())) {
				if (doc.getElementsByTagName("bundle") != null) {
					field.set(dto, new Long(getBundleIdFromPath(doc.getElementsByTagName("bundle").item(0).getTextContent())));
				} else {
					field.set(dto, new Long(getBundleIdFromPath(path)));
				}
			} else if ("usingBundles".equals(field.getName())) {
				// FIXME: implement
			} else if (field.getType().equals(Map.class)) {
				// FIXME: implement
			} else {
				Element elem = (Element) doc.getElementsByTagName(field.getName()).item(0);
				final TypeInfo info = elem.getSchemaTypeInfo();
				// field.set(dto, data.get(field.getName()));
			}
		}
		return dto;
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
			final Object o = keys[i].equals("service.id") || keys[i].equals("service.bundleid") ? new Long(obj.getLong(keys[i])) : obj.get(keys[i]);
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
