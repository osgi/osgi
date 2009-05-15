package org.osgi.test.cases.router;

import junit.framework.TestCase;

import org.osgi.framework.BundleContext;
import org.osgi.service.router.Router;
import org.osgi.util.tracker.ServiceTracker;

public class TestRouter extends TestCase {
	BundleContext context;
	
	public void setBundleContext(BundleContext context) {
		this.context = context;
	}
	public void testSimple() {
		ServiceTracker st = new ServiceTracker(context, Router.class.getName(), null);
		st.open();
		Router router = (Router) st.getService();
		System.out.println(router);
	}
}
