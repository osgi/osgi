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


package org.osgi.test.cases.jndi.exceptionalInitialContextFactoryBuilder1;

import java.util.Hashtable;

import javax.naming.spi.InitialContextFactoryBuilder;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.jndi.provider.CTExceptionalInitialContextFactoryBuilder;
import org.osgi.test.cases.jndi.provider.CTInitialContextFactoryBuilder;

/** 
 * @version $Id$
 */
public class ExceptionalInitialContextFactoryBuilder1Activator implements
		BundleActivator {

	private ServiceRegistration sr1;
	private ServiceRegistration sr2;
	
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting: " + context.getBundle().getLocation());
		Hashtable props1 = new Hashtable();
		Hashtable props2 = new Hashtable();
		String[] interfaces ={InitialContextFactoryBuilder.class.getName()};
		

		props1.put(Constants.SERVICE_RANKING, new Integer(3));
		props2.put(Constants.SERVICE_RANKING, new Integer(2));
		
		CTExceptionalInitialContextFactoryBuilder ctfb1 = new CTExceptionalInitialContextFactoryBuilder();
		CTInitialContextFactoryBuilder ctfb2 = new CTInitialContextFactoryBuilder();
		
		sr1 = context.registerService(interfaces, ctfb1, props1);
		sr2 = context.registerService(interfaces, ctfb2, props2);
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping: " + context.getBundle().getLocation());
		sr1.unregister();
		sr2.unregister();
	}

}
