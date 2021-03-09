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
package org.osgi.test.cases.async.junit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Capability;
import org.osgi.resource.Namespace;
import org.osgi.service.async.Async;
import org.osgi.service.log.LogReaderService;
import org.osgi.test.cases.async.junit.impl.AsyncErrorListener;
import org.osgi.test.cases.async.junit.impl.MyServiceImpl;
import org.osgi.test.cases.async.services.MyService;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;

public class AsyncTestCase extends OSGiTestCase {
	
	private ServiceTracker<Async, Async>	asyncTracker;
	private Async	async;

	private ServiceTracker<LogReaderService, LogReaderService> logReaderTracker;
	private LogReaderService logReader;
	private AsyncErrorListener asyncErrors;
	
	private MyServiceImpl myServiceImpl;

	private ServiceRegistration<MyService> normalReg;
	
	protected void setUp() throws InterruptedException {
		myServiceImpl = new MyServiceImpl();
		normalReg = getContext().registerService(MyService.class, myServiceImpl, null);

		asyncTracker = new ServiceTracker<Async, Async>(getContext(), Async.class, null);
		asyncTracker.open();
		async = asyncTracker.waitForService(5000);

		assertNotNull("No Async service available within 5 seconds", async);

		logReaderTracker = new ServiceTracker<LogReaderService, LogReaderService>(getContext(), LogReaderService.class, null);
		logReaderTracker.open();
		logReader = logReaderTracker.waitForService(5000);
		assertNotNull("No LogReaderService available within 5 seconds", logReader);
		logReader.addLogListener(asyncErrors  = new AsyncErrorListener());
	}
	
	protected void tearDown() {
		normalReg.unregister();

		asyncTracker.close();
		logReader.removeLogListener(asyncErrors);
		logReaderTracker.close();
	}
	
	/**
	 * A basic test that demonstrates that a call can be made asynchronously
	 * @throws Exception
	 */
	public void testAsyncCall() throws Exception {
		
		MyService service = async.mediate(normalReg.getReference(), MyService.class);
		
		// This call waits for a second
		Promise<Integer> p = async.call(service.countSlowly(2));
		
		// The promise should not have resolved yet. Theoretically this is
		// a race, but it should not take over a second to get here from the
		// previous statement.
		assertFalse(p.isDone());
		
		assertEquals("Wrong value.", 2, AsyncTestUtils.awaitResolve(p).intValue());
		assertEquals("Wrong method called.", MyService.METHOD_countSlowly, myServiceImpl.lastMethodCalled());
	}

	/**
	 * A basic test that ensures the provider of the Async service advertises
	 * the Async service capability
	 * 
	 * @throws Exception
	 */
	public void testAsyncServiceCapability() throws Exception {

		List<BundleCapability> capabilities = asyncTracker.getServiceReference().getBundle()
				.adapt(BundleWiring.class).getCapabilities("osgi.service");

		boolean hasCapability = false;
		boolean uses = false;

		for (Capability cap : capabilities) {
			@SuppressWarnings("unchecked")
			List<String> objectClass = (List<String>) cap.getAttributes().get("objectClass");

			if (objectClass.contains(Async.class.getName())) {
				hasCapability = true;
				String usesDirective = cap.getDirectives().get(Namespace.CAPABILITY_USES_DIRECTIVE);
				if (usesDirective != null) {
					Set<String> packages = new HashSet<String>(Arrays.asList(usesDirective.trim().split("\\s*,\\s*")));
					uses = packages.contains("org.osgi.service.async");
				}
				break;
			}
		}
		assertTrue("No osgi.service capability for the Async service", hasCapability);
		assertTrue("Missing uses constraint on the osgi.service capability", uses);
	}

	/**
	 * A basic test that ensures the provider of the Async service advertises
	 * the osgi.implementation capability
	 * 
	 * @throws Exception
	 */
	public void testAsyncImplementationCapability() throws Exception {

		List<BundleCapability> capabilities = asyncTracker.getServiceReference().getBundle()
				.adapt(BundleWiring.class).getCapabilities("osgi.implementation");

		boolean hasCapability = false;
		boolean uses = false;
		Version version = null;

		for (Capability cap : capabilities) {
			String objectClass = (String) cap.getAttributes().get("osgi.implementation");

			if ("osgi.async".equals(objectClass)) {
				hasCapability = true;
				String usesDirective = cap.getDirectives().get(Namespace.CAPABILITY_USES_DIRECTIVE);
				if (usesDirective != null) {
					Set<String> packages = new HashSet<String>(Arrays.asList(usesDirective.trim().split("\\s*,\\s*")));
					uses = packages.contains("org.osgi.service.async") && packages.contains("org.osgi.service.async.delegate");
				}
				version = (Version) cap.getAttributes().get("version");
				break;
			}
		}
		assertTrue("No osgi.implementation capability for the Async service", hasCapability);
		assertTrue("Missing uses constraint on the osgi.implementation capability", uses);
		assertEquals(Version.parseVersion("1.0.0"), version);
	}
}
