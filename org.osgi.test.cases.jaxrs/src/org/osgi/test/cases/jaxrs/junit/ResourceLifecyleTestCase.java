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

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.osgi.service.jaxrs.runtime.JaxRSServiceRuntimeConstants.JAX_RS_SERVICE_ENDPOINT;
import static org.osgi.test.cases.jaxrs.resources.ContextInjectedWhiteboardResource.CUSTOM_HEADER;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.osgi.framework.Bundle;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jaxrs.runtime.JaxRSServiceRuntime;
import org.osgi.service.jaxrs.whiteboard.JaxRSWhiteboardConstants;
import org.osgi.test.cases.jaxrs.resources.AsyncWhiteboardResource;
import org.osgi.test.cases.jaxrs.resources.ContextInjectedWhiteboardResource;
import org.osgi.test.cases.jaxrs.resources.WhiteboardResource;
import org.osgi.test.cases.jaxrs.util.ServiceUpdateHelper;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.promise.Promise;

/**
 * This test covers the lifecycle behaviours described in section 151.4.2
 */
public class ResourceLifecyleTestCase extends OSGiTestCase {
	
	private CloseableHttpClient						client;

	private ServiceUpdateHelper						helper;

	private ServiceReference<JaxRSServiceRuntime>	runtime;

	public void setUp() {
		client = HttpClients.createDefault();

		helper = new ServiceUpdateHelper(getContext());
		helper.open();

		runtime = helper.awaitRuntime(5000);
	}

	public void tearDown() throws IOException {
		helper.close();
		client.close();
	}

