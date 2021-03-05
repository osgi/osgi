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

package org.osgi.test.cases.framework.div.tb17;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.test.cases.div.tb2.NativeCode;

/**
 * Bundle for the NativeCode from a fragment bundle test. This host bundle contains a
 * fragment bundle that in his turn contains a native library. The bundle should be
 * able to load the native code library.
 * 
 * @author Jorge Mascena
 */
public class NativeCodeFragment implements BundleActivator {
	/**
	 * Starts the bundle. Excercises the native code. The bundle should load the
	 * native library from its fragment bundle.
	 *  
	 * @param bc the context where the bundle is executed.
	 */
	public void start(BundleContext bc) throws BundleException {
		NativeCode.test();
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
