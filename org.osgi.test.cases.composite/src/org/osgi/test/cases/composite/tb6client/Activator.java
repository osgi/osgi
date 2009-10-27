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
package org.osgi.test.cases.composite.tb6client;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.composite.TestException;
import org.osgi.test.cases.composite.TestRegistryEventHook;
import org.osgi.test.cases.composite.TestRegistryFindHook;
import org.osgi.test.cases.composite.TestRegistryListenerHook;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		// clear hooks
		TestRegistryEventHook.instance.getBundles();
		TestRegistryFindHook.instance.getBundles();
		TestRegistryListenerHook.instance.getAdded();
		TestRegistryListenerHook.instance.getRemoved();

		ServiceRegistration reg = context.registerService(Runnable.class.getName(), 
				new Runnable() {
					
					public void run() {
						// nothing
					}
				}, null);
		reg.unregister();

		context.getServiceReference(Runnable.class.getName());

		ServiceListener listener = new ServiceListener() {
			
			public void serviceChanged(ServiceEvent event) {
				// nothing
			}
		};
		context.addServiceListener(listener);
		context.removeServiceListener(listener);

		Bundle[] bundles = TestRegistryEventHook.instance.getBundles();
		if (bundles.length != 1 || bundles[0] != context.getBundle())
			throw new TestException("Unexpected event hook bundles", TestException.EVENT_HOOK);
		bundles = TestRegistryFindHook.instance.getBundles();
		if (bundles.length != 1 || bundles[0] != context.getBundle())
			throw new TestException("Unexpected find hook bundles", TestException.FIND_HOOK);
		bundles = TestRegistryListenerHook.instance.getAdded();
		if (bundles.length != 1 || bundles[0] != context.getBundle())
			throw new TestException("Unexpected listener hook added bundles", TestException.LISTENER_HOOK);
		bundles = TestRegistryListenerHook.instance.getRemoved();
		if (bundles.length != 1 || bundles[0] != context.getBundle())
			throw new TestException("Unexpected listener hook removed bundles", TestException.LISTENER_HOOK);
	}

	public void stop(BundleContext context) throws Exception {
		// Nothing
	}

}
