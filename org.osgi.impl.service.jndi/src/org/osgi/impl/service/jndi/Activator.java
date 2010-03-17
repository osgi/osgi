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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;
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
	
	private static Logger logger = Logger.getLogger(Activator.class.getName());

	private BundleContext						m_bundleContext					= null;
	private final List                          m_listOfServiceRegistrations = new LinkedList();

	private CloseableJNDIProviderAdmin	m_jndiProviderAdminService;
	private JNDIContextManagerServiceFactoryImpl m_jndiContextAdminServiceFactory;
	
	/*
	 * Create the Factory Manager's builder implementation, and register it with
	 * the JNDI NamingManager.
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		logger.info("Initializing JNDI Factory Manager Bundle");
		
		m_bundleContext = context;

		// register static singletons with the JNDI framework
		logger.info("Installing JNDI Static Singletons");
		registerInitialContextFactoryBuilderSingleton();
		registerObjectFactoryBuilderSingleton();

		logger.info("Registering URL Context Factory for 'osgi' URL scheme");
		registerOSGiURLContextFactory();
		
		logger.info("Registering Default Runtime Builder for JRE-provided factories");
		registerDefaultRuntimeBuilder();
		
		logger.info("Registering JNDIContextManager service");
		// register the JNDIContextManager service once all Factory
		// Manager initialization is complete
		registerJNDIContextManager();
		
		logger.info("Registering JNDIProviderAdmin service");
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
		logger.info("Shutting down JNDI Factory Manager Bundle");
		
		// close all known Contexts associated with the JNDIContextManager service
		m_jndiContextAdminServiceFactory.closeAll();
		
		// close the JNDIProviderAdmin service
		m_jndiProviderAdminService.close();

		// unregister all the JNDI services registered by this Activator
		Iterator iterator = m_listOfServiceRegistrations.iterator();
		while(iterator.hasNext()) {
			ServiceRegistration serviceRegistration = 
				(ServiceRegistration)iterator.next();
			serviceRegistration.unregister();
		}
	}


	/**
	 * Registers the InitialContextFactoryBuilder static singleton
	 * @throws NamingException on any error that occurs during the setting
	 *         of the builder.  
	 */
	private static void registerInitialContextFactoryBuilderSingleton() throws NamingException {
		try {
			NamingManager.setInitialContextFactoryBuilder(new TraditionalInitialContextFactoryBuilder());
		}
		catch (IllegalStateException illegalStateException) {
			logger.log(Level.SEVERE, 
			           "JNDI Implementation cannot set the InitialContextFactoryBuilder - another builder was already installed",
			           illegalStateException);
			NamingException namingException = 
				new NamingException("Error occurred while attempting to set the IntialContextFactoryBuilder.");
			namingException.setRootCause(illegalStateException);
		} 
		catch(SecurityException securityException) {
			logger.log(Level.SEVERE, 
					   "JNDI Implementation did not have the proper security permissions to install the InitialContextFactoryBuilder",
					   securityException);
			NamingException namingException = 
				new NamingException("Error occurred while attempting to set the IntialContextFactoryBuilder.");
			namingException.setRootCause(securityException);
		}
	}
	
	
	/**
	 * Registers the ObjectFactoryBuilder static singleton
	 * @throws NamingException on any error that occurs during the setting
	 *         of the builder.  
	 */
	private static void registerObjectFactoryBuilderSingleton() throws NamingException {
		try {
			NamingManager.setObjectFactoryBuilder(new TraditionalObjectFactoryBuilder());
		}
		catch (IllegalStateException illegalStateException) {
			logger.log(Level.SEVERE, 
			           "JNDI Implementation cannot set the ObjectFactoryBuilder - another builder was already installed",
			           illegalStateException);
			NamingException namingException = 
				new NamingException("Error occurred while attempting to set the ObjectFactoryBuilder.");
			namingException.setRootCause(illegalStateException);
		} 
		catch(SecurityException securityException) {
			logger.log(Level.SEVERE, 
					   "JNDI Implementation did not have the proper security permissions to install the ObjectFactoryBuilder",
					   securityException);
			NamingException namingException = 
				new NamingException("Error occurred while attempting to set the ObjectFactoryBuilder.");
			namingException.setRootCause(securityException);
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
		m_jndiContextAdminServiceFactory = 
			new JNDIContextManagerServiceFactoryImpl(m_bundleContext);
		ServiceRegistration serviceRegistration = 
			m_bundleContext.registerService(JNDIContextManager.class.getName(),
											m_jndiContextAdminServiceFactory,
					                        null);
		m_listOfServiceRegistrations.add(serviceRegistration);
	}
	

	private void registerJNDIProviderAdmin() {
		m_jndiProviderAdminService = 
			new SecurityAwareJNDIProviderAdminImpl(new JNDIProviderAdminImpl(m_bundleContext));
		
		
		ServiceRegistration serviceRegistration =  
			m_bundleContext.registerService(JNDIProviderAdmin.class.getName(),
					                        m_jndiProviderAdminService,
					                        null);
		m_listOfServiceRegistrations.add(serviceRegistration);
	}

}