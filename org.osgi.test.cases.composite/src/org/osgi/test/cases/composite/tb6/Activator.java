/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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
package org.osgi.test.cases.composite.tb6;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.hooks.service.EventHook;
import org.osgi.framework.hooks.service.FindHook;
import org.osgi.framework.hooks.service.ListenerHook;
import org.osgi.test.cases.composite.TestRegistryEventHook;
import org.osgi.test.cases.composite.TestRegistryFindHook;
import org.osgi.test.cases.composite.TestRegistryListenerHook;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		context.registerService(EventHook.class.getName(), TestRegistryEventHook.instance, null);
		context.registerService(FindHook.class.getName(), TestRegistryFindHook.instance, null);
		context.registerService(ListenerHook.class.getName(), TestRegistryListenerHook.instance, null);
	}

	public void stop(BundleContext context) throws Exception {
		// Nothing
	}

}
