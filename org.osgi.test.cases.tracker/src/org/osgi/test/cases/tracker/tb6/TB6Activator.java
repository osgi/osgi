/*
 * Copyright (c) OSGi Alliance (2016, 2017). All Rights Reserved.
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

package org.osgi.test.cases.tracker.tb6;

import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.tracker.service.TestService1;

/**
 * Register a prototype scope TestService1 service.
 */
public class TB6Activator implements BundleActivator {

	/**
	 * Starts the bundle. Installs several services later filtered by the tbc
	 */
	public void start(BundleContext bc) {
		Hashtable<String, Object> ts1Props = new Hashtable<String, Object>();
		ts1Props.put("name", "TestService1");
		ts1Props.put("version", Float.valueOf(1.0f));
		ts1Props.put("compatible", Float.valueOf(1.0f));
		ts1Props.put("description", "TestService 1");

		bc.registerService(TestService1.class,
				new PrototypeServiceFactory<TestService1>() {
					@Override
					public TestService1 getService(Bundle bundle,
							ServiceRegistration<TestService1> registration) {
						return new TestService1() {};
					}
					@Override
					public void ungetService(Bundle bundle,
							ServiceRegistration<TestService1> registration,
							TestService1 service) {}
				}, ts1Props);
	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
		// empty
	}
}
