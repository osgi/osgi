/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
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


package org.osgi.test.cases.jndi.secure.initialContextFactory2;

import java.util.Hashtable;

import javax.naming.spi.InitialContextFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.jndi.secure.provider.CTInitialContextFactory;

/**
 * @version $Revision$ $Date$
 */
public class InitialContextFactory2Activator implements BundleActivator {

	private ServiceRegistration sr1;
	private ServiceRegistration sr2;
	
	public void start(BundleContext context) throws Exception {
		Hashtable props1 = new Hashtable();
		Hashtable props2 = new Hashtable();
		String[] interfaces ={CTInitialContextFactory.class.getName(), InitialContextFactory.class.getName()};
		
		props1.put("osgi.jndi.serviceName", "CTInitialContextFactory"); 
		props1.put(Constants.SERVICE_RANKING, new Integer(3));
		props2.put("osgi.jndi.serviceName", "CTInitialContextFactory");
		props2.put(Constants.SERVICE_RANKING, new Integer(2));
		
		Hashtable env1 = new Hashtable();
		Hashtable env2 = new Hashtable();
		env1.put("test1", "test1");
		env2.put("test2", "test2");
		
		CTInitialContextFactory ctf1 = new CTInitialContextFactory(env1);
		CTInitialContextFactory ctf2 = new CTInitialContextFactory(env2);
		
		sr1 = context.registerService(interfaces, ctf1, props1);
		sr2 = context.registerService(interfaces, ctf2, props2);
	}

	public void stop(BundleContext context) throws Exception {
		sr1.unregister();
		sr2.unregister();
	}

}
