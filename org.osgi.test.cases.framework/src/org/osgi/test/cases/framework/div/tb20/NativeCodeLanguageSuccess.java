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

package org.osgi.test.cases.framework.div.tb20;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.test.cases.div.tb2.NativeCode;

/**
 * Bundle for native code os language test. This bundle references all valid languages
 * in the native code clauses, so it should be successfully loaded.
 * 
 * @author Jorge Mascena
 */
public class NativeCodeLanguageSuccess implements BundleActivator {
	/**
	 * Starts the bundle. Exercises the native code.
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
