/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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
package org.osgi.test.cases.framework.secure.weaving.tbx;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;

public class Activator implements BundleActivator {
	static final String TESTCLASS = "org.osgi.test.cases.framework.secure.weaving.tbx.TestWeave";
	public void start(final BundleContext context) throws Exception {
		final AssertionFailedError[] error = new AssertionFailedError[1];
		final boolean[] called = {false};
		context.registerService(WeavingHook.class, new WeavingHook() {
			public void weave(WovenClass wovenClass) {
				if (wovenClass.getBundleWiring().getBundle() != context.getBundle())
					return;
				called[0] = true;
				try {
					try {
						wovenClass.getBytes();
						TestCase.fail("Expected security exception on getBytes");
					} catch (SecurityException e) {
						// expected
					}
					try {
						wovenClass.setBytes(new byte[] {1, 2 ,3});
						wovenClass.setBytes(getBytes(wovenClass.getBundleWiring().getBundle(), TESTCLASS)); // set back to good bytes
						TestCase.fail("Expected security exception on setBytes");
					} catch (SecurityException e) {
						// expected
					}

					List dynamicImports = wovenClass.getDynamicImports();
					TestCase.assertNotNull("dynamic imports is null.", dynamicImports);
					try {
						dynamicImports.add("test.addition");
						TestCase.fail("Expected security exception on dynamic imports modification");
					} catch (SecurityException e) {
						// expected
					}
				} catch (AssertionFailedError failed) {
					error[0] = failed;
				} catch (Throwable t) {
					error[0] = new AssertionFailedError("Unexpected error.");
					error[0].initCause(t);
				}
			}
		}, null);
		Class.forName(TESTCLASS);
		TestCase.assertTrue("Failed to call weaving hook", called[0]);
		if (error[0] != null)
			throw error[0];
	}

	public void stop(BundleContext context) throws Exception {
	}

	byte[] getBytes(Bundle source, String testName) {
		try {
			String testClassPath = testName.replace('.', '/') + ".class";
			URL url = source.getEntry(testClassPath);
			TestCase.assertNotNull("Failed to find class resource: " + testClassPath, url);
			InputStream in = url.openStream();
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = in.read(buffer)) > 0) {
				bytes.write(buffer, 0, count);
			}
			in.close();
			return bytes.toByteArray();
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable t) {
			AssertionFailedError error = new AssertionFailedError("Unexpected error.");
			error.initCause(t);
			throw error;
		}
	}
}
