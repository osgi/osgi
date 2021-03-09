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

package org.osgi.test.cases.framework.div.tb15;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * Bundle for the NativeCode optional clause test. This bundle has no optional
 * clause present so the bundle should NOT be loaded if no other native code
 * clause matches. The clauses were built to intentionally NOT match in order to
 * check if the bundle is not loaded.
 * 
 * @author Jorge Mascena
 */
public class NativeCodeFilterNoOptional implements BundleActivator {
	/**
	 * Starts the bundle. Excercises the native code. The bundle should not be
	 * loaded since no native code clause matches and no optional clause is
	 * present.
	 *  
	 * @param bc the context where the bundle is executed.
	 */
	public void start(BundleContext bc) throws BundleException {
		throw new BundleException("Bundle should not be resolved");
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
