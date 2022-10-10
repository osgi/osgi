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
import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.junit.Assume;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jakartars.whiteboard.JakartarsWhiteboardConstants;
import org.osgi.test.cases.jakartars.applications.ApplicationWithPathAnnotation;
import org.osgi.test.cases.jakartars.applications.SimpleApplication;
import org.osgi.test.cases.jakartars.extensions.ConfigurableStringReplacer;
import org.osgi.test.cases.jakartars.extensions.ExtensionConfigProvider;
import org.osgi.test.cases.jakartars.extensions.StringReplacer;
import org.osgi.test.cases.jakartars.resources.ConfigurationAwareResource;
import org.osgi.test.cases.jakartars.resources.SessionManipulator;
import org.osgi.test.cases.jakartars.resources.StringResource;
import org.osgi.test.cases.jakartars.resources.WhiteboardResource;
import org.osgi.util.promise.Promise;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.WriterInterceptor;

/**
 * This test covers the lifecycle behaviours described in section 151.6
 */
public class ApplicationLifecyleTestCase extends AbstractJakartarsTestCase {

	/**
	 * Section 151.6 Register a simple JAX-RS application and show that its
	 * resources get used
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSimpleSingletonResource() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(Application.class,
						new SimpleApplication(emptySet(),
								singleton(new WhiteboardResource())),
						properties);

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
	}

	/**
	 * Section 151.6 Register a simple JAX-RS application and show that its
	 * resources get used
	 *
	 * @throws Exception
	 */
	@Test
	public void testSimplePrototypeResource() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(Application.class,
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
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Do a delete

		httpResponse = client.execute(
				RequestBuilder.delete(baseURI + "test/whiteboard/resource/buzz")
						.build());

		response = assertResponse(httpResponse, 200, null);
		assertEquals("", response);

		// Do another get to show the statelessness

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test/whiteboard/resource")
						.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

	}

	/**
	 * Section 151.6 Application Base service property
	 * 
	 * @throws Exception
	 */
	@Test
	public void testApplicationBase() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = context
				.registerService(Application.class,
						new SimpleApplication(emptySet(),
								singleton(new WhiteboardResource())),
						properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		CloseableHttpResponse httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test/whiteboard/resource")
						.build());

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Change the base and re-request (note that it is valid to have no
		// leading '/')
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"changed");
		reg.setProperties(properties);

