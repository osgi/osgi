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


package org.osgi.test.cases.jndi.objectFactory3;

import java.util.Hashtable;

import javax.naming.spi.ObjectFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.jndi.provider.CTObjectFactory;

/** 
 * @version $Revision$ $Date$
 */
public class ObjectFactory3Activator implements BundleActivator {

	private ServiceRegistration sr;
	
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting: " + context.getBundle().getLocation());
		Hashtable props = new Hashtable();
		String[] interfaces = {CTObjectFactory.class.getName(), ObjectFactory.class.getName()};		
		
		props.put("osgi.jndi.serviceName", "CTObjectFactory");
		props.put(Constants.SERVICE_RANKING, new Integer(2));
		
		Hashtable env = new Hashtable();
		env.put("test2", "test2");
		
		CTObjectFactory of = new CTObjectFactory(env);
		
		sr = context.registerService(interfaces, of, props);
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping: " + context.getBundle().getLocation());
		sr.unregister();
	}

}
