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

import static org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_MULTIPART_ENABLED;
import static org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_MULTIPART_MAXFILESIZE;
import static org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.servlet.runtime.dto.DTOConstants;
import org.osgi.service.servlet.runtime.dto.ErrorPageDTO;
import org.osgi.service.servlet.runtime.dto.FailedErrorPageDTO;
import org.osgi.service.servlet.runtime.dto.FailedServletDTO;
import org.osgi.service.servlet.runtime.dto.RequestInfoDTO;
import org.osgi.service.servlet.runtime.dto.ServletContextDTO;
import org.osgi.service.servlet.runtime.dto.ServletDTO;
import org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

public class ServletTestCase extends BaseHttpWhiteboardTestCase {

	public void test_140_4_4to5() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		Servlet servlet = new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				invoked.set(true);

				response.getWriter().write("a");
			}

		};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, new String[] {"/a", "/b"});
		serviceRegistrations.add(context.registerService(Servlet.class, servlet, properties));

		assertEquals("a", request("a"));
		assertTrue(invoked.get());
		invoked.set(false);
		assertEquals("a", request("b"));
		assertTrue(invoked.get());
	}

	public void test_140_4_9() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		Servlet servlet = new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				invoked.set(true);

				response.getWriter().write("a");
			}

		};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/*");
		serviceRegistrations.add(context.registerService(Servlet.class, servlet, properties));

		assertEquals("a", request("a"));
		assertTrue(invoked.get());
		invoked.set(false);
		assertEquals("a", request("b.html"));
		assertTrue(invoked.get());
		invoked.set(false);
		assertEquals("a", request("some/path/b.html"));
		assertTrue(invoked.get());
		assertEquals("a", request(""));
	}

	public void test_140_4_10() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		Servlet servlet = new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				invoked.set(true);

				response.getWriter().write("a");
			}

		};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "*.html");
		serviceRegistrations.add(context.registerService(Servlet.class, servlet, properties));

		assertEquals("a", request("a.html"));
		assertTrue(invoked.get());
		invoked.set(false);
		assertEquals("a", request("b.html"));
		assertTrue(invoked.get());
		invoked.set(false);
		assertEquals("a", request("some/path/b.html"));
		assertTrue(invoked.get());
		assertEquals("404", request("a.xhtml", null).get("responseCode").get(0));
		assertEquals("404", request("some/path/a.xhtml", null).get("responseCode").get(0));
	}

	public void test_140_4_11to13() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		Servlet servlet = new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				invoked.set(true);

				PrintWriter writer = response.getWriter();
				writer.write((request.getContextPath() == null) ? "" : request.getContextPath());
				writer.write(":");
				writer.write((request.getServletPath() == null) ? "" : request.getServletPath());
				writer.write(":");
				writer.write((request.getPathInfo() == null) ? "" : request.getPathInfo());
			}

		};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "");
		serviceRegistrations.add(context.registerService(Servlet.class, servlet, properties));

		assertEquals("::/", request(""));
		assertTrue(invoked.get());
		assertEquals("404", request("a.xhtml", null).get("responseCode").get(0));
		assertEquals("404", request("some/path/a.xhtml", null).get("responseCode").get(0));
	}

	public void test_140_4_14to15() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		Servlet servlet = new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				invoked.set(true);

				PrintWriter writer = response.getWriter();
				writer.write((request.getContextPath() == null) ? "" : request.getContextPath());
				writer.write(":");
				writer.write((request.getServletPath() == null) ? "" : request.getServletPath());
				writer.write(":");
				writer.write((request.getPathInfo() == null) ? "" : request.getPathInfo());
			}

		};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/");
		serviceRegistrations.add(context.registerService(Servlet.class, servlet, properties));

		assertEquals(":/a.html:", request("a.html"));
		assertTrue(invoked.get());
		invoked.set(false);
		assertEquals(":/a.xhtml:", request("a.xhtml"));
		assertTrue(invoked.get());
		invoked.set(false);
		assertEquals(":/some/path/a.xhtml:", request("some/path/a.xhtml"));
		assertTrue(invoked.get());
	}

	public void test_140_4_16() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		class TestServlet extends HttpServlet {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private final String content;

			public TestServlet(String content) {
				this.content = content;
			}
			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				invoked.set(true);
				response.getWriter().write(content);
			}
		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new TestServlet("a"), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/fee/fi/foo/fum");
		serviceRegistrations.add(context.registerService(Servlet.class, new TestServlet("b"), properties));

		assertEquals("a", request("a"));
		assertTrue(invoked.get());
		invoked.set(false);
		assertEquals("b", request("fee/fi/foo/fum"));
		assertTrue(invoked.get());
	}

	public void test_140_4_17to22() throws Exception {
		BundleContext context = getContext();

		class AServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public AServlet(String content) {
				this.content = content;
			}

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				response.getWriter().write(content);
			}

			private final String	content;

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> srA = context.registerService(Servlet.class, new AServlet("a"), properties);
		serviceRegistrations.add(srA);

		assertEquals("a", request("a"));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> srB = context.registerService(Servlet.class, new AServlet("b"), properties);
		serviceRegistrations.add(srB);

		assertEquals("a", request("a"));

		FailedServletDTO failedServletDTO = getFailedServletDTOByName("b");

		assertNotNull(failedServletDTO);
		assertEquals(
				DTOConstants.FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE,
				failedServletDTO.failureReason);
		assertEquals(
				getServiceId(srB),
				failedServletDTO.serviceId);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "c");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		properties.put(Constants.SERVICE_RANKING, 1000);
		serviceRegistrations.add(context.registerService(Servlet.class, new AServlet("c"), properties));

		assertEquals("c", request("a"));

		failedServletDTO = getFailedServletDTOByName("a");

		assertNotNull(failedServletDTO);
		assertEquals(
				DTOConstants.FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE,
				failedServletDTO.failureReason);
		assertEquals(
				getServiceId(srA),
				failedServletDTO.serviceId);
	}

	public void test_140_4_23to25() throws Exception {
		BundleContext context = getContext();

		class AServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> srA = context.registerService(Servlet.class, new AServlet(), properties);
		serviceRegistrations.add(srA);

		ServletDTO servletDTO = getServletDTOByName(
				DEFAULT,
				AServlet.class.getName());

		assertNotNull(servletDTO);
		assertEquals(getServiceId(srA), servletDTO.serviceId);
	}

	public void test_140_4_26to31() throws Exception {
		BundleContext context = getContext();

		class AServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public AServlet(String content) {
				this.content = content;
			}

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				response.getWriter().write(content);
			}

			private final String	content;

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		properties.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
		ServiceRegistration<Servlet> srA = context.registerService(Servlet.class, new AServlet("a"), properties);
		serviceRegistrations.add(srA);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertEquals("a", requestInfoDTO.servletDTO.name);
		assertEquals(
				getServiceId(srA),
				requestInfoDTO.servletDTO.serviceId);
		assertEquals("a", request("a"));
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED_validate() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, "blah");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}, properties));

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertFalse(requestInfoDTO.servletDTO.asyncSupported);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, "true");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}, properties));

		requestInfoDTO = calculateRequestInfoDTO("/b");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertTrue(requestInfoDTO.servletDTO.asyncSupported);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, "false");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/c");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}, properties));

		requestInfoDTO = calculateRequestInfoDTO("/c");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertFalse(requestInfoDTO.servletDTO.asyncSupported);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, 234l);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/d");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}, properties));

		requestInfoDTO = calculateRequestInfoDTO("/d");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertFalse(requestInfoDTO.servletDTO.asyncSupported);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/e");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}, properties));

		requestInfoDTO = calculateRequestInfoDTO("/e");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertFalse(requestInfoDTO.servletDTO.asyncSupported);
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED() throws Exception {
		BundleContext context = getContext();
		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			final ExecutorService	executor	= Executors.newCachedThreadPool();

			@Override
			protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
				doGetAsync(req.startAsync());
			}

			private void doGetAsync(final AsyncContext asyncContext) {
				executor.submit(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						try {
							invoked.set(true);

							PrintWriter writer = asyncContext.getResponse().getWriter();

							writer.print("a");
						} finally {
							asyncContext.complete();
						}

						return null;
					}
				});
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, "true");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> srA = context.registerService(Servlet.class, new AServlet(), properties);
		serviceRegistrations.add(srA);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertTrue(requestInfoDTO.servletDTO.asyncSupported);
		assertTrue(getServiceId(srA) == requestInfoDTO.servletDTO.serviceId);
		assertEquals("a", requestInfoDTO.servletDTO.name);
		assertEquals("a", request("a"));
		assertTrue(invoked.get());
		invoked.set(false);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, "false");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		ServiceRegistration<Servlet> srB = context.registerService(Servlet.class, new AServlet(), properties);
		serviceRegistrations.add(srB);

		assertEquals("500", request("b", null).get("responseCode").get(0));
		assertFalse(invoked.get());
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_ERROR_PAGE_validate() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, "400");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> srA = context.registerService(Servlet.class, new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}, properties);
		serviceRegistrations.add(srA);

		ErrorPageDTO errorPageDTO = getErrorPageDTOByName(DEFAULT, "a");
		assertNotNull(errorPageDTO);
		assertEquals(1, errorPageDTO.errorCodes.length);
		assertEquals(400, errorPageDTO.errorCodes[0]);
		assertEquals(0, errorPageDTO.exceptions.length);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, new String[] {"400", ServletException.class.getName()});
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> srB = context.registerService(Servlet.class, new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}, properties);
		serviceRegistrations.add(srB);

		errorPageDTO = getErrorPageDTOByName(DEFAULT, "a");
		assertNotNull(errorPageDTO);
		assertEquals(1, errorPageDTO.errorCodes.length);
		assertEquals(400, errorPageDTO.errorCodes[0]);
		assertEquals(0, errorPageDTO.exceptions.length);

		FailedServletDTO failedServletDTO = getFailedServletDTOByName("a");
		assertNotNull(failedServletDTO);
		assertEquals(DTOConstants.FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE, failedServletDTO.failureReason);
		assertEquals(getServiceId(srB), failedServletDTO.serviceId);

		properties.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
		srB.setProperties(properties);

		errorPageDTO = getErrorPageDTOByName(DEFAULT, "a");
		assertNotNull(errorPageDTO);
		assertEquals(400, errorPageDTO.errorCodes[0]);
		assertEquals(ServletException.class.getName(), errorPageDTO.exceptions[0]);

		failedServletDTO = getFailedServletDTOByName("a");
		assertNotNull(failedServletDTO);
		assertEquals(DTOConstants.FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE, failedServletDTO.failureReason);
		assertEquals(getServiceId(srA), failedServletDTO.serviceId);
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_ERROR_PAGE() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "a");
			}

		}

		class BServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				invoked.set(true);
				String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
				response.getWriter().write((message == null) ? "" : message);
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new AServlet(), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, new String[] {HttpServletResponse.SC_BAD_GATEWAY + "", HttpServletResponse.SC_FORBIDDEN + ""});
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/error");
		serviceRegistrations.add(context.registerService(Servlet.class, new BServlet(), properties));

		ServletDTO servletDTO = getServletDTOByName(DEFAULT, "b");
		assertNotNull(servletDTO);

		ErrorPageDTO errorPageDTO = getErrorPageDTOByName(DEFAULT, "b");
		assertNotNull(errorPageDTO);
		assertTrue(Arrays.binarySearch(errorPageDTO.errorCodes, HttpServletResponse.SC_BAD_GATEWAY) >= 0);

		Map<String, List<String>> response = request("a", null);
		assertEquals("a", response.get("responseBody").get(0));
		assertTrue(invoked.get());
		assertEquals(HttpServletResponse.SC_BAD_GATEWAY + "", response.get("responseCode").get(0));
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_ERROR_PAGE_4xx() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "a");
			}

		}

		class BServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				invoked.set(true);
				String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
				response.getWriter().write((message == null) ? "" : message);
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new AServlet(), properties));

		// Register the 4xx (b)
		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, "4xx");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		serviceRegistrations.add(context.registerService(Servlet.class, new BServlet(), properties));

		ServletDTO servletDTO = getServletDTOByName(DEFAULT, "b");
		assertNotNull(servletDTO);
		ErrorPageDTO errorPageDTO = getErrorPageDTOByName(DEFAULT, "b");
		assertNotNull(errorPageDTO);
		assertTrue(Arrays.binarySearch(errorPageDTO.errorCodes, HttpServletResponse.SC_FORBIDDEN) >= 0);

		Map<String, List<String>> response = request("a", null);
		assertEquals("a", response.get("responseBody").get(0));
		assertTrue(invoked.get());
		assertEquals(HttpServletResponse.SC_FORBIDDEN + "", response.get("responseCode").get(0));

		// register a 4xx which will be shadowed (c)
		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "c");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, "4xx");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/c");
		serviceRegistrations.add(context.registerService(Servlet.class, new BServlet(), properties));

		FailedErrorPageDTO failedErrorPageDTO = getFailedErrorPageDTOByName("c");
		assertNotNull(failedErrorPageDTO);
		assertEquals(DTOConstants.FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE, failedErrorPageDTO.failureReason);

		// register a specific 404 which shouldn't shadow 4xx (b)
		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "d");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, "404");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/d");
		properties.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
		serviceRegistrations.add(context.registerService(Servlet.class, new BServlet(), properties));

		failedErrorPageDTO = getFailedErrorPageDTOByName("b");
		assertNull(failedErrorPageDTO);
		failedErrorPageDTO = getFailedErrorPageDTOByName("d");
		assertNull(failedErrorPageDTO);
		errorPageDTO = getErrorPageDTOByName(DEFAULT, "d");
		assertNotNull(errorPageDTO);
		assertEquals(404, errorPageDTO.errorCodes[0]);
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_ERROR_PAGE_5xx() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "a");
			}

		}

		class BServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				invoked.set(true);
				String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
				response.getWriter().write((message == null) ? "" : message);
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new AServlet(), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, "5xx");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/error");
		serviceRegistrations.add(context.registerService(Servlet.class, new BServlet(), properties));

		ServletDTO servletDTO = getServletDTOByName(DEFAULT, "b");
		assertNotNull(servletDTO);
		ErrorPageDTO errorPageDTO = getErrorPageDTOByName(DEFAULT, "b");
		assertNotNull(errorPageDTO);
		assertTrue(Arrays.binarySearch(errorPageDTO.errorCodes, HttpServletResponse.SC_BAD_GATEWAY) >= 0);

		Map<String, List<String>> response = request("a", null);
		assertEquals("a", response.get("responseBody").get(0));
		assertTrue(invoked.get());
		assertEquals(HttpServletResponse.SC_BAD_GATEWAY + "", response.get("responseCode").get(0));
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_ERROR_PAGE_exception() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AException extends ServletException {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		}

		class AServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				throw new AException();
			}

		}

		class BServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				invoked.set(true);
				Object exceptionType = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE);
				String exceptionName = "";
				if (exceptionType instanceof Class) {
					exceptionName = ((Class<?>) exceptionType).getName();
				} else if (exceptionType instanceof String) {
					exceptionName = (String) exceptionType;
				}
				response.getWriter().write(exceptionName);
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new AServlet(), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, ServletException.class.getName());
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/error");
		serviceRegistrations.add(context.registerService(Servlet.class, new BServlet(), properties));

		ServletDTO servletDTO = getServletDTOByName(DEFAULT, "b");
		assertNotNull(servletDTO);
		ErrorPageDTO errorPageDTO = getErrorPageDTOByName(DEFAULT, "b");
		assertNotNull(errorPageDTO);
		assertTrue(Arrays.binarySearch(errorPageDTO.exceptions, ServletException.class.getName()) >= 0);

		Map<String, List<String>> response = request("a", null);
		assertEquals(AException.class.getName(), response.get("responseBody").get(0));
		assertTrue(invoked.get());
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR + "", response.get("responseCode").get(0));
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_NAME() throws Exception {
		BundleContext context = getContext();

		class AServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.getWriter().write(getServletName());
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> srA = context.registerService(Servlet.class, new AServlet(), properties);
		serviceRegistrations.add(srA);

		ServletDTO servletDTO = getServletDTOByName(
				DEFAULT,
				AServlet.class.getName());

		assertNotNull(servletDTO);
		assertEquals(getServiceId(srA), servletDTO.serviceId);
		assertEquals(AServlet.class.getName(), request("a"));
	}

	public void test_table_140_4_initParams() throws Exception {
		BundleContext context = getContext();

		class AServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				String initParameter = getServletConfig().getInitParameter(request.getParameter("p"));

				response.getWriter().write((initParameter == null) ? "" : initParameter);
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + "param1", "value1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + "param2", "value2");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + "param3", 345l);
		ServiceRegistration<Servlet> srA = context.registerService(Servlet.class, new AServlet(), properties);
		serviceRegistrations.add(srA);

		ServletDTO servletDTO = getServletDTOByName(
				DEFAULT,
				AServlet.class.getName());

		assertNotNull(servletDTO);
		assertTrue(servletDTO.initParams.containsKey("param1"));
		assertTrue(servletDTO.initParams.containsKey("param2"));
		assertFalse(servletDTO.initParams.containsKey("param3"));
		assertEquals(getServiceId(srA), servletDTO.serviceId);
		assertEquals("value1", request("a?p=param1"));
		assertEquals("value2", request("a?p=param2"));
		assertEquals("", request("a?p=param3"));
	}

	public void test_140_4_38to42() throws Exception {
		BundleContext context = getContext();
		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void destroy() {
				invoked.set(true);

				super.destroy();
			}

			@Override
			public void init(ServletConfig config) throws ServletException {
				invoked.set(true);

				super.init(config);
			}

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.getWriter().write("a");
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> srA = context.registerService(Servlet.class, new AServlet(), properties);

		assertEquals("a", request("a"));
		assertTrue(invoked.get());
		invoked.set(false);
		srA.unregister();
		assertTrue(invoked.get());
	}

	public void test_140_4_42to44() throws Exception {
		BundleContext context = getContext();
		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void init(ServletConfig config) throws ServletException {
				invoked.set(true);

				throw new ServletException();
			}

		}

		class BServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.getWriter().write("failed");
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new AServlet(), properties));

		FailedServletDTO failedServletDTO = getFailedServletDTOByName("a");
		assertNotNull(failedServletDTO);
		assertEquals(DTOConstants.FAILURE_REASON_EXCEPTION_ON_INIT, failedServletDTO.failureReason);
		assertTrue(invoked.get());

		Map<String, List<String>> response = request("a", null);
		// init failed, no servlet
		assertEquals("404", response.get("responseCode").get(0));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new BServlet(), properties));

		response = request("a", null);
		// BServlet handles the request
		assertEquals("200", response.get("responseCode").get(0));
	}

	public void test_140_4_45to46() throws Exception {
		BundleContext context = getContext();
		final AtomicBoolean invokedInit = new AtomicBoolean(false);
		final AtomicBoolean invokedDestroy = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void destroy() {
				invokedDestroy.set(true);

				super.destroy();
			}

			@Override
			public void init(ServletConfig config) throws ServletException {
				invokedInit.set(true);

				super.init(config);
			}

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.getWriter().write("a");
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> sr = context.registerService(Servlet.class, new AServlet(), properties);
		serviceRegistrations.add(sr);

		assertEquals("a", request("a"));
		assertTrue(invokedInit.get());
		assertFalse(invokedDestroy.get());
		invokedInit.set(false);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		sr.setProperties(properties);
		assertTrue(invokedDestroy.get());
		assertEquals("a", request("b"));
		assertTrue(invokedInit.get());
	}

	public void test_140_4_46to47() throws Exception {
		BundleContext context = getContext();
		final AtomicBoolean invokedGetService = new AtomicBoolean(false);
		final AtomicBoolean invokedInit = new AtomicBoolean(false);
		final AtomicBoolean invokedDestroy = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void destroy() {
				invokedDestroy.set(true);

				super.destroy();
			}

			@Override
			public void init(ServletConfig config) throws ServletException {
				invokedInit.set(true);

				super.init(config);
			}

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.getWriter().write("a");
			}

		}

		PrototypeServiceFactory<Servlet> factory = new PrototypeServiceFactory<Servlet>() {

			@Override
			public void ungetService(Bundle bundle, ServiceRegistration<Servlet> registration, Servlet service) {
			}

			@Override
			public Servlet getService(Bundle bundle, ServiceRegistration<Servlet> registration) {
				invokedGetService.set(true);
				return new AServlet();
			}
		};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> sr = context.registerService(Servlet.class, factory, properties);
		serviceRegistrations.add(sr);

		assertEquals("a", request("a"));
		assertTrue(invokedGetService.get());
		assertTrue(invokedInit.get());
		assertFalse(invokedDestroy.get());
		invokedInit.set(false);
		invokedGetService.set(false);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		sr.setProperties(properties);
		assertTrue(invokedDestroy.get());
		assertTrue(invokedGetService.get());
		assertEquals("a", request("b"));
		assertTrue(invokedInit.get());
	}

	public void test_140_4_48to51() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET, "(some.property=some.value)");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}, properties));

		ServletDTO servletDTO = getServletDTOByName(DEFAULT, "a");
		assertNull(servletDTO);

		FailedServletDTO failedServletDTO = getFailedServletDTOByName("a");
		assertNull(failedServletDTO);
	}

	public void test_140_4_1_22to23() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "a");
			}

		}

		class BServlet extends HttpServlet {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				invoked.set(true);
				throw new ServletException();
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new AServlet(), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, HttpServletResponse.SC_BAD_GATEWAY + "");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		serviceRegistrations.add(context.registerService(Servlet.class, new BServlet(), properties));

		Map<String, List<String>> response = request("a", null);
		assertTrue(!"a".equals(response.get("responseBody").get(0)));
		assertTrue(invoked.get());
		assertEquals(HttpServletResponse.SC_BAD_GATEWAY + "", response.get("responseCode").get(0));
	}

	/**
	 * Tests for 1.1 Update
	 */

	/**
	 * Servlet registrations
	 */
	public void testServletRegistration() throws Exception {
		// new in 1.1: servlet needs only one of those: pattern, name, error
		// page
		// previously pattern was always required

		BundleContext context = getContext();

		long before = this.getHttpRuntimeChangeCount();

		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME,
				"myname");
		serviceRegistrations.add(context.registerService(Servlet.class,
				new HttpServlet() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;}, properties));
		before = this.waitForRegistration(before);

		properties = new Hashtable<>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN,
				"/mypattern");
		serviceRegistrations.add(context.registerService(Servlet.class,
				new HttpServlet() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;}, properties));
		before = this.waitForRegistration(before);

		properties = new Hashtable<>();
		properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE,
				"404");
		serviceRegistrations.add(context.registerService(Servlet.class,
				new HttpServlet() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;}, properties));
		before = this.waitForRegistration(before);

		// two servlets and one error page should be registered now
		final ServletContextDTO dto = this.getServletContextDTOByName(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME);
		assertEquals(1, dto.errorPageDTOs.length);
		assertEquals(404L, dto.errorPageDTOs[0].errorCodes[0]);
		assertEquals(2, dto.servletDTOs.length);
		int indexName = dto.servletDTOs[0].patterns.length == 0 ? 0 : 1;
		int indexPattern = indexName == 0 ? 1 : 0;
		assertEquals("myname", dto.servletDTOs[indexName].name);
		assertEquals(0, dto.servletDTOs[indexName].patterns.length);

		assertEquals(1, dto.servletDTOs[indexPattern].patterns.length);
		assertEquals("/mypattern", dto.servletDTOs[indexPattern].patterns[0]);
	}

	private void setupUploadServlet(final CountDownLatch receivedLatch,
			final Map<String,Long> contents)
			throws Exception {
		final Dictionary<String,Object> servletProps = new Hashtable<String,Object>();
		servletProps.put(HTTP_WHITEBOARD_SERVLET_PATTERN, "/post");
		servletProps.put(HTTP_WHITEBOARD_SERVLET_MULTIPART_ENABLED,
				Boolean.TRUE);
		servletProps.put(HTTP_WHITEBOARD_SERVLET_MULTIPART_MAXFILESIZE, 1024L);

		final Servlet uploadServlet = new HttpServlet() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void doPost(HttpServletRequest req,
					HttpServletResponse resp)
					throws IOException, ServletException {
				try {
					final Collection<Part> parts = req.getParts();
					for (final Part p : parts) {
						contents.put(p.getName(), p.getSize());
					}
					resp.setStatus(201);
				} finally {
					receivedLatch.countDown();
				}

			}
		};

		long before = this.getHttpRuntimeChangeCount();
		serviceRegistrations.add(getContext().registerService(
				Servlet.class.getName(), uploadServlet, servletProps));
		this.waitForRegistration(before);
	}

	private void postContent(final char c, final long length,
			final int expectedRT) throws IOException {
		final URL url = getServerURL("/post");
		final CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			final HttpPost httppost = new HttpPost(url.toExternalForm());

			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < length; i++) {
				sb.append(c);
			}
			final StringBody text = new StringBody(sb.toString(),
					ContentType.TEXT_PLAIN);

			final HttpEntity reqEntity = MultipartEntityBuilder.create()
					.addPart("text", text)
					.build();


			httppost.setEntity(reqEntity);

			final CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				final HttpEntity resEntity = response.getEntity();
				EntityUtils.consume(resEntity);
				assertEquals(expectedRT,
						response.getStatusLine().getStatusCode());
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
	}

	public void testUpload() throws Exception {
		final CountDownLatch receivedLatch = new CountDownLatch(1);
		final Map<String,Long> contents = new HashMap<>();
		setupUploadServlet(receivedLatch, contents);

		postContent('a', 500, 201);
		assertTrue(receivedLatch.await(5, TimeUnit.SECONDS));
		assertEquals(1, contents.size());
		assertEquals(500L, (long) contents.get("text"));
	}

	public void testMaxFileSize() throws Exception {
		final CountDownLatch receivedLatch = new CountDownLatch(1);
		final Map<String,Long> contents = new HashMap<>();
		setupUploadServlet(receivedLatch, contents);

		postContent('b', 2048, 500);
		assertTrue(receivedLatch.await(5, TimeUnit.SECONDS));
		assertTrue(contents.isEmpty());
	}
}
