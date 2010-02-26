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


package org.osgi.test.cases.jndi.initialDirContextFactoryBuilder1;

import java.util.Hashtable;

import javax.naming.spi.InitialContextFactoryBuilder;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.jndi.provider.CTInitialDirContextFactoryBuilder;

/**
 * @version $Revision$ $Date$
 */
public class InitialDirContextFactoryBuilder1Activator implements
		BundleActivator {

	private ServiceRegistration sr;
	
	public void start(BundleContext context) throws Exception {
		Hashtable props = new Hashtable();
		String[] interfaces = {InitialContextFactoryBuilder.class.getName()};
		
		CTInitialDirContextFactoryBuilder ctfb = new CTInitialDirContextFactoryBuilder();
		sr = context.registerService(interfaces, ctfb, props);		

		
	}

	public void stop(BundleContext context) throws Exception {
		sr.unregister();
	}

}
