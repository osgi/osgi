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


package org.osgi.test.cases.jndi.objectFactory2;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.jndi.provider.CTObjectFactory;

/**
 * @version $Revision$ $Date$
 */
public class ObjectFactory2Activator implements BundleActivator {

	private ServiceRegistration sr1;
	private ServiceRegistration sr2;
	
	public void start(BundleContext context) throws Exception {
		Hashtable props1 = new Hashtable();
		Hashtable props2 = new Hashtable();
		
		props1.put("osgi.jndi.serviceName", "CTObjectFactory");
		props2.put("osgi.jndi.serviceName", "CTObjectFactory");
		
		Hashtable env = new Hashtable();
		env.put("test", "This is the right objectFactory");
		
		CTObjectFactory of1 = new CTObjectFactory(env);
		CTObjectFactory of2 = new CTObjectFactory();
		
		sr1 = context.registerService(CTObjectFactory.class.getName(), of1, props1);
		sr2 = context.registerService(CTObjectFactory.class.getName(), of2, props2);
	}

	public void stop(BundleContext context) throws Exception {
		sr1.unregister();
		sr2.unregister();
	}
}
