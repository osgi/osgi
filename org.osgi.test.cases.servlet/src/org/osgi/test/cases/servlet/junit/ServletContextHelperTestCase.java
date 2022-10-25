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

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.service.FindHook;
import org.osgi.service.servlet.context.ServletContextHelper;
import org.osgi.service.servlet.runtime.HttpServiceRuntime;
import org.osgi.service.servlet.runtime.dto.DTOConstants;
import org.osgi.service.servlet.runtime.dto.FailedServletContextDTO;
import org.osgi.service.servlet.runtime.dto.RequestInfoDTO;
import org.osgi.service.servlet.runtime.dto.RuntimeDTO;
import org.osgi.service.servlet.runtime.dto.ServletContextDTO;
import org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants;
import org.osgi.test.cases.servlet.junit.mock.MockSCL;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.http.HttpServlet;

public class ServletContextHelperTestCase extends BaseHttpWhiteboardTestCase {

	public void test_140_2_6to7() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context1");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context2");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context2");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();
		AtomicReference<ServletContext> sc2 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=context1)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=context2)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc2), properties));

		assertNotSame(sc1.get(), sc2.get());
	}

	public void test_140_2_7to8() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context1");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=context1)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		assertEquals("context1", sc1.get().getServletContextName());
	}

	public void test_140_2_8to9() throws Exception {
		BundleContext context = getContext();

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		assertEquals(DEFAULT, sc1.get().getServletContextName());
	}

	public void test_140_2_10to12() throws Exception {
		BundleContext context = getContext();

		Bundle bundle = install("/tb2.jar");

		try {
			bundle.start();

			Dictionary<String, Object> properties = new Hashtable<String, Object>();
			properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "sc1");
			properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/sc1");
			serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

			AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

			properties = new Hashtable<String, Object>();
			properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=sc1)");
			properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
			serviceRegistrations.add(
					context.registerService(
							new String[] {ServletContextListener.class.getName(), MockSCL.class.getName()},
							new MockSCL(sc1), properties));

			ClassLoader classLoader1 = sc1.get().getClassLoader();

			Collection<ServiceReference<MockSCL>> serviceReferences = context.getServiceReferences(MockSCL.class, "(test=true)");

			MockSCL mockSCL = context.getService(serviceReferences.iterator().next());

			ClassLoader classLoader2 = mockSCL.getSC().getClassLoader();

			String bsn1 = getSymbolicName(classLoader1);
			String bsn2 = getSymbolicName(classLoader2);

			assertFalse(bsn1.equals(bsn2));
		} finally {
			bundle.uninstall();
		}
	}

	public void test_140_2_13to14() throws Exception {
		BundleContext context = getContext();
		ServiceReference<ServletContextHelper> serviceReference = context.getServiceReference(ServletContextHelper.class);

		assertNotNull(serviceReference);
		assertEquals(Constants.SCOPE_BUNDLE, serviceReference.getProperty(Constants.SERVICE_SCOPE));
		assertEquals(
				DEFAULT,
				serviceReference.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME));
	}

	public void test_140_2_15to16() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		ServiceRegistration<ServletContextListener> serviceRegistration = context.registerService(ServletContextListener.class, new MockSCL(new AtomicReference<ServletContext>()), properties);
		serviceRegistrations.add(serviceRegistration);

		assertEquals(context.getBundle(), serviceRegistration.getReference().getBundle());
	}

	public void test_140_2_17to22() throws Exception {
		final BundleContext context = getContext();

		FindHook findHook = new FindHook() {

			@Override
			public void find(
					BundleContext bundleContext, String name, String filter,
					boolean allServices, Collection<ServiceReference<?>> references) {

				if (bundleContext != context) {
					return;
				}

				// don't show default ServletContextHelper
				for (Iterator<ServiceReference<?>> iterator = references.iterator(); iterator.hasNext();) {
					ServiceReference<?> sr = iterator.next();

					if (DEFAULT.equals(sr.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME))) {
						iterator.remove();
					}
				}
			}

		};

		serviceRegistrations.add(context.registerService(FindHook.class, findHook, null));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		ServiceRegistration<ServletContextListener> serviceRegistration = context.registerService(ServletContextListener.class, new MockSCL(sc1), properties);
		serviceRegistrations.add(serviceRegistration);

		assertNull(sc1.get());
	}

	public void test_table_140_1_HTTP_WHITEBOARD_CONTEXT_NAME_type() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, Boolean.TRUE);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context1");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		HttpServiceRuntime httpServiceRuntime = getHttpServiceRuntime();

		RuntimeDTO runtimeDTO = httpServiceRuntime.getRuntimeDTO();

		FailedServletContextDTO[] failedServletContextDTOs = runtimeDTO.failedServletContextDTOs;

		assertNotNull(failedServletContextDTOs);
		assertEquals(1, failedServletContextDTOs.length);
		assertEquals(
				DTOConstants.FAILURE_REASON_VALIDATION_FAILED,
				failedServletContextDTOs[0].failureReason);
	}

	public void test_table_140_1_HTTP_WHITEBOARD_CONTEXT_NAME_required() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context1");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		HttpServiceRuntime httpServiceRuntime = getHttpServiceRuntime();

		RuntimeDTO runtimeDTO = httpServiceRuntime.getRuntimeDTO();

		FailedServletContextDTO[] failedServletContextDTOs = runtimeDTO.failedServletContextDTOs;

		assertNotNull(failedServletContextDTOs);
		assertEquals(1, failedServletContextDTOs.length);
		assertEquals(
				DTOConstants.FAILURE_REASON_VALIDATION_FAILED,
				failedServletContextDTOs[0].failureReason);
	}

	public void test_table_140_1_HTTP_WHITEBOARD_CONTEXT_NAME_syntax() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "$badname%");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context1");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		HttpServiceRuntime httpServiceRuntime = getHttpServiceRuntime();

		RuntimeDTO runtimeDTO = httpServiceRuntime.getRuntimeDTO();

		FailedServletContextDTO[] failedServletContextDTOs = runtimeDTO.failedServletContextDTOs;

		assertNotNull(failedServletContextDTOs);
		assertEquals(1, failedServletContextDTOs.length);
		assertEquals(
				DTOConstants.FAILURE_REASON_VALIDATION_FAILED,
				failedServletContextDTOs[0].failureReason);
	}

	public void test_table_140_1_HTTP_WHITEBOARD_CONTEXT_NAME_bindUsingContextSelect() throws Exception {
		BundleContext context = getContext();
		String contextName = "context1";

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, contextName);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context1");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=" + contextName + ")");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		assertEquals(contextName, sc1.get().getServletContextName());
	}

	public void test_table_140_1_HTTP_WHITEBOARD_CONTEXT_NAME_default() throws Exception {
		BundleContext context = getContext();
		ServiceReference<ServletContextHelper> serviceReference = context.getServiceReference(ServletContextHelper.class);

		assertNotNull(serviceReference);
		assertEquals(Constants.SCOPE_BUNDLE, serviceReference.getProperty(Constants.SERVICE_SCOPE));
		assertEquals(
				DEFAULT,
				serviceReference.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME));
	}

	public void test_table_140_1_HTTP_WHITEBOARD_CONTEXT_NAME_overrideDefault() throws Exception {
		BundleContext context = getContext();
		String contextPath = "/context1";

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, DEFAULT);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, contextPath);
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		assertEquals(DEFAULT, sc1.get().getServletContextName());
		assertEquals(contextPath, sc1.get().getContextPath());
	}

	public void test_table_140_1_HTTP_WHITEBOARD_CONTEXT_NAME_highestRanked() throws Exception {
		BundleContext context = getContext();
		String contextPath = "/otherContext";

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, DEFAULT);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context1");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, DEFAULT);
		properties.put(Constants.SERVICE_RANKING, Integer.valueOf(Integer.MAX_VALUE));
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, contextPath);
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		assertEquals(DEFAULT, sc1.get().getServletContextName());
		assertEquals(contextPath, sc1.get().getContextPath());
	}

	public void test_table_140_1_HTTP_WHITEBOARD_CONTEXT_NAME_tieGoesToOldest() throws Exception {
		BundleContext context = getContext();
		String contextPath = "/context1";

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, DEFAULT);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, contextPath);
		properties.put(Constants.SERVICE_RANKING, Integer.valueOf(1000));
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, DEFAULT);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/otherContext");
		properties.put(Constants.SERVICE_RANKING, Integer.valueOf(1000));
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		assertEquals(DEFAULT, sc1.get().getServletContextName());
		assertEquals(contextPath, sc1.get().getContextPath());
	}

	public void test_table_140_1_HTTP_WHITEBOARD_CONTEXT_PATH_type() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, Boolean.FALSE);
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		HttpServiceRuntime httpServiceRuntime = getHttpServiceRuntime();

		RuntimeDTO runtimeDTO = httpServiceRuntime.getRuntimeDTO();

		FailedServletContextDTO[] failedServletContextDTOs = runtimeDTO.failedServletContextDTOs;

		assertNotNull(failedServletContextDTOs);
		assertEquals(1, failedServletContextDTOs.length);
		assertEquals(
				DTOConstants.FAILURE_REASON_VALIDATION_FAILED,
				failedServletContextDTOs[0].failureReason);
	}

	public void test_table_140_1_HTTP_WHITEBOARD_CONTEXT_PATH_required() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		HttpServiceRuntime httpServiceRuntime = getHttpServiceRuntime();

		RuntimeDTO runtimeDTO = httpServiceRuntime.getRuntimeDTO();

		FailedServletContextDTO[] failedServletContextDTOs = runtimeDTO.failedServletContextDTOs;

		assertNotNull(failedServletContextDTOs);
		assertEquals(1, failedServletContextDTOs.length);
		assertEquals(
				DTOConstants.FAILURE_REASON_VALIDATION_FAILED,
				failedServletContextDTOs[0].failureReason);
	}

	public void test_table_140_1_HTTP_WHITEBOARD_CONTEXT_PATH_syntax() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "%context");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		HttpServiceRuntime httpServiceRuntime = getHttpServiceRuntime();

		RuntimeDTO runtimeDTO = httpServiceRuntime.getRuntimeDTO();

		FailedServletContextDTO[] failedServletContextDTOs = runtimeDTO.failedServletContextDTOs;

		assertNotNull(failedServletContextDTOs);
		assertEquals(1, failedServletContextDTOs.length);
		assertEquals(
				DTOConstants.FAILURE_REASON_VALIDATION_FAILED,
				failedServletContextDTOs[0].failureReason);
	}

	public void test_table_140_1_HTTP_WHITEBOARD_CONTEXT_PATH_defaultPath() throws Exception {
		BundleContext context = getContext();

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		ServiceRegistration<ServletContextListener> serviceRegistration = context.registerService(ServletContextListener.class, new MockSCL(sc1), properties);
		serviceRegistrations.add(serviceRegistration);

		assertEquals("", sc1.get().getContextPath());
	}

	public void test_table_140_1_HTTP_WHITEBOARD_CONTEXT_PATH_overrideDefault() throws Exception {
		BundleContext context = getContext();
		String contextPath = "/context1";

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, DEFAULT);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, contextPath);
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		assertEquals(DEFAULT, sc1.get().getServletContextName());
		assertEquals(contextPath, sc1.get().getContextPath());
	}

	public void test_table_140_1_INIT_PARAMs() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_INIT_PARAM_PREFIX + "p1", "v1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_INIT_PARAM_PREFIX + "p2", "v2");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_INIT_PARAM_PREFIX + "p3", Boolean.TRUE);
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=context)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		List<String> initParamNames = Collections.list(sc1.get().getInitParameterNames());

		assertTrue(initParamNames.contains("p1"));
		assertTrue(initParamNames.contains("p2"));
		assertFalse(initParamNames.contains("p3"));

		assertEquals("v1", sc1.get().getInitParameter("p1"));
		assertEquals("v2", sc1.get().getInitParameter("p2"));
	}

	public void test_140_2_23to25() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "foo");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/foo");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "foobar");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/foo/bar");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=foo)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "first");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/bar/someServlet");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=foobar)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "second");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/someServlet");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}, properties));

		ServletContextDTO servletContextDTO = getServletContextDTOByName("foobar");

		assertNotNull(servletContextDTO);

		HttpServiceRuntime httpServiceRuntime = getHttpServiceRuntime();

		RequestInfoDTO requestInfoDTO = httpServiceRuntime.calculateRequestInfoDTO("/foo/bar/someServlet");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertEquals(servletContextDTO.serviceId, requestInfoDTO.servletDTO.servletContextId);
		assertEquals("second", requestInfoDTO.servletDTO.name);
	}

	public void test_140_2_26to27() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "foo");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/foo");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "foobar");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/foo");
		properties.put(Constants.SERVICE_RANKING, Integer.valueOf(Integer.MAX_VALUE));
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=foo)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "first");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/bar/someServlet");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=foobar)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "second");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/bar/someServlet");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}, properties));

		ServletContextDTO servletContextDTO = getServletContextDTOByName("foobar");

		assertNotNull(servletContextDTO);

		HttpServiceRuntime httpServiceRuntime = getHttpServiceRuntime();

		RequestInfoDTO requestInfoDTO = httpServiceRuntime.calculateRequestInfoDTO("/foo/bar/someServlet");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertEquals(servletContextDTO.serviceId, requestInfoDTO.servletDTO.servletContextId);
		assertEquals("second", requestInfoDTO.servletDTO.name);
	}

	public void test_140_2_27() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "foo");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/foo");
		properties.put(Constants.SERVICE_RANKING, Integer.valueOf(1000));
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "foobar");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/foo");
		properties.put(Constants.SERVICE_RANKING, Integer.valueOf(1000));
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=foo)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "first");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/bar/someServlet");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=foobar)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "second");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/bar/someServlet");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}, properties));

		ServletContextDTO servletContextDTO = getServletContextDTOByName("foo");

		assertNotNull(servletContextDTO);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/foo/bar/someServlet");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertEquals(servletContextDTO.serviceId, requestInfoDTO.servletDTO.servletContextId);
		assertEquals("first", requestInfoDTO.servletDTO.name);
	}

	public void test_140_2_34to36() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "foo");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/foo");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "foobar");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/foo/bar");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=foo)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "first");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/*");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=foobar)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "second");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/*");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}, properties));

		ServletContextDTO servletContextDTO = getServletContextDTOByName("foo");

		assertNotNull(servletContextDTO);

		HttpServiceRuntime httpServiceRuntime = getHttpServiceRuntime();

		RequestInfoDTO requestInfoDTO = httpServiceRuntime.calculateRequestInfoDTO("/foo/bars/someServlet");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertEquals(servletContextDTO.serviceId, requestInfoDTO.servletDTO.servletContextId);
		assertEquals("first", requestInfoDTO.servletDTO.name);
	}

	public void test_140_2_39to41() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "foo");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/foo");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "foo");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/foo/bar");
		properties.put(Constants.SERVICE_RANKING, Integer.valueOf(1000));
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		FailedServletContextDTO failedServletContextDTO = getFailedServletContextDTOByName("foo");

		assertNotNull(failedServletContextDTO);
		assertEquals(
				DTOConstants.FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE,
				failedServletContextDTO.failureReason);
	}

	public void test_140_2_41to43() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, DEFAULT);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/foo");
		ServiceRegistration<ServletContextHelper> sr = context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties);
		serviceRegistrations.add(sr);

		FailedServletContextDTO failedServletContextDTO = getFailedServletContextDTOByName(DEFAULT);

		assertNotNull(failedServletContextDTO);
		assertEquals(
				DTOConstants.FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE,
				failedServletContextDTO.failureReason);
		assertEquals("/", failedServletContextDTO.contextPath);
		assertNotSame(
				getServiceId(sr),
				failedServletContextDTO.serviceId);
	}

	public void test_140_2_1() throws Exception {
		BundleContext context = getContext();
		ServiceReference<ServletContextHelper> serviceReference = context.getServiceReference(ServletContextHelper.class);

		assertNotNull(serviceReference);

		ServletContextHelper servletContextHelper = context.getService(serviceReference);

		assertNotNull(servletContextHelper);

		assertNull(servletContextHelper.getMimeType("index.html"));
	}

	public void test_140_2_2() throws Exception {
		BundleContext context = getContext();
		ServiceReference<ServletContextHelper> serviceReference = context.getServiceReference(ServletContextHelper.class);

		assertNotNull(serviceReference);

		ServletContextHelper servletContextHelper = context.getService(serviceReference);

		assertNotNull(servletContextHelper);

		assertNull(servletContextHelper.getRealPath("META-INF/MANIFEST.MF"));
	}

	public void test_140_2_3() throws Exception {
		BundleContext context = getContext();
		ServiceReference<ServletContextHelper> serviceReference = context.getServiceReference(ServletContextHelper.class);

		assertNotNull(serviceReference);

		ServletContextHelper servletContextHelper = context.getService(serviceReference);

		assertNotNull(servletContextHelper);

		assertNotNull(servletContextHelper.getResource("META-INF/MANIFEST.MF"));
	}

	public void test_140_2_4() throws Exception {
		BundleContext context = getContext();
		ServiceReference<ServletContextHelper> serviceReference = context.getServiceReference(ServletContextHelper.class);

		assertNotNull(serviceReference);

		ServletContextHelper servletContextHelper = context.getService(serviceReference);

		assertNotNull(servletContextHelper);

		assertNotNull(servletContextHelper.getResourcePaths("META-INF/"));
	}

	public void test_140_2_5() throws Exception {
		BundleContext context = getContext();
		ServiceReference<ServletContextHelper> serviceReference = context.getServiceReference(ServletContextHelper.class);

		assertNotNull(serviceReference);

		ServletContextHelper servletContextHelper = context.getService(serviceReference);

		assertNotNull(servletContextHelper);

		assertTrue(servletContextHelper.handleSecurity(null, null));
	}

	public void test_140_2_6_addFilter() throws Exception {
		BundleContext context = getContext();
		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);

		try {
			servletContext.addFilter((String) null, (Class<? extends jakarta.servlet.Filter>) null);

			fail();
		} catch (UnsupportedOperationException uoe) {
		}

		try {
			servletContext.addFilter((String) null, (jakarta.servlet.Filter) null);

			fail();
		} catch (UnsupportedOperationException uoe) {
		}

		try {
			servletContext.addFilter((String) null, (String) null);

			fail();
		} catch (UnsupportedOperationException uoe) {
		}
	}

	public void test_140_2_6_addListener() throws Exception {
		BundleContext context = getContext();
		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);

		try {
			servletContext.addListener((Class<? extends java.util.EventListener>) null);

			fail();
		} catch (UnsupportedOperationException uoe) {
		}

		try {
			servletContext.addListener((java.util.EventListener) null);

			fail();
		} catch (UnsupportedOperationException uoe) {
		}

		try {
			servletContext.addListener((String) null);

			fail();
		} catch (UnsupportedOperationException uoe) {
		}
	}

	public void test_140_2_6_addServlet() throws Exception {
		BundleContext context = getContext();
		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);

		try {
			servletContext.addServlet((String) null, (Class<? extends jakarta.servlet.Servlet>) null);

			fail();
		} catch (UnsupportedOperationException uoe) {
		}

		try {
			servletContext.addServlet((String) null, (jakarta.servlet.Servlet) null);

			fail();
		} catch (UnsupportedOperationException uoe) {
		}

		try {
			servletContext.addServlet((String) null, (String) null);

			fail();
		} catch (UnsupportedOperationException uoe) {
		}
	}

	public void test_140_2_6_createFilter() throws Exception {
		BundleContext context = getContext();
		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);

		try {
			servletContext.createFilter((Class<? extends jakarta.servlet.Filter>) null);

			fail();
		} catch (UnsupportedOperationException uoe) {
		}
	}

	public void test_140_2_6_createListener() throws Exception {
		BundleContext context = getContext();
		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);

		try {
			servletContext.createListener((Class<? extends java.util.EventListener>) null);

			fail();
		} catch (UnsupportedOperationException uoe) {
		}
	}

	public void test_140_2_6_createServlet() throws Exception {
		BundleContext context = getContext();
		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);

		try {
			servletContext.createServlet((Class<? extends jakarta.servlet.Servlet>) null);

			fail();
		} catch (UnsupportedOperationException uoe) {
		}
	}

	public void test_140_2_6_declareRoles() throws Exception {
		BundleContext context = getContext();
		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);

		try {
			servletContext.declareRoles("user");

			fail();
		} catch (UnsupportedOperationException uoe) {
		}
	}

	public void test_140_2_6_attributes() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context1");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context2");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context2");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();
		AtomicReference<ServletContext> sc2 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=context1)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=context2)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc2), properties));

		ServletContext servletContext1 = sc1.get();
		ServletContext servletContext2 = sc2.get();

		assertNotNull(servletContext1);
		assertNotNull(servletContext2);

		servletContext1.setAttribute("attribute1", "value1");
		assertTrue(Collections.list(servletContext1.getAttributeNames()).contains("attribute1"));
		assertNull(servletContext2.getAttribute("attribute1"));
		servletContext1.removeAttribute("attribute1");
		assertNull(servletContext1.getAttribute("attribute1"));

		servletContext2.setAttribute("attribute2", "value2");
		assertTrue(Collections.list(servletContext2.getAttributeNames()).contains("attribute2"));
		assertNull(servletContext1.getAttribute("attribute2"));
		servletContext2.removeAttribute("attribute2");
		assertNull(servletContext2.getAttribute("attribute2"));
	}

	public void test_140_2_6_getClassLoader() throws Exception {
		test_140_2_10to12();
	}

	public void test_140_2_6_getContextPath() throws Exception {
		BundleContext context = getContext();
		String contextPath = "/context1";

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, contextPath);
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=context1)");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		assertEquals(contextPath, sc1.get().getContextPath());
	}

	public void test_140_2_6_initParameters() throws Exception {
		test_table_140_1_INIT_PARAMs();
	}

	public void test_140_2_6_getJspConfigdescriptor() throws Exception {
		BundleContext context = getContext();
		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);

		assertNull(servletContext.getJspConfigDescriptor());
	}

	public void test_140_2_6_getMimeType() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		ServletContextHelper servletContextHelper = new ServletContextHelper() {

			@Override
			public String getMimeType(String name) {
				invoked.set(true);

				return null;
			}

		};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context1");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, servletContextHelper, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=context1)");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);

		servletContext.getMimeType("index.html");

		assertTrue(invoked.get());
	}

	public void test_140_2_6_getNamedDispatcher() throws Exception {
		BundleContext context = getContext();
		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "first");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/bar/someServlet");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}, properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);

		RequestDispatcher requestDispatcher = servletContext.getNamedDispatcher("first");

		assertNotNull(requestDispatcher);
	}

	public void test_140_2_6_getRealPath() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		ServletContextHelper servletContextHelper = new ServletContextHelper() {

			@Override
			public String getRealPath(String path) {
				invoked.set(true);

				return null;
			}

		};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context1");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, servletContextHelper, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=context1)");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);

		servletContext.getRealPath("META-INF/MANIFEST.MF");

		assertTrue(invoked.get());
	}

	public void test_140_2_6_getResource() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		ServletContextHelper servletContextHelper = new ServletContextHelper() {

			@Override
			public URL getResource(String path) {
				invoked.set(true);

				return null;
			}

		};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context1");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, servletContextHelper, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=context1)");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);

		servletContext.getResource("META-INF/MANIFEST.MF");

		assertTrue(invoked.get());
	}

	public void test_140_2_6_getRequestDispatcher() throws Exception {
		BundleContext context = getContext();
		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "first");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/bar/someServlet");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}, properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);

		RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher("/bar/someServlet");

		assertNotNull(requestDispatcher);
	}

	public void test_140_2_6_getResourceAsStream() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		ServletContextHelper servletContextHelper = new ServletContextHelper() {

			@Override
			public Set<String> getResourcePaths(String path) {
				invoked.set(true);

				return null;
			}

			@Override
			public URL getResource(String path) {
				invoked.set(true);

				return null;
			}

		};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context1");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, servletContextHelper, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=context1)");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);

		servletContext.getResourceAsStream("META-INF/MANIFEST.MF");

		assertTrue(invoked.get());
	}

	public void test_140_2_6_getResourcePaths() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		ServletContextHelper servletContextHelper = new ServletContextHelper() {

			@Override
			public Set<String> getResourcePaths(String path) {
				invoked.set(true);

				return null;
			}

		};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context1");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, servletContextHelper, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=context1)");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);

		servletContext.getResourcePaths("/META-INF/");

		assertTrue(invoked.get());
	}

	public void test_140_2_6_getServletContextName() throws Exception {
		test_140_2_7to8();
	}

	public void test_140_2_6_getSessionCookieConfig() throws Exception {
		BundleContext context = getContext();

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);

		SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();

		assertNotNull(sessionCookieConfig);

		try {
			sessionCookieConfig.setComment("test");

			fail();
		} catch (IllegalStateException ise) {
		}

		try {
			sessionCookieConfig.setComment("test");

			fail();
		} catch (IllegalStateException ise) {
		}

		try {
			sessionCookieConfig.setDomain("test");

			fail();
		} catch (IllegalStateException ise) {
		}

		try {
			sessionCookieConfig.setHttpOnly(true);

			fail();
		} catch (IllegalStateException ise) {
		}

		try {
			sessionCookieConfig.setMaxAge(0);

			fail();
		} catch (IllegalStateException ise) {
		}

		try {
			sessionCookieConfig.setName("test");

			fail();
		} catch (IllegalStateException ise) {
		}

		try {
			sessionCookieConfig.setPath("test");

			fail();
		} catch (IllegalStateException ise) {
		}

		try {
			sessionCookieConfig.setSecure(false);

			fail();
		} catch (IllegalStateException ise) {
		}
	}

	public void test_140_2_6_setInitParameter() throws Exception {
		BundleContext context = getContext();
		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);

		try {
			servletContext.setInitParameter(null, null);

			fail();
		} catch (IllegalStateException ise) {
		}
	}

	public void test_140_2_6_setSessionTrackingModes() throws Exception {
		BundleContext context = getContext();
		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);

		try {
			servletContext.setSessionTrackingModes(null);

			fail();
		} catch (IllegalStateException ise) {
		}
	}

	public void test_table_140_3_HTTP_WHITEBOARD_CONTEXT_SELECT() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context1");
		properties.put("some.property", "some.value");
		properties.put("foo.property", "foo.value");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context2");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context2");
		properties.put("some.property", "some.value");
		properties.put("fum.property", "fum.value");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(&(some.property=some.value)(fum.property=fum.value))");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		ServletContext servletContext = sc1.get();

		assertNotNull(servletContext);
		assertEquals("context2", servletContext.getServletContextName());
	}

	public void test_table_140_3_HTTP_WHITEBOARD_TARGET() throws Exception {
		BundleContext context = getContext();

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET, "(some.property=some.value)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		ServletContext servletContext = sc1.get();

		assertNull(servletContext);
	}

	public void test_140_3_4to5() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context1");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "context2");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context2");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=context*)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");

		final Stack<AtomicReference<ServletContext>> scs = new Stack<AtomicReference<ServletContext>>();

		PrototypeServiceFactory<ServletContextListener> factory = new PrototypeServiceFactory<ServletContextListener>() {

			@Override
			public ServletContextListener getService(Bundle bundle, ServiceRegistration<ServletContextListener> registration) {
				return new MockSCL(scs.push(new AtomicReference<ServletContext>()));
			}

			@Override
			public void ungetService(Bundle bundle, ServiceRegistration<ServletContextListener> registration, ServletContextListener service) {
			}

		};

		serviceRegistrations.add(context.registerService(ServletContextListener.class, factory, properties));

		Set<String> names = new HashSet<String>();

		for (AtomicReference<ServletContext> scr : scs) {
			assertNotNull(scr);
			ServletContext servletContext = scr.get();
			assertNotNull(servletContext);
			names.add(servletContext.getServletContextName());
		}

		assertTrue(names.contains("context1"));
		assertTrue(names.contains("context2"));
	}

}
