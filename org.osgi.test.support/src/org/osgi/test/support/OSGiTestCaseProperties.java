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


package org.osgi.test.support;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class OSGiTestCaseProperties {
	private OSGiTestCaseProperties() {
		// empty
	}

	private static final long	timeout;
	private static final int	scaling; 
	
	static {
		long t = 60000L;
		try {
			t = getLongProperty("org.osgi.test.testcase.timeout", t);
			if (t < 0) {
				t = 0;
			}
		} catch (SecurityException e) {
			// use default value
		}

		int s = 1;
		try {
			s = getIntegerProperty("org.osgi.test.testcase.scaling", s);
			if (s < 0) {
				s = 0;
			}
		} catch (SecurityException e) {
			// use default value
		}

		timeout = t;
		scaling = s;

	}

	public static long getTimeout() {
		return timeout;
	}

	public static int getScaling() {
		return scaling;
	}

	/**
	 * Return the property value from the bundle context properties.
	 * 
	 * @param key The property key name.
	 * @return The property value or null if the property is not set.
	 */
	public static String getProperty(String key) {
		return AccessController.doPrivileged((PrivilegedAction<String>) () -> {
			Bundle bundle = FrameworkUtil
					.getBundle(OSGiTestCaseProperties.class);
			if (bundle != null) {
				BundleContext context;
				try {
					context = bundle.getBundleContext();
				} catch (SecurityException e) {
					context = null;
				}
				if (context != null) {
					return context.getProperty(key);
				}
			}
			return System.getProperty(key);
		});
	}

	/**
	 * Return the property value from the bundle context properties.
	 * 
	 * @param key The property key name.
	 * @param defaultValue The default property value to return if the property
	 *        is not set.
	 * @return The property value or defaultValue if the property is not set.
	 */
	public static String getProperty(String key, String defaultValue) {
		String propValue = getProperty(key);
		if (propValue == null) {
			return defaultValue;
		}
		return propValue;
	}

	/**
	 * Return the boolean property value from the bundle context properties.
	 * 
	 * @param key The property key name.
	 * @param defaultValue The default property value to return if the property
	 *        is not set.
	 * @return The property value or defaultValue if the property is not set.
	 */
	public static boolean getBooleanProperty(String key, boolean defaultValue) {
		String propValue = getProperty(key);
		if (propValue == null) {
			return defaultValue;
		}
		return Boolean.valueOf(propValue).booleanValue();
	}

	/**
	 * Return the integer property value from the bundle context properties.
	 * 
	 * @param key The property key name.
	 * @param defaultValue The default property value to return if the property
	 *        is not set.
	 * @return The property value or defaultValue if the property is not set.
	 */
	public static int getIntegerProperty(String key, int defaultValue) {
		String propValue = getProperty(key);
		if (propValue == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(propValue);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Return the long property value from the bundle context properties.
	 * 
	 * @param key The property key name.
	 * @param defaultValue The default property value to return if the property
	 *        is not set.
	 * @return The property value or defaultValue if the property is not set.
	 */
	public static long getLongProperty(String key, long defaultValue) {
		String propValue = getProperty(key);
		if (propValue == null) {
			return defaultValue;
		}
		try {
			return Long.parseLong(propValue);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}
