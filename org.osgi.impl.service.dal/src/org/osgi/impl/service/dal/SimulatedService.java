/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.impl.service.dal;

import java.util.Dictionary;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * A common simulated service. It provides an early-access the the service
 * registration with a service factory.
 */
public class SimulatedService implements ServiceFactory {

	/** The service registration. */
	protected ServiceRegistration	serviceReg;

	/** The service reference. */
	protected ServiceReference		serviceRef;

	/**
	 * Registers the service with a service factory. There is an early access to
	 * the service registration.
	 * 
	 * @param classNames The class used for the registration.
	 * @param props The service properties.
	 * @param bc The bundle context used for the registration.
	 */
	protected void register(String[] classNames, Dictionary props, BundleContext bc) {
		init(bc.registerService(classNames, this, props));
	}

	public Object getService(Bundle bundle, ServiceRegistration registration) {
		init(registration);
		return this;
	}

	public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
		// nothing special to do here
	}

	private void init(ServiceRegistration serviceRegA) {
		if (null == this.serviceReg) {
			this.serviceReg = serviceRegA;
			this.serviceRef = serviceRegA.getReference();
		}
	}
}
