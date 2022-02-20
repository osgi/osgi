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
package org.osgi.test.cases.servlet.whiteboard.junit;

import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.servlet.whiteboard.ServletContextHelper;
import org.osgi.service.servlet.whiteboard.runtime.dto.DTOConstants;
import org.osgi.service.servlet.whiteboard.runtime.dto.FailedResourceDTO;
import org.osgi.service.servlet.whiteboard.runtime.dto.ResourceDTO;
import org.osgi.test.cases.servlet.whiteboard.junit.mock.MockSCHFactory;
import org.osgi.test.cases.servlet.whiteboard.junit.mock.MockServlet;

import jakarta.servlet.Servlet;

public class ResourceTestCase extends BaseHttpWhiteboardTestCase {

	public void test_table_140_6_HTTP_WHITEBOARD_RESOURCE_validation() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, 34l);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/org/osgi/test/cases/servlet/whiteboard/junit/res");
		ServiceRegistration<Object> sr = context.registerService(Object.class, new Object(), properties);
		serviceRegistrations.add(sr);

		FailedResourceDTO failedResourceDTO = getFailedResourceDTOByServiceId(getServiceId(sr));
		assertNotNull(failedResourceDTO);
		assertEquals(DTOConstants.FAILURE_REASON_VALIDATION_FAILED, failedResourceDTO.failureReason);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/*");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, 45);
		sr.setProperties(properties);

		failedResourceDTO = getFailedResourceDTOByServiceId(getServiceId(sr));
		assertNotNull(failedResourceDTO);
		assertEquals(DTOConstants.FAILURE_REASON_VALIDATION_FAILED, failedResourceDTO.failureReason);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/*");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, new String[] {"/a", "/b"});
		sr.setProperties(properties);

		failedResourceDTO = getFailedResourceDTOByServiceId(getServiceId(sr));
		assertNotNull(failedResourceDTO);
		assertEquals(DTOConstants.FAILURE_REASON_VALIDATION_FAILED, failedResourceDTO.failureReason);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, new String[] {"/a", "/b"});
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/org/osgi/test/cases/servlet/whiteboard/junit/res/index.txt");
		sr.setProperties(properties);

		failedResourceDTO = getFailedResourceDTOByServiceId(getServiceId(sr));
		assertNull(failedResourceDTO);

		ResourceDTO resourceDTO = getResourceDTOByServiceId(
				DEFAULT,
				getServiceId(sr));
		assertNotNull(resourceDTO);
		assertEquals(2, resourceDTO.patterns.length);
	}

	public void test_table_140_6_HTTP_WHITEBOARD_RESOURCE_required() {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/*");
		ServiceRegistration<Object> sr = context.registerService(Object.class, new Object(), properties);
		serviceRegistrations.add(sr);

		FailedResourceDTO failedResourceDTO = getFailedResourceDTOByServiceId(getServiceId(sr));
		assertNull(failedResourceDTO);
		ResourceDTO resourceDTO = getResourceDTOByServiceId(
				DEFAULT,
				getServiceId(sr));
		assertNull(resourceDTO);

		properties.remove(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/");
		sr.setProperties(properties);

		failedResourceDTO = getFailedResourceDTOByServiceId(getServiceId(sr));
		assertNull(failedResourceDTO);
		resourceDTO = getResourceDTOByServiceId(
				DEFAULT,
				getServiceId(sr));
		assertNull(resourceDTO);
	}

	public void test_140_6() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/*");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/org/osgi/test/cases/servlet/whiteboard/junit/res");
		ServiceRegistration<Object> sr = context.registerService(Object.class, new Object(), properties);
		serviceRegistrations.add(sr);

		ResourceDTO resourceDTO = getResourceDTOByServiceId(
				DEFAULT,
				getServiceId(sr));
		assertNotNull(resourceDTO);
		assertEquals("a", request("index.txt"));

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/*");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/org/osgi/test/cases");
		sr.setProperties(properties);

		resourceDTO = getResourceDTOByServiceId(
				DEFAULT,
				getServiceId(sr));
		assertNotNull(resourceDTO);
		assertEquals("404", request("index.txt", null).get("responseCode").get(0));
		assertEquals("a", request("servlet/whiteboard/junit/res/index.txt"));

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/res/*");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/org/osgi/test/cases/servlet/whiteboard/junit/res");
		sr.setProperties(properties);

		resourceDTO = getResourceDTOByServiceId(
				DEFAULT,
				getServiceId(sr));
		assertNotNull(resourceDTO);
		assertEquals("404", request("servlet/whiteboard/junit/res/index.txt", null).get("responseCode").get(0));
		assertEquals("a", request("res/index.txt"));
	}

	public void test_140_6_20to21() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/other.txt");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/org/osgi/test/cases/servlet/whiteboard/junit/res/index.txt");
		ServiceRegistration<Object> sr = context.registerService(Object.class, new Object(), properties);
		serviceRegistrations.add(sr);

		ResourceDTO resourceDTO = getResourceDTOByServiceId(
				DEFAULT,
				getServiceId(sr));
		assertNotNull(resourceDTO);
		assertEquals("a", request("other.txt"));
		assertEquals("404", request("index.txt", null).get("responseCode").get(0));
		assertEquals("404", request("org/osgi/test/cases/servlet/whiteboard/junit/res/index.txt", null).get("responseCode").get(0));
	}

	public void test_140_6_20to21_commonProperties() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/other.txt");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/org/osgi/test/cases/servlet/whiteboard/junit/res/index.txt");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=foo)");
		ServiceRegistration<Object> sr = context.registerService(Object.class, new Object(), properties);
		serviceRegistrations.add(sr);

		FailedResourceDTO failedResourceDTO = getFailedResourceDTOByServiceId(getServiceId(sr));
		assertNotNull(failedResourceDTO);
		assertEquals(DTOConstants.FAILURE_REASON_NO_SERVLET_CONTEXT_MATCHING, failedResourceDTO.failureReason);

		ResourceDTO resourceDTO = getResourceDTOByServiceId(
				DEFAULT,
				getServiceId(sr));
		assertNull(resourceDTO);
		assertEquals("404", request("other.txt", null).get("responseCode").get(0));

		properties.remove(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET, "(some=foo)");
		sr.setProperties(properties);

		failedResourceDTO = getFailedResourceDTOByServiceId(getServiceId(sr));
		assertNull(failedResourceDTO);

		resourceDTO = getResourceDTOByServiceId(
				DEFAULT,
				getServiceId(sr));
		assertNull(resourceDTO);
		assertEquals("404", request("other.txt", null).get("responseCode").get(0));
	}

	public void test_140_6_28to29() throws Exception {
		BundleContext context = getContext();

		class AServletContextHelper extends ServletContextHelper {

			public AServletContextHelper(Bundle bundle) {
				super(bundle);
			}

			@Override
			public URL getResource(String name) {
				if (!name.startsWith("/org/osgi/test/cases/servlet/whiteboard/junit/res")) {
					name = "/org/osgi/test/cases/servlet/whiteboard/junit/res" + name;
				}
				name = name.replaceAll("//", "/");
				return super.getResource(name);
			}

		};

		MockSCHFactory factory = new MockSCHFactory() {

			@Override
			public ServletContextHelper getService(Bundle bundle, ServiceRegistration<ServletContextHelper> registration) {
				return new AServletContextHelper(bundle);
			}

		};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context1");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, factory, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/*");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=context1)");
		ServiceRegistration<Object> sr = context.registerService(Object.class, new Object(), properties);
		serviceRegistrations.add(sr);

		assertEquals("404", request("context1/other.txt", null).get("responseCode").get(0));
		assertEquals("a", request("context1/index.txt"));
	}

	public void test_140_6_1() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/*");
		serviceRegistrations.add(context.registerService(Servlet.class, new MockServlet().content("b"), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/*");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/org/osgi/test/cases/servlet/whiteboard/junit/res");
		ServiceRegistration<Object> sr = context.registerService(Object.class, new Object(), properties);
		serviceRegistrations.add(sr);

		FailedResourceDTO failedResourceDTO = getFailedResourceDTOByServiceId(getServiceId(sr));
		assertNotNull(failedResourceDTO);
		assertEquals(DTOConstants.FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE, failedResourceDTO.failureReason);

		ResourceDTO resourceDTO = getResourceDTOByServiceId(
				DEFAULT,
				getServiceId(sr));
		assertNull(resourceDTO);
		assertEquals("b", request("index.txt"));

		properties.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
		sr.setProperties(properties);

		resourceDTO = getResourceDTOByServiceId(
				DEFAULT,
				getServiceId(sr));
		assertNotNull(resourceDTO);
		assertEquals("a", request("index.txt"));

		failedResourceDTO = getFailedResourceDTOByServiceId(getServiceId(sr));
		assertNull(failedResourceDTO);
	}

	/**
	 * Tests for 1.1 Update
	 */

	/**
	 * Registration of servlet with http service (not allowed).
	 */
	public void testHttpServiceAndResource() throws Exception {
		final String path = "/tesths";
		Dictionary<String,Object> properties = new Hashtable<String,Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN,
				path);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX,
				path);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
				"(" + HttpWhiteboardConstants.HTTP_SERVICE_CONTEXT_PROPERTY
						+ "=*)");
		final long before = this.getHttpRuntimeChangeCount();
		this.serviceRegistrations.add(this.getContext()
				.registerService(Servlet.class, new MockServlet(), properties));
		this.waitForRegistration(before);

		boolean found = false;
		for (final FailedResourceDTO fsd : this.getHttpServiceRuntime()
				.getRuntimeDTO().failedResourceDTOs) {
			if (fsd.patterns.length > 0 && path.equals(fsd.patterns[0])) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

}
