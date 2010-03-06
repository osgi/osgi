/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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
package org.osgi.test.cases.remoteserviceadmin.junit;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.EndpointListener;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.test.cases.remoteserviceadmin.common.A;
import org.osgi.test.cases.remoteserviceadmin.common.B;
import org.osgi.test.cases.remoteserviceadmin.impl.TestServiceImpl;
import org.osgi.test.support.compatibility.Semaphore;

/**
 * Test the discovery portion of the spec by registering a EndpointDescription
 * in one framework and expecting it to show up in the other framework. 
 * 
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * @version 1.0.0
 */
public class DiscoveryTest extends MultiFrameworkTestCase {
	private static final String	SYSTEM_PACKAGES_EXTRA	= "org.osgi.test.cases.remoteserviceadmin.system.packages.extra";

	/**
	 * @see org.osgi.test.cases.remoteserviceadmin.junit.MultiFrameworkTestCase#getConfiguration()
	 */
	public Map<String, String> getConfiguration() {
		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put(Constants.FRAMEWORK_STORAGE_CLEAN, "true");
		
		//make sure that the server framework System Bundle exports the interfaces
		String systemPackagesXtra = System.getProperty(SYSTEM_PACKAGES_EXTRA);
        configuration.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, systemPackagesXtra);
        configuration.put("osgi.console", "1112");
		return configuration;
	}
	
	/**
	 * 122.6 Discovery
	 */
	public void testDiscovery122_6() throws Exception {
		// verify that the server framework is exporting the test packages
		verifyFramework();
		
		//
		// install test bundle in child framework
		//
		BundleContext childContext = getFramework().getBundleContext();
		
		Bundle tb1Bundle = installBundle(childContext, "/tb1.jar");
		assertNotNull(tb1Bundle);
		
		//
		// register EndpointListener in parent framework
		//
		final EndpointListenerImpl endpointListenerImpl = new EndpointListenerImpl();
		
		// TODO: the current RI requires objectClass to be set in the filter, but that shouldn't be mandated
		final String endpointListenerFilter = "(&(objectClass=" + A.class.getName() + ")(!(org.osgi.framework.uuid=" + getContext().getProperty("org.osgi.framework.uuid") + ")))";
		String secondFilter = "(mykey=has been overridden)";
		Hashtable<String, Object> endpointListenerProperties = new Hashtable<String, Object>();
		endpointListenerProperties.put(EndpointListener.ENDPOINT_LISTENER_SCOPE, new String[]{endpointListenerFilter, secondFilter});

		ServiceRegistration endpointListenerRegistration = getContext().registerService(
				EndpointListener.class.getName(), endpointListenerImpl, endpointListenerProperties);
		assertNotNull(endpointListenerRegistration);

		//
		// 122.6.1 Scope and Filters
		// register an EndpointListener w/o a scope. If called, then fail
		//
		EndpointListenerImpl emptyEndpointListener = scope_and_filter_122_6_1("");
		
		// start test bundle in child framework
		// this will run the test in the child framework and fail
		tb1Bundle.start();
		
		// verify callback in parent framework
		endpointListenerImpl.getSem().waitForSignal(6000);
		
		// 122.6.2 callback has to return first matched filter
		assertEquals("filter doesn't match the first filter", endpointListenerFilter, endpointListenerImpl.getMatchedFilter());
		
		EndpointDescription ep = endpointListenerImpl.getAddedEndpoint(); 
		assertNotNull(ep);
		assertEquals("remote service id is incorrect", 12345, ep.getServiceId());
		assertEquals("remote.id does not match", "someURI", ep.getId());
		assertEquals("remote framework id is incorrect", getFramework()
				.getBundleContext().getProperty("org.osgi.framework.uuid"), ep
				.getFrameworkUUID());
		assertFalse(
				"remote framework id has to be UUID of remote not local framework",
				ep.getFrameworkUUID().equals(
						getContext().getProperty("org.osgi.framework.uuid")));
		assertTrue("discovered interfaces don't contain " + A.class.getName(), ep.getInterfaces().contains(A.class.getName()));
		assertFalse("discovered interfaces must not contain " + B.class.getName(), ep.getInterfaces().contains(B.class.getName()));
		assertTrue("intent list does not contain 'my_intent_is_for_this_to_work'", ep.getIntents().contains("my_intent_is_for_this_to_work"));
		assertEquals("the property of the service should have been overridden by the EndpointDescription", "has been overridden", ep.getProperties().get("mykey")); 
		assertEquals("the property myprop is missing", "myvalue", ep.getProperties().get("myprop"));
		
		// verify 122.6.1
		assertNull(emptyEndpointListener.getAddedEndpoint());
		
		//
		// remove the endpoint
		//
		tb1Bundle.stop();

		// verify callback in parent framework
		endpointListenerImpl.getSem().waitForSignal(6000);
		
		// 122.6.2 callback has to return first matched filter
		assertEquals("filter doesn't match the first filter", endpointListenerFilter, endpointListenerImpl.getMatchedFilter());

		ep = endpointListenerImpl.getRemovedEndpoint();
		assertNotNull(ep);
		assertEquals("remote service id is incorrect", 12345, ep.getServiceId());
		assertEquals("remote.id does not match", "someURI", ep.getId());
		assertEquals("remote framework id is incorrect", getFramework()
				.getBundleContext().getProperty("org.osgi.framework.uuid"), ep
				.getFrameworkUUID());
		assertFalse(
				"remote framework id has to be UUID of remote not local framework",
				ep.getFrameworkUUID().equals(
						getContext().getProperty("org.osgi.framework.uuid")));
		assertTrue("discovered interfaces don't contain " + A.class.getName(), ep.getInterfaces().contains(A.class.getName()));
		assertFalse("discovered interfaces must not contain " + B.class.getName(), ep.getInterfaces().contains(B.class.getName()));
		assertTrue("intent list does not contain 'my_intent_is_for_this_to_work'", ep.getIntents().contains("my_intent_is_for_this_to_work"));
		assertEquals("the property of the service should have been overridden by the EndpointDescription", "has been overridden", ep.getProperties().get("mykey")); 
		assertEquals("the property myprop is missing", "myvalue", ep.getProperties().get("myprop"));
	}

	/**
	 * Test behavior of EndpointListeners
	 * 
	 * @throws Exception
	 */
	public void testEndpointListener() throws Exception {
		ServiceReference[] refs = getContext().getServiceReferences(EndpointListener.class.getName(), null);
		assertNotNull("no EndpointListeners registered", refs);
		assertTrue("no EndpointListeners registered", refs.length > 0);
		
		// register my own listener
		EndpointListenerImpl endpointListener = scope_and_filter_122_6_1("");
		
		TestServiceImpl serviceA = new TestServiceImpl();
		ServiceRegistration reg = getContext().registerService(A.class.getName(), serviceA, null);
		assertNotNull(reg);
		
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(RemoteConstants.ENDPOINT_ID, "myid");
		properties.put(RemoteConstants.SERVICE_IMPORTED_CONFIGS, "myconfigtype");
		
		// create EndpointDescription
		EndpointDescription description = new EndpointDescription(reg.getReference(), properties);
		
		EndpointListener elistener = (EndpointListener) getContext().getService(refs[0]);
		assertNotNull(elistener);
		
		String filter = "(" + RemoteConstants.ENDPOINT_ID + "=myid)";

		// test add part
		elistener.endpointAdded(description, filter);

		endpointListener.getSem().waitForSignal(10000);

		assertSame(description, endpointListener.getAddedEndpoint());
		assertEquals(filter, endpointListener.getMatchedFilter());

		// add another listener to test replay
		EndpointListenerImpl endpointListener2 = scope_and_filter_122_6_1("");
		endpointListener2.getSem().waitForSignal(6000);

		assertSame(description, endpointListener2.getAddedEndpoint());
		assertEquals(filter, endpointListener2.getMatchedFilter());

		// change the scope
		
		// test remove part
		elistener.endpointRemoved(description, filter);

		endpointListener.getSem().waitForSignal(6000);

		assertSame(description, endpointListener.getRemovedEndpoint());
		assertEquals(filter, endpointListener.getMatchedFilter());
		
		// add again
		// test add part
		elistener.endpointAdded(description, filter);

		endpointListener.getSem().waitForSignal(6000);

		assertSame(description, endpointListener.getAddedEndpoint());
		assertEquals(filter, endpointListener.getMatchedFilter());
		
		// now remove client
		for (int i = 0; i < refs.length; i++) {
			getContext().ungetService(refs[i]);
		}
		
		// check that endpoint got removed
		endpointListener.getSem().waitForSignal(6000);

		assertSame(description, endpointListener.getRemovedEndpoint());
		assertEquals(filter, endpointListener.getMatchedFilter());
	}
	
	/**
	 * Test empty filter scope
	 */
	private EndpointListenerImpl scope_and_filter_122_6_1(String scope) throws Exception {
		Hashtable<String, String> elp = new Hashtable<String, String>();
		elp.put(EndpointListener.ENDPOINT_LISTENER_SCOPE, scope);
		
		EndpointListenerImpl el = new EndpointListenerImpl();
		ServiceRegistration elr = getContext().registerService(EndpointListener.class.getName(), el, elp);
		assertNotNull(elr);
		
		return el;
	}
	
	

	class EndpointListenerImpl implements EndpointListener {
		private Semaphore sem = new Semaphore(0);
		private String matchedFilter;
		private EndpointDescription addedEndpoint;
		private EndpointDescription removedEndpoint;

		/**
		 * @see org.osgi.service.remoteserviceadmin.EndpointListener#endpointAdded(org.osgi.service.remoteserviceadmin.EndpointDescription, java.lang.String)
		 */
		public void endpointAdded(EndpointDescription endpoint,
				String matchedFilter) {
			this.addedEndpoint = endpoint;
			this.matchedFilter = matchedFilter;
			sem.signal();
		}

		/**
		 * @see org.osgi.service.remoteserviceadmin.EndpointListener#endpointRemoved(org.osgi.service.remoteserviceadmin.EndpointDescription, java.lang.String)
		 */
		public void endpointRemoved(EndpointDescription endpoint,
				String matchedFilter) {
			this.removedEndpoint = endpoint;
			this.matchedFilter = matchedFilter;
			sem.signal();
		}
		
		/**
		 * @return the sem
		 */
		public Semaphore getSem() {
			return sem;
		}
		
		/**
		 * @return the matchedFilter
		 */
		public String getMatchedFilter() {
			return matchedFilter;
		}
		
		/**
		 * @return the addedEndpoint
		 */
		public EndpointDescription getAddedEndpoint() {
			return addedEndpoint;
		}
		
		/**
		 * @return the removedEndpoint
		 */
		public EndpointDescription getRemovedEndpoint() {
			return removedEndpoint;
		}
	}
}
