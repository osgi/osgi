package org.osgi.test.cse.toyCse;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {
	private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);
	private static BundleContext context;

	private String cseName = "in-cse";


	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		LOGGER.info("Start Toy CSE.");
		Activator.context = bundleContext;
		ResourceDTO cseResource = new ResourceDTO();

		//CSEBase
		cseResource.resourceName = cseName;
		cseResource.resourceType = 5;

		// register Toy Cse
		CseService cse = new CseService("/" + cseName, cseResource);
		context.registerService(CseService.class.getName(), cse, null);

		LOGGER.info("Toy CSE Start Success.");
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		LOGGER.info("Stop Toy CSE.");
	}

}