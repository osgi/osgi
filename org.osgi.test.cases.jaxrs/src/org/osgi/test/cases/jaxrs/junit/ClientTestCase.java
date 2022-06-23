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
package org.osgi.test.cases.jaxrs.junit;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.osgi.framework.Constants.SCOPE_PROTOTYPE;
import static org.osgi.framework.Constants.SERVICE_SCOPE;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jaxrs.client.PromiseRxInvoker;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;
import org.osgi.test.cases.jaxrs.resources.AsyncWhiteboardResource;
import org.osgi.test.cases.jaxrs.resources.WhiteboardResource;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.service.ServiceExtension;
import org.osgi.util.promise.Promise;

@ExtendWith(ServiceExtension.class)
public class ClientTestCase extends AbstractJAXRSTestCase {
	
	/**
	 * A basic test that ensures the ClientBuilder is registered as a prototype
	 * scoped service and that it can be used to get a resource (151.9)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJaxRsClientService(@InjectService
	ServiceAware<ClientBuilder> builderService) throws Exception {

		assertNotNull(builderService.waitForService(2000));

		for (ServiceReference<ClientBuilder> ref : builderService
				.getServiceReferences()) {
			assertEquals(SCOPE_PROTOTYPE, ref.getProperty(SERVICE_SCOPE));
		}

		Client c = builderService.getService().build();

		String baseURI = getBaseURI();

		WebTarget target = c.target(baseURI).path("whiteboard/resource");

		// Do a get
		assertEquals(NOT_FOUND.getStatusCode(),
				target.request().get().getStatusInfo().getStatusCode());

		// Register a resource
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		awaitSelection.getValue();

		// Do another get

		assertEquals("[buzz, fizz, fizzbuzz]",
				target.request().get(String.class));
		
	}

	/**
	 * A basic test that ensures the PromiseRxInvoker can be used (151.9.2)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJaxRsPromiseRxInvoker(@InjectService
	ServiceAware<ClientBuilder> builderService) throws Exception {

		assertNotNull(builderService.waitForService(2000));

		Client c = builderService.getService().build();

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(AsyncWhiteboardResource.class,
						new AsyncWhiteboardResource(() -> {}, () -> {}),
						properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		WebTarget target = c.target(baseURI).path("whiteboard/async/{name}");

		Promise<String> p = target.resolveTemplate("name", "Bob")
				.request()
				.rx(PromiseRxInvoker.class)
				.get(String.class);

		assertFalse(p.isDone());

		Semaphore s = new Semaphore(0);

		p.onResolve(s::release);

		assertTrue(s.tryAcquire(5, TimeUnit.SECONDS));

		assertEquals("Bob", p.getValue());

	}

}
