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

import static java.util.Collections.*;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.PAYMENT_REQUIRED;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;
import org.osgi.test.cases.jaxrs.applications.SimpleApplication;
import org.osgi.test.cases.jaxrs.extensions.BoundStringReplacer;
import org.osgi.test.cases.jaxrs.extensions.ConfigurableStringReplacer;
import org.osgi.test.cases.jaxrs.extensions.ExtensionConfigProvider;
import org.osgi.test.cases.jaxrs.extensions.HeaderStringReplacer;
import org.osgi.test.cases.jaxrs.extensions.NPEMapper;
import org.osgi.test.cases.jaxrs.extensions.OSGiTextMimeTypeCodec;
import org.osgi.test.cases.jaxrs.extensions.PromiseConverterProvider;
import org.osgi.test.cases.jaxrs.extensions.StringReplacer;
import org.osgi.test.cases.jaxrs.extensions.StringReplacerDynamicFeature;
import org.osgi.test.cases.jaxrs.extensions.StringReplacerFeature;
import org.osgi.test.cases.jaxrs.resources.EchoResource;
import org.osgi.test.cases.jaxrs.resources.NameBoundWhiteboardResource;
import org.osgi.test.cases.jaxrs.resources.StringResource;
import org.osgi.test.cases.jaxrs.resources.WhiteboardResource;
import org.osgi.test.cases.jaxrs.resources.WhiteboardResourceSubClass;
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
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);
		
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
			properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
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
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<NameBoundWhiteboardResource> resourceReg = getContext()
				.registerService(NameBoundWhiteboardResource.class,
						new NameBoundWhiteboardResource(), properties);

		try {

			awaitSelection.getValue();

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties = new Hashtable<>();
			properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
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
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

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
			properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
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
				properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
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
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);
		properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION_SELECT,
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
			properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
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
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

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
			properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
					Boolean.TRUE);
			properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION_SELECT,
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
				properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
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
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

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
			properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
					Boolean.TRUE);
			properties.put(JaxrsWhiteboardConstants.JAX_RS_WHITEBOARD_TARGET,
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
						JaxrsWhiteboardConstants.JAX_RS_WHITEBOARD_TARGET,
						rejectFilter);
				extensionReg.setProperties(properties);

				awaitSelection.getValue();

				httpResponse = client.execute(getRequest);

				response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("[buzz, fizz, fizzbuzz]", response);

				// Reset the target
				awaitSelection = helper.awaitModification(runtime, 5000);

				properties.put(
						JaxrsWhiteboardConstants.JAX_RS_WHITEBOARD_TARGET,
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

	/**
	 * Section 151.5 Register a simple JAX-RS extension feature and show that it
	 * gets applied to the request
	 * 
	 * @throws Exception
	 */
	public void testFeatureExtension() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

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
			properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
					Boolean.TRUE);
			ServiceRegistration<Feature> extensionReg = getContext()
					.registerService(Feature.class,
							new StringReplacerFeature("fizz", "fizzbuzz"),
							properties);
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
	 * Section 151.5 Register a JAX-RS extension dynamic feature and show that
	 * it gets applied to requests which fit the dynamic feature
	 * 
	 * @throws Exception
	 */
	public void testDynamicFeatureExtension() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

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

			// Register a second resource that won't be targeted by the
			// dynamic feature

			ServiceRegistration<WhiteboardResource> subclassResourceReg = getContext()
					.registerService(WhiteboardResource.class,
							new WhiteboardResourceSubClass(), properties);
			try {
				awaitSelection.getValue();

				HttpUriRequest getRequest2 = RequestBuilder
						.get(baseURI + "whiteboard/resource-subclass").build();

				httpResponse = client.execute(getRequest2);

				response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("[buzz, fizz, fizzbuzz]", response);

				awaitSelection = helper.awaitModification(runtime, 5000);

				properties = new Hashtable<>();
				properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
						Boolean.TRUE);
				ServiceRegistration<DynamicFeature> extensionReg = getContext()
						.registerService(DynamicFeature.class,
								new StringReplacerDynamicFeature("fizz",
										"fizzbuzz"),
								properties);
				try {
					awaitSelection.getValue();

					httpResponse = client.execute(getRequest);

					response = assertResponse(httpResponse, 200, TEXT_PLAIN);
					assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

					// Not applied to the second resource
					httpResponse = client.execute(getRequest2);

					response = assertResponse(httpResponse, 200, TEXT_PLAIN);
					assertEquals("[buzz, fizz, fizzbuzz]", response);
				} finally {
					extensionReg.unregister();
				}
			} finally {
				subclassResourceReg.unregister();
			}

		} finally {
			resourceReg.unregister();
		}
	}

	/**
	 * Section 151.5 Register a JAX-RS ReaderInterceptor and show that it gets
	 * applied to the request
	 * 
	 * @throws Exception
	 */
	public void testReaderInterceptorExtension() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<EchoResource> resourceReg = getContext()
				.registerService(EchoResource.class, new EchoResource(),
						properties);

		try {

			awaitSelection.getValue();

			String baseURI = getBaseURI();

			// Do a get

			HttpUriRequest getRequest = RequestBuilder
					.get(baseURI + "echo/body")
					.setEntity(new StringEntity("fizz"))
					.build();

			CloseableHttpResponse httpResponse = client.execute(getRequest);

			String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("fizz", response);

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties = new Hashtable<>();
			properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
					Boolean.TRUE);
			ServiceRegistration<ReaderInterceptor> extensionReg = getContext()
					.registerService(ReaderInterceptor.class,
							new StringReplacer("fizz", "fizzbuzz"), properties);
			try {
				awaitSelection.getValue();

				httpResponse = client.execute(getRequest);

				response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("fizzbuzz", response);
			} finally {
				extensionReg.unregister();
			}
		} finally {
			resourceReg.unregister();
		}
	}

	/**
	 * Section 151.5 Register a JAX-RS ContainerRequestFilter and show that it
	 * gets applied to the request
	 * 
	 * @throws Exception
	 */
	public void testContainerRequestFilterExtension() throws Exception {
		
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);
		
		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);
		
		ServiceRegistration<EchoResource> resourceReg = getContext()
				.registerService(EchoResource.class, new EchoResource(),
						properties);
		
		try {
			
			awaitSelection.getValue();
			
			String baseURI = getBaseURI();
			
			// Do a get
			
			HttpUriRequest getRequest = RequestBuilder
					.get(baseURI + "echo/header")
					.setHeader("echo", "fizz")
					.build();
			
			CloseableHttpResponse httpResponse = client.execute(getRequest);
			
			String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("fizz", response);
			
			awaitSelection = helper.awaitModification(runtime, 5000);
			
			properties = new Hashtable<>();
			properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
					Boolean.TRUE);
			ServiceRegistration<ContainerRequestFilter> extensionReg = getContext()
					.registerService(ContainerRequestFilter.class,
							new HeaderStringReplacer("fizz", "fizzbuzz"),
							properties);
			try {
				awaitSelection.getValue();
				
				httpResponse = client.execute(getRequest);
				
				response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("fizzbuzz",
						httpResponse.getFirstHeader("echo").getValue());
				assertEquals("fizzbuzz", response);
			} finally {
				extensionReg.unregister();
			}
		} finally {
			resourceReg.unregister();
		}
	}

	/**
	 * Section 151.5 Register a JAX-RS ContainerResponseFilter and show that it
	 * gets applied to the request
	 * 
	 * @throws Exception
	 */
	public void testContainerResponseFilterExtension() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<EchoResource> resourceReg = getContext()
				.registerService(EchoResource.class, new EchoResource(),
						properties);

		try {

			awaitSelection.getValue();

			String baseURI = getBaseURI();

			// Do a get

			HttpUriRequest getRequest = RequestBuilder
					.get(baseURI + "echo/header")
					.setHeader("echo", "fizz")
					.build();

			CloseableHttpResponse httpResponse = client.execute(getRequest);

			String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("fizz", response);

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties = new Hashtable<>();
			properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
					Boolean.TRUE);
			ServiceRegistration<ContainerResponseFilter> extensionReg = getContext()
					.registerService(ContainerResponseFilter.class,
							new HeaderStringReplacer("fizz", "fizzbuzz"),
							properties);
			try {
				awaitSelection.getValue();

				httpResponse = client.execute(getRequest);

				response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("fizzbuzz",
						httpResponse.getFirstHeader("echo").getValue());
				assertEquals("fizz", response);
			} finally {
				extensionReg.unregister();
			}
		} finally {
			resourceReg.unregister();
		}
	}

	/**
	 * Section 151.5 Register a JAX-RS MessageBodyReader and show that it
	 * gets applied to the request
	 * 
	 * @throws Exception
	 */
	public void testMessageBodyReaderExtension() throws Exception {
		
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);
		
		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);
		
		ServiceRegistration<EchoResource> resourceReg = getContext()
				.registerService(EchoResource.class, new EchoResource(),
						properties);
		
		try {
			
			awaitSelection.getValue();
			
			String baseURI = getBaseURI();
			
			// Do a get
			
			HttpUriRequest getRequest = RequestBuilder
					.get(baseURI + "echo/body")
					.setEntity(new StringEntity("fizz",
							ContentType.create("osgi/text", "UTF-8")))
					.build();
			
			CloseableHttpResponse httpResponse = client.execute(getRequest);
			
			String response = assertResponse(httpResponse, 200,
					"osgi/text;charset=UTF-8");
			assertEquals("fizz", response);
			
			awaitSelection = helper.awaitModification(runtime, 5000);
			
			properties = new Hashtable<>();
			properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
					Boolean.TRUE);
			@SuppressWarnings("rawtypes")
			ServiceRegistration<MessageBodyReader> extensionReg = getContext()
					.registerService(MessageBodyReader.class,
							new OSGiTextMimeTypeCodec(), properties);
			try {
				awaitSelection.getValue();
				
				httpResponse = client.execute(getRequest);
				
				response = assertResponse(httpResponse, 200,
						"osgi/text;charset=UTF-8");
				assertEquals("OSGi Read: fizz", response);
			} finally {
				extensionReg.unregister();
			}
		} finally {
			resourceReg.unregister();
		}
	}

	/**
	 * Section 151.5 Register a JAX-RS MessageBodyWriter and show that it gets
	 * applied to the request
	 * 
	 * @throws Exception
	 */
	public void testMessageBodyWriterExtension() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<EchoResource> resourceReg = getContext()
				.registerService(EchoResource.class, new EchoResource(),
						properties);

		try {

			awaitSelection.getValue();

			String baseURI = getBaseURI();

			// Do a get

			HttpUriRequest getRequest = RequestBuilder
					.get(baseURI + "echo/body")
					.setEntity(new StringEntity("fizz",
							ContentType.create("osgi/text", "UTF-8")))
					.build();

			CloseableHttpResponse httpResponse = client.execute(getRequest);

			String response = assertResponse(httpResponse, 200,
					"osgi/text;charset=UTF-8");
			assertEquals("fizz", response);

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties = new Hashtable<>();
			properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
					Boolean.TRUE);
			@SuppressWarnings("rawtypes")
			ServiceRegistration<MessageBodyWriter> extensionReg = getContext()
					.registerService(MessageBodyWriter.class,
							new OSGiTextMimeTypeCodec(), properties);
			try {
				awaitSelection.getValue();

				httpResponse = client.execute(getRequest);

				response = assertResponse(httpResponse, 200,
						"osgi/text;charset=UTF-8");
				assertEquals("OSGi Write: fizz", response);
			} finally {
				extensionReg.unregister();
			}
		} finally {
			resourceReg.unregister();
		}
	}

	/**
	 * Section 151.5 Register a JAX-RS ParamConverterProvider and show that it
	 * gets applied to the request
	 * 
	 * @throws Exception
	 */
	public void testParamConverterProviderExtension() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<EchoResource> resourceReg = getContext()
				.registerService(EchoResource.class, new EchoResource(),
						properties);

		try {

			awaitSelection.getValue();

			String baseURI = getBaseURI();

			// Do a get

			HttpUriRequest getRequest = RequestBuilder
					.get(baseURI + "echo/promise")
					.addHeader("echo", "fizz")
					.build();

			CloseableHttpResponse httpResponse = client.execute(getRequest);

			assertResponse(httpResponse, 500, null);

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties = new Hashtable<>();
			properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
					Boolean.TRUE);
			ServiceRegistration<ParamConverterProvider> extensionReg = getContext()
					.registerService(ParamConverterProvider.class,
							new PromiseConverterProvider(), properties);
			try {
				awaitSelection.getValue();

				httpResponse = client.execute(getRequest);

				String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("fizz", response);
			} finally {
				extensionReg.unregister();
			}
		} finally {
			resourceReg.unregister();
		}
	}

	/**
	 * Section 151.5 Register a JAX-RS ExceptionMapper and show that it gets
	 * applied to the request
	 * 
	 * @throws Exception
	 */
	public void testExceptionMapperExtension() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<StringResource> resourceReg = getContext()
				.registerService(StringResource.class, new StringResource(null),
						properties);

		try {

			awaitSelection.getValue();

			String baseURI = getBaseURI();

			// Do a get

			HttpUriRequest getRequest = RequestBuilder
					.get(baseURI + "whiteboard/string/length").build();

			CloseableHttpResponse httpResponse = client.execute(getRequest);

			assertResponse(httpResponse, 500, null);

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties = new Hashtable<>();
			properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
					Boolean.TRUE);
			@SuppressWarnings("rawtypes")
			ServiceRegistration<ExceptionMapper> extensionReg = getContext()
					.registerService(ExceptionMapper.class, new NPEMapper(),
							properties);
			try {
				awaitSelection.getValue();

				httpResponse = client.execute(getRequest);

				String response = assertResponse(httpResponse,
						PAYMENT_REQUIRED.getStatusCode(), TEXT_PLAIN);
				assertEquals("NPE", response);
			} finally {
				extensionReg.unregister();
			}
		} finally {
			resourceReg.unregister();
		}
	}

	/**
	 * Section 151.4.2 Singleton services must be released if the application
	 * goes out of scope
	 * 
	 * @throws Exception
	 */
	public void testExtensionWhenApplicationChanges() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION, Boolean.TRUE);
		properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
				"(foo=bar)");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		Semaphore getSemaphore = new Semaphore(0);

		Semaphore releaseSemaphore = new Semaphore(0);

		ServiceRegistration<WriterInterceptor> reg = getContext()
				.registerService(WriterInterceptor.class,
						getServiceFactory(() -> {
							getSemaphore.release();
							return new StringReplacer("fizz", "fizzbuzz");
						}, (sr, s) -> releaseSemaphore.release()), properties);

		try {

			awaitSelection.getValue();

			properties = new Hashtable<>();
			properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
					"/test");
			properties.put("foo", "bar");

			awaitSelection = helper.awaitModification(runtime, 5000);

			ServiceRegistration<Application> appReg = getContext()
					.registerService(Application.class,
							new SimpleApplication(singleton(WhiteboardResource.class), emptySet()),
							properties);

			try {

				awaitSelection.getValue();

				String baseURI = getBaseURI();

				// Do a get

				CloseableHttpResponse httpResponse = client.execute(
						RequestBuilder.get(baseURI + "test/whiteboard/resource")
								.build());

				String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

			} finally {
				appReg.unregister();
			}

			assertTrue(getSemaphore.availablePermits() > 0);
			assertTrue(
					releaseSemaphore.tryAcquire(getSemaphore.availablePermits(),
							100, TimeUnit.MILLISECONDS));

		} finally {
			reg.unregister();
		}
	}
}
