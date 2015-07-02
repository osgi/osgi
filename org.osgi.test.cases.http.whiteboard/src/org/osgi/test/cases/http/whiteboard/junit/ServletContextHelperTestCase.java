package org.osgi.test.cases.http.whiteboard.junit;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.Manifest;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import junit.framework.Assert;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.runtime.HttpServiceRuntime;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.runtime.dto.FailedServletContextDTO;
import org.osgi.service.http.runtime.dto.RuntimeDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.test.cases.http.whiteboard.junit.mock.MockSCL;


public class ServletContextHelperTestCase extends BaseHttpWhiteboardTestCase {

	@Override
	protected String[] getBundlePaths() {
		return new String[] {"/tb2.jar"};
	}

	public void test_defaultServletContextHelperIsFactory() throws Exception {
		bundles.get(0).stop();

		BundleContext context = getContext();
		ServiceReference<ServletContextHelper> serviceReference = context.getServiceReference(ServletContextHelper.class);

		Assert.assertNotNull(serviceReference);
		Assert.assertEquals(Constants.SCOPE_BUNDLE, serviceReference.getProperty(Constants.SERVICE_SCOPE));
		Assert.assertEquals("default", serviceReference.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME));

		bundles.get(0).start();
	}

	public void test_defaultServletContextHelperExists() throws Exception {
		BundleContext context = getContext();
		ServiceReference<ServletContextHelper> serviceReference = context.getServiceReference(ServletContextHelper.class);

		Assert.assertNotNull(serviceReference);
		Assert.assertEquals(Constants.SCOPE_BUNDLE, serviceReference.getProperty(Constants.SERVICE_SCOPE));
		Assert.assertEquals("default", serviceReference.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME));
		Assert.assertEquals("test.value", serviceReference.getProperty("test.property"));
	}

	public void test_defaultServletContextHelperIn() throws Exception {
		BundleContext context = getContext();
		ServiceReference<HttpServiceRuntime> serviceReference = context.getServiceReference(HttpServiceRuntime.class);

		Assert.assertNotNull(serviceReference);

		HttpServiceRuntime httpServiceRuntime = context.getService(serviceReference);

		RuntimeDTO runtimeDTO = httpServiceRuntime.getRuntimeDTO();

		final int numberOfContexts = runtimeDTO.servletContextDTOs.length;

		Assert.assertTrue(numberOfContexts >= 2);

		bundles.get(0).stop();

		runtimeDTO = httpServiceRuntime.getRuntimeDTO();

		Assert.assertEquals(numberOfContexts - 1, runtimeDTO.servletContextDTOs.length);

		bundles.get(0).start();

		runtimeDTO = httpServiceRuntime.getRuntimeDTO();

		Assert.assertEquals(numberOfContexts, runtimeDTO.servletContextDTOs.length);
	}

	public void test_failedServletContextHelpers() throws Exception {
		BundleContext context = getContext();
		ServiceReference<HttpServiceRuntime> serviceReference = context.getServiceReference(HttpServiceRuntime.class);

		Assert.assertNotNull(serviceReference);

		HttpServiceRuntime httpServiceRuntime = context.getService(serviceReference);

		RuntimeDTO runtimeDTO = httpServiceRuntime.getRuntimeDTO();

		FailedServletContextDTO[] failedServletContextDTOs = runtimeDTO.failedServletContextDTOs;

		// at least four failed
		Assert.assertTrue(failedServletContextDTOs.length >= 4);

		final String[] failedContextPaths = {"/sc2", "/sc4", "badpath"}; // and
																			// null
		int numberOfFailed = 0;
		for (FailedServletContextDTO failedServletContextDTO : failedServletContextDTOs) {
			boolean isFailed = failedServletContextDTO.contextPath == null;
			if (!isFailed) {
				for (final String path : failedContextPaths) {
					if (path.equals(failedServletContextDTO.contextPath)) {
						isFailed = true;
						break;
					}
				}
			}
			if (isFailed) {
				Assert.assertEquals(
						DTOConstants.FAILURE_REASON_VALIDATION_FAILED, failedServletContextDTO.failureReason);
				numberOfFailed++;
			}
		}
		assertEquals(4, numberOfFailed);
	}

	public void test_140_2_7to8() throws Exception {
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

	public void test_140_2_8to9() throws Exception {
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

	public void test_140_2_9to10() throws Exception {
		BundleContext context = getContext();

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		assertEquals("default", sc1.get().getServletContextName());
	}

	public void test_140_2_11to13() throws Exception {
		BundleContext context = getContext();

		AtomicReference<ServletContext> sc1 = new AtomicReference<ServletContext>();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=sc1)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		serviceRegistrations.add(context.registerService(ServletContextListener.class, new MockSCL(sc1), properties));

		Collection<ServiceReference<MockSCL>> serviceReferences =
				context.getServiceReferences(MockSCL.class, "(test.name=sc1scl)");

		assertTrue(serviceReferences.size() > 0);

		ServiceReference<MockSCL> reference = serviceReferences.iterator().next();

		MockSCL service = context.getService(reference);

		ClassLoader classLoader1 = sc1.get().getClassLoader();
		ClassLoader classLoader2 = service.getSC().getClassLoader();

		String bsn1 = getSymbolicName(classLoader1);
		String bsn2 = getSymbolicName(classLoader2);

		assertNotSame(bsn1, bsn2);
	}

	private String getSymbolicName(ClassLoader classLoader) throws IOException {
		InputStream inputStream = classLoader.getResourceAsStream(
			"META-INF/MANIFEST.MF");

		Manifest manifest = new Manifest(inputStream);

		return manifest.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLICNAME);
	}

}
