/*
 * Copyright (c) OSGi Alliance (2014, 2015). All Rights Reserved.
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
package org.osgi.test.cases.http.whiteboard.junit;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.AsyncContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import junit.framework.Assert;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.runtime.HttpServiceRuntime;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.runtime.dto.ErrorPageDTO;
import org.osgi.service.http.runtime.dto.FailedServletDTO;
import org.osgi.service.http.runtime.dto.RequestInfoDTO;
import org.osgi.service.http.runtime.dto.ResourceDTO;
import org.osgi.service.http.runtime.dto.RuntimeDTO;
import org.osgi.service.http.runtime.dto.ServletContextDTO;
import org.osgi.service.http.runtime.dto.ServletDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

public class HttpWhiteboardTestCase extends BaseHttpWhiteboardTestCase {

	@Override
	protected String[] getBundlePaths() {
		return new String[] {"/tb1.jar", "/tb2.jar"};
	}

	public void test_140_4_4to5() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		Servlet servlet = new HttpServlet() {

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
		assertEquals("404", request("", null).get("responseCode").get(0));
	}

	public void test_140_4_10() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		Servlet servlet = new HttpServlet() {

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

		Servlet servlet = new HttpServlet() {

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
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, servlet, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/fee/fi/foo/fum");
		serviceRegistrations.add(context.registerService(Servlet.class, servlet, properties));

		assertEquals(":/a:", request("a"));
		assertTrue(invoked.get());
		invoked.set(false);
		assertEquals(":/fee/fi/foo/fum:", request("fee/fi/foo/fum"));
		assertTrue(invoked.get());
	}

	public void test_140_4_17to22() throws Exception {
		BundleContext context = getContext();

		class AServlet extends HttpServlet {

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
				srB.getReference().getProperty(Constants.SERVICE_ID),
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
				srA.getReference().getProperty(Constants.SERVICE_ID),
				failedServletDTO.serviceId);
	}

	public void test_140_4_23to25() throws Exception {
		BundleContext context = getContext();

		class AServlet extends HttpServlet {
		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> srA = context.registerService(Servlet.class, new AServlet(), properties);
		serviceRegistrations.add(srA);

		ServletDTO servletDTO = getServletDTOByName(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
				AServlet.class.getName());

		assertNotNull(servletDTO);
		assertEquals(srA.getReference().getProperty(Constants.SERVICE_ID), servletDTO.serviceId);
	}

	public void test_140_4_26to31() throws Exception {
		BundleContext context = getContext();

		class AServlet extends HttpServlet {

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
				srA.getReference().getProperty(Constants.SERVICE_ID),
				requestInfoDTO.servletDTO.serviceId);
		assertEquals("a", request("a"));

		HttpService httpService = getHttpService();

		if (httpService == null) {
			return;
		}

		httpService.registerServlet("/a", new AServlet("b"), null, null);

		requestInfoDTO = calculateRequestInfoDTO("/a");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertFalse((Long) srA.getReference().getProperty(Constants.SERVICE_ID) == requestInfoDTO.servletDTO.serviceId);
		assertEquals("b", request("a"));
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED() throws Exception {
		BundleContext context = getContext();
		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			final ExecutorService	executor	= Executors.newCachedThreadPool();

			protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
				doGetAsync(req.startAsync());
			}

			private void doGetAsync(final AsyncContext asyncContext) {
				executor.submit(new Callable<Void>() {
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
		assertTrue((Long) srA.getReference().getProperty(Constants.SERVICE_ID) == requestInfoDTO.servletDTO.serviceId);
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

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_ERROR_PAGE() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "a");
			}

		}

		class BServlet extends HttpServlet {

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
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, HttpServletResponse.SC_BAD_GATEWAY + "");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/error");
		serviceRegistrations.add(context.registerService(Servlet.class, new BServlet(), properties));

		ServletDTO servletDTO = getServletDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "b");
		assertNotNull(servletDTO);

		ErrorPageDTO errorPageDTO = getErrorPageDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "b");
		assertNotNull(errorPageDTO);
		assertEquals(HttpServletResponse.SC_BAD_GATEWAY, errorPageDTO.errorCodes[0]);

		Map<String, List<String>> response = request("a", null);
		assertEquals("a", response.get("responseBody").get(0));
		assertTrue(invoked.get());
		assertEquals(HttpServletResponse.SC_BAD_GATEWAY + "", response.get("responseCode").get(0));
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_ERROR_PAGE_4xx() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "a");
			}

		}

		class BServlet extends HttpServlet {

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
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, "4xx");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/error");
		serviceRegistrations.add(context.registerService(Servlet.class, new BServlet(), properties));

		ServletDTO servletDTO = getServletDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "b");
		assertNotNull(servletDTO);
		ErrorPageDTO errorPageDTO = getErrorPageDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "b");
		assertNotNull(errorPageDTO);
		assertTrue(Arrays.binarySearch(errorPageDTO.errorCodes, HttpServletResponse.SC_FORBIDDEN) >= 0);

		Map<String, List<String>> response = request("a", null);
		assertEquals("a", response.get("responseBody").get(0));
		assertTrue(invoked.get());
		assertEquals(HttpServletResponse.SC_FORBIDDEN + "", response.get("responseCode").get(0));
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_ERROR_PAGE_5xx() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "a");
			}

		}

		class BServlet extends HttpServlet {

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

		ServletDTO servletDTO = getServletDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "b");
		assertNotNull(servletDTO);
		ErrorPageDTO errorPageDTO = getErrorPageDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "b");
		assertNotNull(errorPageDTO);
		assertTrue(Arrays.binarySearch(errorPageDTO.errorCodes, HttpServletResponse.SC_BAD_GATEWAY) >= 0);

		Map<String, List<String>> response = request("a", null);
		assertEquals("a", response.get("responseBody").get(0));
		assertTrue(invoked.get());
		assertEquals(HttpServletResponse.SC_BAD_GATEWAY + "", response.get("responseCode").get(0));
	}

	public void test_basicServlet() throws Exception {
		Assert.assertEquals("a", request("TestServlet1"));
	}

	public void test_servletInContext() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "sc1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/sc1");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelperFactory(), properties));

		Servlet servlet = new HttpServlet() {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				response.getWriter().write(getServletContext().getContextPath());
			}

		};

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=sc1)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/TestServlet2");
		serviceRegistrations.add(context.registerService(Servlet.class, servlet, properties));

		Assert.assertEquals("/sc1", request("/sc1/TestServlet2"));
	}

	// public void test_Servlet6() throws Exception {
	// String expected = "a";
	// String actual = request("something/a.TestServlet6");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Servlet7() throws Exception {
	// String expected = "a";
	// String actual = request("TestServlet7/a");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Servlet8() throws Exception {
	// String expected = "Equinox Jetty-based Http Service";
	// String actual = request("TestServlet8");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Servlet9() throws Exception {
	// String expected = "Equinox Jetty-based Http Service";
	// String actual = request("TestServlet9");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Servlet10() throws Exception {
	// String expected = "a";
	// String actual = request("TestServlet10");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Servlet11() throws Exception {
	// String expected = "a";
	// String actual = request("TestServlet11");
	// Assert.assertEquals(expected, actual);
	// }
	//

	public void test_ErrorPage1() throws Exception {
		Map<String, List<String>> response = request("TestErrorPage1/a", null);
		String responseCode = response.get("responseCode").get(0);
		String actual = response.get("responseBody").get(0);

		Assert.assertEquals("403", responseCode);
		Assert.assertEquals("403 ERROR", actual);
	}

	public void test_ErrorPage2() throws Exception {
		Map<String, List<String>> response = request("TestErrorPage2/a", null);
		String responseCode = response.get("responseCode").get(0);
		String actual = response.get("responseBody").get(0);

		Assert.assertEquals("500", responseCode);
		Assert.assertEquals("500 ERROR", actual);
	}

	public void test_basicFilter() throws Exception {
		Assert.assertEquals("bab", request("TestFilter1/bab"));
	}

	public void test_twoFiltersOrderedByRegistration() throws Exception {
		Assert.assertEquals("bcacb", request("TestFilter2/bcacb"));
	}

	public void test_threeFiltersOrderedByRegistration() throws Exception {
		Assert.assertEquals("bcdadcb", request("TestFilter3/bcdadcb"));
	}

	public void test_threeFiltersOrderedByRanking() throws Exception {
		Assert.assertEquals("dbcacbd", request("TestFilter4/dbcacbd"));
	}

	public void test_basicExtensionFilter() throws Exception {
		Assert.assertEquals("bab", request("something/bab.TestFilter5"));
	}

	public void test_twoExtensionFiltersOrderedByRegistration() throws Exception {
		Assert.assertEquals("bcacb", request("something/bcacb.TestFilter6"));
	}

	public void test_threeExtensionFiltersOrderedByRegistration() throws Exception {
		Assert.assertEquals("bcdadcb", request("something/bcdadcb.TestFilter7"));
	}

	public void test_threeExtensionFiltersOrderedByRanking() throws Exception {
		Assert.assertEquals("dbcacbd", request("something/dbcacbd.TestFilter8"));
	}

	// public void test_Filter9() throws Exception {
	// String expected = "bab";
	// String actual = request("TestFilter9/bab");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Filter10() throws Exception {
	// String expected = "cbabc";
	// String actual = request("TestFilter10/cbabc");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Filter11() throws Exception {
	// String expected = "cbdadbc";
	// String actual = request("TestFilter11/cbdadbc");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Filter12() throws Exception {
	// String expected = "dcbabcd";
	// String actual = request("TestFilter12/dcbabcd");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Filter13() throws Exception {
	// String expected = "bab";
	// String actual = request("something/a.TestFilter13");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Filter14() throws Exception {
	// String expected = "cbabc";
	// String actual = request("something/a.TestFilter14");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Filter15() throws Exception {
	// String expected = "cbdadbc";
	// String actual = request("something/a.TestFilter15");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Filter16() throws Exception {
	// String expected = "dcbabcd";
	// String actual = request("something/a.TestFilter16");
	// Assert.assertEquals(expected, actual);
	// }

	public void test_Registration11() throws Exception {
		BundleContext bundleContext = getContext();
		ServiceReference<HttpServiceRuntime> serviceReference =
				bundleContext.getServiceReference(HttpServiceRuntime.class);

		Assert.assertNotNull(serviceReference);

		HttpServiceRuntime runtime = bundleContext.getService(serviceReference);
		RuntimeDTO runtimeDTO = runtime.getRuntimeDTO();
		ServletContextDTO[] servletContextDTOs = runtimeDTO.servletContextDTOs;

		Assert.assertTrue(servletContextDTOs.length > 0);
	}

	public void test_resourceDTO() throws Exception {
		BundleContext bundleContext = getContext();
		ServiceReference<HttpServiceRuntime> serviceReference =
				bundleContext.getServiceReference(HttpServiceRuntime.class);

		Assert.assertNotNull(serviceReference);

		HttpServiceRuntime runtime = bundleContext.getService(serviceReference);
		RuntimeDTO runtimeDTO = runtime.getRuntimeDTO();
		ServletContextDTO[] servletContextDTOs = runtimeDTO.servletContextDTOs;

		ServletContextDTO servletContextDTO = null;

		for (ServletContextDTO curServletContextDTO : servletContextDTOs) {
			if (curServletContextDTO.name.equals("default")) {
				servletContextDTO = curServletContextDTO;
			}
		}

		Assert.assertNotNull(servletContextDTO);

		Assert.assertEquals(2, servletContextDTO.resourceDTOs.length);
		final int index;
		if (servletContextDTO.resourceDTOs[0].patterns[0].equals("/TestResource1/*"))
		{
			index = 0;
		}
		else
		{
			index = 1;
		}
		ResourceDTO resourceDTO = servletContextDTO.resourceDTOs[index];

		Assert.assertEquals("/TestResource1/*", resourceDTO.patterns[0]);
		Assert.assertEquals("/org/osgi/test/cases/http/whiteboard/tb1/resources", resourceDTO.prefix);

		resourceDTO = servletContextDTO.resourceDTOs[index == 0 ? 1 : 0];
		Assert.assertEquals("/TestResource2/a", resourceDTO.patterns[0]);
		Assert.assertEquals("/org/osgi/test/cases/http/whiteboard/tb1/resources/resource1.txt", resourceDTO.prefix);
	}

	public void test_resourceMatchByRegexMatch() throws Exception {
		Assert.assertEquals("a", request("TestResource1/resource1.txt"));
	}

	public void test_resourceMatchByExactMatch() throws Exception {
		Assert.assertEquals("a", request("TestResource2/a"));
	}

	// public void test_Resource3() throws Exception {
	// String expected = "a";
	// String actual = request("TestResource3/resource1.txt");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Resource4() throws Exception {
	// String expected = "dcbabcd";
	// String actual = request("TestResource4/resource1.txt");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Resource5_1() throws Exception {
	// String expected = "dcbabcd";
	// String actual = request("TestResource4/resource1.txt");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Runtime() throws Exception {
	// BundleContext bundleContext = getContext();
	// ServiceReference<HttpServiceRuntime> serviceReference =
	// bundleContext.getServiceReference(HttpServiceRuntime.class);
	// Assert.assertNotNull(serviceReference);
	//
	// if (serviceReference == null) {
	// return;
	// }
	//
	// HttpServiceRuntime runtime = bundleContext.getService(serviceReference);
	// Assert.assertNotNull(runtime);
	// RuntimeDTO runtimeDTO = runtime.getRuntimeDTO();
	// ServletContextDTO[] servletContextDTOs = runtimeDTO.servletContextDTOs;
	// Assert.assertTrue(servletContextDTOs.length > 0);
	// ServletContextDTO servletContextDTO = servletContextDTOs[0];
	// Assert.assertNotNull(servletContextDTO.contextName);
	// }

	// public void test_ServletContext1() throws Exception {
	// String expected =
	// "/org/eclipse/equinox/http/servlet/tests/tb1/resource1.txt";
	// String actual = request("TestServletContext1");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_ServletContextHelper10() throws Exception {
	// String expected = "cac";
	// String actual = request("a/TestServletContextHelper10/a");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_WBServlet1() throws Exception {
	// String expected = "a";
	// String actual = request("WBServlet1/a");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_WBServlet2() throws Exception {
	// String expected = "bab";
	// String actual = request("WBServlet2/a");
	// Assert.assertEquals(expected, actual);
	// }

}
