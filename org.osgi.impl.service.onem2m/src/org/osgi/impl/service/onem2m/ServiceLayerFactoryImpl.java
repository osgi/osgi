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
package org.osgi.impl.service.onem2m;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.onem2m.protocol.ServiceLayerUtil;
import org.osgi.impl.service.onem2m.protocol.service.ServiceLayerImplService;
import org.osgi.framework.BundleContext;

import org.osgi.service.onem2m.ServiceLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceLayerFactoryImpl implements ServiceFactory<ServiceLayer> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLayerFactoryImpl.class);
	private BundleContext context;

	public ServiceLayerFactoryImpl(BundleContext context) {
		this.context = context;
	}

	@Override
	public ServiceLayer getService(Bundle bundle, ServiceRegistration<ServiceLayer> registration) {

		LOGGER.info("Start factory");

		String bundleSymbolicName = bundle.getSymbolicName();

		// get Properties
		Map<String, String> property = ServiceLayerUtil.getProperty(bundleSymbolicName, bundle.getBundleContext());


		ServiceLayer sl = new ServiceLayerImplService(property.get(ServiceLayerUtil.ORIGIN), context,bundle);
		LOGGER.info("End factory");
		return sl;

	}

	@Override
	public void ungetService(Bundle bundle, ServiceRegistration<ServiceLayer> registration, ServiceLayer service) {
		// NOP

	}

	

}
