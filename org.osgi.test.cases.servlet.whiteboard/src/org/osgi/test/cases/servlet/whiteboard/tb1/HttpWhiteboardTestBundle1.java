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
package org.osgi.test.cases.servlet.whiteboard.tb1;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants;

import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

public class HttpWhiteboardTestBundle1 implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		Dictionary<String, Object> properties;

		// test_basicServlet

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/TestServlet1");
		serviceRegistrations.add(
				context.registerService(Servlet.class, new TestServlet("a"), properties));

		// test_servletInContext

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/TestServlet2");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
				"(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=sc1)");
		serviceRegistrations.add(
				context.registerService(Servlet.class, new TestServletReturnsContextPath(), properties));

		// test_ErrorPage1

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/TestErrorPage1/*");
		serviceRegistrations.add(
				context.registerService(Servlet.class, new TestServlet(null, HttpServletResponse.SC_FORBIDDEN), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, "403");
		serviceRegistrations.add(
				context.registerService(Servlet.class, new TestServlet("403 ERROR"), properties));

		// test_ErrorPage2

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/TestErrorPage2/*");
		serviceRegistrations.add(
				context.registerService(Servlet.class, new TestServlet(null, null, new ServletException()), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, ServletException.class.getName());
		serviceRegistrations.add(
				context.registerService(Servlet.class, new TestServlet("500 ERROR"), properties));

		// test_basicFilter

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/TestFilter1/*");
		serviceRegistrations.add(
				context.registerService(Servlet.class, new TestServlet("a"), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/TestFilter1/*");
		serviceRegistrations.add(
				context.registerService(Filter.class, new TestFilter('b'), properties));

		// test_twoFiltersOrderedByRegistration

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/TestFilter2/*");
		serviceRegistrations.add(
				context.registerService(Servlet.class, new TestServlet("a"), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/TestFilter2/*");
		serviceRegistrations.add(
				context.registerService(Filter.class, new TestFilter('b'), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/TestFilter2/*");
		serviceRegistrations.add(
				context.registerService(Filter.class, new TestFilter('c'), properties));

		// test_threeFiltersOrderedByRegistration

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/TestFilter3/*");
		serviceRegistrations.add(
				context.registerService(Servlet.class, new TestServlet("a"), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/TestFilter3/*");
		serviceRegistrations.add(
				context.registerService(Filter.class, new TestFilter('b'), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/TestFilter3/*");
		serviceRegistrations.add(
				context.registerService(Filter.class, new TestFilter('c'), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/TestFilter3/*");
		serviceRegistrations.add(
				context.registerService(Filter.class, new TestFilter('d'), properties));

		// test_threeFiltersOrderedByRanking

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/TestFilter4/*");
		serviceRegistrations.add(
				context.registerService(Servlet.class, new TestServlet("a"), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/TestFilter4/*");
		serviceRegistrations.add(
				context.registerService(Filter.class, new TestFilter('b'), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/TestFilter4/*");
		serviceRegistrations.add(
				context.registerService(Filter.class, new TestFilter('c'), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/TestFilter4/*");
		properties.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
		serviceRegistrations.add(
				context.registerService(Filter.class, new TestFilter('d'), properties));

		// test_basicExtensionFilter

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "*.TestFilter5");
		serviceRegistrations.add(
				context.registerService(Servlet.class, new TestServlet("a"), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "*.TestFilter5");
		serviceRegistrations.add(
				context.registerService(Filter.class, new TestFilter('b'), properties));

		// test_twoExtensionFiltersOrderedByRegistration

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "*.TestFilter6");
		serviceRegistrations.add(
				context.registerService(Servlet.class, new TestServlet("a"), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "*.TestFilter6");
		serviceRegistrations.add(
				context.registerService(Filter.class, new TestFilter('b'), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "*.TestFilter6");
		serviceRegistrations.add(
				context.registerService(Filter.class, new TestFilter('c'), properties));

		// test_threeExtensionFiltersOrderedByRegistration

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "*.TestFilter7");
		serviceRegistrations.add(
				context.registerService(Servlet.class, new TestServlet("a"), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "*.TestFilter7");
		serviceRegistrations.add(
				context.registerService(Filter.class, new TestFilter('b'), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "*.TestFilter7");
		serviceRegistrations.add(
				context.registerService(Filter.class, new TestFilter('c'), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "*.TestFilter7");
		serviceRegistrations.add(
				context.registerService(Filter.class, new TestFilter('d'), properties));

		// test_threeExtensionFiltersOrderedByRanking

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "*.TestFilter8");
		serviceRegistrations.add(
				context.registerService(Servlet.class, new TestServlet("a"), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "*.TestFilter8");
		serviceRegistrations.add(
				context.registerService(Filter.class, new TestFilter('b'), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "*.TestFilter8");
		serviceRegistrations.add(
				context.registerService(Filter.class, new TestFilter('c'), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "*.TestFilter8");
		properties.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
		serviceRegistrations.add(
				context.registerService(Filter.class, new TestFilter('d'), properties));

		// test_resourceDTO &
		// test_resourceMatchByRegexMatch

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/TestResource1/*");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/org/osgi/test/cases/servlet/whiteboard/tb1/resources");
		serviceRegistrations.add(
				context.registerService(Object.class, new Object(), properties));

		// test_resourceMatchByExactMatch

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/TestResource2/a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/org/osgi/test/cases/servlet/whiteboard/tb1/resources/resource1.txt");
		serviceRegistrations.add(
				context.registerService(Object.class, new Object(), properties));
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		for (ServiceRegistration<?> serviceRegistration : serviceRegistrations) {
			serviceRegistration.unregister();
		}

		serviceRegistrations.clear();
	}

	private List<ServiceRegistration<?>>	serviceRegistrations	= new CopyOnWriteArrayList<ServiceRegistration<?>>();

}
