package org.osgi.test.cases.http.whiteboard.junit;

import java.io.IOException;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.AsyncContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.runtime.dto.FailedFilterDTO;
import org.osgi.service.http.runtime.dto.FilterDTO;
import org.osgi.service.http.runtime.dto.RequestInfoDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.test.cases.http.whiteboard.junit.mock.MockFilter;
import org.osgi.test.cases.http.whiteboard.junit.mock.MockServlet;

public class FilterTestCase extends BaseHttpWhiteboardTestCase {

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED_validate() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED, "true");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		ServiceRegistration<Filter> sr = context.registerService(Filter.class, new MockFilter(), properties);
		serviceRegistrations.add(sr);

		FilterDTO filterDTO = getFilterDTOByName(DEFAULT, "a");
		assertNotNull(filterDTO);
		assertTrue(filterDTO.asyncSupported);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED, "false");
		sr.setProperties(properties);

		filterDTO = getFilterDTOByName(DEFAULT, "a");
		assertNotNull(filterDTO);
		assertFalse(filterDTO.asyncSupported);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED, 234l);
		sr.setProperties(properties);

		filterDTO = getFilterDTOByName(DEFAULT, "a");
		assertNotNull(filterDTO);
		assertFalse(filterDTO.asyncSupported);

		properties.remove(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED);
		sr.setProperties(properties);

		filterDTO = getFilterDTOByName(DEFAULT, "a");
		assertNotNull(filterDTO);
		assertFalse(filterDTO.asyncSupported);
	}

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_DISPATCHER_async() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED, "true");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_DISPATCHER, "ASYNC");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/b");
		ServiceRegistration<?> srA = context.registerService(Filter.class, new MockFilter().around("b"), properties);
		serviceRegistrations.add(srA);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		ServiceRegistration<?> srB = context.registerService(Servlet.class, new MockServlet().content("a"), properties);
		serviceRegistrations.add(srB);

		final AtomicBoolean invoked = new AtomicBoolean(false);
		MockServlet mockServlet = new MockServlet() {

			final ExecutorService	executor	= Executors.newCachedThreadPool();

			protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
				doGetAsync(req.startAsync());
			}

			private void doGetAsync(final AsyncContext asyncContext) {
				executor.submit(new Callable<Void>() {
					public Void call() throws Exception {
						try {
							invoked.set(true);

							asyncContext.dispatch("/b");
						} finally {
							asyncContext.complete();
						}

						return null;
					}
				});
			}

		};

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, "true");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<?> srC = context.registerService(Servlet.class, mockServlet, properties);
		serviceRegistrations.add(srC);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(0, requestInfoDTO.filterDTOs.length);
		assertEquals(getServiceId(srC), requestInfoDTO.servletDTO.serviceId);

		assertEquals("bab", request("a"));
		assertTrue(invoked.get());
	}

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_DISPATCHER_request() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		ServiceRegistration<?> srA = context.registerService(Filter.class, new MockFilter().around("b"), properties);
		serviceRegistrations.add(srA);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<?> srB = context.registerService(Servlet.class, new MockServlet().content("a"), properties);
		serviceRegistrations.add(srB);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		FilterDTO filterDTO = requestInfoDTO.filterDTOs[0];
		assertEquals(getServiceId(srA), filterDTO.serviceId);
		assertEquals(1, filterDTO.dispatcher.length);
		assertEquals("REQUEST", filterDTO.dispatcher[0]);

		assertEquals("bab", request("a"));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_DISPATCHER, "REQUEST");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		srA.setProperties(properties);

		assertEquals("bab", request("a"));
	}

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_DISPATCHER_include() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_DISPATCHER, "INCLUDE");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		ServiceRegistration<?> srA = context.registerService(Filter.class, new MockFilter().around("b"), properties);
		serviceRegistrations.add(srA);

		FilterDTO filterDTO = getFilterDTOByName(DEFAULT, "a");
		assertNotNull(filterDTO);
		assertEquals(1, filterDTO.dispatcher.length);
		assertEquals("INCLUDE", filterDTO.dispatcher[0]);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<?> srB = context.registerService(Servlet.class, new MockServlet().content("a"), properties);
		serviceRegistrations.add(srB);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(0, requestInfoDTO.filterDTOs.length);
		assertEquals("a", request("a"));

		MockServlet mockServlet = new MockServlet() {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				RequestDispatcher requestDispatcher = request.getRequestDispatcher("/a");

				requestDispatcher.include(request, response);
			}

		};

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		ServiceRegistration<?> srC = context.registerService(Servlet.class, mockServlet, properties);
		serviceRegistrations.add(srC);

		assertEquals("bab", request("b"));
	}

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_DISPATCHER_forward() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_DISPATCHER, "FORWARD");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		ServiceRegistration<?> srA = context.registerService(Filter.class, new MockFilter().around("b"), properties);
		serviceRegistrations.add(srA);

		FilterDTO filterDTO = getFilterDTOByName(DEFAULT, "a");
		assertNotNull(filterDTO);
		assertEquals(1, filterDTO.dispatcher.length);
		assertEquals("FORWARD", filterDTO.dispatcher[0]);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<?> srB = context.registerService(Servlet.class, new MockServlet().content("a"), properties);
		serviceRegistrations.add(srB);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(0, requestInfoDTO.filterDTOs.length);
		assertEquals("a", request("a"));

		MockServlet mockServlet = new MockServlet() {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				RequestDispatcher requestDispatcher = request.getRequestDispatcher("/a");

				requestDispatcher.forward(request, response);
			}

		};

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		ServiceRegistration<?> srC = context.registerService(Servlet.class, mockServlet, properties);
		serviceRegistrations.add(srC);

		assertEquals("bab", request("b"));
	}

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_DISPATCHER_error() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_DISPATCHER, "ERROR");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET, "a");
		ServiceRegistration<?> srA = context.registerService(Filter.class, new MockFilter().around("b"), properties);
		serviceRegistrations.add(srA);

		FilterDTO filterDTO = getFilterDTOByName(DEFAULT, "a");
		assertNotNull(filterDTO);
		assertEquals(1, filterDTO.dispatcher.length);
		assertEquals("ERROR", filterDTO.dispatcher[0]);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, "4xx");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<?> srB = context.registerService(Servlet.class, new MockServlet().content("a"), properties);
		serviceRegistrations.add(srB);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(0, requestInfoDTO.filterDTOs.length);
		assertEquals("a", request("a"));

		MockServlet mockServlet = new MockServlet() {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}

		};

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		ServiceRegistration<?> srC = context.registerService(Servlet.class, mockServlet, properties);
		serviceRegistrations.add(srC);

		assertEquals("bab", request("b"));
	}

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_DISPATCHER_multiple() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_DISPATCHER, new String[] {"REQUEST", "ERROR"});
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET, "a");
		ServiceRegistration<?> srA = context.registerService(Filter.class, new MockFilter().around("b"), properties);
		serviceRegistrations.add(srA);

		FilterDTO filterDTO = getFilterDTOByName(DEFAULT, "a");
		assertNotNull(filterDTO);
		assertEquals(2, filterDTO.dispatcher.length);
		Arrays.sort(filterDTO.dispatcher);
		assertTrue(Arrays.binarySearch(filterDTO.dispatcher, "ERROR") >= 0);
		assertTrue(Arrays.binarySearch(filterDTO.dispatcher, "REQUEST") >= 0);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, "4xx");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<?> srB = context.registerService(Servlet.class, new MockServlet().content("a"), properties);
		serviceRegistrations.add(srB);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals("bab", request("a"));

		MockServlet mockServlet = new MockServlet() {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}

		};

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		ServiceRegistration<?> srC = context.registerService(Servlet.class, mockServlet, properties);
		serviceRegistrations.add(srC);

		assertEquals("bab", request("b"));
	}

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_NAME() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new MockServlet().content("a"), properties));

		MockFilter mockFilter = new MockFilter() {

			@Override
			public void init(FilterConfig config) throws ServletException {
				super.init(config);
				this.config = config;
			}

			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
				response.getWriter().write(config.getFilterName());
				chain.doFilter(request, response);
			}

			private FilterConfig config;

		};

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		ServiceRegistration<?> srA = context.registerService(Filter.class, mockFilter, properties);
		serviceRegistrations.add(srA);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals(mockFilter.getClass().getName(), requestInfoDTO.filterDTOs[0].name);
		assertEquals(mockFilter.getClass().getName() + "a", request("a"));

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "b");
		srA.setProperties(properties);

		requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals("b", requestInfoDTO.filterDTOs[0].name);
		assertEquals("ba", request("a"));
	}

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_PATTERN() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, new String[] {"", "/"});
		serviceRegistrations.add(context.registerService(Servlet.class, new MockServlet().content("a"), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/**");
		ServiceRegistration<?> sr = context.registerService(Filter.class, new MockFilter().around("b"), properties);
		serviceRegistrations.add(sr);

		FailedFilterDTO failedFilterDTO = getFailedFilterDTOByName("a");
		assertNotNull(failedFilterDTO);
		assertEquals(DTOConstants.FAILURE_REASON_VALIDATION_FAILED, failedFilterDTO.failureReason);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/*");
		sr.setProperties(properties);

		failedFilterDTO = getFailedFilterDTOByName("a");
		assertNull(failedFilterDTO);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals("bab", request("a"));
		assertEquals("bab", request("a.html"));
		assertEquals("bab", request("some/path/b.html"));
		assertEquals("bab", request(""));

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "");
		sr.setProperties(properties);

		requestInfoDTO = calculateRequestInfoDTO("/");
		assertNotNull(requestInfoDTO);
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals("a", request("a"));
		assertEquals("a", request("a.html"));
		assertEquals("a", request("some/path/b.html"));
		assertEquals("bab", request(""));

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "*.html");
		sr.setProperties(properties);

		requestInfoDTO = calculateRequestInfoDTO("/a.html");
		assertNotNull(requestInfoDTO);
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals("a", request("a"));
		assertEquals("bab", request("a.html"));
		assertEquals("bab", request("some/path/b.html"));
		assertEquals("a", request(""));
	}

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_REGEX() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, new String[] {"", "/"});
		serviceRegistrations.add(context.registerService(Servlet.class, new MockServlet().content("a"), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_REGEX, "**");
		ServiceRegistration<?> sr = context.registerService(Filter.class, new MockFilter().around("b"), properties);
		serviceRegistrations.add(sr);

		FailedFilterDTO failedFilterDTO = getFailedFilterDTOByName("a");
		assertNotNull(failedFilterDTO);
		assertEquals(DTOConstants.FAILURE_REASON_VALIDATION_FAILED, failedFilterDTO.failureReason);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_REGEX, "/.+");
		sr.setProperties(properties);

		failedFilterDTO = getFailedFilterDTOByName("a");
		assertNull(failedFilterDTO);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals("bab", request("a"));
		assertEquals("bab", request("a.html"));
		assertEquals("bab", request("some/path/b.html"));
		assertEquals("a", request(""));

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_REGEX, "/?");
		sr.setProperties(properties);

		requestInfoDTO = calculateRequestInfoDTO("/");
		assertNotNull(requestInfoDTO);
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals("a", request("a"));
		assertEquals("a", request("a.html"));
		assertEquals("a", request("some/path/b.html"));
		assertEquals("bab", request(""));

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_REGEX, ".*\\.html");
		sr.setProperties(properties);

		requestInfoDTO = calculateRequestInfoDTO("/a.html");
		assertNotNull(requestInfoDTO);
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals("a", request("a"));
		assertEquals("bab", request("a.html"));
		assertEquals("bab", request("some/path/b.html"));
		assertEquals("a", request(""));
	}

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_SERVLET() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, new String[] {"", "/"});
		serviceRegistrations.add(context.registerService(Servlet.class, new MockServlet().content("a"), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, new String[] {"/b"});
		serviceRegistrations.add(context.registerService(Servlet.class, new MockServlet().content("b"), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET, "**");
		ServiceRegistration<?> sr = context.registerService(Filter.class, new MockFilter().around("b"), properties);
		serviceRegistrations.add(sr);

		FailedFilterDTO failedFilterDTO = getFailedFilterDTOByName("a");
		assertNull(failedFilterDTO);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET, "a");
		sr.setProperties(properties);

		failedFilterDTO = getFailedFilterDTOByName("a");
		assertNull(failedFilterDTO);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals("bab", request("a"));
		assertEquals("bab", request("a.html"));
		assertEquals("bab", request("some/path/b.html"));
		assertEquals("bab", request(""));
		assertEquals("b", request("b"));
	}

	public void test_table_140_5_initParams() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new MockServlet().content("a"), properties));

		MockFilter mockFilter = new MockFilter() {

			@Override
			public void init(FilterConfig config) throws ServletException {
				this.config = config;
			}

			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
				String initParameter = config.getInitParameter(request.getParameter("p"));

				response.getWriter().write((initParameter == null) ? "" : initParameter);
			}

			private FilterConfig	config;

		};

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_INIT_PARAM_PREFIX + "param1", "value1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_INIT_PARAM_PREFIX + "param2", "value2");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_INIT_PARAM_PREFIX + "param3", 345l);
		ServiceRegistration<?> sr = context.registerService(Filter.class, mockFilter, properties);
		serviceRegistrations.add(sr);

		FilterDTO filterDTO = getFilterDTOByName(DEFAULT, "a");

		assertNotNull(filterDTO);
		assertTrue(filterDTO.initParams.containsKey("param1"));
		assertTrue(filterDTO.initParams.containsKey("param2"));
		assertFalse(filterDTO.initParams.containsKey("param3"));
		assertEquals(getServiceId(sr), filterDTO.serviceId);
		assertEquals("value1", request("a?p=param1"));
		assertEquals("value2", request("a?p=param2"));
		assertEquals("", request("a?p=param3"));
	}

	public void test_140_5_7to10() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		ServiceRegistration<?> sr = context.registerService(Filter.class, new MockFilter(), properties);
		serviceRegistrations.add(sr);

		FailedFilterDTO failedFilterDTO = getFailedFilterDTOByName("a");
		assertNull(failedFilterDTO);
		FilterDTO filterDTO = getFilterDTOByName(DEFAULT, "a");
		assertNull(filterDTO);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		sr.setProperties(properties);

		failedFilterDTO = getFailedFilterDTOByName("a");
		assertNull(failedFilterDTO);

		filterDTO = getFilterDTOByName(DEFAULT, "a");
		assertNotNull(filterDTO);
		assertEquals(getServiceId(sr), filterDTO.serviceId);

		properties.remove(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_REGEX, ".*");
		sr.setProperties(properties);

		failedFilterDTO = getFailedFilterDTOByName("a");
		assertNull(failedFilterDTO);

		filterDTO = getFilterDTOByName(DEFAULT, "a");
		assertNotNull(filterDTO);
		assertEquals(getServiceId(sr), filterDTO.serviceId);

		properties.remove(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_REGEX);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET, "a");
		sr.setProperties(properties);

		failedFilterDTO = getFailedFilterDTOByName("a");
		assertNull(failedFilterDTO);

		filterDTO = getFilterDTOByName(DEFAULT, "a");
		assertNotNull(filterDTO);
		assertEquals(getServiceId(sr), filterDTO.serviceId);
	}

	public void test_140_5_11to17() throws Exception {
		BundleContext context = getContext();
		final AtomicBoolean invokedDestroy = new AtomicBoolean(false);
		final AtomicBoolean invokedInit = new AtomicBoolean(false);

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new MockServlet().content("a"), properties));

		MockFilter mockFilter = new MockFilter() {

			@Override
			public void destroy() {
				invokedDestroy.set(true);
			}

			@Override
			public void init(FilterConfig config) throws ServletException {
				invokedInit.set(true);
			}

		};

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		ServiceRegistration<?> sr = context.registerService(Filter.class, mockFilter.around("b"), properties);
		serviceRegistrations.add(sr);

		assertEquals("bab", request("a"));
		assertTrue(invokedInit.get());
		invokedInit.set(false);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET, "b");
		sr.setProperties(properties);
		assertTrue(invokedDestroy.get());
		assertEquals("bab", request("a"));
		assertTrue(invokedInit.get());
	}

	public void test_140_5_17to20() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET, "(some.property=some.value)");
		serviceRegistrations.add(context.registerService(Filter.class, new MockFilter(), properties));

		FilterDTO filterDTO = getFilterDTOByName(DEFAULT, "a");
		assertNull(filterDTO);

		FailedFilterDTO failedFilterDTO = getFailedFilterDTOByName("a");
		assertNull(failedFilterDTO);
	}

	public void test_140_5_21to25() throws Exception {
		BundleContext context = getContext();
		final AtomicBoolean invokedGetService = new AtomicBoolean(false);
		final AtomicBoolean invokedDestroy = new AtomicBoolean(false);
		final AtomicBoolean invokedInit = new AtomicBoolean(false);

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new MockServlet().content("a"), properties));

		class AFilter extends MockFilter {

			@Override
			public void destroy() {
				invokedDestroy.set(true);
			}

			@Override
			public void init(FilterConfig config) throws ServletException {
				invokedInit.set(true);
			}

		}

		PrototypeServiceFactory<Filter> factory = new PrototypeServiceFactory<Filter>() {

			public void ungetService(Bundle bundle, ServiceRegistration<Filter> registration, Filter service) {
			}

			public Filter getService(Bundle bundle, ServiceRegistration<Filter> registration) {
				invokedGetService.set(true);
				return new AFilter().around("b");
			}

		};

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		ServiceRegistration<?> sr = context.registerService(Filter.class, factory, properties);
		serviceRegistrations.add(sr);

		assertEquals("bab", request("a"));
		assertTrue(invokedGetService.get());
		assertTrue(invokedInit.get());
		invokedGetService.set(false);
		invokedInit.set(false);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET, "b");
		sr.setProperties(properties);
		assertTrue(invokedDestroy.get());
		assertTrue(invokedGetService.get());
		assertEquals("bab", request("a"));
		assertTrue(invokedInit.get());
	}

	public void test_140_5_26to31() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new MockServlet().content("a"), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		properties.put(Constants.SERVICE_RANKING, 1000);
		ServiceRegistration<?> srB = context.registerService(Filter.class, new MockFilter().around("b"), properties);
		serviceRegistrations.add(srB);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "c");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		properties.put(Constants.SERVICE_RANKING, 1000);
		ServiceRegistration<?> srC = context.registerService(Filter.class, new MockFilter().around("c"), properties);
		serviceRegistrations.add(srC);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(2, requestInfoDTO.filterDTOs.length);
		assertEquals("bcacb", request("a"));

		properties.put(Constants.SERVICE_RANKING, 2000);
		srC.setProperties(properties);
		assertEquals("cbabc", request("a"));
	}

	public void test_140_5_32to33() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new MockServlet().content("a"), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=foo)");
		ServiceRegistration<?> srB = context.registerService(Filter.class, new MockFilter().around("b"), properties);
		serviceRegistrations.add(srB);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "c");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		ServiceRegistration<?> srC = context.registerService(Filter.class, new MockFilter().around("c"), properties);
		serviceRegistrations.add(srC);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "d");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET, "(something=foo)");
		ServiceRegistration<?> srD = context.registerService(Filter.class, new MockFilter().around("d"), properties);
		serviceRegistrations.add(srD);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals("cac", request("a"));
	}

	/**
	 * Tests for 1.1 Update
	 */
	
	/**
	 * Filters for HttpService servlets.
	 */
	public void testHttpServiceServletAndWhiteboardFilter() throws Exception {
		final String path = "/tesths";
		final HttpService service = this.getHttpService();
		service.registerServlet(path, new HttpServlet() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void doGet(HttpServletRequest req,
					HttpServletResponse resp) throws IOException {
				resp.getWriter().print("helloworld");
				resp.flushBuffer();
			}
		}, null, null);

		try {
			Dictionary<String,Object> properties = new Hashtable<String,Object>();
			properties.put(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN,
					path);
			properties.put(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
					"(" + HttpWhiteboardConstants.HTTP_SERVICE_CONTEXT_PROPERTY
							+ "=*)");
			long before = this.getHttpRuntimeChangeCount();
			this.serviceRegistrations.add(this.getContext()
					.registerService(Filter.class, new Filter() {

						@Override
						public void init(FilterConfig arg0)
								throws ServletException {}

						@Override
						public void doFilter(ServletRequest req,
								ServletResponse resp, FilterChain chain)
								throws IOException, ServletException {
							resp.getWriter().write("FILTER-");
							chain.doFilter(req, resp);
						}

						@Override
						public void destroy() {}
					}, properties));
			this.waitForRegistration(before);
			assertEquals("FILTER-helloworld", this.request(path));
		} finally {
			service.unregister(path);
		}
	}
}
