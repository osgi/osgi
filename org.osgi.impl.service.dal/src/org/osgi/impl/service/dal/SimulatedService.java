/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
	 * Constructs a new service with the given arguments.
	 * 
	 * @param className The class name.
	 * @param props The service properties.
	 * @param bc The bundle context to register the service.
	 */
	public SimulatedService(String className, Dictionary props, BundleContext bc) {
		init(bc.registerService(className, this, props));
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
