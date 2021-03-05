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

package org.osgi.test.cases.framework.classloading.tb1b;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.framework.classloading.exports.service.SomeService;

/**
 * This bundle activator is used to register some services
 * 
 * @author left
 * @author $Id$
 */
public class Activator implements BundleActivator {

	private ServiceRegistration<SomeService> sr;

	/**
	 * Creates a new instance of Activator
	 */
	public Activator() {

	}

	/**
	 * Start the bundle and register the service.
	 * 
	 * @param context the bundle context
	 * @throws Exception if some problem occur
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		Dictionary<String,Object> props;

		props = new Hashtable<>();
		props.put("version", "1.0.0");

		sr = context.registerService(SomeService.class,
				new SomeServiceImpl(context.getBundle()), props);
	}

	/**
	 * Stop the bundle and unregister the service
	 * 
	 * @param context the bundle context
	 * @throws Exception if some problem occur
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		sr.unregister();
		sr = null;
	}
}