		awaitSelection.getValue();
			
		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "changed/whiteboard/resource")
						.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

	}

	/**
	 * Section 151.6 Application Base service property
	 * 
	 * @throws Exception
	 */
	@Test
	public void testApplicationPathAnnotation() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = context
				.registerService(Application.class,
						new ApplicationWithPathAnnotation(emptySet(),
								singleton(new WhiteboardResource())),
						properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		CloseableHttpResponse httpResponse = client.execute(RequestBuilder
				.get(baseURI + "test/annotated/whiteboard/resource")
				.build());

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Change the base and re-request (note that it is valid to have no
		// leading '/'
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"changed");
		reg.setProperties(properties);

		httpResponse = client.execute(RequestBuilder
				.get(baseURI + "changed/annotated/whiteboard/resource")
				.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

	}

	/**
	 * Section 151.6 Application Select service property
	 * 
	 * @throws Exception
	 */
	@Test
	public void testApplicationWhiteboardResource() throws Exception {

		// Register the first empty application at "test"

		Dictionary<String,Object> properties = new Hashtable<>();

		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_NAME, "test");
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(Application.class,
				new SimpleApplication(emptySet(), emptySet()), properties);

		awaitSelection.getValue();

		// Register the second empty application at "test2"

		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_NAME,
				"test2");
		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test2");

		awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(Application.class,
				new SimpleApplication(emptySet(), emptySet()), properties);

		awaitSelection.getValue();

		// Register an untargeted whiteboard resource
		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);

		awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<WhiteboardResource> resourceReg = context
				.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		awaitSelection.getValue();


		// Do a get on each application, and the default application
		String baseURI = getBaseURI();

		CloseableHttpResponse httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test/whiteboard/resource")
						.build());

		String response = assertResponse(httpResponse, 404, null);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test2/whiteboard/resource")
						.build());

		response = assertResponse(httpResponse, 404, null);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/resource").build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Rebind the resource targeting test2

		awaitSelection = helper.awaitModification(runtime, 5000);

		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_SELECT,
				"(osgi.jakartars.name=test2)");
		resourceReg.setProperties(properties);

		awaitSelection.getValue();

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test/whiteboard/resource")
						.build());

		response = assertResponse(httpResponse, 404, null);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test2/whiteboard/resource")
						.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/resource").build());

		response = assertResponse(httpResponse, 404, null);

		// Target just the default application

		awaitSelection = helper.awaitModification(runtime, 5000);

		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_SELECT,
				"(osgi.jakartars.name="
						+ JakartarsWhiteboardConstants.JAKARTA_RS_DEFAULT_APPLICATION
						+ ")");
		resourceReg.setProperties(properties);

		awaitSelection.getValue();

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test/whiteboard/resource")
						.build());
		response = assertResponse(httpResponse, 404, null);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test2/whiteboard/resource")
						.build());

		response = assertResponse(httpResponse, 404, null);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/resource").build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Target all the applications

		awaitSelection = helper.awaitModification(runtime, 5000);

		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_SELECT,
				"(osgi.jakartars.name=*)");
		resourceReg.setProperties(properties);

		awaitSelection.getValue();

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test/whiteboard/resource")
						.build());
		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test2/whiteboard/resource")
						.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/resource").build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

	}

	/**
	 * Section 151.6 Application Isolation
	 * 
	 * @throws Exception
	 */
	@Test
	public void testApplicationIsolationExtensions() throws Exception {

		// Register the first application at "test"

		Dictionary<String,Object> properties = new Hashtable<>();

		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_NAME, "test");
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(Application.class,
				new SimpleApplication(emptySet(),
						singleton(new StringReplacer("fizz", "fizzbuzz"))),
				properties);

		awaitSelection.getValue();

		// Register the second application at "test2"

		awaitSelection = helper.awaitModification(runtime, 5000);

		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_NAME, "test2");
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test2");
		context.registerService(Application.class,
				new SimpleApplication(emptySet(), emptySet()), properties);

		awaitSelection.getValue();

		// Register a whiteboard resource targeting all applications

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);
		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_SELECT,
				"(osgi.jakartars.name=*)");

		awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(WhiteboardResource.class,
				new WhiteboardResource(), properties);


		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get on each application, and the default application

		CloseableHttpResponse httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test/whiteboard/resource")
						.build());

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test2/whiteboard/resource")
						.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/resource").build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

	}

	/**
	 * Section 151.6 Application Isolation
	 * 
	 * @throws Exception
	 */
	@Test
	public void testApplicationIsolationExtensions2() throws Exception {

		// Register the first empty application at "test"

		Dictionary<String,Object> properties = new Hashtable<>();

		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_NAME, "test");
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		// Register the second empty application at "test2"

		context.registerService(Application.class,
				new SimpleApplication(emptySet(), emptySet()), properties);

		awaitSelection.getValue();

		awaitSelection = helper.awaitModification(runtime, 5000);

		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_NAME,
				"test2");
		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test2");
		context.registerService(Application.class,
				new SimpleApplication(emptySet(), emptySet()), properties);

		awaitSelection.getValue();

		// Register a whiteboard targeting all the applications

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);
		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_SELECT,
				"(osgi.jakartars.name=*)");

		awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(WhiteboardResource.class,
				new WhiteboardResource(), properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get on each application, and the default application

		CloseableHttpResponse httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test/whiteboard/resource")
						.build());

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test2/whiteboard/resource")
						.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/resource").build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Register an extension targeting both test applications

		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_SELECT,
				"(osgi.jakartars.name=t*)");
		context.registerService(WriterInterceptor.class,
				new StringReplacer("fizz", "fizzbuzz"), properties);

		awaitSelection.getValue();

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test/whiteboard/resource")
						.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test2/whiteboard/resource")
						.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/resource").build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

	}

	/**
	 * Section 151.3 Extension select applied to application properties
	 * 
	 * @throws Exception
	 */
	@Test
	public void testApplicationProvidedExtensionDependency() throws Exception {

		// Register the first empty application at "test"

		Dictionary<String,Object> properties = new Hashtable<>();

		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_NAME, "test");
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(Application.class,
				new SimpleApplication(emptySet(), emptySet()), properties);

		awaitSelection.getValue();

		// Register the second empty application at "test2"

		awaitSelection = helper.awaitModification(runtime, 5000);

		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_NAME,
				"test2");
		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test2");
		properties.put("replacer-config", "fizz-buzz");
		context.registerService(Application.class,
				new SimpleApplication(emptySet(), singleton(
						new ExtensionConfigProvider("fizz", "fizzbuzz"))),
				properties);

		awaitSelection.getValue();

		// Register a whiteboard resource targeting all applications

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);
		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_SELECT,
				"(osgi.jakartars.name=*)");

		awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get on each application, and the default application

		CloseableHttpResponse httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test/whiteboard/resource")
						.build());

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test2/whiteboard/resource")
						.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/resource").build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Register an extension which needs config
		// This will only be satisfied by one of the applications

		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_SELECT,
				"(osgi.jakartars.name=*)");
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION_SELECT,
				"(replacer-config=*)");
		context.registerService(WriterInterceptor.class,
				new ConfigurableStringReplacer(), properties);

		awaitSelection.getValue();

		// Do a get on each application, and the default application

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test/whiteboard/resource")
						.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test2/whiteboard/resource")
						.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/resource").build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

	}

	/**
	 * Section 151.6.2 Extension select prevents application binding without the
	 * extension
	 * 
	 * @throws Exception
	 */
	@Test
	public void testApplicationExtensionDependency() throws Exception {

		// Register the first empty application at "test"
		
		Dictionary<String,Object> properties = new Hashtable<>();

		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_NAME, "test");
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(Application.class,
				new SimpleApplication(emptySet(), emptySet()), properties);

		awaitSelection.getValue();
		
		// Register the first empty application at "test"

		awaitSelection = helper.awaitModification(runtime, 5000);

		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_NAME,
				"test2");
		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test2");
		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION_SELECT,
				"(replacer-config=*)");

		context.registerService(Application.class,
						new SimpleApplication(
								singleton(ConfigurableStringReplacer.class),
								emptySet()),
						properties);

		awaitSelection.getValue();

		// Register a whiteboard application targeting all applications
		
		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);
		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_SELECT,
				"(osgi.jakartars.name=*)");

		awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get on each application, and the default application

		CloseableHttpResponse httpResponse = client
				.execute(RequestBuilder
						.get(baseURI + "test/whiteboard/resource")
						.build());

		String response = assertResponse(httpResponse, 200,
				TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		httpResponse = client.execute(RequestBuilder
				.get(baseURI + "test2/whiteboard/resource")
				.build());

		response = assertResponse(httpResponse, 404, null);

		httpResponse = client.execute(RequestBuilder
				.get(baseURI + "whiteboard/resource").build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Register an extension which provides the config needed
		// by the test 2 application

		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_SELECT,
				"(osgi.jakartars.name=*)");
		properties.put("replacer-config", "fizz-buzz");
		context.registerService(ContextResolver.class,
						new ExtensionConfigProvider("fizz",
								"fizzbuzz"),
						properties);
		awaitSelection.getValue();

		// Do a get on each application, and the default application
		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test/whiteboard/resource")
						.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test2/whiteboard/resource")
						.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/resource").build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

	}

	/**
	 * Section 151.3 Use whiteboard targeting for applications
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSimpleWhiteboardTarget() throws Exception {

		Long serviceId = (Long) runtime.getProperty(Constants.SERVICE_ID);

		String selectFilter = "(service.id=" + serviceId + ")";
		String rejectFilter = "(!" + selectFilter + ")";

		// Register an empty application at "test", targeting the whiteboard
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test");
		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_WHITEBOARD_TARGET,
				selectFilter);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = context.registerService(
				Application.class,
				new SimpleApplication(singleton(WhiteboardResource.class),
						emptySet()),
				properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do the get
		HttpUriRequest getRequest = RequestBuilder
				.get(baseURI + "test/whiteboard/resource")
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
	 * Section 151.2.2 Application Isolation from the Http Container
	 * 
	 * @throws Exception
	 */
	@Test
	public void testApplicationIsolationContainer() throws Exception {

		try {
			getClass().getClassLoader()
					.loadClass("jakarta.servlet.http.HttpSession");
		} catch (ClassNotFoundException cnfe) {
			Assume.assumeNoException(
					"No servlet container detected, so session isolation cannot be tested",
					cnfe);
		}

		// Register the first application at "test"

		Dictionary<String,Object> properties = new Hashtable<>();

		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_NAME, "test");
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(Application.class,
				new SimpleApplication(singleton(SessionManipulator.class),
						emptySet()),
				properties);

		awaitSelection.getValue();

		// Register the second application at "test2"

		awaitSelection = helper.awaitModification(runtime, 5000);

		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_NAME, "test2");
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test2");
		context.registerService(Application.class, new SimpleApplication(
				singleton(SessionManipulator.class), emptySet()), properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Set and get a session property on test

		CloseableHttpResponse httpResponse = client.execute(
				RequestBuilder.put(baseURI + "test/whiteboard/session/fizz")
						.setEntity(new StringEntity("buzz"))
						.build());

		assertResponse(httpResponse, 200, null);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test/whiteboard/session/fizz")
						.build());

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("buzz", response);

		// Set the same property with a different value on test2

		httpResponse = client.execute(
				RequestBuilder.put(baseURI + "test2/whiteboard/session/fizz")
						.setEntity(new StringEntity("fizzbuzz"))
						.build());

		assertResponse(httpResponse, 200, null);

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test2/whiteboard/session/fizz")
						.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("fizzbuzz", response);

		// Check that test was isolated from the change

		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test/whiteboard/session/fizz")
						.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("buzz", response);

	}

	/**
	 * Section 151.4.2 Register a simple JAX-RS singleton resource and show that
	 * it gets re-used between requests
	 * 
	 * @throws Exception
	 */
	@Test
	public void testShadowDefaultPath() throws Exception {

		// Register a whiteboard resource

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE,
				Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		context.registerService(StringResource.class,
				new StringResource("fizz"), properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		CloseableHttpResponse httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/string").build());

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("fizz", response);

		// Register an application at the default path

		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/");

		context.registerService(Application.class,
							new SimpleApplication(emptySet(),
									singleton(new StringResource("buzz"))),
							properties);

		awaitSelection.getValue();

		// Check the overriding resource is called
		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/string").build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("buzz", response);

	}

	/**
	 * Section 151.4.2 Register a simple JAX-RS singleton resource and show that
	 * it gets re-used between requests
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMoveDefaultApplication() throws Exception {

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

		CloseableHttpResponse httpResponse = client.execute(
				RequestBuilder.get(baseURI + "whiteboard/resource").build());

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizz, fizzbuzz]", response);

		// Register a new default application at "test"
		awaitSelection = helper.awaitModification(runtime, 5000);

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test");
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_NAME,
				".default");

		context.registerService(Application.class,
				new SimpleApplication(emptySet(),
						singleton(new StringReplacer("fizz", "fizzbuzz"))),
				properties);

		awaitSelection.getValue();

		// Do the get
		httpResponse = client.execute(
				RequestBuilder.get(baseURI + "test/whiteboard/resource")
						.build());

		response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

	}

	/**
	 * Section 151.6 Application service property access
	 * 
	 * @throws Exception
	 */
	@Test
	public void testApplicationServiceProps() throws Exception {

		// Register an application at "test"

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = context
				.registerService(Application.class,
						new SimpleApplication(emptySet(),
								singleton(new ConfigurationAwareResource())),
						properties);

		awaitSelection.getValue();

		String baseURI = getBaseURI();

		// Do a get

		CloseableHttpResponse httpResponse = client.execute(RequestBuilder
				.get(baseURI + "test/whiteboard/config/service.id")
				.build());

		String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
		assertEquals(
				String.valueOf(reg.getReference().getProperty("service.id")),
				response);

	}

	/**
	 * Section 151.6 Application service property access
	 * 
	 * @throws Exception
	 */
	@Test
	public void testApplicationServicePropsInFeature() throws Exception {

		// Register an application at "test"

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_BASE,
				"/test");
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_NAME, "test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = context.registerService(
				Application.class,
				new SimpleApplication(emptySet(),
						singleton(new ConfigurationAwareResource())),
				properties);

		awaitSelection.getValue();

		// Register an extension targeting test and grab the service properties

		properties = new Hashtable<>();
		properties.put(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION,
				Boolean.TRUE);
		properties.put(
				JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_SELECT,
				"(osgi.jakartars.name=test)");

		awaitSelection = helper.awaitModification(runtime, 5000);

		AtomicReference<Object> value = new AtomicReference<Object>();

		context.registerService(Feature.class, ctx -> {
			value.set(ctx.getConfiguration()
					.getProperty(
							JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_SERVICE_PROPERTIES));
			return true;
		}, properties);

		awaitSelection.getValue();

		// Get the
		Object o = value.get();

		assertNotNull(o);
		assertTrue(o instanceof Map);
		assertEquals(reg.getReference().getProperty("service.id"),
						((Map< ? , ? >) o).get("service.id"));

	}
}