	/**
	 * Section 151.4.2 Register a simple JAX-RS singleton resource and show that
	 * it gets re-used between requests
	 * 
	 * @throws Exception
	 */
	public void testSimpleSingletonResource() throws Exception {
		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(JaxRSWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);
		

		Promise<Void> awaitSelection = helper.awaitModification(runtime,
				5000);

		ServiceRegistration<WhiteboardResource> reg = getContext()
				.registerService(WhiteboardResource.class, new WhiteboardResource(), properties);
		
		try {

			awaitSelection.getValue();
			
			String baseURI = getBaseURI();

			// Do a get

			CloseableHttpResponse httpResponse = client.execute(RequestBuilder
					.get(baseURI + "whiteboard/resource").build());

			String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("[buzz, fizz, fizzbuzz]", response);

			// Do a delete

			httpResponse = client.execute(RequestBuilder
					.delete(baseURI + "whiteboard/resource/buzz").build());

			response = assertResponse(httpResponse, 200, null);
			assertEquals("", response);

			// Do another get to show the singleton-ness

			httpResponse = client.execute(RequestBuilder
					.get(baseURI + "whiteboard/resource").build());

			response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("[fizz, fizzbuzz]", response);
		
		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.4.2 Register a simple JAX-RS prototype resource and show that
	 * it gets re-created between requests
	 * 
	 * @throws Exception
	 */
	public void testSimplePrototypeResource() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxRSWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		AtomicInteger creationCount = new AtomicInteger();

		ServiceRegistration<WhiteboardResource> reg = getContext()
				.registerService(WhiteboardResource.class,
						getPrototypeServiceFactory(() -> {
							creationCount.incrementAndGet();
							return new WhiteboardResource();
						}, (sr, s) -> {}), properties);

		try {

			awaitSelection.getValue();

			String baseURI = getBaseURI();

			// We have a base count as the whiteboard may request a resource to
			// check it out first
			int baseCount = creationCount.get();

			// Do a get

			CloseableHttpResponse httpResponse = client.execute(RequestBuilder
					.get(baseURI + "whiteboard/resource").build());

			String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("[buzz, fizz, fizzbuzz]", response);
			assertEquals(baseCount + 1, creationCount.get());

			// Do a delete

			httpResponse = client.execute(RequestBuilder
					.delete(baseURI + "whiteboard/resource/buzz").build());

			response = assertResponse(httpResponse, 200, null);
			assertEquals("", response);
			assertEquals(baseCount + 2, creationCount.get());

			// Do another get to show the singleton-ness

			httpResponse = client.execute(RequestBuilder
					.get(baseURI + "whiteboard/resource").build());

			response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("[buzz, fizz, fizzbuzz]", response);
			assertEquals(baseCount + 3, creationCount.get());

		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.4.2.1 Register a simple JAX-RS prototype resource and show
	 * that it gets field injected with the relevant JAX-RS object
	 * 
	 * @throws Exception
	 */
	public void testContextFieldInjection() throws Exception {
	
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxRSWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);
	
		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);
	
		ServiceRegistration<ContextInjectedWhiteboardResource> reg = getContext()
				.registerService(ContextInjectedWhiteboardResource.class,
						getPrototypeServiceFactory(
								ContextInjectedWhiteboardResource::new,
								(x, y) -> {}),
						properties);
	
		try {
	
			awaitSelection.getValue();
	
			String baseURI = getBaseURI();
	
			// Do a get
	
			CloseableHttpResponse httpResponse = client.execute(RequestBuilder
					.get(baseURI + "whiteboard/context")
					.addHeader(CUSTOM_HEADER, "test")
					.build());
	
			String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("test", response);
	
		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.4.2.2 Register an async JAX-RS resource and show that it is
	 * not released until after the request completes
	 * 
	 * @throws Exception
	 */
	public void testAsyncPrototypeResource() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxRSWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		Semaphore preCompleteSemaphore = new Semaphore(0);

		AtomicBoolean wasReleasedPreCompletion = new AtomicBoolean(true);

		ServiceRegistration<AsyncWhiteboardResource> reg = getContext()
				.registerService(AsyncWhiteboardResource.class,
						getPrototypeServiceFactory(
								() -> new AsyncWhiteboardResource(
										() -> preCompleteSemaphore.release(),
										() -> {}),
								(sr,s) -> wasReleasedPreCompletion.set(
										!preCompleteSemaphore.tryAcquire())),
						properties);

		try {

			awaitSelection.getValue();

			String baseURI = getBaseURI();

			// Do a get

			CloseableHttpResponse httpResponse = client.execute(RequestBuilder
					.get(baseURI + "whiteboard/async/quack").build());

			String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("quack", response);

			assertFalse(wasReleasedPreCompletion.get());

		} finally {
			reg.unregister();
		}
	}

	private String getBaseURI() {
		Object value = runtime.getProperty(JAX_RS_SERVICE_ENDPOINT);

		if (value instanceof String) {
			return (String) value;
		} else if (value instanceof String[]) {
			return ((String[]) value)[0];
		} else if (value instanceof Collection) {
			return String.valueOf(((Collection< ? >) value).iterator().next());
		}

		throw new IllegalArgumentException(
				"The JAXRS Service Runtime did not declare an endpoint property");
	}

	private String assertResponse(CloseableHttpResponse response,
			int responseCode, String mediaType) throws IOException {

		assertEquals(responseCode, response.getStatusLine().getStatusCode());

		HttpEntity entity = response.getEntity();

		if (mediaType != null) {
			assertEquals(mediaType, entity.getContentType().getValue());
		}

		if (entity != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			entity.writeTo(baos);
			return baos.toString("UTF-8");
		}
		return null;
	}

	private <T> PrototypeServiceFactory<T> getPrototypeServiceFactory(
			Supplier<T> supplier,
			BiConsumer<ServiceRegistration<T>,T> destroyer) {
		return new PrototypeServiceFactory<T>() {

			@Override
			public T getService(Bundle bundle,
					ServiceRegistration<T> registration) {
				return supplier.get();
			}

			@Override
			public void ungetService(Bundle bundle,
					ServiceRegistration<T> registration, T service) {
				destroyer.accept(registration, service);
			}

		};
	}
}
