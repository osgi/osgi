package org.osgi.test.cases.http.whiteboard.junit;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.Manifest;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.service.FindHook;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.runtime.HttpServiceRuntime;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.runtime.dto.FailedServletContextDTO;
import org.osgi.service.http.runtime.dto.RequestInfoDTO;
import org.osgi.service.http.runtime.dto.RuntimeDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.test.cases.http.whiteboard.junit.mock.MockSCL;

public class ServletContextHelperTestCase extends BaseHttpWhiteboardTestCase {

	@Override
	protected String[] getBundlePaths() {
		return new String[0];
	}

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

		assertEquals("default", sc1.get().getServletContextName());
	}

	public void test_140_2_10to12() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "sc1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/sc1");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelperFactory(), properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();
		AtomicReference<ServletContext> sc2 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=sc1)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=sc1)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class.getName(), new MockSCL(sc2), properties));

		ClassLoader classLoader1 = sc1.get().getClassLoader();
		ClassLoader classLoader2 = sc2.get().getClassLoader();

		String bsn1 = getSymbolicName(classLoader1);
		String bsn2 = getSymbolicName(classLoader2);

		assertNotSame(bsn1, bsn2);
	}

	public void test_140_2_13to14() throws Exception {
		BundleContext context = getContext();
		ServiceReference<ServletContextHelper> serviceReference = context.getServiceReference(ServletContextHelper.class);

		assertNotNull(serviceReference);
		assertEquals(Constants.SCOPE_BUNDLE, serviceReference.getProperty(Constants.SERVICE_SCOPE));
		assertEquals(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
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

			public void find(
					BundleContext bundleContext, String name, String filter,
					boolean allServices, Collection<ServiceReference<?>> references) {

				if (bundleContext != context) {
					return;
				}

				// don't show default ServletContextHelper
				for (Iterator<ServiceReference<?>> iterator = references.iterator(); iterator.hasNext();) {
					ServiceReference<?> sr = iterator.next();

					if ("default".equals(sr.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME))) {
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
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
				serviceReference.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME));
	}

	public void test_table_140_1_HTTP_WHITEBOARD_CONTEXT_NAME_overrideDefault() throws Exception {
		BundleContext context = getContext();
		String contextPath = "/context1";

		ServletContextHelper servletContextHelper = new ServletContextHelper() {};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, contextPath);
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, servletContextHelper, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		assertEquals(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, sc1.get().getServletContextName());
		assertEquals(contextPath, sc1.get().getContextPath());
	}

	public void test_table_140_1_HTTP_WHITEBOARD_CONTEXT_NAME_highestRanked() throws Exception {
		BundleContext context = getContext();
		String contextPath = "/otherContext";

		ServletContextHelper servletContextHelper = new ServletContextHelper() {};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/context1");
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, servletContextHelper, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME);
		properties.put(Constants.SERVICE_RANKING, new Integer(Integer.MAX_VALUE));
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, contextPath);
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, servletContextHelper, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		assertEquals(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, sc1.get().getServletContextName());
		assertEquals(contextPath, sc1.get().getContextPath());
	}

	public void test_table_140_1_HTTP_WHITEBOARD_CONTEXT_NAME_tieGoesToOldest() throws Exception {
		BundleContext context = getContext();
		String contextPath = "/context1";

		ServletContextHelper servletContextHelper = new ServletContextHelper() {};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, contextPath);
		properties.put(Constants.SERVICE_RANKING, new Integer(1000));
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, servletContextHelper, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/otherContext");
		properties.put(Constants.SERVICE_RANKING, new Integer(1000));
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, servletContextHelper, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		assertEquals(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, sc1.get().getServletContextName());
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
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, contextPath);
		serviceRegistrations.add(context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties));

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		assertEquals(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, sc1.get().getServletContextName());
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

	public void test_140_2_23to16() throws Exception {
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
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {}, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=foobar)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "second");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/someServlet");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {}, properties));

		HttpServiceRuntime httpServiceRuntime = getHttpServiceRuntime();

		RequestInfoDTO requestInfoDTO = httpServiceRuntime.calculateRequestInfoDTO("/foo/bar/someServlet");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertEquals("second", requestInfoDTO.servletDTO.name);
	}

	private HttpServiceRuntime getHttpServiceRuntime() {
		BundleContext context = getContext();

		ServiceReference<HttpServiceRuntime> serviceReference = context.getServiceReference(HttpServiceRuntime.class);

		assertNotNull(serviceReference);

		return context.getService(serviceReference);
	}

	private String getSymbolicName(ClassLoader classLoader) throws IOException {
		InputStream inputStream = classLoader.getResourceAsStream(
			"META-INF/MANIFEST.MF");

		Manifest manifest = new Manifest(inputStream);

		return manifest.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLICNAME);
	}

}
