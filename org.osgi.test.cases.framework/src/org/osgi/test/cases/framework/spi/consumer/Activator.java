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

package org.osgi.test.cases.framework.spi.consumer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ServiceLoader;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.framework.spi.api.TestPlugin;

/**
 * Consumer bundle that uses ServiceLoader to discover TestPlugin implementations
 */
public class Activator implements BundleActivator {
	
	private ServiceRegistration<List<?>> registration;
	
	@Override
	public void start(BundleContext context) throws Exception {
		// Use ServiceLoader to discover implementations
		ServiceLoader<TestPlugin> loader = ServiceLoader.load(TestPlugin.class, 
				TestPlugin.class.getClassLoader());
		
		List<String> discoveredPlugins = new ArrayList<>();
		for (TestPlugin plugin : loader) {
			discoveredPlugins.add(plugin.getName() + ":" + plugin.getVersion());
		}
		
		// Register discovered plugins as a service for testing
		Hashtable<String, Object> props = new Hashtable<>();
		props.put("discovered.plugins", discoveredPlugins.toArray(new String[0]));
		registration = context.registerService(List.class, discoveredPlugins, props);
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		if (registration != null) {
			registration.unregister();
		}
	}
}
