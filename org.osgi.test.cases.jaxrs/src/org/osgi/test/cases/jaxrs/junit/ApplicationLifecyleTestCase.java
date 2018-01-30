/*
 * Copyright (c) OSGi Alliance (2017, 2018). All Rights Reserved.
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

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.WriterInterceptor;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.junit.Assume;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;
import org.osgi.test.cases.jaxrs.applications.ApplicationWithPathAnnotation;
import org.osgi.test.cases.jaxrs.applications.SimpleApplication;
import org.osgi.test.cases.jaxrs.extensions.ConfigurableStringReplacer;
import org.osgi.test.cases.jaxrs.extensions.ExtensionConfigProvider;
import org.osgi.test.cases.jaxrs.extensions.StringReplacer;
import org.osgi.test.cases.jaxrs.resources.ConfigurationAwareResource;
import org.osgi.test.cases.jaxrs.resources.SessionManipulator;
import org.osgi.test.cases.jaxrs.resources.StringResource;
import org.osgi.test.cases.jaxrs.resources.WhiteboardResource;
import org.osgi.util.promise.Promise;

/**
 * This test covers the lifecycle behaviours described in section 151.6
 */
public class ApplicationLifecyleTestCase extends AbstractJAXRSTestCase {

	/**
	 * Section 151.6 Register a simple JAX-RS application and show that its
	 * resources get used
	 * 
	 * @throws Exception
	 */
	public void testSimpleSingletonResource() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = getContext()
				.registerService(Application.class,
						new SimpleApplication(emptySet(),
								singleton(new WhiteboardResource())),
						properties);

