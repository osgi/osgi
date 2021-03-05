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

package org.osgi.test.cases.jmx.cm.tb2;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.test.cases.jmx.cm.tb2.api.HelloSayer;
import org.osgi.test.cases.jmx.cm.tb2.impl.ConfiguratorImpl;
import org.osgi.test.cases.jmx.cm.tb2.impl.HelloSayerImpl;


public class Activator implements BundleActivator {
	private ServiceRegistration< ? >	helloRegistration;
	private ServiceRegistration< ? >	configRegistration;
	private ServiceRegistration< ? >	configFactoryRegistration;
	
	public void start(BundleContext context) throws Exception {
		System.out.println("Hello moon, I am started");
		
		//create configuration service
		ConfiguratorImpl cfg = new ConfiguratorImpl();

		//register as Managed Service
		Dictionary<String,Object> props = new Hashtable<>();
		props.put("test_key", "test_value");
		props.put(Constants.SERVICE_PID,
				context.getBundle().getBundleId() + ".1");
		
		configRegistration = context.registerService(
				ManagedService.class.getName(), cfg, props);
		
		//register as Managed Service Factory		
		Dictionary<String,Object> propsFactory = new Hashtable<>();
		propsFactory.put("test_key", "test_value");
		propsFactory.put(Constants.SERVICE_PID,
				context.getBundle().getBundleId() + ".factory");

		configFactoryRegistration = context.registerService(
				ManagedServiceFactory.class.getName(), cfg,
				propsFactory);
		
		helloRegistration = context.registerService(HelloSayer.class.getCanonicalName(), new HelloSayerImpl(), null);
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("stopped");
		helloRegistration.unregister();
		configRegistration.unregister();
		configFactoryRegistration.unregister();
	}
}
