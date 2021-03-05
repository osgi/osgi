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

package org.osgi.test.cases.metatype.tb2;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.metatype.MetaTypeProvider;

/**
 * 
 * This bundle activator will register a managed service that implements the
 * interface MetaTypeProvider
 * 
 * @author left
 * @author $Id$
 */
public class Activator implements BundleActivator {

	private ServiceRegistration< ? > sr;

	/**
	 * Creates a new instance of Activator
	 */
	public Activator() {
		// empty
	}

	/**
	 * Start the bundle registering a service that implements the interface
	 * MetaTypeProvider
	 * 
	 * @param context The execution context of the bundle being started.
	 */
	public void start(BundleContext context) {
		Hashtable<String,Object> properties;

		properties = new Hashtable<>();
		properties.put(Constants.SERVICE_PID,
				"org.osgi.test.cases.metatype.ocd1");

		sr = context.registerService(new String[] {
				ManagedService.class.getName(),
				MetaTypeProvider.class.getName()}, new MetaTypeProviderImpl(),
				properties);
	}

	/**
	 * Unregister the service
	 * 
	 * @param context The execution context of the bundle being stopped.
	 */
	public void stop(BundleContext context) {
		sr.unregister();

		sr = null;
	}

}