		try {

			awaitSelection.getValue();

			String baseURI = getBaseURI();

			// Do a get

			CloseableHttpResponse httpResponse = client.execute(RequestBuilder
					.get(baseURI + "test/whiteboard/resource").build());

			String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("[buzz, fizz, fizzbuzz]", response);

			// Do a delete

			httpResponse = client.execute(RequestBuilder
					.delete(baseURI + "test/whiteboard/resource/buzz").build());

			response = assertResponse(httpResponse, 200, null);
			assertEquals("", response);

			// Do another get to show the singleton-ness

			httpResponse = client.execute(RequestBuilder
					.get(baseURI + "test/whiteboard/resource").build());

			response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("[fizz, fizzbuzz]", response);

		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.6 Register a simple JAX-RS application and show that its
	 * resources get used
	 *
	 * @throws Exception
	 */
	public void testSimplePrototypeResource() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = getContext().registerService(
				Application.class,
				new SimpleApplication(singleton(WhiteboardResource.class),
						emptySet()),
				properties);

		try {

			awaitSelection.getValue();

			String baseURI = getBaseURI();

			// Do a get

			CloseableHttpResponse httpResponse = client.execute(RequestBuilder
					.get(baseURI + "test/whiteboard/resource").build());

			String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("[buzz, fizz, fizzbuzz]", response);

			// Do a delete

			httpResponse = client.execute(RequestBuilder
					.delete(baseURI + "test/whiteboard/resource/buzz").build());

			response = assertResponse(httpResponse, 200, null);
			assertEquals("", response);

			// Do another get to show the statelessness

			httpResponse = client.execute(RequestBuilder
					.get(baseURI + "test/whiteboard/resource").build());

			response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("[buzz, fizz, fizzbuzz]", response);

		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.6 Application Base service property
	 * 
	 * @throws Exception
	 */
	public void testApplicationBase() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = getContext()
				.registerService(Application.class,
						new SimpleApplication(emptySet(),
								singleton(new WhiteboardResource())),
						properties);

		try {

			awaitSelection.getValue();

			String baseURI = getBaseURI();

			// Do a get

			CloseableHttpResponse httpResponse = client.execute(RequestBuilder
					.get(baseURI + "test/whiteboard/resource").build());

			String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("[buzz, fizz, fizzbuzz]", response);

			// Change the base and re-request (note that it is valid to have no
			// leading '/')
			awaitSelection = helper.awaitModification(runtime, 5000);

			properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
					"changed");
			reg.setProperties(properties);

			awaitSelection.getValue();
			

			httpResponse = client.execute(RequestBuilder
					.get(baseURI + "changed/whiteboard/resource").build());

			response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("[buzz, fizz, fizzbuzz]", response);

		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.6 Application Base service property
	 * 
	 * @throws Exception
	 */
	public void testApplicationPathAnnotation() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = getContext()
				.registerService(Application.class,
						new ApplicationWithPathAnnotation(emptySet(),
								singleton(new WhiteboardResource())),
						properties);

		try {

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

			properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
					"changed");
			reg.setProperties(properties);

			awaitSelection.getValue();

			httpResponse = client.execute(RequestBuilder
					.get(baseURI + "changed/annotated/whiteboard/resource")
					.build());

			response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("[buzz, fizz, fizzbuzz]", response);

		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.6 Application Select service property
	 * 
	 * @throws Exception
	 */
	public void testApplicationWhiteboardResource() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();

		properties.put(JaxrsWhiteboardConstants.JAX_RS_NAME, "test");
		properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = getContext().registerService(
				Application.class,
				new SimpleApplication(emptySet(), emptySet()), properties);

		try {

			awaitSelection.getValue();

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties.put(JaxrsWhiteboardConstants.JAX_RS_NAME, "test2");
			properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
					"/test2");
			ServiceRegistration<Application> reg2 = getContext()
					.registerService(Application.class,
							new SimpleApplication(emptySet(), emptySet()),
							properties);

			try {

				awaitSelection.getValue();

				properties = new Hashtable<>();
				properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE,
						Boolean.TRUE);

				awaitSelection = helper.awaitModification(runtime, 5000);

				ServiceRegistration<WhiteboardResource> resourceReg = getContext()
						.registerService(WhiteboardResource.class,
								new WhiteboardResource(), properties);

				try {

					awaitSelection.getValue();

					String baseURI = getBaseURI();

					// Do a get on each application, and the default application

					CloseableHttpResponse httpResponse = client
							.execute(RequestBuilder
									.get(baseURI + "test/whiteboard/resource")
									.build());

					String response = assertResponse(httpResponse, 404, null);

					httpResponse = client.execute(RequestBuilder
							.get(baseURI + "test2/whiteboard/resource")
							.build());

					response = assertResponse(httpResponse, 404, null);

					httpResponse = client.execute(RequestBuilder
							.get(baseURI + "whiteboard/resource").build());

					response = assertResponse(httpResponse, 200, TEXT_PLAIN);
					assertEquals("[buzz, fizz, fizzbuzz]", response);

					// Rebind the resource

					awaitSelection = helper.awaitModification(runtime, 5000);

					properties.put(
							JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
							"(osgi.jaxrs.name=test2)");
					resourceReg.setProperties(properties);

					awaitSelection.getValue();

					httpResponse = client.execute(RequestBuilder
							.get(baseURI + "test/whiteboard/resource").build());

					response = assertResponse(httpResponse, 404, null);

					httpResponse = client.execute(RequestBuilder
							.get(baseURI + "test2/whiteboard/resource")
							.build());

					response = assertResponse(httpResponse, 200, TEXT_PLAIN);
					assertEquals("[buzz, fizz, fizzbuzz]", response);

					httpResponse = client.execute(RequestBuilder
							.get(baseURI + "whiteboard/resource").build());

					response = assertResponse(httpResponse, 404, null);

					// Target just the default application

					awaitSelection = helper.awaitModification(runtime, 5000);

					properties.put(
							JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
							"(osgi.jaxrs.name="
									+ JaxrsWhiteboardConstants.JAX_RS_DEFAULT_APPLICATION
									+ ")");
					resourceReg.setProperties(properties);

					awaitSelection.getValue();

					httpResponse = client.execute(RequestBuilder
							.get(baseURI + "test/whiteboard/resource").build());
					response = assertResponse(httpResponse, 404, null);

					httpResponse = client.execute(RequestBuilder
							.get(baseURI + "test2/whiteboard/resource")
							.build());

					response = assertResponse(httpResponse, 404, null);

					httpResponse = client.execute(RequestBuilder
							.get(baseURI + "whiteboard/resource").build());

					response = assertResponse(httpResponse, 200, TEXT_PLAIN);
					assertEquals("[buzz, fizz, fizzbuzz]", response);

					// Target all the applications

					awaitSelection = helper.awaitModification(runtime, 5000);

					properties.put(
							JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
							"(osgi.jaxrs.name=*)");
					resourceReg.setProperties(properties);

					awaitSelection.getValue();

					httpResponse = client.execute(RequestBuilder
							.get(baseURI + "test/whiteboard/resource").build());
					response = assertResponse(httpResponse, 200, TEXT_PLAIN);
					assertEquals("[buzz, fizz, fizzbuzz]", response);

					httpResponse = client.execute(RequestBuilder
							.get(baseURI + "test2/whiteboard/resource")
							.build());

					response = assertResponse(httpResponse, 200, TEXT_PLAIN);
					assertEquals("[buzz, fizz, fizzbuzz]", response);

					httpResponse = client.execute(RequestBuilder
							.get(baseURI + "whiteboard/resource").build());

					response = assertResponse(httpResponse, 200, TEXT_PLAIN);
					assertEquals("[buzz, fizz, fizzbuzz]", response);
				} finally {
					resourceReg.unregister();
				}

			} finally {
				reg2.unregister();
			}
		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.6 Application Isolation
	 * 
	 * @throws Exception
	 */
	public void testApplicationIsolationExtensions() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();

		properties.put(JaxrsWhiteboardConstants.JAX_RS_NAME, "test");
		properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = getContext().registerService(
				Application.class,
				new SimpleApplication(emptySet(),
						singleton(new StringReplacer("fizz", "fizzbuzz"))),
				properties);

		try {

			awaitSelection.getValue();

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties.put(JaxrsWhiteboardConstants.JAX_RS_NAME, "test2");
			properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
					"/test2");
			ServiceRegistration<Application> reg2 = getContext()
					.registerService(Application.class,
							new SimpleApplication(emptySet(), emptySet()),
							properties);

			try {

				awaitSelection.getValue();

				properties = new Hashtable<>();
				properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE,
						Boolean.TRUE);
				properties.put(
						JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
						"(osgi.jaxrs.name=*)");

				awaitSelection = helper.awaitModification(runtime, 5000);

				ServiceRegistration<WhiteboardResource> resourceReg = getContext()
						.registerService(WhiteboardResource.class,
								new WhiteboardResource(), properties);

				try {

					awaitSelection.getValue();

					String baseURI = getBaseURI();

					// Do a get on each application, and the default application

					CloseableHttpResponse httpResponse = client
							.execute(RequestBuilder
									.get(baseURI + "test/whiteboard/resource")
									.build());

					String response = assertResponse(httpResponse, 200,
							TEXT_PLAIN);
					assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

					httpResponse = client.execute(RequestBuilder
							.get(baseURI + "test2/whiteboard/resource")
							.build());

					response = assertResponse(httpResponse, 200, TEXT_PLAIN);
					assertEquals("[buzz, fizz, fizzbuzz]", response);

					httpResponse = client.execute(RequestBuilder
							.get(baseURI + "whiteboard/resource").build());

					response = assertResponse(httpResponse, 200, TEXT_PLAIN);
					assertEquals("[buzz, fizz, fizzbuzz]", response);

				} finally {
					resourceReg.unregister();
				}

			} finally {
				reg2.unregister();
			}
		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.6 Application Isolation
	 * 
	 * @throws Exception
	 */
	public void testApplicationIsolationExtensions2() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();

