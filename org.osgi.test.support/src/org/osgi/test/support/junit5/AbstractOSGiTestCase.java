/*
 * Copyright (c) OSGi Alliance (2009, 2020). All Rights Reserved.
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

package org.osgi.test.support.junit5;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.support.sleep.Sleep;

/**
 * JUnit 5 base test case class.
 */
@ExtendWith(BundleContextExtension.class)
public abstract class AbstractOSGiTestCase {
	public static class TestName {
		private final TestInfo testInfo;

		TestName(TestInfo testInfo) {
			this.testInfo = testInfo;
		}

		public String getMethodName() {
			return testInfo.getTestMethod().map(Method::getName).get();
		}
	}

	public TestName	testName;

	@InjectBundleContext
	BundleContext	context;

	@BeforeEach
	void setUp(TestInfo testInfo) throws Exception {
		testName = new TestName(testInfo);
	}
	/**
	 * Returns the current Bundle Context
	 */
	public BundleContext getContext() {
		BundleContext c = context;
		if (c != null) {
			return c;
		}
		Bundle b = FrameworkUtil.getBundle(getClass());
		assertThat(b)
				.as("Bundle not found using FrameworkUtil.getBundle(\"%s\")",
						getClass())
				.isNotNull();
		return context = b.getBundleContext();
	}

	/**
	 * Installs a resource within the test bundle as a bundle
	 *
	 * @param bundle a path to an entry that contains a bundle to install
	 * @return The installed bundle
	 * @throws BundleException if an error occurred while installing the bundle
	 * @throws IOException if an error occurred while reading the bundle content
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
		assertThat(url).as("Cannot find resource %s in bundle %s", entry,
				getContext().getBundle()).isNotNull();
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
		} catch (InterruptedException e) {
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
	 *            is not set.
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
	 *            is not set.
	 * @return The property value or defaultValue if the property is not set.
	 */
	public boolean getBooleanProperty(String key, boolean defaultValue) {
		String propValue = getProperty(key);
		if (propValue == null) {
			return defaultValue;
		}
		return Boolean.parseBoolean(propValue);
	}

	/**
	 * Return the integer property value from the bundle context properties.
	 * 
	 * @param key The property key name.
	 * @param defaultValue The default property value to return if the property
	 *            is not set.
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
	 *            is not set.
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
