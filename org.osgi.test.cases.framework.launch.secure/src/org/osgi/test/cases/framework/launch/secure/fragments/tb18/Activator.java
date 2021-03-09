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

package org.osgi.test.cases.framework.launch.secure.fragments.tb18;

import java.io.InputStream;
import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

/**
 * 
 * Bundle for Extension Bundles tests. Invoker with
 * AdminPermission[<bundle>, EXTENSIONLIFECYCLE] should be able to
 * install extension bundles.
 * 
 * @author jorge.mascena@cesar.org.br
 * 
 * @author $Id$
 */
public class Activator implements BundleActivator{

	/**
	 * Starts Bundle. Tries to install an extension bundle.
	 * @param context
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */

	public void start(BundleContext context) throws Exception {
		Collection<ServiceReference<InputStream>> bundleRefs;
		bundleRefs = context.getServiceReferences(InputStream.class,
				"(bundle=fragments.tb16b.jar)");
		if (bundleRefs.isEmpty())
			throw new BundleException(
					"fragments.tb16b.jar bundle inputstream unavailable");
		InputStream in = context.getService(bundleRefs.iterator().next());
		// Install extension bundle
		Bundle tb16b = context.installBundle("fragments.tb16b.jar", in);
		tb16b.uninstall();
	}

	/**
	 * Stops Bundle. Nothing to be done here.
	 * 
	 * @param context
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) {
		// empty
	}

}
