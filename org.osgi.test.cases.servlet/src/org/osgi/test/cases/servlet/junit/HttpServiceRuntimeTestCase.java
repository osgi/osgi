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
package org.osgi.test.cases.servlet.junit;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.namespace.contract.ContractNamespace;
import org.osgi.namespace.implementation.ImplementationNamespace;
import org.osgi.namespace.service.ServiceNamespace;
import org.osgi.resource.Capability;
import org.osgi.resource.Namespace;
import org.osgi.service.servlet.context.ServletContextHelper;
import org.osgi.service.servlet.runtime.HttpServiceRuntime;
import org.osgi.service.servlet.runtime.HttpServiceRuntimeConstants;
import org.osgi.service.servlet.runtime.dto.FailedErrorPageDTO;
import org.osgi.service.servlet.runtime.dto.FailedFilterDTO;
import org.osgi.service.servlet.runtime.dto.FailedListenerDTO;
import org.osgi.service.servlet.runtime.dto.FailedResourceDTO;
import org.osgi.service.servlet.runtime.dto.FailedServletContextDTO;
import org.osgi.service.servlet.runtime.dto.FailedServletDTO;
import org.osgi.service.servlet.runtime.dto.RequestInfoDTO;
import org.osgi.service.servlet.runtime.dto.ServletContextDTO;
import org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants;
import org.osgi.test.cases.servlet.junit.mock.MockFilter;
import org.osgi.test.cases.servlet.junit.mock.MockSCL;
import org.osgi.test.cases.servlet.junit.mock.MockServlet;
import org.osgi.test.support.version.Versions;

import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextListener;

public class HttpServiceRuntimeTestCase extends BaseHttpWhiteboardTestCase {

	public void test_140_9_4to6() throws Exception {
		BundleContext context = getContext();

		Collection<ServiceReference<HttpServiceRuntime>> references = context.getServiceReferences(HttpServiceRuntime.class, null);

		assertEquals(1, references.size());
	}

	public void test_140_9_HTTP_SERVICE_ENDPOINT() throws Exception {
		BundleContext context = getContext();

		ServiceReference<HttpServiceRuntime> srA = context.getServiceReference(HttpServiceRuntime.class);

		Object value = srA.getProperty(HttpServiceRuntimeConstants.HTTP_SERVICE_ENDPOINT);
		assertNotNull(value);

		Collection<String> endpoints = getEndPoints(srA);

		for (String endPoint : endpoints) {
			assertTrue(endPoint.endsWith("/"));
		}
	}

	public void test_140_9_ServletContextDTO_default_servlet() throws Exception {
		BundleContext context = getContext();
		ServletContextDTO servletContextDTO = getServletContextDTOByName(DEFAULT);
		assertNotNull(servletContextDTO);

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<?> sr = context.registerService(Servlet.class, new MockServlet(), properties);
		serviceRegistrations.add(sr);

		servletContextDTO = getServletContextDTOByName(DEFAULT);
		assertEquals(1, servletContextDTO.servletDTOs.length);
		assertEquals(getServiceId(sr), servletContextDTO.servletDTOs[0].serviceId);
	}

	public void test_140_9_ServletContextDTO_default_resource() throws Exception {
		BundleContext context = getContext();
		ServletContextDTO servletContextDTO = getServletContextDTOByName(DEFAULT);
		assertNotNull(servletContextDTO);

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/");
		ServiceRegistration<?> sr = context.registerService(Object.class, new Object(), properties);
		serviceRegistrations.add(sr);

		servletContextDTO = getServletContextDTOByName(DEFAULT);
		assertEquals(1, servletContextDTO.resourceDTOs.length);
		assertEquals(getServiceId(sr), servletContextDTO.resourceDTOs[0].serviceId);
	}

