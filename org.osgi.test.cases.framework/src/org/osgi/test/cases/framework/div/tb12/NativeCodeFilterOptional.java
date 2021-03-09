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

package org.osgi.test.cases.framework.div.tb12;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.test.cases.div.tb2.NativeCode;

/**
 * Bundle for the NativeCode optional clause test. This bundle has an optional
 * clause present to make sure it will be loaded even if no other native code
 * clause matches. The clauses were built to intentionally NOT match in order to
 * check if the bundle is loaded.
 * 
 * @author Jorge Mascena
 */
public class NativeCodeFilterOptional implements BundleActivator {
	/**
	 * Starts the bundle. Excercises the native code. The
	 * <CODE>org.osgi.test.cases.div.tb2.NativeCode.test()</CODE> call
	 * should throw a BundleException since no native code clause should match.
	 *  
	 * @param bc the context where the bundle is executed.
	 */
	public void start(BundleContext bc) throws BundleException {
		try {
			NativeCode.test();
		}
		catch (UnsatisfiedLinkError e) {
			// there should be no match
			return;
		}
		// if started ok, then there was a match
		throw new BundleException("No native code clause should match");
	}

	/**
	 * Stops the bundle.
	 * 
	 * @param bc the context where the bundle is executed.
	 */
	public void stop(BundleContext bc) {
		// empty
	}
}
