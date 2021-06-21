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
package org.osgi.test.cases.framework.secure.weaving.tb.weaver;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.test.cases.framework.secure.junit.hooks.weaving.export.TestConstants;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class TestWeavingHook implements WeavingHook {
	private volatile boolean called;
	private volatile AssertionFailedError error;
	
	public void weave(WovenClass wovenClass) {
		if (!wovenClass.getClassName().equals(TestConstants.WOVEN_CLASS))
			return;
		called = true;
		SecurityException rethrow = null;
		try {
			try {
				wovenClass.getBytes();
				if (TestConstants.isExpectSecurityExceptionAll())
					TestCase.fail("Expected security exception on getBytes");
			} catch (SecurityException e) {
				if (!TestConstants.isExpectSecurityExceptionAll()) {
					e.printStackTrace();
					TestCase.fail("Did not expect security exception on getBytes");
				}
			}
			try {
				wovenClass.setBytes(new byte[] {1, 2 ,3});
				if (!TestConstants.isInvalidSetBytes())
					wovenClass.setBytes(getBytes(wovenClass.getBundleWiring().getBundle(), TestConstants.WOVEN_CLASS)); // set back to good bytes
				if (TestConstants.isExpectSecurityExceptionAll())
					TestCase.fail("Expected security exception on setBytes");
			} catch (SecurityException e) {
				if (!TestConstants.isExpectSecurityExceptionAll()) {
					e.printStackTrace();
					TestCase.fail("Did not expect security exception on setBytes");
				}
			}
			rethrow = testDynamicImports(wovenClass);
		} catch (AssertionFailedError failed) {
			error = failed;
		} catch (Throwable t) {
			error = new AssertionFailedError("Unexpected error.");
			error.initCause(t);
		}
		if (rethrow != null && TestConstants.isRethrowingSecurityException())
			throw rethrow;
	}
	
	private SecurityException testDynamicImports(WovenClass wovenClass) {
		SecurityException result = null;
		final List<String> dynamicImports = wovenClass.getDynamicImports();
		TestCase.assertNotNull("Dynamic imports is null", dynamicImports);
		result = testDynamicImports(new Runnable() {
			public void run() {
				dynamicImports.set(0, TestConstants.DYNAMIC_IMPORT_PACKAGE);
			}
		});
		clearDynamicImports(wovenClass);
		result = testDynamicImports(new Runnable() {
			public void run() {
				dynamicImports.add(TestConstants.DYNAMIC_IMPORT_PACKAGE);
			}
		});
		clearDynamicImports(wovenClass);
		result = testDynamicImports(new Runnable() {
			public void run() {
				dynamicImports.add(0, TestConstants.DYNAMIC_IMPORT_PACKAGE);
			}
		});
		clearDynamicImports(wovenClass);
		result = testDynamicImports(new Runnable() {
			public void run() {
				String[] pkgs = new String[] {TestConstants.DYNAMIC_IMPORT_PACKAGE};
				dynamicImports.addAll(Arrays.asList(pkgs));
			}
		});
		clearDynamicImports(wovenClass);
		result = testDynamicImports(new Runnable() {
			public void run() {
				String[] pkgs = new String[] {TestConstants.DYNAMIC_IMPORT_PACKAGE};
				dynamicImports.addAll(0, Arrays.asList(pkgs));
			}
		});
		return result;
	}
	
	private void clearDynamicImports(WovenClass wovenClass) {
		if (!TestConstants.isExpectSecurityExceptionAll())
			wovenClass.getDynamicImports().clear();
	}
	
	private SecurityException testDynamicImports(Runnable runnable) {
		try {
			runnable.run();
			if (TestConstants.isExpectSecurityExceptionAll() || TestConstants.isExpectSecurityExceptionAddDynamicImport())
				TestCase.fail("Expected security exception on dynamic imports modification");
		} 
		catch (SecurityException e) {
			if (!TestConstants.isExpectSecurityExceptionAll() && !TestConstants.isExpectSecurityExceptionAddDynamicImport())
				TestCase.fail("Did not expect security exception on dynamic imports modification");
			return e;
		}
		catch (UnsupportedOperationException e) {
			// All operations being tested are optional.
		}
		return null;
	}
	
	AssertionFailedError getError() {
		return error;
	}
	
	boolean isCalled() {
		return called;
	}
	
	private byte[] getBytes(Bundle source, String testName) {
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
