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
package org.osgi.test.cases.framework.secure.junit.adaptions.export;

import static junit.framework.TestCase.*;

import org.osgi.framework.Bundle;

import junit.framework.AssertionFailedError;

public class AdaptTestService<T> {
	private final Class<T> serviceClazz;
	private final boolean hasPermission;
	private final Bundle adaptBundle;
	private volatile AssertionFailedError error = new AssertionFailedError("Service not called!");
	public AdaptTestService(Class<T> serviceClazz, boolean hasPermission,
			Bundle adaptBundle) {
		this.serviceClazz = serviceClazz;
		this.hasPermission = hasPermission;
		this.adaptBundle = adaptBundle;
	}

	public AssertionFailedError getError() {
		return error;
	}

	public void doTest() {
		try {
			doTest0();
		} catch (AssertionFailedError e) {
			error = e;
		}
	}

	private void doTest0() {
		error = null;
		try {
			T adapted = adaptBundle.adapt(serviceClazz);
			assertTrue(
					"Expected a security exception adapting: " + serviceClazz,
					hasPermission);
			assertNotNull("Adapted type is null: " + serviceClazz.getName(),
					adapted);
			assertTrue("Wrong adapted type: " + adapted.getClass(),
					serviceClazz.isAssignableFrom(adapted.getClass()));
		} catch (SecurityException e) {
			assertFalse(
					"Expected a security exception adapting: " + serviceClazz,
					hasPermission);
		}
	}
}
