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
package org.osgi.test.cases.remoteserviceadmin.common;

import static junit.framework.TestCase.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.osgi.framework.BundleContext;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.test.support.OSGiTestCase;

public class Utils {

	/**
	 * Magic value. Properties with this value will be replaced by a socket port
	 * number that is currently free.
	 */
	private static final String FREE_PORT = "@@FREE_PORT@@";

	/**
	 * @param scopeobj
	 * @param description
	 * @return
	 * 
	 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
	 */
	public static String isInterested(Object scopeobj,
			EndpointDescription description) {
		if (scopeobj instanceof List<?>) {
			@SuppressWarnings("unchecked")
			List<String> scope = (List<String>) scopeobj;
			for (Iterator<String> it = scope.iterator(); it.hasNext();) {
				String filter = it.next();

				if (description.matches(filter)) {
					return filter;
				}
			}
		} else if (scopeobj instanceof String[]) {
			String[] scope = (String[]) scopeobj;
			for (String filter : scope) {
				if (description.matches(filter)) {
					return filter;
				}
			}
		} else if (scopeobj instanceof String) {
			StringTokenizer st = new StringTokenizer((String) scopeobj, " ");
			for (; st.hasMoreTokens();) {
				String filter = st.nextToken();
				if (description.matches(filter)) {
					return filter;
				}
			}
		}
		return null;
	}
	
	/**
	 * @param testCase
	 * @return EndpointDescription reconstructed from a HEX string passed by the
	 *         exporting bundle in the child framework
	 * @throws IOException
	 */
	public static EndpointDescription reconstructEndpoint(String version,
			OSGiTestCase testCase) throws IOException {
		return reconstructEndpoint(version, testCase, 0);
	}

	/**
	 * @param testCase
	 * @param registrationNumber
	 *            use the nth export statement
	 * @return EndpointDescription reconstructed from a HEX string passed by the
	 *         exporting bundle in the child framework
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static EndpointDescription reconstructEndpoint(String version,
			OSGiTestCase testCase, int registrationNumber) throws IOException {
		String propstr = testCase.getProperty("RSA_TCK.EndpointDescription_"
				+ version + "_" + registrationNumber);

		// see
		// org.osgi.test.cases.remoteserviceadmin.tb2.Activator#exportEndpointDescription()
		// decode byte[] from hex
		byte[] ba = new byte[propstr.length() / 2];

		for (int x = 0; x < ba.length; ++x) {
			int sp = x * 2;
			int a = Integer.parseInt("" + propstr.charAt(sp), 16);
			int b = Integer.parseInt("" + propstr.charAt(sp + 1), 16);
			ba[x] = (byte) (a * 16 + b);
		}

		ByteArrayInputStream bis = new ByteArrayInputStream(ba);
		ObjectInputStream ois = new ObjectInputStream(bis);

		Map<String, Object> props = null;
		try {
			props = (Map<String, Object>) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		assert (props != null);

		return new EndpointDescription(props);
	}
	
	/**
	 * Write the contents of the EndpointDescription into System properties for
	 * the parent framework to read and then import.
	 * 
	 * @param ed
	 * @throws IOException
	 */
	public static void exportEndpointDescription(EndpointDescription ed,
			String version, int curentRegistrationCounterValue)
			throws IOException {
		// Marc Schaaf: I switched to Java serialization to support String[] and
		// lists as
		// EndpointDescription Properties. The Byte Array is encoded as a HEX
		// string to save
		// it as a system property

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);

		Map<String, Object> props = new HashMap<String, Object>();
		for (Iterator<String> it = ed.getProperties().keySet().iterator(); it
				.hasNext();) {
			String key = it.next();
			props.put(key, ed.getProperties().get(key));
		}

		oos.writeObject(props);

		// encode byte[] as hex
		byte[] ba = bos.toByteArray();
		String out = "";
		for (int x = 0; x < ba.length; ++x) {
			out += Integer.toString((ba[x] & 0xff) + 0x100, 16).substring(1);
		}

		System.getProperties().put(
				"RSA_TCK.EndpointDescription_" + version + "_"
						+ curentRegistrationCounterValue, out);

	}

	public static void processFreePortProperties(Map<String, Object> properties) {
		String freePort = getFreePort();
		for (Iterator<Entry<String,Object>> it = properties.entrySet()
				.iterator(); it.hasNext();) {
			Entry<String,Object> entry = it.next();
			if (entry.getValue().toString().trim().equals(FREE_PORT)) {
				entry.setValue(freePort);
			}
		}
	}

	private static String getFreePort() {
		try {
			ServerSocket ss = new ServerSocket(0);
			String port = "" + ss.getLocalPort();
			ss.close();
			return port;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return
	 */
	public static Map<String, Object> loadServerTCKProperties(
			BundleContext context) {
		String serverconfig = context
				.getProperty("org.osgi.test.cases.remoteserviceadmin.serverconfig");
		assertNotNull(
				"did not find org.osgi.test.cases.remoteserviceadmin.serverconfig system property",
				serverconfig);
		Map<String, Object> properties = new HashMap<String, Object>();

		for (StringTokenizer tok = new StringTokenizer(serverconfig, ","); tok
				.hasMoreTokens();) {
			String propertyName = tok.nextToken();
			String value = context.getProperty(propertyName);
			assertNotNull("system property not found: " + propertyName,
					value);
			properties.put(propertyName, value);
		}

		return properties;
	}


}
