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

package org.osgi.test.cases.framework.div.tb21;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.test.cases.div.tb2.NativeCode;

/**
 * Bundle for native code os version test. This bundle should not be
 * loaded since the version ranges in the native code clauses should
 * not match any valid os version.
 * 
 * @author Jorge Mascena
 */
public class NativeCodeVersion implements BundleActivator {
	/**
	 * Starts the bundle. This method should not be invoked since the
	 * version range in the native code clauses should not match.
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
