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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.servlet.whiteboard.Preprocessor;
import org.osgi.service.servlet.whiteboard.runtime.dto.PreprocessorDTO;
import org.osgi.test.cases.servlet.whiteboard.junit.mock.MockPreprocessor;
import org.osgi.test.cases.servlet.whiteboard.junit.mock.MockServlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * Preprocessors are introduced in 1.1
 */
public class PreprocessorTestCase extends BaseHttpWhiteboardTestCase {

	public void testPreprocessorInitParameters() throws Exception {
		Dictionary<String,Object> properties = new Hashtable<>();
		properties
				.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_PREPROCESSOR_INIT_PARAM_PREFIX
						+ "param1", "value1");
		properties
				.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_PREPROCESSOR_INIT_PARAM_PREFIX
						+ "param2", "value2");
		properties
				.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_PREPROCESSOR_INIT_PARAM_PREFIX
						+ "param3", 345l);

		long before = this.getHttpRuntimeChangeCount();
		final ServiceRegistration<Preprocessor> reg = getContext()
				.registerService(Preprocessor.class, new MockPreprocessor(),
						properties);
		this.serviceRegistrations.add(reg);
		this.waitForRegistration(before);

		final PreprocessorDTO[] dtos = this.getHttpServiceRuntime()
				.getRuntimeDTO().preprocessorDTOs;
		assertEquals(1, dtos.length);

		assertTrue(dtos[0].initParams.containsKey("param1"));
		assertTrue(dtos[0].initParams.containsKey("param2"));
		assertFalse(dtos[0].initParams.containsKey("param3"));
		assertEquals(getServiceId(reg), dtos[0].serviceId);
	}

	public void testPreprocessorRanging() throws Exception {
		// register preprocessor with ranking -5
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(Constants.SERVICE_RANKING, -5);

		long before = this.getHttpRuntimeChangeCount();
		this.serviceRegistrations
				.add(getContext().registerService(Preprocessor.class.getName(),
						new MockPreprocessor().around("d"), properties));
		before = this.waitForRegistration(before);

		// register preprocessor with ranking 8
		properties = new Hashtable<>();
		properties.put(Constants.SERVICE_RANKING, 8);

		this.serviceRegistrations
				.add(getContext().registerService(Preprocessor.class.getName(),
						new MockPreprocessor().around("a"), properties));
		before = this.waitForRegistration(before);

		// register preprocessor with invalid ranking
		properties = new Hashtable<>();
		properties.put(Constants.SERVICE_RANKING, 3L); // this is invalid ->
														// ranking = 0

		this.serviceRegistrations
				.add(getContext().registerService(Preprocessor.class.getName(),
						new MockPreprocessor().around("b"), properties));
		before = this.waitForRegistration(before);

		// register preprocessor with no ranking
		properties = new Hashtable<>();

		this.serviceRegistrations
				.add(getContext().registerService(Preprocessor.class.getName(),
						new MockPreprocessor().around("c"), properties));
		before = this.waitForRegistration(before);

		// check that we have four preprocessors
		final PreprocessorDTO[] dtos = this.getHttpServiceRuntime()
				.getRuntimeDTO().preprocessorDTOs;
		assertEquals(4, dtos.length);

		// register endpoint
		final String path = "/available";
		properties = new Hashtable<>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN,
				path);
		this.serviceRegistrations.add(getContext().registerService(
				Servlet.class, new MockServlet().content("hello"), properties));

		assertEquals("abcdhellodcba", request(path));
	}

	/**
	 * Test a request with a servlet registered at that url and check if the
	 * preprocessor is invoked. Do the same with a non existing url.
	 */
	public void testPreprocessorInvocation() throws Exception {
		// register preprocessor
		final List<String> filterActions = new ArrayList<>();
		long before = this.getHttpRuntimeChangeCount();
		this.serviceRegistrations.add(getContext().registerService(
				Preprocessor.class.getName(), new MockPreprocessor() {

					@Override
					public void doFilter(ServletRequest request,
							ServletResponse response, FilterChain chain)
							throws IOException, ServletException {
						filterActions.add("a");
						super.doFilter(request, new HttpServletResponseWrapper(
								(HttpServletResponse) response) {

							private boolean hasStatus = false;

							private void addStatus(final int sc) {
								if (!hasStatus) {
									hasStatus = true;
									filterActions.add(String.valueOf(sc));
								}
							}

							@Override
							public void setStatus(int sc) {
								addStatus(sc);
								super.setStatus(sc);
							}

							@Override
							public void sendError(int sc, String msg)
									throws IOException {
								addStatus(sc);
								super.sendError(sc, msg);
							}

							@Override
							public void sendError(int sc) throws IOException {
								addStatus(sc);
								super.sendError(sc);
							}

						}, chain);
						filterActions.add("b");
					}

				}, null));
		before = this.waitForRegistration(before);

		// register endpoint
		final String path = "/available";
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN,
				path);
		this.serviceRegistrations.add(getContext().registerService(
				Servlet.class, new MockServlet().content("hello"), properties));

		assertEquals("hello", request(path));
		assertEquals(2, filterActions.size());
		assertEquals("a", filterActions.get(0));
		assertEquals("b", filterActions.get(1));

		// request a non existing pattern - this will somehow set the status
		// code to 404
		filterActions.clear();
		request("/foo");
		assertEquals(3, filterActions.size());
		assertEquals("a", filterActions.get(0));
		assertEquals("404", filterActions.get(1));
		assertEquals("b", filterActions.get(2));
	}
}
