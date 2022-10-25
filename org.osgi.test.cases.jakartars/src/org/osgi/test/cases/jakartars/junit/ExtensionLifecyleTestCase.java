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
import static jakarta.ws.rs.core.Response.Status.PAYMENT_REQUIRED;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jakartars.whiteboard.JakartarsWhiteboardConstants;
import org.osgi.test.cases.jakartars.applications.SimpleApplication;
import org.osgi.test.cases.jakartars.extensions.BoundStringReplacer;
import org.osgi.test.cases.jakartars.extensions.ConfigurableStringReplacer;
import org.osgi.test.cases.jakartars.extensions.ExtensionConfigProvider;
import org.osgi.test.cases.jakartars.extensions.HeaderStringReplacer;
import org.osgi.test.cases.jakartars.extensions.NPEMapper;
import org.osgi.test.cases.jakartars.extensions.OSGiTextMimeTypeCodec;
import org.osgi.test.cases.jakartars.extensions.PromiseConverterProvider;
import org.osgi.test.cases.jakartars.extensions.StringReplacer;
import org.osgi.test.cases.jakartars.extensions.StringReplacerDynamicFeature;
import org.osgi.test.cases.jakartars.extensions.StringReplacerFeature;
import org.osgi.test.cases.jakartars.resources.EchoResource;
import org.osgi.test.cases.jakartars.resources.NameBoundWhiteboardResource;
import org.osgi.test.cases.jakartars.resources.StringResource;
import org.osgi.test.cases.jakartars.resources.WhiteboardResource;
import org.osgi.test.cases.jakartars.resources.WhiteboardResourceSubClass;
import org.osgi.util.promise.Promise;

import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.WriterInterceptor;

/**
 * This test covers the behaviours described in section 151.5
 */
public class ExtensionLifecyleTestCase extends AbstractJakartarsTestCase {
	

	/**
	 * Section 151.5 Register a simple JAX-RS extension resource and show that
	 * it gets applied to the request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSimpleExtension() throws Exception {
		
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

		HttpUriRequest getRequest = RequestBuilder
				.get(baseURI + "whiteboard/resource")
				.build();

		CloseableHttpResponse httpResponse = client.execute(getRequest);

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Register an extension
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		context.registerService(WriterInterceptor.class,
						new StringReplacer("fizz", "fizzbuzz"), properties);
		awaitSelection.getValue();

		// Do a get
		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

	}

	/**
	 * Section 151.5.1 Show that name binding works correctly
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNameBoundExtension() throws Exception {

		// Register a whiteboard resource

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(NameBoundWhiteboardResource.class,
						new NameBoundWhiteboardResource(), properties);

		awaitSelection.getValue();

		// Register a whiteboard extension
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		context.registerService(WriterInterceptor.class,
				new BoundStringReplacer("fizz", "fizzbuzz"), properties);

		String baseURI = getBaseURI();

		// One method should be intercepted but not the other

		HttpUriRequest getRequest = RequestBuilder
				.get(baseURI + "whiteboard/name/bound")
				.build();

		CloseableHttpResponse httpResponse = client.execute(getRequest);

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

		getRequest = RequestBuilder.get(baseURI + "whiteboard/name/unbound")
				.build();

		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

	}

	/**
	 * Section 151.5 Register a simple JAX-RS extension resource and show that
	 * it gets applied to the request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExtensionOrdering() throws Exception {

		// Register a whiteboard resource

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		HttpUriRequest getRequest = RequestBuilder
				.get(baseURI + "whiteboard/resource")
				.build();

		CloseableHttpResponse httpResponse = client.execute(getRequest);

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Register a whiteboard extension

		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		context.registerService(WriterInterceptor.class,
				new StringReplacer("fizz", "fizzbuzz"), properties);

		awaitSelection.getValue();

		// Do a get
		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

		// Register another extension
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		ServiceRegistration<WriterInterceptor> extensionReg2 = context
				.registerService(WriterInterceptor.class,
						new StringReplacer("buzz", "fizzbuzz"), properties);

		awaitSelection.getValue();

		// Do a get

		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[fizzbuzz, fizzfizzbuzz, fizzfizzbuzzfizzbuzz]", response,
				"The second writer interceptor was not applied after the first.");

		// Promote the second filter to be first
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties.put(Constants.SERVICE_RANKING, Integer.valueOf(10));
		extensionReg2.setProperties(properties);

		awaitSelection.getValue();

		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[fizzbuzzbuzz, fizzbuzz, fizzbuzzfizzbuzzbuzz]", response,
				"The filter ranking was not observed");

		// Reset the second filter ranking to default
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties.remove(Constants.SERVICE_RANKING);
		extensionReg2.setProperties(properties);

		awaitSelection.getValue();

		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[fizzbuzz, fizzfizzbuzz, fizzfizzbuzzfizzbuzz]",
				response);

	}

	/**
	 * Section 151.3 Register a JAX-RS resource which requires an extension
	 * 
	 * @throws Exception
	 */
	@Test
	public void testResourceRequiresExtension() throws Exception {

		// Register a whiteboard resource with an extension requirement

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION_SELECT,
				"(foo=bar)");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		HttpUriRequest getRequest = RequestBuilder
				.get(baseURI + "whiteboard/resource")
				.build();

