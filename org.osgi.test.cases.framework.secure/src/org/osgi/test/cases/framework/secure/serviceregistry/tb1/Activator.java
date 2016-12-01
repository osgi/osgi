/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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
package org.osgi.test.cases.framework.secure.serviceregistry.tb1;

import static junit.framework.TestCase.*;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.framework.secure.junit.serviceregistry.export.TestService;

public class Activator implements BundleActivator {
	/**
	 * Starts the bundle.
	 */
    public void start(BundleContext context) {
        ServiceReference<TestService> reference = context.getServiceReference(TestService.class);
        assertNotNull(reference);
        assertEquals("service.scope not set correctly", Constants.SCOPE_PROTOTYPE, reference.getProperty(Constants.SERVICE_SCOPE));

        ServiceObjects<TestService> objects = context.getServiceObjects(reference);
        assertNotNull(objects);
        assertEquals(reference, objects.getServiceReference());
        TestService service = objects.getService();
        assertNotNull(service);
	}

	/**
	 * Stops the bundle.
	 */
    public void stop(BundleContext context) {
		// empty
	}
}
