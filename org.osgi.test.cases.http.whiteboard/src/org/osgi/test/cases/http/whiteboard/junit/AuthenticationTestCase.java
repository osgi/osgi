
package org.osgi.test.cases.http.whiteboard.junit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * Tests for
 * {@link ServletContextHelper#handleSecurity(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
 * and
 * {@link ServletContextHelper#finishSecurity(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
 */
public class AuthenticationTestCase extends BaseHttpWhiteboardTestCase {

	private static final String	AUTH_PAR	= "auth";

	private static final String	REC_PAR		= "rec";

	private void setup(final List<String> callStack) throws Exception {
		final BundleContext context = getContext();

		// setup context 1
		final Dictionary<String,Object> ctx1Props = new Hashtable<String,Object>();
		ctx1Props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME,
				"context1");
		ctx1Props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH,
				"/context1");
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

				}, ctx1Props));

		// setup context 2
		final Dictionary<String,Object> ctx2Props = new Hashtable<String,Object>();
		ctx2Props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME,
				"context2");
		ctx2Props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH,
				"/context2");
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

				}, ctx2Props));

		// servlet for both contexts
		final Servlet servlet = new HttpServlet() {

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

		final Dictionary<String,Object> servletProps = new Hashtable<String,Object>();
		servletProps.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN,
				new String[] {
						"/servlet"
				});
		servletProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
				"(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME
						+ "=context1)");
		serviceRegistrations.add(
				context.registerService(Servlet.class, servlet, servletProps));

		servletProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
				"(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME
						+ "=context2)");
		serviceRegistrations.add(
				context.registerService(Servlet.class, servlet, servletProps));
	}

	public void test_handleFinishSecurity() throws Exception {
		final List<String> callStack = new ArrayList<>();
		setup(callStack);

		// request without auth -> no servlet invoked
		request("/context1/servlet");
		assertTrue(callStack.isEmpty());

		// request with auth -> servlet invoked
		request("/context1/servlet?auth=true");
		assertEquals(3, callStack.size());
		assertEquals(Arrays.asList("handle1", "servlet/context1", "finish1"),
				callStack);
		callStack.clear();

		// request to context2, no auth required
		request("/context2/servlet");
		assertEquals(3, callStack.size());
		assertEquals(Arrays.asList("handle2", "servlet/context2", "finish2"),
				callStack);
	}

	public void test_includeSecurity() throws Exception {
		final List<String> callStack = new ArrayList<>();
		setup(callStack);

		// request without auth -> no servlet invoked
		request("/context1/servlet?forward=true");
		assertTrue(callStack.isEmpty());

		// request with auth and include -> servlet invoked
		request("/context1/servlet?include=true&auth=true");
		assertEquals(6, callStack.size());
		assertEquals(Arrays.asList("handle1", "servlet/context1", "handle1",
				"servlet/context1", "finish1", "finish1"), callStack);
		callStack.clear();
	}

	public void test_forwardSecurity() throws Exception {
		final List<String> callStack = new ArrayList<>();
		setup(callStack);

		// request without auth -> no servlet invoked
		request("/context1/servlet?forward=true");
		assertTrue(callStack.isEmpty());

		// request with auth and forward -> servlet invoked
		request("/context1/servlet?forward=true&auth=true");
		assertEquals(6, callStack.size());
		assertEquals(Arrays.asList("handle1", "servlet/context1", "handle1",
				"servlet/context1", "finish1", "finish1"), callStack);
		callStack.clear();
	}

	public void test_exceptionFromServlet() throws Exception {
		final List<String> callStack = new ArrayList<>();
		setup(callStack);

		request("/context1/servlet?throw=true&auth=true");
		assertEquals(4, callStack.size());
		assertEquals(Arrays.asList("handle1", "servlet/context1", "throw",
				"finish1"), callStack);
		callStack.clear();
	}
}
