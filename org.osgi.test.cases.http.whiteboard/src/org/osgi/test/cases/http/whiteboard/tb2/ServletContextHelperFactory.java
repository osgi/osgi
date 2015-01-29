package org.osgi.test.cases.http.whiteboard.tb2;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.context.ServletContextHelper;

public class ServletContextHelperFactory
		implements ServiceFactory<ServletContextHelper> {

	public ServletContextHelper getService(
			Bundle bundle,
			ServiceRegistration<ServletContextHelper> registration) {

		return new ServletContextHelper(bundle) {};
	}

	public void ungetService(
			Bundle bundle,
			ServiceRegistration<ServletContextHelper> registration,
			ServletContextHelper service) {
	}

}