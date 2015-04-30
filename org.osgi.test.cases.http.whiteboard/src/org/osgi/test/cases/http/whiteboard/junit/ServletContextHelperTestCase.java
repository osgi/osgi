package org.osgi.test.cases.http.whiteboard.junit;

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

}
