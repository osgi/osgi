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

package org.osgi.test.cases.component.tb33.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.framework.Bundle;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.component.service.DataService;

/**
 * Component that implements PrototypeServiceFactory<DataService> to provide
 * prototype-scoped DataService instances. This demonstrates the enhancement 
 * where a component can implement PrototypeServiceFactory instead of the 
 * service interface directly.
 * 
 * @author $Id$
 */
public class DataServicePrototypeFactoryComponent implements PrototypeServiceFactory<DataService> {
	
	private static final AtomicInteger instanceCounter = new AtomicInteger(0);
	
	@Override
	public DataService getService(Bundle bundle, ServiceRegistration<DataService> registration) {
		// Create a unique prototype instance for each request
		String instanceId = "prototype-" + instanceCounter.incrementAndGet();
		return new DataServiceImpl(instanceId, bundle.getBundleId());
	}
	
	@Override
	public void ungetService(Bundle bundle, ServiceRegistration<DataService> registration, DataService service) {
		// Cleanup if needed
		// In this test case, no cleanup is required
	}
}
