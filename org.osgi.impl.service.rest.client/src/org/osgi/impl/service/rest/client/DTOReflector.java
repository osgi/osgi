/*
 *	Licensed Materials - Property of IBM.
 *	(C) Copyright IBM Corporation 2013
 *	All Rights Reserved.
 *
 *	US Government Users Restricted Rights -
 *	Use, duplication or disclosure restricted by
 *	GSA ADP Schedule Contract with IBM Corporation.
 *
 *  Created by Jan S. Rellermeyer
 *  Copyright 2013 ibm.com. All rights reserved.
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

public final class DTOReflector {

	public static <T extends DTO> T getDTO(final Class<T> clazz,
			final JSONObject data) throws Exception {
		final Field[] fields = clazz.getFields();

		final T dto = clazz.newInstance();
		for (final Field field : fields) {
			if ("bundle".equals(field.getName())) {
				field.set(dto, getBundleIdFromPath(data.getString("bundle")));
			} else if ("usingBundles".equals(field.getName())) {
				field.set(
						dto,
						getBundleIdsFromPaths(data.getJSONArray("usingBundles")));
				// } else if (field.getType().equals(MapDTO.class)) {
				// field.set(dto,
				// getMapDTOfromJsonObject(data.getJSONObject(field
				// .getName())));
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