		properties.put(JaxrsWhiteboardConstants.JAX_RS_NAME, "test");
		properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = getContext().registerService(
				Application.class,
				new SimpleApplication(emptySet(), emptySet()), properties);

		try {

			awaitSelection.getValue();

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties.put(JaxrsWhiteboardConstants.JAX_RS_NAME, "test2");
			properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
					"/test2");
			ServiceRegistration<Application> reg2 = getContext()
					.registerService(Application.class,
							new SimpleApplication(emptySet(), emptySet()),
							properties);

			try {

				awaitSelection.getValue();

				properties = new Hashtable<>();
				properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE,
						Boolean.TRUE);
				properties.put(
						JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
						"(osgi.jaxrs.name=*)");

				awaitSelection = helper.awaitModification(runtime, 5000);

				ServiceRegistration<WhiteboardResource> resourceReg = getContext()
						.registerService(WhiteboardResource.class,
								new WhiteboardResource(), properties);

				try {

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

					response = assertResponse(httpResponse, 200, TEXT_PLAIN);
					assertEquals("[buzz, fizz, fizzbuzz]", response);

					httpResponse = client.execute(RequestBuilder
							.get(baseURI + "whiteboard/resource").build());

					response = assertResponse(httpResponse, 200, TEXT_PLAIN);
					assertEquals("[buzz, fizz, fizzbuzz]", response);

					awaitSelection = helper.awaitModification(runtime, 5000);

					properties = new Hashtable<>();
					properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
							Boolean.TRUE);
					properties.put(
							JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
							"(osgi.jaxrs.name=t*)");
					ServiceRegistration<WriterInterceptor> extensionReg = getContext()
							.registerService(WriterInterceptor.class,
									new StringReplacer("fizz", "fizzbuzz"),
									properties);
					try {

						awaitSelection.getValue();

						httpResponse = client.execute(RequestBuilder
								.get(baseURI + "test/whiteboard/resource")
								.build());

						response = assertResponse(httpResponse, 200,
								TEXT_PLAIN);
						assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]",
								response);

						httpResponse = client.execute(RequestBuilder
								.get(baseURI + "test2/whiteboard/resource")
								.build());

						response = assertResponse(httpResponse, 200,
								TEXT_PLAIN);
						assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]",
								response);

						httpResponse = client.execute(RequestBuilder
								.get(baseURI + "whiteboard/resource").build());

						response = assertResponse(httpResponse, 200,
								TEXT_PLAIN);
						assertEquals("[buzz, fizz, fizzbuzz]", response);
					} finally {
						extensionReg.unregister();
					}

				} finally {
					resourceReg.unregister();
				}

			} finally {
				reg2.unregister();
			}
		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.3 Extension select applied to application properties
	 * 
	 * @throws Exception
	 */
	public void testApplicationProvidedExtensionDependency() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();

		properties.put(JaxrsWhiteboardConstants.JAX_RS_NAME, "test");
		properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = getContext().registerService(
				Application.class,
				new SimpleApplication(emptySet(), emptySet()), properties);

		try {

			awaitSelection.getValue();

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties.put(JaxrsWhiteboardConstants.JAX_RS_NAME, "test2");
			properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
					"/test2");
			properties.put("replacer-config", "fizz-buzz");
			ServiceRegistration<Application> reg2 = getContext()
					.registerService(Application.class,
							new SimpleApplication(emptySet(),
									singleton(new ExtensionConfigProvider(
											"fizz", "fizzbuzz"))),
							properties);

			try {

				awaitSelection.getValue();

				properties = new Hashtable<>();
				properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE,
						Boolean.TRUE);
				properties.put(
						JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
						"(osgi.jaxrs.name=*)");

				awaitSelection = helper.awaitModification(runtime, 5000);

				ServiceRegistration<WhiteboardResource> resourceReg = getContext()
						.registerService(WhiteboardResource.class,
								new WhiteboardResource(), properties);

				try {

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

					response = assertResponse(httpResponse, 200, TEXT_PLAIN);
					assertEquals("[buzz, fizz, fizzbuzz]", response);

					httpResponse = client.execute(RequestBuilder
							.get(baseURI + "whiteboard/resource").build());

					response = assertResponse(httpResponse, 200, TEXT_PLAIN);
					assertEquals("[buzz, fizz, fizzbuzz]", response);

					// Register an extension which needs config
					// This will only be satisfied by one of the applications

					awaitSelection = helper.awaitModification(runtime, 5000);

					properties = new Hashtable<>();
					properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
							Boolean.TRUE);
					properties.put(
							JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
							"(osgi.jaxrs.name=*)");
					properties.put(
							JaxrsWhiteboardConstants.JAX_RS_EXTENSION_SELECT,
							"(replacer-config=*)");
					ServiceRegistration<WriterInterceptor> extensionReg = getContext()
							.registerService(WriterInterceptor.class,
									new ConfigurableStringReplacer(),
									properties);
					try {

						awaitSelection.getValue();

						httpResponse = client.execute(RequestBuilder
								.get(baseURI + "test/whiteboard/resource")
								.build());

						response = assertResponse(httpResponse, 200,
								TEXT_PLAIN);
						assertEquals("[buzz, fizz, fizzbuzz]",
								response);

						httpResponse = client.execute(RequestBuilder
								.get(baseURI + "test2/whiteboard/resource")
								.build());

						response = assertResponse(httpResponse, 200,
								TEXT_PLAIN);
						assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]",
								response);

						httpResponse = client.execute(RequestBuilder
								.get(baseURI + "whiteboard/resource").build());

						response = assertResponse(httpResponse, 200,
								TEXT_PLAIN);
						assertEquals("[buzz, fizz, fizzbuzz]", response);
					} finally {
						extensionReg.unregister();
					}
				} finally {
					resourceReg.unregister();
				}
			} finally {
				reg2.unregister();
			}
		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.6.2 Extension select prevents application binding without the
	 * extension
	 * 
	 * @throws Exception
	 */
	public void testApplicationExtensionDependency() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();

		properties.put(JaxrsWhiteboardConstants.JAX_RS_NAME, "test");
		properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = getContext().registerService(
				Application.class,
				new SimpleApplication(emptySet(), emptySet()), properties);

		try {

			awaitSelection.getValue();

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties.put(JaxrsWhiteboardConstants.JAX_RS_NAME, "test2");
			properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
					"/test2");
			properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION_SELECT,
					"(replacer-config=*");

			ServiceRegistration<Application> reg2 = getContext()
					.registerService(Application.class,
							new SimpleApplication(
									singleton(ConfigurableStringReplacer.class),
									emptySet()),
							properties);

			try {

				awaitSelection.getValue();

				properties = new Hashtable<>();
				properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE,
						Boolean.TRUE);
				properties.put(
						JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
						"(osgi.jaxrs.name=*)");

				awaitSelection = helper.awaitModification(runtime, 5000);

				ServiceRegistration<WhiteboardResource> resourceReg = getContext()
						.registerService(WhiteboardResource.class,
								new WhiteboardResource(), properties);

				try {

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
					properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
							Boolean.TRUE);
					properties.put(
							JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
							"(osgi.jaxrs.name=*)");
					properties.put("replacer-config", "fizz-buzz");
					@SuppressWarnings("rawtypes")
					ServiceRegistration<ContextResolver> extensionReg = getContext()
							.registerService(ContextResolver.class,
									new ExtensionConfigProvider("fizz", "buzz"),
									properties);
					try {

						awaitSelection.getValue();

						httpResponse = client.execute(RequestBuilder
								.get(baseURI + "test/whiteboard/resource")
								.build());

						response = assertResponse(httpResponse, 200,
								TEXT_PLAIN);
						assertEquals("[buzz, fizz, fizzbuzz]", response);

						httpResponse = client.execute(RequestBuilder
								.get(baseURI + "test2/whiteboard/resource")
								.build());

						response = assertResponse(httpResponse, 200,
								TEXT_PLAIN);
						assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]",
								response);

						httpResponse = client.execute(RequestBuilder
								.get(baseURI + "whiteboard/resource").build());

						response = assertResponse(httpResponse, 200,
								TEXT_PLAIN);
						assertEquals("[buzz, fizz, fizzbuzz]", response);
					} finally {
						extensionReg.unregister();
					}
				} finally {
					resourceReg.unregister();
				}
			} finally {
				reg2.unregister();
			}
		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.3 Use whiteboard targeting for applications
	 * 
	 * @throws Exception
	 */
	public void testSimpleWhiteboardTarget() throws Exception {

		Long serviceId = (Long) runtime.getProperty(Constants.SERVICE_ID);

		String selectFilter = "(service.id=" + serviceId + ")";
		String rejectFilter = "(!" + selectFilter + ")";

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
				"/test");
		properties.put(JaxrsWhiteboardConstants.JAX_RS_WHITEBOARD_TARGET,
				selectFilter);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = getContext().registerService(
				Application.class,
				new SimpleApplication(singleton(WhiteboardResource.class),
						emptySet()),
				properties);

		try {

			awaitSelection.getValue();

			String baseURI = getBaseURI();

			HttpUriRequest getRequest = RequestBuilder
					.get(baseURI + "test/whiteboard/resource").build();

			CloseableHttpResponse httpResponse = client.execute(getRequest);

			String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("[buzz, fizz, fizzbuzz]", response);

			// Change the target
			awaitSelection = helper.awaitModification(runtime, 5000);

			properties.put(JaxrsWhiteboardConstants.JAX_RS_WHITEBOARD_TARGET,
					rejectFilter);
			reg.setProperties(properties);

			awaitSelection.getValue();

			httpResponse = client.execute(getRequest);

			assertResponse(httpResponse, 404, null);

			// Reset the target
			awaitSelection = helper.awaitModification(runtime, 5000);

			properties.put(JaxrsWhiteboardConstants.JAX_RS_WHITEBOARD_TARGET,
					selectFilter);
			reg.setProperties(properties);

			awaitSelection.getValue();

			httpResponse = client.execute(getRequest);

			response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("[buzz, fizz, fizzbuzz]", response);

		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.2.2 Application Isolation from the Http Container
	 * 
	 * @throws Exception
	 */
	public void testApplicationIsolationContainer() throws Exception {

		try {
			getClass().getClassLoader()
					.loadClass("javax.servlet.http.HttpSession");
		} catch (ClassNotFoundException cnfe) {
			Assume.assumeNoException(
					"No servlet container detected, so session isolation cannot be tested",
					cnfe);
		}

		Dictionary<String,Object> properties = new Hashtable<>();

		properties.put(JaxrsWhiteboardConstants.JAX_RS_NAME, "test");
		properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = getContext().registerService(
				Application.class,
				new SimpleApplication(singleton(SessionManipulator.class),
						emptySet()),
				properties);

		try {

			awaitSelection.getValue();

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties.put(JaxrsWhiteboardConstants.JAX_RS_NAME, "test2");
			properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
					"/test2");
			ServiceRegistration<Application> reg2 = getContext()
					.registerService(Application.class,
							new SimpleApplication(
									singleton(SessionManipulator.class),
									emptySet()),
							properties);

			try {

				awaitSelection.getValue();

				String baseURI = getBaseURI();

				// Set and get a session property on test

				CloseableHttpResponse httpResponse = client
						.execute(RequestBuilder
								.put(baseURI + "test/whiteboard/session/fizz")
								.setEntity(new StringEntity("buzz"))
								.build());

				assertResponse(httpResponse, 200, null);

				httpResponse = client.execute(RequestBuilder
						.get(baseURI + "test/whiteboard/session/fizz").build());

				String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("buzz", response);

				// Set the same property with a different value on test2

				httpResponse = client.execute(RequestBuilder
						.put(baseURI + "test2/whiteboard/session/fizz")
						.setEntity(new StringEntity("fizzbuzz"))
						.build());

				assertResponse(httpResponse, 200, null);

				httpResponse = client.execute(RequestBuilder
						.get(baseURI + "test2/whiteboard/session/fizz")
						.build());

				response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("fizzbuzz", response);

				// Check that test was isolated from the change

				httpResponse = client.execute(RequestBuilder
						.get(baseURI + "test/whiteboard/session/fizz").build());

				response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("buzz", response);
			} finally {
				reg2.unregister();
			}
		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.4.2 Register a simple JAX-RS singleton resource and show that
	 * it gets re-used between requests
	 * 
	 * @throws Exception
	 */
	public void testShadowDefaultPath() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<StringResource> reg = getContext().registerService(
				StringResource.class, new StringResource("fizz"), properties);

		try {

			awaitSelection.getValue();

			String baseURI = getBaseURI();

			// Do a get

			CloseableHttpResponse httpResponse = client.execute(
					RequestBuilder.get(baseURI + "whiteboard/string").build());

			String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("fizz", response);

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties = new Hashtable<>();
			properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
					"/");

			ServiceRegistration<Application> reg2 = getContext()
					.registerService(Application.class,
							new SimpleApplication(emptySet(),
									singleton(new StringResource("buzz"))),
							properties);

			try {

				awaitSelection.getValue();

				httpResponse = client.execute(RequestBuilder
						.get(baseURI + "whiteboard/string").build());

				response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("buzz", response);

			} finally {
				reg2.unregister();
			}
		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.4.2 Register a simple JAX-RS singleton resource and show that
	 * it gets re-used between requests
	 * 
	 * @throws Exception
	 */
	public void testMoveDefaultApplication() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_RESOURCE, Boolean.TRUE);

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<WhiteboardResource> reg = getContext()
				.registerService(WhiteboardResource.class,
						new WhiteboardResource(), properties);

		try {

			awaitSelection.getValue();

			String baseURI = getBaseURI();

			// Do a get

			CloseableHttpResponse httpResponse = client.execute(RequestBuilder
					.get(baseURI + "whiteboard/resource").build());

			String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals("[buzz, fizz, fizzbuzz]", response);

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties = new Hashtable<>();
			properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
					"/test");
			properties.put(JaxrsWhiteboardConstants.JAX_RS_NAME, ".default");

			ServiceRegistration<Application> reg2 = getContext()
					.registerService(Application.class,
							new SimpleApplication(emptySet(), singleton(
									new StringReplacer("fizz", "fizzbuzz"))),
							properties);

			try {

				awaitSelection.getValue();

				httpResponse = client.execute(RequestBuilder
						.get(baseURI + "test/whiteboard/resource").build());

				response = assertResponse(httpResponse, 200, TEXT_PLAIN);
				assertEquals("[buzz, fizzbuzz, fizzbuzzbuzz]", response);

			} finally {
				reg2.unregister();
			}
		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.6 Application service property access
	 * 
	 * @throws Exception
	 */
	public void testApplicationServiceProps() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = getContext()
				.registerService(Application.class,
						new SimpleApplication(emptySet(),
								singleton(new ConfigurationAwareResource())),
						properties);

		try {

			awaitSelection.getValue();

			String baseURI = getBaseURI();

			// Do a get

			CloseableHttpResponse httpResponse = client.execute(RequestBuilder
					.get(baseURI + "test/whiteboard/config/service.id")
					.build());

			String response = assertResponse(httpResponse, 200, TEXT_PLAIN);
			assertEquals(
					String.valueOf(
							reg.getReference().getProperty("service.id")),
					response);

		} finally {
			reg.unregister();
		}
	}

	/**
	 * Section 151.6 Application service property access
	 * 
	 * @throws Exception
	 */
	public void testApplicationServicePropsInFeature() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
				"/test");
		properties.put(JaxrsWhiteboardConstants.JAX_RS_NAME, "test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = getContext().registerService(
				Application.class,
				new SimpleApplication(emptySet(),
						singleton(new ConfigurationAwareResource())),
				properties);

		try {

			awaitSelection.getValue();

			properties = new Hashtable<>();
			properties.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
					Boolean.TRUE);
			properties.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
					"(osgi.jaxrs.name=test)");

			awaitSelection = helper.awaitModification(runtime, 5000);

			AtomicReference<Object> value = new AtomicReference<Object>();

			ServiceRegistration<Feature> extensionReg = getContext()
					.registerService(Feature.class,
							ctx -> {
								value.set(ctx.getConfiguration().getProperty(
										JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SERVICE_PROPERTIES));
								return true;
							}, properties);

			try {

				awaitSelection.getValue();

				Object o = value.get();

				assertNotNull(o);
				assertTrue(o instanceof Map);
				assertEquals(
						reg.getReference().getProperty("service.id"),
						((Map< ? , ? >) o).get("service.id"));

			} finally {
				extensionReg.unregister();
			}

		} finally {
			reg.unregister();
		}
	}
}
