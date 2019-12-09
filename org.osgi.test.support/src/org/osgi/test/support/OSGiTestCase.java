/*
 * Copyright (c) OSGi Alliance (2009, 2014). All Rights Reserved.
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

package org.osgi.test.support;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Comparator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.test.support.sleep.Sleep;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public abstract class OSGiTestCase extends TestCase {
	private volatile BundleContext context;

	/**
	 * This method is called by the JUnit runner for OSGi, and gives us a Bundle
	 * Context.
	 */
	public void setBundleContext(BundleContext context) {
		this.context = context;
	}

	/**
	 * Returns the current Bundle Context
	 */
	public BundleContext getContext() {
		BundleContext c = context;
		if (c == null) {
			Bundle b = FrameworkUtil.getBundle(getClass());
			if ( b != null )
				return context = b.getBundleContext();

			fail("No Bundle context set, are you running in OSGi Test mode?");
		}
		return c;
	}

	/**
	 * Fail with cause t.
	 *
	 * @param message
	 *            Failure message.
	 * @param t
	 *            Cause of the failure.
	 */
	public static void fail(String message, Throwable t) {
		AssertionFailedError e = new AssertionFailedError(message + ": "
				+ t.getMessage());
		e.initCause(t);
		throw e;
	}

	/**
	 * Assert a constant from class has a specific value.
	 *
	 * @param expected
	 *            Expected value.
	 * @param fieldName
	 *            Constant field name.
	 * @param fieldClass
	 *            Class containing constant.
	 */
	public static void assertConstant(Object expected, String fieldName,
			Class<?> fieldClass) {
		try {
			Field f = fieldClass.getField(fieldName);
			assertTrue(Modifier.isPublic(f.getModifiers()));
			assertTrue(Modifier.isStatic(f.getModifiers()));
			assertTrue(Modifier.isFinal(f.getModifiers()));
			assertEquals(fieldName, expected, f.get(null));
		} catch (NoSuchFieldException e) {
			fail("missing field: " + fieldName, e);
		} catch (IllegalAccessException e) {
			fail("bad field: " + fieldName, e);
		}
	}

	public static <T> void assertEquals(String message,
			Comparator<T> comparator, List<T> expected, List<T> actual) {
		if (expected.size() != actual.size()) {
			failNotEquals(message, expected, actual);
		}

		for (int i = 0, l = expected.size(); i < l; i++) {
			assertEquals(message, comparator, expected.get(i), actual.get(i));
		}
	}

	public static <T> void assertEquals(String message,
			Comparator<T> comparator, T expected, T actual) {
		if (comparator.compare(expected, actual) == 0) {
			return;
		}
		failNotEquals(message, expected, actual);
	}

	/**
	 * Installs a resource within the test bundle as a bundle
	 *
	 * @param bundle
	 *            a path to an entry that contains a bundle to install
	 * @return The installed bundle
	 * @throws BundleException
	 *             if an error occurred while installing the bundle
	 * @throws IOException
	 *             if an error occurred while reading the bundle content
	 */
	public Bundle install(String bundle) throws BundleException, IOException {
		return getContext().installBundle(bundle, entryStream(bundle));
	}

	/**
	 * Get InputStream to an entry within the test bundle
	 * 
	 * @param entry a path to an entry
	 * @return An InputStream to the entry
	 * @throws IOException if an error occurred while reading the entry content
	 */
	public InputStream entryStream(String entry) throws IOException {
		URL url = getContext().getBundle().getEntry(entry);
		assertNotNull("Can not find resource: " + entry, url);
		return url.openStream();
	}

	/**
	 * Sleep for the requested amount of milliseconds.
	 * 
	 * @param timeout
	 */
	public static void sleep(long timeout) {
		try {
			Sleep.sleep(timeout);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			fail("Unexpected interruption.", e);
		}
	}

	public static <T> T doPrivileged(PrivilegedAction< ? extends T> action) {
		return AccessController.doPrivileged(action);
	}

	public static <T> T doPrivilegedException(
			PrivilegedExceptionAction< ? extends T> action) throws Exception {
		try {
			return AccessController.doPrivileged(action);
		} catch (PrivilegedActionException e) {
			throw e.getException();
		}
	}

	/**
	 * Return the property value from the bundle context properties.
	 * 
	 * @param key The property key name.
	 * @return The property value or null if the property is not set.
	 */
	public String getProperty(String key) {
		return doPrivileged(() -> {
			if (context != null) {
				return context.getProperty(key);
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
	public String getProperty(String key, String defaultValue) {
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
	public boolean getBooleanProperty(String key, boolean defaultValue) {
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
	public int getIntegerProperty(String key, int defaultValue) {
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
	public long getLongProperty(String key, long defaultValue) {
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
