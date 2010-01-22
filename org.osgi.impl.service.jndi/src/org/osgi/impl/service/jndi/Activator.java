/*
 * Copyright 2009 Oracle Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.osgi.impl.service.jndi;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;
import javax.naming.spi.ObjectFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jndi.JNDIConstants;
import org.osgi.service.jndi.JNDIContextManager;
import org.osgi.service.jndi.JNDIProviderAdmin;

/**
 * Activator implementation for the JNDI Factory Manager Bundle.
 * 
 * This activator's main purpose is to register the JNDI Builder singleton
 * implementations that allow the Factory Manager to override the default JNDI
 * framework.
 * 
 * 
 */
public class Activator implements BundleActivator {

	private static final String					OSGI_URL_SCHEME					= "osgi";
	
	private static Logger m_logger = Logger.getLogger(Activator.class.getName());

	private BundleContext						m_bundleContext					= null;
	private final List                          m_listOfServiceRegistrations = new LinkedList();

	/*
	 * Create the Factory Manager's builder implementation, and register it with
	 * the JNDI NamingManager.
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		m_logger.info("Initializing JNDI Factory Manager Bundle");
		
		m_bundleContext = context;

		// register static singletons with the JNDI framework
		m_logger.info("Installing Factory Manager as a JNDI Builder");
		NamingManager.setInitialContextFactoryBuilder(new TraditionalInitialContextFactoryBuilder());
		NamingManager.setObjectFactoryBuilder(new TraditionalObjectFactoryBuilder());

		m_logger.info("Registering URL Context Factory for 'osgi' URL scheme");
		registerOSGiURLContextFactory();
		
		m_logger.info("Registering Default Runtime Builder for JRE-provided factories");
		registerDefaultRuntimeBuilder();
		
		m_logger.info("Registering JNDIContextManager service");
		// register the JNDIContextManager service once all Factory
		// Manager initialization is complete
		registerJNDIContextManager();
		// register the JNDIProviderAdmin interface, used by OSGi-aware
		// context implementations to resolve JNDI references
		registerJNDIProviderAdmin();
	}

	

	/*
	 * Allow the Builder implementation to clean up any
	 * ServiceListener/ServiceTracker instances.
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		m_logger.info("Shutting down JNDI Factory Manager Bundle");
		
		Iterator iterator = m_listOfServiceRegistrations.iterator();
		while(iterator.hasNext()) {
			ServiceRegistration serviceRegistration = 
				(ServiceRegistration)iterator.next();
			serviceRegistration.unregister();
		}
	}

	/**
	 * Registers the OSGi URL Context Factory.
	 * 
	 */
	private void registerOSGiURLContextFactory() {
		Hashtable serviceProperties = new Hashtable();
		serviceProperties.put(JNDIConstants.JNDI_URLSCHEME, OSGI_URL_SCHEME);

		ServiceRegistration serviceRegistration = 
			m_bundleContext.registerService(ObjectFactory.class.getName(), 
										    new OSGiURLContextFactoryServiceFactory(), 
										    serviceProperties);
		m_listOfServiceRegistrations.add(serviceRegistration);
	}
	
	
	/**
	 * Registers the InitialContextFactoryBuilder implementation that 
	 * is responsible for loading the JDK-defined providers that must be
	 * loaded from the boot classpath.  
	 * 
	 */
	private void registerDefaultRuntimeBuilder() {
		ServiceRegistration serviceRegistration = 
			m_bundleContext.registerService(InitialContextFactoryBuilder.class.getName(), 
					                        new DefaultRuntimeInitialContextFactoryBuilder(), 
					                        null);
		m_listOfServiceRegistrations.add(serviceRegistration);
	}
	
	
	private void registerJNDIContextManager() {
		ServiceRegistration serviceRegistration = 
			m_bundleContext.registerService(JNDIContextManager.class.getName(),
					                        new JNDIContextManagerServiceFactoryImpl(),
					                        null);
		m_listOfServiceRegistrations.add(serviceRegistration);
	}
	

	private void registerJNDIProviderAdmin() {
		ServiceRegistration serviceRegistration =  
			m_bundleContext.registerService(JNDIProviderAdmin.class.getName(),
					                        new JNDIProviderAdminServiceFactoryImpl(),
					                        null);
		m_listOfServiceRegistrations.add(serviceRegistration);
	}

}