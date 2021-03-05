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
package org.osgi.test.cases.framework.secure.syncbundlelistener.tb1b;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;

/**
 * Bundle for the CauseFrameworkEvent test.
 * 
 * @author Ericsson Telecom AB
 */
public class PermissionTester implements BundleActivator,
		SynchronousBundleListener {
	/**
	 * Starts the bundle.
	 */
	public void start(BundleContext context) {
		context.addBundleListener(this);
	}

	public void stop(BundleContext context) {
		// empty
	}

	public void bundleChanged(BundleEvent event) {
		// empty
	}
}
