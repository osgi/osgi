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

package org.osgi.impl.service.resourcemonitoring.persistency;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.impl.service.resourcemonitoring.persistency.json.JSONArray;
import org.osgi.impl.service.resourcemonitoring.persistency.json.JSONList;
import org.osgi.impl.service.resourcemonitoring.persistency.json.JSONLong;
import org.osgi.impl.service.resourcemonitoring.persistency.json.JSONObject;
import org.osgi.impl.service.resourcemonitoring.persistency.json.JSONString;

/**
 *
 */
public class PersistenceImpl implements Persistence {

	/** PERSISTENT_FILE */
	public static final String	PERSISTENT_FILE							= "context.json";

	private static final String	RESOURCE_CONTEXT_NAME_PARAMETER			= "name";
	private static final String	RESOURCE_CONTEXT_BUNDLE_IDS_PARAMETER	= "bundle.ids";

	// private static final String RESOURCE_CONTEXT_MONITORS = "monitors";
	// private static final String RESOURCE_MONITOR_TYPE = "monitor.type";
	// private static final String RESOURCE_MONITOR_ENABLED = "monitor.enabled";

	/**
	 * Persist the provided list of resource context as a JSON file.
	 */
	@Override
	public void persist(BundleContext context,
			ResourceContextInfo[] resourceContextInfos) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < resourceContextInfos.length; i++) {
			ResourceContextInfo resourceContextInfo = resourceContextInfos[i];
			String resourceContextToJson = resourceContextToJson(resourceContextInfo);
			sb.append(resourceContextToJson);
			if (i != resourceContextInfos.length - 1) {
				sb.append(",");
			}
		}

		sb.append("]");

		File file = context.getDataFile(PERSISTENT_FILE);
		if (file != null) {
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(file);
				fos.write(sb.toString().getBytes());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	public ResourceContextInfo[] load(BundleContext context) {
		// open context.json file
		String json = null;
		json = readPersistentFile(context);

		ResourceContextInfo[] resourceContexts = null;
		if (json != null) {

			// remove carriage return character
			// json = json.replaceAll("\n", "");

			JSONObject jsonObject = JSONObject.parseJsonObject(json);
			JSONArray array = (JSONArray) jsonObject;
			List<JSONObject> resourceContextsAsJsonList = array
					.getElements();
			resourceContexts = new ResourceContextInfo[resourceContextsAsJsonList
					.size()];
			int i = 0;
			for (Iterator<JSONObject> it = resourceContextsAsJsonList
					.iterator(); it.hasNext();) {
				// each element is a Resource Context as a JSON list
				JSONList resourceContextJsonList = (JSONList) it.next();

				// retrieve resource context name
				String resourceContextName = ((JSONString) resourceContextJsonList
						.getElements().get(RESOURCE_CONTEXT_NAME_PARAMETER))
						.getValue();

				// retrieve list of bundles associated to the context
				List<Long> bundles = new ArrayList<>();
				JSONArray bundleIdsJsonArray = (JSONArray) resourceContextJsonList
						.getElements().get(
								RESOURCE_CONTEXT_BUNDLE_IDS_PARAMETER);
				if (bundleIdsJsonArray != null) {
					for (Iterator<JSONObject> bundleIdsIt = bundleIdsJsonArray
							.getElements().iterator(); bundleIdsIt.hasNext();) {
						JSONLong bundleIdJsonLong = (JSONLong) bundleIdsIt
								.next();
						bundles.add(Long.valueOf(Long.toString(bundleIdJsonLong.getValue())));
					}
				}

				ResourceContextInfo resourceContext = new ResourceContextInfo(
						resourceContextName, bundles);
				resourceContexts[i++] = resourceContext;
			}

		} else {
			resourceContexts = new ResourceContextInfo[0];
		}

		return resourceContexts;
	}

	private String readPersistentFile(BundleContext context) {
		String json = null;
		File file = context.getDataFile(PERSISTENT_FILE);
		if (!file.exists()) {
			return null;
		}
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] fileContent = new byte[(int) file.length()];
			fis.read(fileContent);
			json = new String(fileContent);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * Get a json string representing the provided resource context
	 * 
	 * @param resourceContextInfo resource context to be transformed into json
	 *        string.
	 * @return a json string {"name":"contextname","bundle.ids":[id1,id2]}
	 */
	private static String resourceContextToJson(
			ResourceContextInfo resourceContextInfo) {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		// name
		sb.append("\"");
		sb.append(RESOURCE_CONTEXT_NAME_PARAMETER);
		sb.append("\":\"");
		sb.append(resourceContextInfo.getName());
		sb.append("\",");

		// bundle ids
		sb.append("\"");
		sb.append(RESOURCE_CONTEXT_BUNDLE_IDS_PARAMETER);
		sb.append("\":[");
		List<Long> bundleIds = resourceContextInfo.getBundleIds();
		boolean notFirst = false;
		for (Iterator<Long> it = bundleIds.iterator(); it.hasNext();) {
			if (notFirst) {
				sb.append(",");
			} else {
				notFirst = true;
			}
			Long bundleId = it.next();
			sb.append(bundleId);
		}
		sb.append("]");
		sb.append("}");

		return sb.toString();
	}

}
