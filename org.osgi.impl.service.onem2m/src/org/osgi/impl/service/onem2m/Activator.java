package org.osgi.service.onem2m.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.service.onem2m.ServiceLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {
	private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);
	private static BundleContext context;


	public static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		LOGGER.info("Start Service Layer");
		Activator.context = bundleContext;

		// register factory service
		ServiceFactory<ServiceLayer> slf = new ServiceLayerFactoryImpl();
		context.registerService(ServiceLayer.class.getName(), slf, null);

		LOGGER.info("END Service Layer");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		LOGGER.info("stop Service Layer");
	}

}