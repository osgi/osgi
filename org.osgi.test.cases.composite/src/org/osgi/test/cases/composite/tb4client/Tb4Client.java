/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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
package org.osgi.test.cases.composite.tb4client;

import junit.framework.TestCase;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.composite.TestException;
import org.osgi.test.cases.composite.tb4.SomeService;
import org.osgi.test.cases.composite.tb4.params.Work1;
import org.osgi.test.cases.composite.tb4.params.Work2;

public class Tb4Client implements BundleActivator {
	private static String TEST_HEADER = "Test-Property";
	private static String TEST_KEY = "test.property";

	public void start(BundleContext context) {
		// we are simpletons and just assume the service is there already
		// do not use this as example code!!
		ServiceReference ref = context.getServiceReference(SomeService.class.getName());
		if (ref == null)
			throw new TestException("Could not find service reference.", TestException.NO_SERVICE_REFERENCE);
		String testPropValue = (String) context.getBundle().getHeaders("").get(TEST_HEADER);
		if (testPropValue != null && !testPropValue.equals(ref.getProperty(TEST_KEY)))
			throw new TestException("Wrong service property value, expected: " + testPropValue + " but found: " + ref.getProperty(TEST_KEY), TestException.WRONG_SERVICE_PROPERTY);

		SomeService service = (SomeService) context.getService(ref);
		if (service == null)
			throw new TestException("Test service is null.", TestException.NO_SERVICE);

		service.doWork(new Work1());
		service.doWork(new Work2());
	}

	public void stop(BundleContext context) throws Exception {
		// nothing
	}

}
