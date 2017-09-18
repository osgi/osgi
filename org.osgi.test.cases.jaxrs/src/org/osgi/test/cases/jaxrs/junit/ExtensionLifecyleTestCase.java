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

import java.util.Dictionary;
import java.util.Hashtable;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.WriterInterceptor;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jaxrs.whiteboard.JaxRSWhiteboardConstants;
import org.osgi.test.cases.jaxrs.extensions.BoundStringReplacer;
import org.osgi.test.cases.jaxrs.extensions.ConfigurableStringReplacer;
import org.osgi.test.cases.jaxrs.extensions.ExtensionConfigProvider;
import org.osgi.test.cases.jaxrs.extensions.StringReplacer;
import org.osgi.test.cases.jaxrs.resources.NameBoundWhiteboardResource;
import org.osgi.test.cases.jaxrs.resources.WhiteboardResource;
import org.osgi.util.promise.Promise;

/**
 * This test covers the behaviours described in section 151.5
 */
public class ExtensionLifecyleTestCase extends AbstractJAXRSTestCase {
	

	/**
	 * Section 151.5 Register a simple JAX-RS extension resource and show that
	 * it gets applied to the request
	 * 
	 * @throws Exception
	 */
	public void testSimpleExtension() throws Exception {
		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(JaxRSWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);
		
		Promise<Void> awaitSelection = helper.awaitModification(runtime,
				5000);

		ServiceRegistration<WhiteboardResource> resourceReg = getContext()
				.registerService(WhiteboardResource.class, new WhiteboardResource(), properties);
		
		try {

			awaitSelection.getValue();
			
			String baseURI = getBaseURI();

			// Do a get

			HttpUriRequest getRequest = RequestBuilder
					.get(baseURI + "whiteboard/resource").build();

			CloseableHttpResponse httpResponse = client.execute(getRequest);

			String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("[buzz, fizz, fizzbuzz]", response);

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties = new Hashtable<>();
			properties.put(JaxRSWhiteboardConstants.JAX_RS_EXTENSION,
					Boolean.TRUE);
			ServiceRegistration<WriterInterceptor> extensionReg = getContext()
					.registerService(WriterInterceptor.class,
							new StringReplacer("fizz", "fizzbuzz"), properties);
			try {
				awaitSelection.getValue();

				httpResponse = client.execute(getRequest);

				response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);
			} finally {
				extensionReg.unregister();
			}
		} finally {
			resourceReg.unregister();
		}
	}

	/**
	 * Section 151.5.1 Show that name binding works correctly
	 * 
	 * @throws Exception
	 */
	public void testNameBoundExtension() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxRSWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<NameBoundWhiteboardResource> resourceReg = getContext()
				.registerService(NameBoundWhiteboardResource.class,
						new NameBoundWhiteboardResource(), properties);

		try {

			awaitSelection.getValue();

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties = new Hashtable<>();
			properties.put(JaxRSWhiteboardConstants.JAX_RS_EXTENSION,
					Boolean.TRUE);
			ServiceRegistration<WriterInterceptor> extensionReg = getContext()
					.registerService(WriterInterceptor.class,
							new BoundStringReplacer("fizz", "fizzbuzz"),
							properties);
			try {
				awaitSelection.getValue();

				String baseURI = getBaseURI();

				// One method should be intercepted but not the other

				HttpUriRequest getRequest = RequestBuilder
						.get(baseURI + "whiteboard/name/bound").build();

				CloseableHttpResponse httpResponse = client.execute(getRequest);

				String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

				getRequest = RequestBuilder
						.get(baseURI + "whiteboard/name/unbound").build();

				httpResponse = client.execute(getRequest);

				response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("[buzz, fizz, fizzbuzz]", response);

			} finally {
				extensionReg.unregister();
			}
		} finally {
			resourceReg.unregister();
		}
	}

	/**
	 * Section 151.5 Register a simple JAX-RS extension resource and show that
	 * it gets applied to the request
	 * 
	 * @throws Exception
	 */
	public void testExtensionOrdering() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxRSWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<WhiteboardResource> resourceReg = getContext()
				.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		try {

			awaitSelection.getValue();

			String baseURI = getBaseURI();

			// Do a get

			HttpUriRequest getRequest = RequestBuilder
					.get(baseURI + "whiteboard/resource").build();

			CloseableHttpResponse httpResponse = client.execute(getRequest);

			String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("[buzz, fizz, fizzbuzz]", response);

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties = new Hashtable<>();
			properties.put(JaxRSWhiteboardConstants.JAX_RS_EXTENSION,
					Boolean.TRUE);
			ServiceRegistration<WriterInterceptor> extensionReg = getContext()
					.registerService(WriterInterceptor.class,
							new StringReplacer("fizz", "fizzbuzz"), properties);
			try {
				awaitSelection.getValue();

				httpResponse = client.execute(getRequest);

				response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

				awaitSelection = helper.awaitModification(runtime, 5000);

				properties = new Hashtable<>();
				properties.put(JaxRSWhiteboardConstants.JAX_RS_EXTENSION,
						Boolean.TRUE);
				ServiceRegistration<WriterInterceptor> extensionReg2 = getContext()
						.registerService(WriterInterceptor.class,
								new StringReplacer("buzz", "fizzbuzz"),
								properties);
				try {
					awaitSelection.getValue();

					httpResponse = client.execute(getRequest);

					response = assertResponse(httpResponse, 200, TEXT_PLAIN);
					assertEquals(
							"The second writer interceptor was not applied after the first.",
							"[fizzbuzz, fizzfizzbuzz, fizzfizzbuzzfizzbuzz]",
							response);

					// Promote the second filter to be first
					awaitSelection = helper.awaitModification(runtime, 5000);

					properties.put(Constants.SERVICE_RANKING, Long.valueOf(10));
					extensionReg2.setProperties(properties);

					awaitSelection.getValue();

					httpResponse = client.execute(getRequest);

					response = assertResponse(httpResponse, 200, TEXT_PLAIN);
					assertEquals("The filter ranking was not observed",
							"[fizzbuzzbuzz, fizzbuzz, fizzbuzzfizzbuzzbuzz]",
							response);

					// Reset the second filter ranking to default
					awaitSelection = helper.awaitModification(runtime, 5000);

					properties.remove(Constants.SERVICE_RANKING);
					extensionReg2.setProperties(properties);

					awaitSelection.getValue();

					httpResponse = client.execute(getRequest);

					response = assertResponse(httpResponse, 200, TEXT_PLAIN);
					assertEquals(
							"[fizzbuzz, fizzfizzbuzz, fizzfizzbuzzfizzbuzz]",
							response);
				} finally {
					extensionReg2.unregister();
				}
			} finally {
				extensionReg.unregister();
			}
		} finally {
			resourceReg.unregister();
		}
	}

	/**
	 * Section 151.3 Register a JAX-RS resource which requires an extension
	 * 
	 * @throws Exception
	 */
	public void testResourceRequiresExtension() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxRSWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);
		properties.put(JaxRSWhiteboardConstants.JAX_RS_EXTENSION_SELECT,
				"(foo=bar)");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<WhiteboardResource> resourceReg = getContext()
				.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		try {

			awaitSelection.getValue();

			String baseURI = getBaseURI();

			// Do a get

			HttpUriRequest getRequest = RequestBuilder
					.get(baseURI + "whiteboard/resource").build();

			CloseableHttpResponse httpResponse = client.execute(getRequest);

			String response = assertResponse(httpResponse, 404, null);

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties = new Hashtable<>();
			properties.put(JaxRSWhiteboardConstants.JAX_RS_EXTENSION,
					Boolean.TRUE);
			properties.put("foo", "bar");

			ServiceRegistration<WriterInterceptor> extensionReg = getContext()
					.registerService(WriterInterceptor.class,
							new StringReplacer("fizz", "fizzbuzz"), properties);
			try {
				awaitSelection.getValue();

				httpResponse = client.execute(getRequest);

				response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);
			} finally {
				extensionReg.unregister();
			}
		} finally {
			resourceReg.unregister();
		}
	}

	/**
	 * Section 151.3 Register a JAX-RS extension which requires an extension
	 * 
	 * @throws Exception
	 */
	public void testExtensionRequiresExtension() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxRSWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<WhiteboardResource> resourceReg = getContext()
				.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		try {

			awaitSelection.getValue();

			String baseURI = getBaseURI();

			// Do a get

			HttpUriRequest getRequest = RequestBuilder
					.get(baseURI + "whiteboard/resource").build();

			CloseableHttpResponse httpResponse = client.execute(getRequest);

			String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("[buzz, fizz, fizzbuzz]", response);

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties = new Hashtable<>();
			properties.put(JaxRSWhiteboardConstants.JAX_RS_EXTENSION,
					Boolean.TRUE);
			properties.put(JaxRSWhiteboardConstants.JAX_RS_EXTENSION_SELECT,
					"(foo=bar)");

			ServiceRegistration<WriterInterceptor> extensionReg = getContext()
					.registerService(WriterInterceptor.class,
							getPrototypeServiceFactory(
									ConfigurableStringReplacer::new,
									(a, b) -> {}),
							properties);
			try {
				awaitSelection.getValue();

				httpResponse = client.execute(getRequest);

				response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("[buzz, fizz, fizzbuzz]", response);

				// Now register the contextresolver
				awaitSelection = helper.awaitModification(runtime, 5000);

				properties = new Hashtable<>();
				properties.put(JaxRSWhiteboardConstants.JAX_RS_EXTENSION,
						Boolean.TRUE);
				properties.put("foo", "bar");

				@SuppressWarnings("rawtypes")
				ServiceRegistration<ContextResolver> contextResolverReg = getContext()
						.registerService(ContextResolver.class,
								new ExtensionConfigProvider("fizz", "fizzbuzz"),
								properties);

				try {
					awaitSelection.getValue();

					httpResponse = client.execute(getRequest);

					response = assertResponse(httpResponse, 200, TEXT_PLAIN);
					assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);
				} finally {
					contextResolverReg.unregister();
				}
			} finally {
				extensionReg.unregister();
			}
		} finally {
			resourceReg.unregister();
		}
	}

	/**
	 * Section 151.3 Use whiteboard targeting for extensions
	 * 
	 * @throws Exception
	 */
	public void testSimpleWhiteboardTarget() throws Exception {

		Long serviceId = (Long) runtime.getProperty(Constants.SERVICE_ID);

		String selectFilter = "(service.id=" + serviceId + ")";
		String rejectFilter = "(!" + selectFilter + ")";

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxRSWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<WhiteboardResource> resourceReg = getContext()
				.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		try {

			awaitSelection.getValue();

			String baseURI = getBaseURI();

			// Do a get

			HttpUriRequest getRequest = RequestBuilder
					.get(baseURI + "whiteboard/resource").build();

			CloseableHttpResponse httpResponse = client.execute(getRequest);

			String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("[buzz, fizz, fizzbuzz]", response);

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties = new Hashtable<>();
			properties.put(JaxRSWhiteboardConstants.JAX_RS_EXTENSION,
					Boolean.TRUE);
			properties.put(JaxRSWhiteboardConstants.JAX_RS_WHITEBOARD_TARGET,
					selectFilter);
			ServiceRegistration<WriterInterceptor> extensionReg = getContext()
					.registerService(WriterInterceptor.class,
							new StringReplacer("fizz", "fizzbuzz"), properties);
			try {
				awaitSelection.getValue();

				httpResponse = client.execute(getRequest);

				response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

				// Change the target
				awaitSelection = helper.awaitModification(runtime, 5000);

				properties.put(
						JaxRSWhiteboardConstants.JAX_RS_WHITEBOARD_TARGET,
						rejectFilter);
				extensionReg.setProperties(properties);

				awaitSelection.getValue();

				httpResponse = client.execute(getRequest);

				response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("[buzz, fizz, fizzbuzz]", response);

				// Reset the target
				awaitSelection = helper.awaitModification(runtime, 5000);

				properties.put(
						JaxRSWhiteboardConstants.JAX_RS_WHITEBOARD_TARGET,
						selectFilter);
				extensionReg.setProperties(properties);

				awaitSelection.getValue();

				httpResponse = client.execute(getRequest);

				response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);
			} finally {
				extensionReg.unregister();
			}
		} finally {
			resourceReg.unregister();
		}
	}
}