	public void test_140_9_ServletContextDTO_default_filter() throws Exception {
		BundleContext context = getContext();
		ServletContextDTO servletContextDTO = getServletContextDTOByName(DEFAULT);
		assertNotNull(servletContextDTO);

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		ServiceRegistration<?> sr = context.registerService(Filter.class, new MockFilter(), properties);
		serviceRegistrations.add(sr);

		servletContextDTO = getServletContextDTOByName(DEFAULT);
		assertEquals(1, servletContextDTO.filterDTOs.length);
		assertEquals(getServiceId(sr), servletContextDTO.filterDTOs[0].serviceId);
	}

	public void test_140_9_ServletContextDTO_default_errorPage() throws Exception {
		BundleContext context = getContext();
		ServletContextDTO servletContextDTO = getServletContextDTOByName(DEFAULT);
		assertNotNull(servletContextDTO);

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, "404");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<?> sr = context.registerService(Servlet.class, new MockServlet(), properties);
		serviceRegistrations.add(sr);

		servletContextDTO = getServletContextDTOByName(DEFAULT);
		assertEquals(1, servletContextDTO.errorPageDTOs.length);
		assertEquals(getServiceId(sr), servletContextDTO.errorPageDTOs[0].serviceId);
	}

	public void test_140_9_ServletContextDTO_default_listener() throws Exception {
		BundleContext context = getContext();
		ServletContextDTO servletContextDTO = getServletContextDTOByName(DEFAULT);
		assertNotNull(servletContextDTO);

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		ServiceRegistration< ? > sr = context.registerService(
				ServletContextListener.class,
				new MockSCL(new AtomicReference<ServletContext>()), properties);
		serviceRegistrations.add(sr);

		servletContextDTO = getServletContextDTOByName(DEFAULT);
		assertEquals(1, servletContextDTO.listenerDTOs.length);
		assertEquals(getServiceId(sr), servletContextDTO.listenerDTOs[0].serviceId);
	}

	public void test_140_9_ServletContextDTO_custom_servlet() throws Exception {
		BundleContext context = getContext();
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/a");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		ServletContextDTO servletContextDTO = getServletContextDTOByName("a");
		assertNotNull(servletContextDTO);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=a)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<?> sr = context.registerService(Servlet.class, new MockServlet(), properties);
		serviceRegistrations.add(sr);

		servletContextDTO = getServletContextDTOByName("a");
		assertEquals(1, servletContextDTO.servletDTOs.length);
		assertEquals(getServiceId(sr), servletContextDTO.servletDTOs[0].serviceId);
	}

	public void test_140_9_ServletContextDTO_custom_resource() throws Exception {
		BundleContext context = getContext();
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/a");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		ServletContextDTO servletContextDTO = getServletContextDTOByName("a");
		assertNotNull(servletContextDTO);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=a)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/");
		ServiceRegistration<?> sr = context.registerService(Object.class, new Object(), properties);
		serviceRegistrations.add(sr);

		servletContextDTO = getServletContextDTOByName("a");
		assertEquals(1, servletContextDTO.resourceDTOs.length);
		assertEquals(getServiceId(sr), servletContextDTO.resourceDTOs[0].serviceId);
	}

	public void test_140_9_ServletContextDTO_custom_filter() throws Exception {
		BundleContext context = getContext();
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/a");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		ServletContextDTO servletContextDTO = getServletContextDTOByName("a");
		assertNotNull(servletContextDTO);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=a)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		ServiceRegistration<?> sr = context.registerService(Filter.class, new MockFilter(), properties);
		serviceRegistrations.add(sr);

		servletContextDTO = getServletContextDTOByName("a");
		assertEquals(1, servletContextDTO.filterDTOs.length);
		assertEquals(getServiceId(sr), servletContextDTO.filterDTOs[0].serviceId);
	}

	public void test_140_9_ServletContextDTO_custom_errorPage() throws Exception {
		BundleContext context = getContext();
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/a");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		ServletContextDTO servletContextDTO = getServletContextDTOByName("a");
		assertNotNull(servletContextDTO);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=a)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, "404");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<?> sr = context.registerService(Servlet.class, new MockServlet(), properties);
		serviceRegistrations.add(sr);

		servletContextDTO = getServletContextDTOByName("a");
		assertEquals(1, servletContextDTO.errorPageDTOs.length);
		assertEquals(getServiceId(sr), servletContextDTO.errorPageDTOs[0].serviceId);
	}

	public void test_140_9_ServletContextDTO_custom_listener() throws Exception {
		BundleContext context = getContext();
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/a");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		ServletContextDTO servletContextDTO = getServletContextDTOByName("a");
		assertNotNull(servletContextDTO);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=a)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		ServiceRegistration< ? > sr = context.registerService(
				ServletContextListener.class,
				new MockSCL(new AtomicReference<ServletContext>()), properties);
		serviceRegistrations.add(sr);

		servletContextDTO = getServletContextDTOByName("a");
		assertEquals(1, servletContextDTO.listenerDTOs.length);
		assertEquals(getServiceId(sr), servletContextDTO.listenerDTOs[0].serviceId);
	}

	public void test_140_9_ServletContextDTO_failed_servletContextHelper() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, 34);
		ServiceRegistration<?> sr = context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties);
		serviceRegistrations.add(sr);

		FailedServletContextDTO failedServletContextDTO = getFailedServletContextDTOByServiceId(getServiceId(sr));
		assertNotNull(failedServletContextDTO);
	}

	public void test_140_9_ServletContextDTO_failed_servlet() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, 24);
		ServiceRegistration<?> sr = context.registerService(Servlet.class, new MockServlet(), properties);
		serviceRegistrations.add(sr);

		FailedServletDTO failedServletDTO = getFailedServletDTOByServiceId(getServiceId(sr));
		assertNotNull(failedServletDTO);
	}

	public void test_140_9_ServletContextDTO_failed_resource() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, 456);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/");
		ServiceRegistration<?> sr = context.registerService(Object.class, new Object(), properties);
		serviceRegistrations.add(sr);

		FailedResourceDTO failedResourceDTO = getFailedResourceDTOByServiceId(getServiceId(sr));
		assertNotNull(failedResourceDTO);
	}

	public void test_140_9_ServletContextDTO_failed_filter() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, 678);
		ServiceRegistration<?> sr = context.registerService(Filter.class, new MockFilter(), properties);
		serviceRegistrations.add(sr);

		FailedFilterDTO failedFilterDTO = getFailedFilterDTOByServiceId(getServiceId(sr));
		assertNotNull(failedFilterDTO);
	}

	public void test_140_9_ServletContextDTO_failed_errorPage() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, "404");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new MockServlet(), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, "404");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		ServiceRegistration<?> sr = context.registerService(Servlet.class, new MockServlet(), properties);
		serviceRegistrations.add(sr);

		FailedErrorPageDTO failedErrorPageDTO = getFailedErrorPageDTOByServiceId(getServiceId(sr));
		assertNotNull(failedErrorPageDTO);
	}

	public void test_140_9_ServletContextDTO_failed_listener() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, 4567);
		ServiceRegistration< ? > sr = context.registerService(
				ServletContextListener.class,
				new MockSCL(new AtomicReference<ServletContext>()), properties);
		serviceRegistrations.add(sr);

		FailedListenerDTO failedListenerDTO = getFailedListenerDTOByServiceId(getServiceId(sr));
		assertNotNull(failedListenerDTO);
	}

	public void test_140_9_RequestInfoDTO_servlet() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<?> sr = context.registerService(Servlet.class, new MockServlet(), properties);
		serviceRegistrations.add(sr);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/");
		serviceRegistrations.add(context.registerService(Servlet.class, new MockServlet(), properties));

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertNull(requestInfoDTO.resourceDTO);
		assertEquals(getServiceId(sr), requestInfoDTO.servletDTO.serviceId);
	}

	public void test_140_9_RequestInfoDTO_resource() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/");
		ServiceRegistration<?> sr = context.registerService(Servlet.class, new MockServlet(), properties);
		serviceRegistrations.add(sr);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new MockServlet(), properties));

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertNull(requestInfoDTO.servletDTO);
		assertNotNull(requestInfoDTO.resourceDTO);
		assertEquals(getServiceId(sr), requestInfoDTO.resourceDTO.serviceId);
	}

	public void test_140_9_RequestInfoDTO_filters() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/");
		ServiceRegistration<?> sr = context.registerService(Servlet.class, new MockServlet(), properties);
		serviceRegistrations.add(sr);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Filter.class, new MockFilter(), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/*");
		serviceRegistrations.add(context.registerService(Filter.class, new MockFilter(), properties));

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertNull(requestInfoDTO.servletDTO);
		assertNotNull(requestInfoDTO.resourceDTO);
		assertEquals(2, requestInfoDTO.filterDTOs.length);
		assertEquals(getServiceId(sr), requestInfoDTO.resourceDTO.serviceId);
	}

	public void test_140_11_1() throws Exception {
		BundleContext context = getContext();

		ServiceReference<HttpServiceRuntime> srA = context.getServiceReference(HttpServiceRuntime.class);

		BundleWiring bundleWiring = srA.getBundle().adapt(BundleWiring.class);

		List<BundleCapability> capabilities = bundleWiring.getCapabilities(ImplementationNamespace.IMPLEMENTATION_NAMESPACE);

		assertTrue(capabilities.size() > 0);
		boolean found = false;

		for (Capability capability : capabilities) {
			Map<String, Object> attributes = capability.getAttributes();
			String name = (String) attributes.get(ImplementationNamespace.IMPLEMENTATION_NAMESPACE);

			if (name != null) {
				Version version = (Version)attributes.get("version");

				if (name.equals("osgi.http") && (version != null) && (version.equals(new Version("2.0.0")))) {
					Map<String, String> directives = capability.getDirectives();

					String uses = directives.get(Namespace.CAPABILITY_USES_DIRECTIVE);

					List<String> packages = Arrays.asList(uses.split("\\s*,\\s*"));

					assertTrue(packages.contains("jakarta.servlet"));
					assertTrue(packages.contains("jakarta.servlet.http"));
					assertTrue(packages.contains("org.osgi.service.servlet.context"));
					assertTrue(packages.contains("org.osgi.service.servlet.whiteboard"));

					found = true;
				}
			}
		}

		assertTrue(found);
	}

	public void test_140_11_2() throws Exception {
		BundleContext context = getContext();

		ServiceReference<HttpServiceRuntime> srA = context.getServiceReference(HttpServiceRuntime.class);

		BundleWiring bundleWiring = srA.getBundle().adapt(BundleWiring.class);

		List<BundleCapability> capabilities = bundleWiring.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE);

		boolean found = false;

		for (Capability capability : capabilities) {
			Map<String, Object> attributes = capability.getAttributes();
			String name = (String) attributes.get(PackageNamespace.PACKAGE_NAMESPACE);

			if (name.equals("jakarta.servlet")) {
				found = true;
			}
		}

		if (found) {
			capabilities = bundleWiring.getCapabilities(ContractNamespace.CONTRACT_NAMESPACE);

			boolean foundContract = false;

			for (Capability capability : capabilities) {
				Map<String, Object> attributes = capability.getAttributes();

				String name = (String) attributes.get(ContractNamespace.CONTRACT_NAMESPACE);

				if ("JakartaServlet".equals(name)) {
					Collection<Version> versions = Versions.plus(attributes.get(
							ContractNamespace.CAPABILITY_VERSION_ATTRIBUTE));

					for (Version v : versions) {
						if (v.equals(new Version("5.0.0"))) {
							Map<String,String> directives = capability
									.getDirectives();

							String uses = directives
									.get(Namespace.CAPABILITY_USES_DIRECTIVE);

							List<String> packages = Arrays
									.asList(uses.split("\\s*,\\s*"));

							assertTrue(packages.contains("jakarta.servlet"));
							assertTrue(packages
									.contains("jakarta.servlet.annotation"));
							assertTrue(packages
									.contains("jakarta.servlet.descriptor"));
							assertTrue(packages.contains("jakarta.servlet.http"));

							foundContract = true;
						}
					}
				}
			}

			assertTrue(foundContract);
		}
		else {
			List<BundleRequirement> requirements = bundleWiring.getRequirements(ContractNamespace.CONTRACT_NAMESPACE);

			boolean foundContract = false;

			if (!requirements.isEmpty()) {
				List<BundleWire> wires = bundleWiring.getRequiredWires(ContractNamespace.CONTRACT_NAMESPACE);

				for (BundleWire wire : wires) {
					BundleCapability capability = wire.getCapability();
					Map<String, Object> attributes = capability.getAttributes();

					String name = (String) attributes.get(ContractNamespace.CONTRACT_NAMESPACE);

					if ("JakartaServlet".equals(name)) {
						Collection<Version> versions = Versions.plus(attributes
								.get(ContractNamespace.CAPABILITY_VERSION_ATTRIBUTE));

						for (Version v : versions) {
							if (v.equals(new Version("5.0.0"))) {
								Map<String,String> directives = capability
										.getDirectives();

								String uses = directives.get(
										Namespace.CAPABILITY_USES_DIRECTIVE);

								List<String> packages = Arrays
										.asList(uses.split("\\s*,\\s*"));

								assertTrue(packages.contains("jakarta.servlet"));
								assertTrue(packages
										.contains("jakarta.servlet.annotation"));
								assertTrue(packages
										.contains("jakarta.servlet.descriptor"));
								assertTrue(packages
										.contains("jakarta.servlet.http"));

								foundContract = true;
							}
						}
					}
				}
			}

			assertTrue(foundContract);
		}
	}

	public void test_140_11_3() throws Exception {
		BundleContext context = getContext();

		ServiceReference<HttpServiceRuntime> srA = context.getServiceReference(HttpServiceRuntime.class);

		BundleWiring bundleWiring = srA.getBundle().adapt(BundleWiring.class);

		List<BundleCapability> capabilities = bundleWiring.getCapabilities(ServiceNamespace.SERVICE_NAMESPACE);

		boolean found = false;

		for (Capability capability : capabilities) {
			Map<String, Object> attributes = capability.getAttributes();
			@SuppressWarnings("unchecked")
			List<String> objectClasses = (List<String>) attributes.get(ServiceNamespace.CAPABILITY_OBJECTCLASS_ATTRIBUTE);

			if ((objectClasses != null) && objectClasses.contains(HttpServiceRuntime.class.getName())) {
				Map<String, String> directives = capability.getDirectives();

				String uses = directives.get("uses");

				List<String> packages = Arrays.asList(uses.split(","));

				assertTrue(packages.contains(
						"org.osgi.service.servlet.runtime"));
				assertTrue(packages.contains(
						"org.osgi.service.servlet.runtime.dto"));

				found = true;
			}
		}

		assertTrue(found);
	}

	@SuppressWarnings("unchecked")
	private Collection<String> getEndPoints(ServiceReference<HttpServiceRuntime> srA) {
		Object value = srA.getProperty(HttpServiceRuntimeConstants.HTTP_SERVICE_ENDPOINT);

		if (value == null) {
			return Collections.emptyList();
		}
		else if (value.getClass().isArray()) {
			assertTrue(Array.getLength(value) > 0);

			return Arrays.asList((String[]) value);
		}
		else if (value instanceof Collection) {
			return (Collection<String>) value;
		}

		return Collections.singletonList((String) value);
	}

}
