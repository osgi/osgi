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
package org.osgi.test.cases.div.tb2; // could not rename this package without rebuilding native so/dll

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * Bundle for the NativeCode test.
 * 
 * @author Ericsson Radio Systems AB
 */
public class NativeCode implements BundleActivator {
	static boolean	initOk	= false;
	/**
	 * Static initializer to load the native library.
	 */
	static {
		try {
			System.loadLibrary("Native");
			initOk = true;
		}
		catch (UnsatisfiedLinkError ule) {
			// initOk will remain false.
		}
	}

	/**
	 * The native method.
	 */
	public native void count(int i);

	/**
	 * Starts the bundle. Excercises the native code.
	 */
	public void start(BundleContext bc) throws BundleException {
		test();
	}

	public static void test() {
		NativeCode n = new NativeCode();
		if (!initOk) {
			throw new UnsatisfiedLinkError("Native code not initialized.");
		}
		n.count(10000000);
	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
		// empty
	}
}
