/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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

package org.osgi.test.cases.tr069todmt.junit;

import junit.framework.TestCase;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.tr069todmt.TR069ConnectorFactory;

public class TR069toDMTTestCase extends TestCase {

	private BundleContext	context;

	public void setBundleContext(BundleContext context) {
		this.context = context;
	}

	public void testTR069ConnectorFactory() {
		ServiceReference reference = context
				.getServiceReference(TR069ConnectorFactory.class.getName());
		assertNotNull(reference);
		TR069ConnectorFactory service = (TR069ConnectorFactory) context
				.getService(reference);
		assertNotNull(service);
	}
}
