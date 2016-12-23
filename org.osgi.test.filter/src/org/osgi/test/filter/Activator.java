/*
 * Copyright (c) OSGi Alliance (2014, 2016). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.filter;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.service.EventListenerHook;
import org.osgi.framework.hooks.service.FindHook;

/**
 * Install the Service Hooks if there is a configuration.
 */
public class Activator implements BundleActivator {

	private static final String	debugProperty			= "org.osgi.test.filter.debug";
	private boolean				debug;
	private static final String	serviceExcludeProperty	= "org.osgi.test.filter.exclude.services";
	private String				serviceExcludeFilter;
	private ServiceRegistration< ? >	hookReg;

	@Override
	public void start(BundleContext context) throws Exception {
		debug = Boolean.valueOf(context.getProperty(debugProperty)).booleanValue();
		serviceExcludeFilter = context.getProperty(serviceExcludeProperty);
		if (serviceExcludeFilter == null) {
			return; // no work to do!
		}
		hookReg = context.registerService(new String[] {EventListenerHook.class.getName(), FindHook.class.getName()},
				new ServiceHooks(context.createFilter(serviceExcludeFilter), debug), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (serviceExcludeFilter == null) {
			return; // no work to do!
		}
		hookReg.unregister();
	}
}
