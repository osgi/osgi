/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

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
class SimulatedService implements ServiceFactory<Object> {

	/** The service registration. */
	protected ServiceRegistration< ? >	serviceReg;

	/** The service reference. */
	protected ServiceReference< ? >		serviceRef;

	/** The service properties. */
	protected Dictionary<String,Object>	serviceProps;

	/**
	 * Registers the service with a service factory. There is an early access to
	 * the service registration.
	 * 
	 * @param classNames The class used for the registration.
	 * @param props The service properties.
	 * @param bc The bundle context used for the registration.
	 */
	protected void register(String[] classNames,
			Dictionary<String,Object> props, BundleContext bc) {
		this.serviceProps = props;
		init(bc.registerService(classNames, this, props));
	}

	@Override
	public Object getService(Bundle bundle,
			ServiceRegistration<Object> registration) {
		init(registration);
		return this;
	}

	@Override
	public void ungetService(Bundle bundle,
			ServiceRegistration<Object> registration, Object service) {
		// nothing special to do here
	}

	private void init(ServiceRegistration< ? > serviceRegA) {
		if (null == this.serviceReg) {
			this.serviceReg = serviceRegA;
			this.serviceRef = serviceRegA.getReference();
		}
	}
}
