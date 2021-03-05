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


package org.osgi.test.cases.jndi.secure.builders;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.ObjectFactoryBuilder;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.jndi.secure.provider.CTInitialContextFactoryBuilder;
import org.osgi.test.cases.jndi.secure.provider.CTObjectFactoryBuilder;

/**
 * @author $Id$
 */
public class BuilderBundleActivator implements BundleActivator {

	private List<ServiceRegistration< ? >> serviceRegistrations = new ArrayList<>();
	
	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting: " + context.getBundle().getLocation());
		Hashtable<String,Object> builderProps = new Hashtable<>();
		String[] builderInterfaces = {InitialContextFactoryBuilder.class.getName()};
		
		CTInitialContextFactoryBuilder ctfb = new CTInitialContextFactoryBuilder();
		serviceRegistrations.add(context.registerService(builderInterfaces, ctfb, builderProps));
		
		Hashtable<String,Object> ofbProps = new Hashtable<>();
		String[] ofbInterfaces = {ObjectFactoryBuilder.class.getName()};
		
		CTObjectFactoryBuilder ofb = new CTObjectFactoryBuilder();
		serviceRegistrations.add(context.registerService(ofbInterfaces, ofb, ofbProps));
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping: " + context.getBundle().getLocation());
		for(int i=0; i < serviceRegistrations.size(); i++) {
			ServiceRegistration< ? > sr = serviceRegistrations.get(i);
			sr.unregister();
		}
	}

}
