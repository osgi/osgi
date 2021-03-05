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

package org.osgi.test.cases.framework.launch.fragments.tb20;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Bundle for Extension Bundles tests. This is a regular bundle (tb1) to
 * be compared to another bundle (tb4) that should be treated as a regular
 * bundle since its host symbolic name is not system.bundle.
 * 
 * @author jorge.mascena@cesar.org.br
 * 
 * @author $Id$
 */
public class Activator implements BundleActivator {

	/**
	 * Starts Bundle. Nothing to be done here.
	 * 
	 * @param context
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
	}

	/**
	 * Stops Bundle. Nothing to be done here.
	 * 
	 * @param context
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}
}
