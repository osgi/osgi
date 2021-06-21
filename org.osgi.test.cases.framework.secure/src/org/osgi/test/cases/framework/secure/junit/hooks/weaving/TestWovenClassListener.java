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
package org.osgi.test.cases.framework.secure.junit.hooks.weaving;

import org.osgi.framework.PackagePermission;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.hooks.weaving.WovenClassListener;
import org.osgi.test.cases.framework.secure.junit.hooks.weaving.export.TestConstants;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class TestWovenClassListener implements WovenClassListener {
	private static final PackagePermission permission = new PackagePermission(TestConstants.DYNAMIC_IMPORT_PACKAGE, PackagePermission.IMPORT);
	
	private volatile boolean called;
	private volatile AssertionFailedError error;
	
	public void modified(WovenClass wovenClass) {
		if (!wovenClass.getClassName().equals(TestConstants.WOVEN_CLASS))
			return;
		called = true;
		try {
			switch (wovenClass.getState()) {
				case WovenClass.TRANSFORMED:
				case WovenClass.TRANSFORMING_FAILED:
				case WovenClass.DEFINED:
				case WovenClass.DEFINE_FAILED:
					assertDynamicImport(wovenClass);
					assertHasPermission(wovenClass);
					assertClassLoad(wovenClass);
			}
		} catch (AssertionFailedError failed) {
			error = failed;
		} catch (Throwable t) {
			error = new AssertionFailedError("Unexpected error.");
			error.initCause(t);
		}
	}
	
	AssertionFailedError getError() {
		return error;
	}
	
	boolean isCalled() {
		return called;
	}
	
	private void assertClassLoad(WovenClass wovenClass) {
		if (TestConstants.isExpectSecurityExceptionAll() || 
				TestConstants.isExpectSecurityExceptionAddDynamicImport() ||
				wovenClass.getState() == WovenClass.TRANSFORMED) {
			try {
				wovenClass.getBundleWiring().getBundle().loadClass(TestConstants.DYNAMIC_IMPORT_CLASS);
				TestCase.fail("The class should not have loaded");
			}
			catch (ClassNotFoundException e) {
				// Okay.
			}
		}
		else {
			try {
				wovenClass.getBundleWiring().getBundle().loadClass(TestConstants.DYNAMIC_IMPORT_CLASS);
			}
			catch (ClassNotFoundException e) {
				TestCase.fail("The class should have loaded");
			}
		}
	}
	
	private void assertDynamicImport(WovenClass wovenClass) {
		if (TestConstants.isExpectSecurityExceptionAll() || TestConstants.isExpectSecurityExceptionAddDynamicImport()) {
			if (wovenClass.getDynamicImports().contains(TestConstants.DYNAMIC_IMPORT_PACKAGE))
				TestCase.fail("The dynamic import was added");
		}
		else {
			if (!wovenClass.getDynamicImports().contains(TestConstants.DYNAMIC_IMPORT_PACKAGE))
				TestCase.fail("The dynamic import was not added");
		}
	}
	
	private void assertHasPermission(WovenClass wovenClass) {
		if (TestConstants.isExpectSecurityExceptionAll() || 
				TestConstants.isExpectSecurityExceptionAddDynamicImport() ||
				wovenClass.getState() == WovenClass.TRANSFORMED) {
			if (wovenClass.getBundleWiring().getBundle().hasPermission(permission))
				TestCase.fail("The woven bundle should not have package permission");
		}
		else {
			if (!wovenClass.getBundleWiring().getBundle().hasPermission(permission))
				TestCase.fail("The woven bundle should have package permission");
		}
	}
}
