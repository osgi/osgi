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


package org.osgi.test.cases.jndi.secure.providerBundle;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.naming.spi.DirObjectFactory;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ObjectFactoryBuilder;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.jndi.secure.provider.CTDirObjectFactoryBuilder;
import org.osgi.test.cases.jndi.secure.provider.CTDirObjectFactory;
import org.osgi.test.cases.jndi.secure.provider.CTObjectFactoryBuilder;
import org.osgi.test.cases.jndi.secure.provider.CTObjectFactory;
import org.osgi.test.cases.jndi.secure.provider.CTInitialDirContextFactoryBuilder;
import org.osgi.test.cases.jndi.secure.provider.CTInitialDirContextFactory;
import org.osgi.test.cases.jndi.secure.provider.CTInitialContextFactoryBuilder;
import org.osgi.test.cases.jndi.secure.provider.CTInitialContextFactory;

/**
 * @version $Revision$ $Date$
 */
public class ProviderBundleActivator implements BundleActivator {

	private ArrayList serviceRegistrations = new ArrayList();
	
	public void start(BundleContext context) throws Exception {
		Hashtable factoryProps = new Hashtable();
		String[] factoryInterfaces ={CTInitialContextFactory.class.getName(), InitialContextFactory.class.getName()};
		
		CTInitialContextFactory ctf = new CTInitialContextFactory();
	    serviceRegistrations.add(context.registerService(factoryInterfaces, ctf, factoryProps));
	    
		Hashtable builderProps = new Hashtable();
		String[] builderInterfaces = {InitialContextFactoryBuilder.class.getName()};
		
		CTInitialContextFactoryBuilder ctfb = new CTInitialContextFactoryBuilder();
		serviceRegistrations.add(context.registerService(builderInterfaces, ctfb, builderProps));
		
		Hashtable dirCTFProps = new Hashtable();
		String[] dirCTFInterfaces ={CTInitialDirContextFactory.class.getName(), InitialContextFactory.class.getName()};
		
		CTInitialDirContextFactory dctf = new CTInitialDirContextFactory();
		serviceRegistrations.add(context.registerService(dirCTFInterfaces, dctf, dirCTFProps));
		
		Hashtable dirBuilderProps = new Hashtable();
		String[] dirBuilderInterfaces = {InitialContextFactoryBuilder.class.getName()};
		
		CTInitialDirContextFactoryBuilder dctfb = new CTInitialDirContextFactoryBuilder();
		serviceRegistrations.add(context.registerService(dirBuilderInterfaces, dctfb, dirBuilderProps));	
		
		Hashtable ofProps = new Hashtable();
		String[] ofInterfaces = {CTObjectFactory.class.getName(), ObjectFactory.class.getName()};
		
		CTObjectFactory of = new CTObjectFactory();
		serviceRegistrations.add(context.registerService(ofInterfaces, of, ofProps));
		
		Hashtable ofbProps = new Hashtable();
		String[] ofbInterfaces = {ObjectFactoryBuilder.class.getName()};
		
		CTObjectFactoryBuilder ofb = new CTObjectFactoryBuilder();
		serviceRegistrations.add(context.registerService(ofbInterfaces, ofb, ofbProps));
		
		Hashtable dofProps = new Hashtable();
		String[] dofInterfaces = {CTDirObjectFactory.class.getName(), DirObjectFactory.class.getName()};
		
		CTDirObjectFactory dof = new CTDirObjectFactory();
		serviceRegistrations.add(context.registerService(dofInterfaces, dof, dofProps));
		
		Hashtable dofbProps = new Hashtable();
		String[] dofbInterfaces = {ObjectFactoryBuilder.class.getName()};
		
		CTDirObjectFactoryBuilder dofb = new CTDirObjectFactoryBuilder();
		serviceRegistrations.add(context.registerService(dofbInterfaces, dofb, dofbProps));	
	}

	public void stop(BundleContext context) throws Exception {
		for(int i=0; i < serviceRegistrations.size(); i++) {
			ServiceRegistration sr = (ServiceRegistration) serviceRegistrations.get(i);
			sr.unregister();
		}
	}

}