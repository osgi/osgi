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

		Assert.assertEquals(2, runtimeDTO.servletContextDTOs.length);

		bundles.get(0).stop();

		runtimeDTO = httpServiceRuntime.getRuntimeDTO();

		Assert.assertEquals(1, runtimeDTO.servletContextDTOs.length);

		bundles.get(0).start();

		runtimeDTO = httpServiceRuntime.getRuntimeDTO();

		Assert.assertEquals(2, runtimeDTO.servletContextDTOs.length);
	}

	public void test_failedServletContextHelpers() throws Exception {
		BundleContext context = getContext();
		ServiceReference<HttpServiceRuntime> serviceReference = context.getServiceReference(HttpServiceRuntime.class);

		Assert.assertNotNull(serviceReference);

		HttpServiceRuntime httpServiceRuntime = context.getService(serviceReference);

		RuntimeDTO runtimeDTO = httpServiceRuntime.getRuntimeDTO();

		FailedServletContextDTO[] failedServletContextDTOs = runtimeDTO.failedServletContextDTOs;

		Assert.assertEquals(4, failedServletContextDTOs.length);

		for (FailedServletContextDTO failedServletContextDTO : failedServletContextDTOs) {
			Assert.assertEquals(
					DTOConstants.FAILURE_REASON_VALIDATION_FAILED, failedServletContextDTO.failureReason);
		}
	}

}
