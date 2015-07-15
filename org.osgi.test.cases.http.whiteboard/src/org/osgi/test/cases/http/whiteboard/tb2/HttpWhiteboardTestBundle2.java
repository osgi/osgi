package org.osgi.test.cases.http.whiteboard.tb2;

import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class HttpWhiteboardTestBundle2 implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		Dictionary<String, Object> properties;

		// missing path - should fail

//		properties = new Hashtable<String, Object>();
//		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "sc3");
//		serviceRegistrations.add(
//				context.registerService(
//						ServletContextHelper.class, new ServletContextHelperFactory(),
//						properties));

		// bad path - should fail

//		properties = new Hashtable<String, Object>();
//		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "sc5");
//		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "badpath");
//		serviceRegistrations.add(
//				context.registerService(
//						ServletContextHelper.class, new ServletContextHelperFactory(),
//						properties));
	}

	public void stop(BundleContext context) throws Exception {
		for (ServiceRegistration<?> serviceRegistration : serviceRegistrations) {
			serviceRegistration.unregister();
		}

		serviceRegistrations.clear();
	}

	private List<ServiceRegistration<?>>	serviceRegistrations	= new CopyOnWriteArrayList<ServiceRegistration<?>>();

}