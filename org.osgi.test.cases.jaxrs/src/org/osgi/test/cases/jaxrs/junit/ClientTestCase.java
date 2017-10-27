/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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
package org.osgi.test.cases.jaxrs.junit;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.osgi.framework.Constants.*;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jaxrs.whiteboard.JaxRSWhiteboardConstants;
import org.osgi.test.cases.jaxrs.resources.WhiteboardResource;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;

public class ClientTestCase extends AbstractJAXRSTestCase {
	
	/**
	 * A basic test that ensures the ClientBuilder is registered as a prototype
	 * scoped service and that it can be used to get a resource (151.9)
	 * 
	 * @throws Exception
	 */
	public void testJaxRsClientService() throws Exception {

		ServiceTracker<ClientBuilder,ClientBuilder> tracker = new ServiceTracker<>(
				getContext(), ClientBuilder.class, null);
		tracker.open();

		assertNotNull(tracker.waitForService(2000));

		for (ServiceReference<ClientBuilder> ref : tracker.getTracked()
				.keySet()) {
			assertEquals(SCOPE_PROTOTYPE, ref.getProperty(SERVICE_SCOPE));
		}

		Client c = tracker.getService().build();

		String baseURI = getBaseURI();

		WebTarget target = c.target(baseURI + "/whiteboard/resource");

		assertEquals(NOT_FOUND.getStatusCode(),
				target.request().get().getStatusInfo().getStatusCode());

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxRSWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<WhiteboardResource> reg = getContext()
				.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		try {

			awaitSelection.getValue();

			// Do another get

			assertEquals("[buzz, fizz, fizzbuzz]",
					target.request().get(String.class));
		
		} finally {
			reg.unregister();
		}
	}

}
