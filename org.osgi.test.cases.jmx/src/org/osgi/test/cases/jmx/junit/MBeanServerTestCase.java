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

package org.osgi.test.cases.jmx.junit;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import junit.framework.TestCase;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;


public class MBeanServerTestCase extends TestCase {

	private ServiceReference ref;
	private MBeanServer mBeanServer;
	private BundleContext	context;

	protected void setUp() {
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		context.registerService(
                MBeanServer.class.getCanonicalName(),
                mBeanServer, null);
	}
	
	public void setBundleContext(BundleContext context) {
		this.context = context;
	}

	public void testMBeanServerExistence() {
		ref = context.getServiceReference(MBeanServer.class.getCanonicalName());
		assertNotNull("No MBeanServer service available", ref);
		mBeanServer = (MBeanServer) context.getService(ref);
		assertNotNull(mBeanServer);
	}
}
