package org.osgi.test.cases.serviceloader.impl1;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.test.cases.serviceloader.impl.ColorProviderImpl;
import org.osgi.test.cases.serviceloader.spi.ColorProvider;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		ColorProviderImpl impl = new ColorProviderImpl();
		
		Hashtable<String, Object> props = new Hashtable<String, Object>();
		props.put("serviceloader.mediator", (long)-1);
		context.registerService(ColorProvider.class, impl, props);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
