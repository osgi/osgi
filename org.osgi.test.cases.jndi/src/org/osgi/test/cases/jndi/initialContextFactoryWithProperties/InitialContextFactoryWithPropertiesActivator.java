/*
 * Copyright (c) IBM Corporation (2010). All Rights Reserved.
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


package org.osgi.test.cases.jndi.initialContextFactoryWithProperties;

import java.util.Hashtable;

import javax.naming.spi.InitialContextFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.jndi.provider.CTInitialContextFactory;

/**
 * @version $Id$
 */
public class InitialContextFactoryWithPropertiesActivator implements BundleActivator {

	private ServiceRegistration sr;
	
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting: " + context.getBundle().getLocation());
		Hashtable props = new Hashtable();
		String[] interfaces ={CTInitialContextFactory.class.getName(), InitialContextFactory.class.getName()};
		
		CTInitialContextFactory ctf = new CTInitialContextFactory();
		sr = context.registerService(interfaces, ctf, props);	
		
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping: " + context.getBundle().getLocation());
		sr.unregister();
	}

}