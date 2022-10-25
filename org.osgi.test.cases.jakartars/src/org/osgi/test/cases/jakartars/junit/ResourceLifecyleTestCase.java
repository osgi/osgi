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


package org.osgi.test.cases.jakartars.junit;

import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.osgi.test.cases.jakartars.resources.ContextInjectedWhiteboardResource.CUSTOM_HEADER;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jakartars.whiteboard.JakartarsWhiteboardConstants;
import org.osgi.test.cases.jakartars.applications.SimpleApplication;
import org.osgi.test.cases.jakartars.resources.AsyncReturnWhiteboardResource;
import org.osgi.test.cases.jakartars.resources.AsyncWhiteboardResource;
import org.osgi.test.cases.jakartars.resources.ContextInjectedWhiteboardResource;
import org.osgi.test.cases.jakartars.resources.WhiteboardResource;
import org.osgi.util.promise.Promise;

import jakarta.ws.rs.core.Application;

/**
 * This test covers the lifecycle behaviours described in section 151.4
 */
public class ResourceLifecyleTestCase extends AbstractJakartarsTestCase {
	

	/**
	 * Section 151.4.2 Register a simple JAX-RS singleton resource and show that
	 * it gets re-used between requests
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSimpleSingletonResource() throws Exception {
		
		// Register a whiteboard resource
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);
		
		Promise<Void> awaitSelection = helper.awaitModification(runtime,
				5000);

		context.registerService(WhiteboardResource.class,
				new WhiteboardResource(), properties);

		awaitSelection.getValue();
		
		String baseURI = getBaseURI();

		// Do a get

		CloseableHttpResponse httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/resource").build());

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Do a delete

		httpResponse = client.execute(
				RequestBuilder.delete(baseURI + "whiteboard/resource/buzz")
						.build());

		response = assertResponse(httpResponse, 200, null);
		assertEquals("", response);

		// Do another get to show the singleton-ness

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/resource").build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[fizz, fizzbuzz]", response);
		
	}

	/**
	 * Section 151.4.2 Register a simple JAX-RS prototype resource and show that
	 * it gets re-created between requests
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSimplePrototypeResource() throws Exception {

		// Register a whiteboard resource

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		AtomicInteger creationCount = new AtomicInteger();

		context.registerService(WhiteboardResource.class,
						getPrototypeServiceFactory(() -> {
							creationCount.incrementAndGet();
							return new WhiteboardResource();
						}, (sr, s) -> {}), properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// We have a base count as the whiteboard may request a resource to
		// check it out first
		int baseCount = creationCount.get();

		// Do a get

		CloseableHttpResponse httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/resource").build());

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);
		assertEquals(baseCount + 1, creationCount.get());

		// Do a delete

		httpResponse = client.execute(
				RequestBuilder.delete(baseURI + "whiteboard/resource/buzz")
						.build());

		response = assertResponse(httpResponse, 200, null);
		assertEquals("", response);
		assertEquals(baseCount + 2, creationCount.get());

		// Do another get to show the singleton-ness

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/resource").build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);
		assertEquals(baseCount + 3, creationCount.get());

	}

	/**
	 * Section 151.4.2.1 Register a simple JAX-RS prototype resource and show
	 * that it gets field injected with the relevant JAX-RS object
	 * 
	 * @throws Exception
	 */
	@Test
	public void testContextFieldInjection() throws Exception {
	
		// Register a whiteboard resource

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);
	
		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);
	
		context.registerService(ContextInjectedWhiteboardResource.class,
						getPrototypeServiceFactory(
								ContextInjectedWhiteboardResource::new,
								(x, y) -> {}),
						properties);
	
		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		CloseableHttpResponse httpResponse = client
				.execute(RequestBuilder.get(baseURI + "whiteboard/context")
						.addHeader(CUSTOM_HEADER, "test")
						.build());

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("test", response);
	
	}

	/**
	 * Section 151.4.2.2 Register an async JAX-RS resource and show that it is
	 * not released until after the request completes
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAsyncPrototypeResource() throws Exception {

		// Register a whiteboard resource

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		Semaphore preCompleteSemaphore = new Semaphore(0);
		Semaphore releaseSemaphore = new Semaphore(0);

		AtomicBoolean wasReleasedPreCompletion = new AtomicBoolean(true);

		context.registerService(AsyncWhiteboardResource.class,
						getPrototypeServiceFactory(
								() -> new AsyncWhiteboardResource(
										() -> preCompleteSemaphore.release(),
										() -> {}),
						(sr, s) -> {
							wasReleasedPreCompletion
									.set(!preCompleteSemaphore.tryAcquire());
							releaseSemaphore.release();
						}),
						properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Clear the semaphore as setup may involve get/release of the service
		releaseSemaphore.drainPermits();

		// Do a get

		CloseableHttpResponse httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/async/quack").build());

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("quack", response);

		// This should be very fast as the response has been received already
		assertTrue(releaseSemaphore.tryAcquire(100, TimeUnit.MILLISECONDS));
		assertFalse(wasReleasedPreCompletion.get());

	}

	/**
	 * Section 151.4.2.3 Show that async return values are supported
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAsyncReturnValues() throws Exception {

		// Register a whiteboard resource

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(AsyncReturnWhiteboardResource.class,
						new AsyncReturnWhiteboardResource(), properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		CloseableHttpResponse httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/asyncReturn/cs/woof")
						.build());

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("woof", response);

		httpResponse.close();

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/asyncReturn/p/miaow")
						.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("miaow", response);

	}

	/**
	 * Section 151.3 Use whiteboard targeting for resources
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSimpleWhiteboardTarget() throws Exception {

		Long serviceId = (Long) runtime.getProperty(Constants.SERVICE_ID);

		String selectFilter = "(service.id=" + serviceId + ")";
		String rejectFilter = "(!" + selectFilter + ")";

		// Register a whiteboard resource targeting the whiteboard

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);
		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_WHITEBOARD_TARGET,
				selectFilter);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<WhiteboardResource> reg = context
				.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		HttpUriRequest getRequest = RequestBuilder
				.get(baseURI + "whiteboard/resource")
				.build();

		CloseableHttpResponse httpResponse = client.execute(getRequest);

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Change the target
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_WHITEBOARD_TARGET,
				rejectFilter);
		reg.setProperties(properties);

		awaitSelection.getValue();

		httpResponse = client.execute(getRequest);

		assertResponse(httpResponse, 404, null);

		// Reset the target
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_WHITEBOARD_TARGET,
				selectFilter);
		reg.setProperties(properties);

		awaitSelection.getValue();

		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

	}

	/**
	 * Section 151.4.2 Singleton services must be released if the application
	 * goes out of scope
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSingletonResourceWhenApplicationChanges() throws Exception {

		// Register a whiteboard resource with an application target

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);
		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_SELECT,
				"(foo=bar)");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		Semaphore getSemaphore = new Semaphore(0);

		Semaphore releaseSemaphore = new Semaphore(0);

		context.registerService(WhiteboardResource.class,
						getServiceFactory(() -> {
							getSemaphore.release();
							return new WhiteboardResource();
						}, (sr, s) -> releaseSemaphore.release()), properties);

		awaitSelection.getValue();

		// Register a whiteboard application

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test");
		properties.put("foo", "bar");

		awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> appReg = context.registerService(
				Application.class,
				new SimpleApplication(emptySet(), emptySet()), properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		CloseableHttpResponse httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test/whiteboard/resource")
						.build());

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Do a delete

		httpResponse = client.execute(
				RequestBuilder.delete(baseURI + "test/whiteboard/resource/buzz")
						.build());

		response = assertResponse(httpResponse, 200, null);
		assertEquals("", response);

		// Do another get to show the singleton-ness

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test/whiteboard/resource")
						.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[fizz, fizzbuzz]", response);

		// Unregister the application
		appReg.unregister();

		assertTrue(getSemaphore.availablePermits() > 0);
		assertTrue(releaseSemaphore.tryAcquire(getSemaphore.availablePermits(),
				100, TimeUnit.MILLISECONDS));

	}
}
