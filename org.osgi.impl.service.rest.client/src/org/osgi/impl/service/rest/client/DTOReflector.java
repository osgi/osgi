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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.dto.DTO;

/**
 * A reflector for turning DTOs into JSON representations and vice versa.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
public final class DTOReflector {

	public static <T extends DTO> T getDTO(final Class<T> clazz,
			final JSONObject data) throws Exception {
		final Field[] fields = clazz.getFields();

		final T dto = clazz.newInstance();
		for (final Field field : fields) {
			if ("bundle".equals(field.getName())) {
				field.set(dto, new Long(getBundleIdFromPath(data.getString("bundle"))));
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

	private static long getBundleIdFromPath(final String path) {
		final int pos = path.lastIndexOf('/') + 1;
		return Long.parseLong(path.substring(pos));
	}

	private static long[] getBundleIdsFromPaths(JSONArray array)
			throws JSONException {
		final long[] result = new long[array.length()];

		for (int i = 0; i < array.length(); i++) {
			result[i] = getBundleIdFromPath(array.getString(i));
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> getMapfromJsonObject(final JSONObject obj)
			throws JSONException {
		final Map<K, V> result = new HashMap<K, V>();

		final String[] keys = JSONObject.getNames(obj);

		for (int i = 0; i < keys.length; i++) {
			result.put((K) keys[i], (V) obj.get(keys[i]));
		}

		return result;
	}

	public static <T extends DTO> Collection<T> getDTOs(final Class<T> clazz,
			final JSONArray array) throws Exception {
		final Collection<T> result = new ArrayList<T>();
		for (int i = 0; i < array.length(); i++) {
			result.add(DTOReflector.getDTO(clazz, array.getJSONObject(i)));
		}
		return result;
	}

	public static <T extends DTO> JSONObject getJson(final Class<T> clazz,
			final T dto) throws Exception {

		final Field[] fields = clazz.getFields();

		final JSONObject obj = new JSONObject();
		for (final Field field : fields) {
			obj.put(field.getName(), field.get(dto));
		}

		return obj;
	}

}