		CloseableHttpResponse httpResponse = client.execute(getRequest);

		String response = assertResponse(httpResponse, 404, null);

		// Register the extension
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		properties.put("foo", "bar");

		context.registerService(WriterInterceptor.class,
				new StringReplacer("fizz", "fizzbuzz"), properties);

		awaitSelection.getValue();

		// The get should work this time
		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

	}

	/**
	 * Section 151.3 Register a JAX-RS extension which requires an extension
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExtensionRequiresExtension() throws Exception {

		// Register a whiteboard resource
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		HttpUriRequest getRequest = RequestBuilder
				.get(baseURI + "whiteboard/resource")
				.build();

		CloseableHttpResponse httpResponse = client.execute(getRequest);

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Register a whiteboard extension

		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION_SELECT,
				"(foo=bar)");

		context.registerService(WriterInterceptor.class,
				getPrototypeServiceFactory(ConfigurableStringReplacer::new,
						(a, b) -> {
						}),
				properties);

		awaitSelection.getValue();

		// Do another get
		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Now register the contextresolver
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		properties.put("foo", "bar");

		context.registerService(ContextResolver.class,
				new ExtensionConfigProvider("fizz", "fizzbuzz"), properties);

		awaitSelection.getValue();

		// Do a final get
		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

	}

	/**
	 * Section 151.3 Use whiteboard targeting for extensions
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSimpleWhiteboardTarget() throws Exception {

		Long serviceId = (Long) runtime.getProperty(Constants.SERVICE_ID);

		String selectFilter = "(service.id=" + serviceId + ")";
		String rejectFilter = "(!" + selectFilter + ")";

		// Register a whiteboard resource

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		HttpUriRequest getRequest = RequestBuilder
				.get(baseURI + "whiteboard/resource")
				.build();

		CloseableHttpResponse httpResponse = client.execute(getRequest);

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Register an extension targeting the whiteboard

		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_WHITEBOARD_TARGET,
				selectFilter);
		ServiceRegistration<WriterInterceptor> extensionReg = context
				.registerService(WriterInterceptor.class,
						new StringReplacer("fizz", "fizzbuzz"), properties);

		awaitSelection.getValue();

		// Do another get
		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

		// Change the target
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_WHITEBOARD_TARGET,
				rejectFilter);
		extensionReg.setProperties(properties);

		awaitSelection.getValue();

		// Do another get

		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Reset the target
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_WHITEBOARD_TARGET,
				selectFilter);
		extensionReg.setProperties(properties);

		awaitSelection.getValue();

		// Do one last get
		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

	}

	/**
	 * Section 151.5 Register a simple JAX-RS extension feature and show that it
	 * gets applied to the request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFeatureExtension() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		HttpUriRequest getRequest = RequestBuilder
				.get(baseURI + "whiteboard/resource")
				.build();

		CloseableHttpResponse httpResponse = client.execute(getRequest);

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		context.registerService(Feature.class,
				new StringReplacerFeature("fizz", "fizzbuzz"), properties);

		awaitSelection.getValue();

		// Do another get
		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

	}

	/**
	 * Section 151.5 Register a JAX-RS extension dynamic feature and show that
	 * it gets applied to requests which fit the dynamic feature
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDynamicFeatureExtension() throws Exception {

		// Register a whiteboard resource

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		HttpUriRequest getRequest = RequestBuilder
				.get(baseURI + "whiteboard/resource")
				.build();

		CloseableHttpResponse httpResponse = client.execute(getRequest);

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		awaitSelection = helper.awaitModification(runtime, 5000);

		// Register a second resource that won't be targeted by the
		// dynamic feature

		context.registerService(WhiteboardResource.class,
				new WhiteboardResourceSubClass(), properties);

		awaitSelection.getValue();

		// Get the second resource
		HttpUriRequest getRequest2 = RequestBuilder
				.get(baseURI + "whiteboard/resource-subclass")
				.build();

		httpResponse = client.execute(getRequest2);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Register the whiteboard extension
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		context.registerService(DynamicFeature.class,
						new StringReplacerDynamicFeature("fizz", "fizzbuzz"),
						properties);

		awaitSelection.getValue();

		// Do the gets to check the result
		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

		// Not applied to the second resource
		httpResponse = client.execute(getRequest2);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

	}

	/**
	 * Section 151.5 Register a JAX-RS ReaderInterceptor and show that it gets
	 * applied to the request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReaderInterceptorExtension() throws Exception {

		// Register a whiteboard resource

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(EchoResource.class, new EchoResource(),
						properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		HttpUriRequest getRequest = RequestBuilder.get(baseURI + "echo/body")
				.setEntity(new StringEntity("fizz"))
				.build();

		CloseableHttpResponse httpResponse = client.execute(getRequest);

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("fizz", response);

		// Register a whiteboard extension
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		context.registerService(ReaderInterceptor.class,
				new StringReplacer("fizz", "fizzbuzz"), properties);

		awaitSelection.getValue();

		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("fizzbuzz", response);

	}

	/**
	 * Section 151.5 Register a JAX-RS ContainerRequestFilter and show that it
	 * gets applied to the request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testContainerRequestFilterExtension() throws Exception {
		
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);
		
		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);
		
		context.registerService(EchoResource.class, new EchoResource(),
						properties);
		
		awaitSelection.getValue();
			
		String baseURI = getBaseURI();
			
		// Do a get
			
		HttpUriRequest getRequest = RequestBuilder.get(baseURI + "echo/header")
				.setHeader("echo", "fizz")
				.build();

		CloseableHttpResponse httpResponse = client.execute(getRequest);

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("fizz", response);

		// Register the extension
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		context.registerService(ContainerRequestFilter.class,
				new HeaderStringReplacer("fizz", "fizzbuzz"), properties);
		awaitSelection.getValue();
			
		// Do a get
		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("fizzbuzz",
				httpResponse.getFirstHeader("echo").getValue());
		assertEquals("fizzbuzz", response);
	}

	/**
	 * Section 151.5 Register a JAX-RS ContainerResponseFilter and show that it
	 * gets applied to the request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testContainerResponseFilterExtension() throws Exception {

		// Register a whiteboard resource

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(EchoResource.class, new EchoResource(),
						properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		HttpUriRequest getRequest = RequestBuilder.get(baseURI + "echo/header")
				.setHeader("echo", "fizz")
				.build();

		CloseableHttpResponse httpResponse = client.execute(getRequest);

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("fizz", response);

		// Register a whiteboard extension
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		context.registerService(ContainerResponseFilter.class,
				new HeaderStringReplacer("fizz", "fizzbuzz"), properties);

		awaitSelection.getValue();

		// Do a get
		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("fizzbuzz",
				httpResponse.getFirstHeader("echo").getValue());
		assertEquals("fizz", response);

	}

	/**
	 * Section 151.5 Register a JAX-RS MessageBodyReader and show that it
	 * gets applied to the request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMessageBodyReaderExtension() throws Exception {

		// Register a whiteboard resource
		
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);
		
		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);
		
		context.registerService(EchoResource.class, new EchoResource(),
						properties);
		
		awaitSelection.getValue();
			
		String baseURI = getBaseURI();
			
		// Do a get
			
		HttpUriRequest getRequest = RequestBuilder.get(baseURI + "echo/body")
				.setEntity(new StringEntity("fizz",
						ContentType.create("osgi/text", "UTF-8")))
				.build();

		CloseableHttpResponse httpResponse = client.execute(getRequest);

		String response = assertResponse(httpResponse, 200,
				"osgi/text;charset=UTF-8");
		assertEquals("fizz", response);

		awaitSelection = helper.awaitModification(runtime, 5000);

		// Register a whiteboard extension
		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		context.registerService(MessageBodyReader.class,
						new OSGiTextMimeTypeCodec(), properties);
		awaitSelection.getValue();
			
		// Do a get
		httpResponse = client.execute(getRequest);
			
		response = assertResponse(httpResponse, 200,
					"osgi/text;charset=UTF-8");
		assertEquals("OSGi Read: fizz", response);

	}

	/**
	 * Section 151.5 Register a JAX-RS MessageBodyWriter and show that it gets
	 * applied to the request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMessageBodyWriterExtension() throws Exception {

		// Register a whiteboard resource

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(EchoResource.class, new EchoResource(),
						properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		HttpUriRequest getRequest = RequestBuilder.get(baseURI + "echo/body")
				.setEntity(new StringEntity("fizz",
						ContentType.create("osgi/text", "UTF-8")))
				.build();

		CloseableHttpResponse httpResponse = client.execute(getRequest);

		String response = assertResponse(httpResponse, 200,
				"osgi/text;charset=UTF-8");
		assertEquals("fizz", response);

		// Register a whiteboard extension
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		context.registerService(MessageBodyWriter.class,
				new OSGiTextMimeTypeCodec(), properties);

		awaitSelection.getValue();

		// Do a final get
		httpResponse = client.execute(getRequest);

		response = assertResponse(httpResponse, 200, "osgi/text;charset=UTF-8");
		assertEquals("OSGi Write: fizz", response);

	}

	/**
	 * Section 151.5 Register a JAX-RS ParamConverterProvider and show that it
	 * gets applied to the request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testParamConverterProviderExtension() throws Exception {

		// Register a whiteboard resource

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(EchoResource.class, new EchoResource(),
						properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		HttpUriRequest getRequest = RequestBuilder.get(baseURI + "echo/promise")
				.addHeader("echo", "fizz")
				.build();

		CloseableHttpResponse httpResponse = client.execute(getRequest);

		assertResponse(httpResponse, 500, null);

		// Register a whiteboard extension
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		context.registerService(ParamConverterProvider.class,
				new PromiseConverterProvider(), properties);
		awaitSelection.getValue();

		// Do a get
		httpResponse = client.execute(getRequest);

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("fizz", response);

	}

	/**
	 * Section 151.5 Register a JAX-RS ExceptionMapper and show that it gets
	 * applied to the request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExceptionMapperExtension() throws Exception {

		// Register a whiteboard resource

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(StringResource.class, new StringResource(null),
						properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		HttpUriRequest getRequest = RequestBuilder
				.get(baseURI + "whiteboard/string/length")
				.build();

		CloseableHttpResponse httpResponse = client.execute(getRequest);

		assertResponse(httpResponse, 500, null);

		// Register a whiteboard extension

		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		context.registerService(ExceptionMapper.class, new NPEMapper(),
				properties);
		awaitSelection.getValue();

		httpResponse = client.execute(getRequest);

		String response = assertResponse(httpResponse,
				PAYMENT_REQUIRED.getStatusCode(), TEXT_PLAIN);
		assertEquals("NPE", response);

	}

	/**
	 * Section 151.4.2 Singleton services must be released if the application
	 * goes out of scope
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExtensionWhenApplicationChanges() throws Exception {

		// Register a whiteboard extension selecting an application
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_SELECT,
				"(foo=bar)");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		Semaphore getSemaphore = new Semaphore(0);

		Semaphore releaseSemaphore = new Semaphore(0);

		context.registerService(WriterInterceptor.class,
						getServiceFactory(() -> {
							getSemaphore.release();
							return new StringReplacer("fizz", "fizzbuzz");
						}, (sr, s) -> releaseSemaphore.release()), properties);

		awaitSelection.getValue();

		// Register the application to be targeted
		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test");
		properties.put("foo", "bar");

		awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> appReg = context.registerService(
				Application.class,
				new SimpleApplication(singleton(WhiteboardResource.class),
						emptySet()),
				properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		CloseableHttpResponse httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test/whiteboard/resource")
						.build());

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

		// Unregister the application and check the extension is released

		appReg.unregister();

		assertTrue(getSemaphore.availablePermits() > 0);
		assertTrue(releaseSemaphore.tryAcquire(getSemaphore.availablePermits(),
				100, TimeUnit.MILLISECONDS));

	}
}
