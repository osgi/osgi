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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.osgi.framework.BundleContext;
import org.osgi.service.servlet.context.ServletContextHelper;
import org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants;
import org.osgi.test.common.dictionary.Dictionaries;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Tests for
 * {@link ServletContextHelper#handleSecurity(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse)}
 * and
 * {@link ServletContextHelper#finishSecurity(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse)}
 */
public class AuthenticationTestCase extends BaseHttpWhiteboardTestCase {

	private static final String	AUTH_PAR	= "auth";

	private static final String	REC_PAR		= "rec";

	private void setup(final Queue<String> callStack) throws Exception {
		final BundleContext context = getContext();

		// setup context 1
		serviceRegistrations.add(context.registerService(
				ServletContextHelper.class, new ServletContextHelper() {

					@Override
					public boolean handleSecurity(
							final HttpServletRequest request,
							final HttpServletResponse response)
							throws IOException {
						if (request.getParameter(AUTH_PAR) != null) {
							callStack.add("handle1");
							return true;
						}
						return false;
					}

					@Override
					public void finishSecurity(final HttpServletRequest request,
							final HttpServletResponse response) {
						callStack.add("finish1");
					}

				},
				Dictionaries.dictionaryOf(
						HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME,
						"context1",
						HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH,
						"/context1")));

		// setup context 2
		serviceRegistrations.add(context.registerService(
				ServletContextHelper.class, new ServletContextHelper() {

					@Override
					public boolean handleSecurity(
							final HttpServletRequest request,
							final HttpServletResponse response)
							throws IOException {
						callStack.add("handle2");
						return true;
					}

					@Override
					public void finishSecurity(final HttpServletRequest request,
							final HttpServletResponse response) {
						callStack.add("finish2");
					}

				},
				Dictionaries.dictionaryOf(
						HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME,
						"context2",
						HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH,
						"/context2")));

		// servlet for both contexts
		class AServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request,
					HttpServletResponse response)
					throws ServletException, IOException {

				callStack.add("servlet" + request.getContextPath());

				if (request.getContextPath().equals("/context1")
						&& request.getAttribute(REC_PAR) == null) {
					if (request.getParameter("forward") != null) {
						request.setAttribute(REC_PAR, "true");
						request.getRequestDispatcher("/servlet")
									.forward(request, response);
						return;
					} else if (request.getParameter("include") != null) {
						request.setAttribute(REC_PAR, "true");
						request.getRequestDispatcher("/servlet")
									.include(request, response);
					} else if (request.getParameter("throw") != null) {
						callStack.add("throw");
						throw new ServletException("throw");
					}
				}
				response.setStatus(200);
			}

		};

		serviceRegistrations.add(
				context.registerService(Servlet.class, new AServlet(),
						Dictionaries.dictionaryOf(
								HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN,
								new String[] {
										"/servlet"
								},
								HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
								"(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME
										+ "=context1)")));

		serviceRegistrations.add(
				context.registerService(Servlet.class, new AServlet(),
						Dictionaries.dictionaryOf(
								HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN,
								new String[] {
										"/servlet"
								},
								HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
								"(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME
										+ "=context2)")));
	}

	public void test_handleFinishSecurity() throws Exception {
		final Queue<String> callStack = new ConcurrentLinkedQueue<>();
		setup(callStack);

		// request without auth -> no servlet invoked
		request("/context1/servlet");
		assertThat(callStack).isEmpty();

		// request with auth -> servlet invoked
		request("/context1/servlet?auth=true");
		assertThat(callStack).containsExactly("handle1", "servlet/context1",
				"finish1");
		callStack.clear();

		// request to context2, no auth required
		request("/context2/servlet");
		assertThat(callStack).containsExactly("handle2", "servlet/context2",
				"finish2");
	}

	public void test_includeSecurity() throws Exception {
		final Queue<String> callStack = new ConcurrentLinkedQueue<>();
		setup(callStack);

		// request without auth -> no servlet invoked
		request("/context1/servlet?forward=true");
		assertThat(callStack).isEmpty();

		// request with auth and include -> servlet invoked
		request("/context1/servlet?include=true&auth=true");
		assertThat(callStack).containsExactly("handle1", "servlet/context1",
				"handle1", "servlet/context1", "finish1", "finish1");
		callStack.clear();
	}

	public void test_forwardSecurity() throws Exception {
		final Queue<String> callStack = new ConcurrentLinkedQueue<>();
		setup(callStack);

		// request without auth -> no servlet invoked
		request("/context1/servlet?forward=true");
		assertThat(callStack).isEmpty();

		// request with auth and forward -> servlet invoked
		request("/context1/servlet?forward=true&auth=true");
		assertThat(callStack).containsExactly("handle1", "servlet/context1",
				"handle1", "servlet/context1", "finish1", "finish1");
		callStack.clear();
	}

	public void test_exceptionFromServlet() throws Exception {
		final Queue<String> callStack = new ConcurrentLinkedQueue<>();
		setup(callStack);

		request("/context1/servlet?throw=true&auth=true");
		assertThat(callStack).containsExactly("handle1", "servlet/context1",
				"throw", "finish1");
		callStack.clear();
	}
}
