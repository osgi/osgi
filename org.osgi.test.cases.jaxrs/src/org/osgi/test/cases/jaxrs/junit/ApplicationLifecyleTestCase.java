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

import java.util.Dictionary;
import java.util.Hashtable;

import javax.ws.rs.core.Application;
import javax.ws.rs.ext.WriterInterceptor;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jaxrs.whiteboard.JaxRSWhiteboardConstants;
import org.osgi.test.cases.jaxrs.applications.ApplicationWithPathAnnotation;
import org.osgi.test.cases.jaxrs.applications.SimpleApplication;
import org.osgi.test.cases.jaxrs.extensions.ConfigurableStringReplacer;
import org.osgi.test.cases.jaxrs.extensions.ExtensionConfigProvider;
import org.osgi.test.cases.jaxrs.extensions.StringReplacer;
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
		properties.put(JaxRSWhiteboardConstants.JAX_RS_APPLICATION_BASE,
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
		properties.put(JaxRSWhiteboardConstants.JAX_RS_APPLICATION_BASE,
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
		properties.put(JaxRSWhiteboardConstants.JAX_RS_APPLICATION_BASE,
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

			properties.put(JaxRSWhiteboardConstants.JAX_RS_APPLICATION_BASE,
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
		properties.put(JaxRSWhiteboardConstants.JAX_RS_APPLICATION_BASE,
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

			properties.put(JaxRSWhiteboardConstants.JAX_RS_APPLICATION_BASE,
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

		properties.put(JaxRSWhiteboardConstants.JAX_RS_NAME, "test");
		properties.put(JaxRSWhiteboardConstants.JAX_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = getContext().registerService(
				Application.class,
				new SimpleApplication(emptySet(), emptySet()), properties);

		try {

			awaitSelection.getValue();

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties.put(JaxRSWhiteboardConstants.JAX_RS_NAME, "test2");
			properties.put(JaxRSWhiteboardConstants.JAX_RS_APPLICATION_BASE,
					"/test2");
			ServiceRegistration<Application> reg2 = getContext()
					.registerService(Application.class,
							new SimpleApplication(emptySet(), emptySet()),
							properties);

			try {

				awaitSelection.getValue();

				properties = new Hashtable<>();
				properties.put(JaxRSWhiteboardConstants.JAX_RS_RESOURCE,
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
							JaxRSWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
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
							JaxRSWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
							"(osgi.jaxrs.name="
									+ JaxRSWhiteboardConstants.JAX_RS_DEFAULT_APPLICATION
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
							JaxRSWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
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

		properties.put(JaxRSWhiteboardConstants.JAX_RS_NAME, "test");
		properties.put(JaxRSWhiteboardConstants.JAX_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = getContext().registerService(
				Application.class,
				new SimpleApplication(emptySet(),
						singleton(new StringReplacer("fizz", "buzz"))),
				properties);

		try {

			awaitSelection.getValue();

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties.put(JaxRSWhiteboardConstants.JAX_RS_NAME, "test2");
			properties.put(JaxRSWhiteboardConstants.JAX_RS_APPLICATION_BASE,
					"/test2");
			ServiceRegistration<Application> reg2 = getContext()
					.registerService(Application.class,
							new SimpleApplication(emptySet(), emptySet()),
							properties);

			try {

				awaitSelection.getValue();

				properties = new Hashtable<>();
				properties.put(JaxRSWhiteboardConstants.JAX_RS_RESOURCE,
						Boolean.TRUE);
				properties.put(
						JaxRSWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
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

		properties.put(JaxRSWhiteboardConstants.JAX_RS_NAME, "test");
		properties.put(JaxRSWhiteboardConstants.JAX_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = getContext().registerService(
				Application.class,
				new SimpleApplication(emptySet(), emptySet()), properties);

		try {

			awaitSelection.getValue();

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties.put(JaxRSWhiteboardConstants.JAX_RS_NAME, "test2");
			properties.put(JaxRSWhiteboardConstants.JAX_RS_APPLICATION_BASE,
					"/test2");
			ServiceRegistration<Application> reg2 = getContext()
					.registerService(Application.class,
							new SimpleApplication(emptySet(), emptySet()),
							properties);

			try {

				awaitSelection.getValue();

				properties = new Hashtable<>();
				properties.put(JaxRSWhiteboardConstants.JAX_RS_RESOURCE,
						Boolean.TRUE);
				properties.put(
						JaxRSWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
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
					properties.put(JaxRSWhiteboardConstants.JAX_RS_EXTENSION,
							Boolean.TRUE);
					properties.put(
							JaxRSWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
							"(osgi.jaxrs.name=t*)");
					ServiceRegistration<WriterInterceptor> extensionReg = getContext()
							.registerService(WriterInterceptor.class,
									new StringReplacer("fizz", "buzz"),
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
	 * Section 151.6 Application Isolation
	 * 
	 * @throws Exception
	 */
	public void testApplicationExtensionsDependency() throws Exception {

		Dictionary<String,Object> properties = new Hashtable<>();

		properties.put(JaxRSWhiteboardConstants.JAX_RS_NAME, "test");
		properties.put(JaxRSWhiteboardConstants.JAX_RS_APPLICATION_BASE,
				"/test");

		Promise<Void> awaitSelection = helper.awaitModification(runtime, 5000);

		ServiceRegistration<Application> reg = getContext().registerService(
				Application.class,
				new SimpleApplication(emptySet(), emptySet()), properties);

		try {

			awaitSelection.getValue();

			awaitSelection = helper.awaitModification(runtime, 5000);

			properties.put(JaxRSWhiteboardConstants.JAX_RS_NAME, "test2");
			properties.put(JaxRSWhiteboardConstants.JAX_RS_APPLICATION_BASE,
					"/test2");
			properties.put("replacer-config", "fizz-buzz");
			ServiceRegistration<Application> reg2 = getContext()
					.registerService(Application.class,
							new SimpleApplication(emptySet(),
									singleton(new ExtensionConfigProvider(
											"fizz", "buzz"))),
							properties);

			try {

				awaitSelection.getValue();

				properties = new Hashtable<>();
				properties.put(JaxRSWhiteboardConstants.JAX_RS_RESOURCE,
						Boolean.TRUE);
				properties.put(
						JaxRSWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
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
					properties.put(JaxRSWhiteboardConstants.JAX_RS_EXTENSION,
							Boolean.TRUE);
					properties.put(
							JaxRSWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
							"(osgi.jaxrs.name=*)");
					properties.put(
							JaxRSWhiteboardConstants.JAX_RS_EXTENSION_SELECT,
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

	// TODO test whiteboard target
	// TODO test replace default

	// TODO test path clash
	// TODO test name clash
	// TODO clash resolution tests

}
