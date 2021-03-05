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


package org.osgi.test.cases.jndi.secure.factories;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.spi.DirObjectFactory;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.ObjectFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.jndi.secure.provider.CTDirObjectFactory;
import org.osgi.test.cases.jndi.secure.provider.CTInitialContextFactory;
import org.osgi.test.cases.jndi.secure.provider.CTObjectFactory;

/**
 * @author $Id$
 */
public class FactoryBundleActivator implements BundleActivator {

	private List<ServiceRegistration< ? >> serviceRegistrations = new ArrayList<>();
	
	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting: " + context.getBundle().getLocation());
		Hashtable<String,Object> factoryProps = new Hashtable<>();
		String[] factoryInterfaces ={CTInitialContextFactory.class.getName(), InitialContextFactory.class.getName()};
		
		CTInitialContextFactory ctf = new CTInitialContextFactory();
	    serviceRegistrations.add(context.registerService(factoryInterfaces, ctf, factoryProps));

		Hashtable<String,Object> ofProps = new Hashtable<>();
		String[] ofInterfaces = {CTObjectFactory.class.getName(), ObjectFactory.class.getName()};
		
		CTObjectFactory of = new CTObjectFactory();
		serviceRegistrations.add(context.registerService(ofInterfaces, of, ofProps));
		
		Hashtable<String,Object> dofProps = new Hashtable<>();
		String[] dofInterfaces = {CTDirObjectFactory.class.getName(), DirObjectFactory.class.getName()};
		
		CTDirObjectFactory dof = new CTDirObjectFactory();
		serviceRegistrations.add(context.registerService(dofInterfaces, dof, dofProps));
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
