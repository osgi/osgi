package org.osgi.test.cases.http.whiteboard.junit.mock;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.context.ServletContextHelper;

public class MockSCHFactory implements ServiceFactory<ServletContextHelper> {

	@Override
	public ServletContextHelper getService(
			Bundle bundle,
			ServiceRegistration<ServletContextHelper> registration) {

		return new ServletContextHelper(bundle) {};
	}

	@Override
	public void ungetService(
			Bundle bundle,
			ServiceRegistration<ServletContextHelper> registration,
			ServletContextHelper service) {
	}

}
