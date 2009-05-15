package org.osgi.impl.service.router;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.router.Router;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		context.registerService(Router.class.getName(), new Router() {}, null);
	}

	public void stop(BundleContext context) throws Exception {
	}

}
