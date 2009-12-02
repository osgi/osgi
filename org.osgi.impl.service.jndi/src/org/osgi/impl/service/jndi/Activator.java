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
import java.util.logging.Logger;

import javax.naming.spi.NamingManager;
import javax.naming.spi.ObjectFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jndi.JNDIContextAdmin;
import org.osgi.service.jndi.JNDIContextManager;
import org.osgi.service.jndi.JndiConstants;

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
	private OSGiInitialContextFactoryBuilder	m_builder						= null;
	private ServiceRegistration					m_osgiUrlFactoryRegistration	= null;
	private ServiceRegistration					m_contextManagerRegistration	= null;
	private ServiceRegistration                 m_contextAdminRegistration      = null;

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
		m_builder = new OSGiInitialContextFactoryBuilder(m_bundleContext);

		// register with the JNDI framework
		m_logger.info("Installing Factory Manager as a JNDI Builder");
		NamingManager.setInitialContextFactoryBuilder(m_builder);
		NamingManager.setObjectFactoryBuilder(m_builder);

		m_logger.info("Registering URL Context Factory for 'osgi' URL scheme");
		registerOSGiURLContextFactory();
		
		m_logger.info("Registering JNDIContextManager service");
		// register the JNDIContextManager service once all Factory
		// Manager initialization is complete
		registerJNDIContextManager();
		// register the JNDIContextAdmin interface, used by OSGi-aware
		// context implementations to resolve JNDI references
		registerJNDIContextAdmin();
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
		if (m_builder != null) {
			m_builder.close();
		}

		if (m_osgiUrlFactoryRegistration != null) {
			m_osgiUrlFactoryRegistration.unregister();
		}
		
		// unregister the JNDIContextManager service, so that 
		// other services can be made aware that the JNDI bundle is shutting
		// down
		if (m_contextManagerRegistration != null) {
			m_contextManagerRegistration.unregister();
		}
		
		if (m_contextAdminRegistration != null) {
			m_contextAdminRegistration.unregister();
		}
	}

	/**
	 * Registers the OSGi URL Context Factory.
	 * 
	 */
	private void registerOSGiURLContextFactory() {
		Hashtable serviceProperties = new Hashtable();
		serviceProperties.put(JndiConstants.JNDI_URLSCHEME,
				              OSGI_URL_SCHEME);

		m_osgiUrlFactoryRegistration = 
			m_bundleContext.registerService(ObjectFactory.class.getName(), 
										    new OSGiURLContextFactory(m_bundleContext), 
										    serviceProperties);
	}
	
	
	private void registerJNDIContextManager() {
		m_contextManagerRegistration = 
			m_bundleContext.registerService(JNDIContextManager.class.getName(),
					                        new JNDIContextManagerServiceFactoryImpl(),
					                        null);
	}
	

	private void registerJNDIContextAdmin() {
		m_contextAdminRegistration =  
			m_bundleContext.registerService(JNDIContextAdmin.class.getName(),
					                        new JNDIContextAdminServiceFactoryImpl(m_builder),
					                        null);
	}

}