package org.osgi.impl.service.resourcemanagement.persistency;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.impl.service.resourcemanagement.persistency.json.JSONArray;
import org.osgi.impl.service.resourcemanagement.persistency.json.JSONList;
import org.osgi.impl.service.resourcemanagement.persistency.json.JSONLong;
import org.osgi.impl.service.resourcemanagement.persistency.json.JSONObject;
import org.osgi.impl.service.resourcemanagement.persistency.json.JSONString;
import org.osgi.service.resourcemanagement.ResourceContext;

public class PersistenceImpl implements Persistence {

	public static final String PERSISTENT_FILE = "context.json";

	private static final String RESOURCE_CONTEXT_NAME_PARAMETER = "name";
	private static final String RESOURCE_CONTEXT_BUNDLE_IDS_PARAMETER = "bundle.ids";
	private static final String RESOURCE_CONTEXT_MONITORS = "monitors";
	private static final String RESOURCE_MONITOR_TYPE = "monitor.type";
	private static final String RESOURCE_MONITOR_ENABLED = "monitor.enabled";
	
	

	/**
	 * Persist the provided list of resource context as a JSON file.
	 */
	public void persist(BundleContext context,
			ResourceContext[] resourceContexts) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < resourceContexts.length; i++) {
			ResourceContext resourceContext = resourceContexts[i];
			String resourceContextToJson = resourceContextToJson(resourceContext);
			sb.append(resourceContextToJson);
			if (i != resourceContexts.length - 1) {
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

	public ResourceContextInfo[] load(BundleContext context) {
		// open context.json file
		String json = null;
		json = readPersistentFile(context);

		ResourceContextInfo[] resourceContexts = null;
		if (json != null) {

			// remove carriage return character
			json = json.replaceAll("\n", "");

			JSONObject jsonObject = JSONObject.parseJsonObject(json);
			JSONArray array = (JSONArray) jsonObject;
			List<JSONObject> resourceContextsAsJsonList = array.getElements();
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
				Set<Long> bundles = new HashSet<Long>();
				JSONArray bundleIdsJsonArray = (JSONArray) resourceContextJsonList
						.getElements().get(
								RESOURCE_CONTEXT_BUNDLE_IDS_PARAMETER);
				if (bundleIdsJsonArray != null) {
					for (Iterator<JSONObject> bundleIdsIt = bundleIdsJsonArray
							.getElements().iterator(); bundleIdsIt.hasNext();) {
						JSONLong bundleIdJsonLong = (JSONLong) bundleIdsIt
								.next();
						bundles.add(bundleIdJsonLong.getValue());
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
		try {
			FileInputStream fis = new FileInputStream(file);
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
	 * @param resourceContext
	 *            resource context to be transformed into json string.
	 * @return a json string {"name":"contextname","bundle.ids":[id1,id2]}
	 */
	private static String resourceContextToJson(ResourceContext resourceContext) {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		// name
		sb.append("\"");
		sb.append(RESOURCE_CONTEXT_NAME_PARAMETER);
		sb.append("\":\"");
		sb.append(resourceContext.getName());
		sb.append("\",");

		// bundle ids
		sb.append("\"");
		sb.append(RESOURCE_CONTEXT_BUNDLE_IDS_PARAMETER);
		sb.append("\":[");
		long[] bundleIds = resourceContext.getBundleIds();
		for (int i = 0; i < bundleIds.length; i++) {
			if (i != 0) {
				sb.append(",");
			}
			long bundleId = bundleIds[i];
			sb.append(bundleId);
		}
		sb.append("]");
		sb.append("}");

		return sb.toString();
	}

}
