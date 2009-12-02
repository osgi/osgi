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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.naming.spi.DirObjectFactory;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ObjectFactoryBuilder;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jndi.JndiConstants;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This class represents the main integration point between the JNDI framework
 * provided by the JDK and OSGi.
 * 
 * This builder class is responsible for providing instances of
 * InitialContextFactory and ObjectFactory to the JDK's NamingManager upon
 * request. The builder uses the OSGi service registry to locate JNDI providers.
 * 
 */
class OSGiInitialContextFactoryBuilder implements
		InitialContextFactoryBuilder, ObjectFactoryBuilder, FactoryManager {

	private static Logger m_logger = 
		Logger.getLogger(OSGiInitialContextFactoryBuilder.class.getName());
	

	private static final String	JNDI_PROPERTIES_FILE_NAME	= "jndi.properties";

	private final static String NO_CONTEXT_FACTORIES_MSG = "No JNDI implementations available";
	
	private final BundleContext	m_bundleContext;

	private ServiceTracker		m_contextFactoryServiceTracker			= null;
	private ServiceTracker		m_contextFactoryBuilderServiceTracker	= null;
	private ServiceTracker		m_objectFactoryServiceTracker			= null;
	private ServiceTracker		m_objectFactoryBuilderServiceTracker	= null;
	private ServiceTracker		m_urlContextFactoryServiceTracker		= null;
	private ServiceTracker      m_dirObjectFactoryServiceTracker        = null;

	public OSGiInitialContextFactoryBuilder(BundleContext bundleContext) {
		m_bundleContext = bundleContext;
		createServiceTrackers(m_bundleContext);
	}

	/**
	 * This builder implementation uses the OSGi service registry to find
	 * matching JNDI service providers.
	 */
	public InitialContextFactory createInitialContextFactory(
			Hashtable environment) throws NamingException {
		// check for valid tracker setup
		if (m_contextFactoryServiceTracker == null) {
			throw new NoInitialContextException(NO_CONTEXT_FACTORIES_MSG);
		}
		
		return getInitialContextFactoryInternal(getCombinedEnvironment(environment));
	}

	private InitialContextFactory getInitialContextFactoryInternal(Hashtable environment) throws NoInitialContextException {
		if (environment.get(Context.INITIAL_CONTEXT_FACTORY) != null) {
			final String initialContextFactoryName = 
				(String) environment.get(Context.INITIAL_CONTEXT_FACTORY);
			Object factory = obtainFactoryService(initialContextFactoryName,
					m_contextFactoryServiceTracker);
			if (factory != null) {
				return new InitialContextFactoryWrapper(
						(InitialContextFactory) factory, this);
			}
			else {
				// query known builders to see if any can support the 
				// given environment
				InitialContextFactory contextFactory = getContextFactoryFromBuilder(environment);
				if (contextFactory != null) {
					return new InitialContextFactoryWrapper(contextFactory,
							this);
				}
			}
			
			// attempt to load this provider from the system classpath
			try {
				Class clazz = 
					getClass().getClassLoader().loadClass(initialContextFactoryName);
				return (InitialContextFactory)clazz.newInstance();
			}
			catch (Exception e) {
				m_logger.log(Level.FINEST, "Error while trying to load system-level JNDI provider", e);
			}
			
			throw new NoInitialContextException(NO_CONTEXT_FACTORIES_MSG); 
		}
		else {
			// query known builders to see if any can support
			// the given environment
			InitialContextFactory contextFactory = getContextFactoryFromBuilder(environment);
			if (contextFactory != null) {
				return new InitialContextFactoryWrapper(contextFactory, this);
			}

			// return the first context factory if one exists
			InitialContextFactory defaultContextFactory = 
				(InitialContextFactory) m_contextFactoryServiceTracker.getService();

			if (defaultContextFactory == null) {
				throw new NoInitialContextException(NO_CONTEXT_FACTORIES_MSG);
			}
			else {
				return new InitialContextFactoryWrapper(defaultContextFactory, this);
			}
		}
	}

	


	/**
	 * This Builder implementation uses the OSGi Service registry to find
	 * matching JNDI service providers for resolving references.
	 * 
	 */
	public ObjectFactory createObjectFactory(Object obj, Hashtable environment)
			throws NamingException {
		if (m_objectFactoryServiceTracker == null) {
			throw new NoInitialContextException("No Object factories available");
		}

		final Object objToResolve = getObjectToResolve(obj);
		if (objToResolve instanceof Reference) {
			final Reference reference = (Reference) objToResolve;
			if (reference.getFactoryClassName() != null) {
				// if a factory class name is specified, look through the list
				// of known ObjectFactories, and try to find a service published
				// that also supports the custom interface.
				Object factory = 
					obtainFactoryService(reference.getFactoryClassName(), m_objectFactoryServiceTracker);
				if (factory != null) {
					return (ObjectFactory) factory;
				} else {
					// search for InitialContextFactoryBuilder implementations that
					// are published as OSGi services
					ObjectFactory objectFactory = 
						getObjectFactoryFromBuilder(environment, reference);
					if(objectFactory != null) {
						return objectFactory;
					}
				}
			}
			else {
				ObjectFactory urlContextFactory = 
					getURLContextFactoryFromReference(reference);
				if (urlContextFactory != null) {
					return urlContextFactory;
				}
				
				// search for InitialContextFactoryBuilder implementations that
				// are published as OSGi services
				ObjectFactory objectFactory = getObjectFactoryFromBuilder(environment, reference);
				if(objectFactory != null) {
					return objectFactory;
				}
			}
		}

		// if no matching ObjectFactory services exist,
		// return a specialized ObjectFactory implementation that
		// merely returns the reference passed in.  
		// This allows the Factory Manager to more closely comply with the
		// behavior specified in the javadoc for NamingManger.getObjectInstance()
		return new ReturnReferenceInfoObjectFactory(obj);
	}

		
	/**
	 * Returns a URL Context Factory implementation that is published to support
	 * the provided URL scheme.
	 * 
	 * @param urlScheme the URL scheme that the URL context factory must support
	 * @return a javax.naming.spi.ObjectFactory instance that supports the
	 *         requested URL scheme, or null if no matching factory was found
	 */
	public ObjectFactory getURLContextFactory(String urlScheme) {
		if (m_urlContextFactoryServiceTracker.getServiceReferences() != null) {
			ServiceReference[] serviceReferences = sortServiceReferences(m_urlContextFactoryServiceTracker);
			for (int i = 0; i < serviceReferences.length; i++) {
				ServiceReference serviceReference = serviceReferences[i];
				if (serviceReference.getProperty(JndiConstants.JNDI_URLSCHEME)
						.equals(urlScheme)) {
					return (ObjectFactory) m_bundleContext
							.getService(serviceReference);
				}
			}
		}
		return null;
	}

	/**
	 * Simple close method to close the ServiceTracker objects currently in use
	 * by the FactoryManager.
	 */
	protected void close() {
		m_contextFactoryBuilderServiceTracker.close();
		m_contextFactoryServiceTracker.close();
		m_objectFactoryServiceTracker.close();
		m_objectFactoryBuilderServiceTracker.close();
		m_urlContextFactoryServiceTracker.close();
		m_dirObjectFactoryServiceTracker.close();
	}

	private void createServiceTrackers(BundleContext bundleContext) {
		// create trackers
		m_contextFactoryServiceTracker = createServiceTracker(bundleContext,
				InitialContextFactory.class.getName());

		m_contextFactoryBuilderServiceTracker = createServiceTracker(
				bundleContext, InitialContextFactoryBuilder.class.getName());

		m_objectFactoryServiceTracker = new ServiceTracker(bundleContext,
				ObjectFactory.class.getName(), null) {
			public Object addingService(ServiceReference serviceReference) {
				if (serviceReference.getProperty(JndiConstants.JNDI_URLSCHEME) == null) {
					return super.addingService(serviceReference);
				}

				return null;
			}
		};
		
		m_dirObjectFactoryServiceTracker = new ServiceTracker(bundleContext,
				DirObjectFactory.class.getName(), null) {
			public Object addingService(ServiceReference serviceReference) {
				if (serviceReference.getProperty(JndiConstants.JNDI_URLSCHEME) == null) {
					return super.addingService(serviceReference);
				}

				return null;
			}
		};

		m_objectFactoryBuilderServiceTracker = createServiceTracker(
				bundleContext, ObjectFactoryBuilder.class.getName());

		m_urlContextFactoryServiceTracker = new ServiceTracker(bundleContext,
				ObjectFactory.class.getName(), null) {
			public Object addingService(ServiceReference serviceReference) {
				if (serviceReference.getProperty(JndiConstants.JNDI_URLSCHEME) != null) {
					return super.addingService(serviceReference);
				}

				return null;
			}
		};

		// open trackers
		m_contextFactoryServiceTracker.open();
		m_contextFactoryBuilderServiceTracker.open();

		m_objectFactoryServiceTracker.open();
		m_objectFactoryBuilderServiceTracker.open();
		m_dirObjectFactoryServiceTracker.open();

		m_urlContextFactoryServiceTracker.open();
	}

	private Object obtainFactoryService(String factoryServiceInterface,
			ServiceTracker serviceTracker) {
		ServiceReference[] serviceReferences = sortServiceReferences(serviceTracker);
		for (int i = 0; i < serviceReferences.length; i++) {
			ServiceReference serviceReference = serviceReferences[i];
			String[] serviceInterfaces = (String[]) serviceReference
					.getProperty(Constants.OBJECTCLASS);
			List interfaceList = Arrays.asList(serviceInterfaces);
			if (interfaceList.contains(factoryServiceInterface)) {
				return m_bundleContext.getService(serviceReference);
			}
		}

		return null;
	}

	/**
	 * Convenience method to query each known InitialContextFactoryBuilder, and to return 
	 * the first non-null factory produced by a builder.
	 * 
	 * The first builder to return a non-null result is 
	 * given precedence as per Section 5.2.1.1 of RFC 142. 
	 * 
	 * @param environment the JNDI environment
	 * @return an InitialContextFactory instance that can support this request 
	 *         or null if no match can be found. 
	 */
	private InitialContextFactory getContextFactoryFromBuilder(
			Hashtable environment) {
		if (m_contextFactoryBuilderServiceTracker.getServiceReferences() != null) {
			final ServiceReference[] serviceReferences = sortServiceReferences(m_contextFactoryBuilderServiceTracker);
			for (int i = 0; i < serviceReferences.length; i++) {
				ServiceReference serviceReference = serviceReferences[i];
				InitialContextFactoryBuilder builder = (InitialContextFactoryBuilder) m_bundleContext
						.getService(serviceReference);
				try {
					InitialContextFactory contextFactory = builder
							.createInitialContextFactory(environment);
					// the first builder to return a non-null result is
					// given precedence as per Section 5.2.1.1 of RFC
					// 142
					if (contextFactory != null) {
						return contextFactory;
					}
				}
				catch (NamingException namingException) {
					// catch exception, allow iteration to continue
				}
			}

		}

		return null;
	}
	
	
	/**
	 * Convenience method to query each known ObjectFactoryBuilder, and to return 
	 * the first non-null factory produced by a builder.
	 * 
	 * The first builder to return a non-null result is 
	 * given precedence as per Section 5.2.2.1 of RFC 142. 
	 * 
	 * @param environment the JNDI environment
	 * @param reference the Reference to resolve
	 * @return an ObjectFactory instance that matches this Reference, 
	 *         or null if no match can be found. 
	 */
	private ObjectFactory getObjectFactoryFromBuilder(Hashtable environment, Reference reference) {
		if (m_objectFactoryBuilderServiceTracker.getServiceReferences() != null) {
			final ServiceReference[] serviceReferences = sortServiceReferences(m_objectFactoryBuilderServiceTracker);
			for (int i = 0; i < serviceReferences.length; i++) {
				ServiceReference serviceReference = serviceReferences[i];
				ObjectFactoryBuilder builder = (ObjectFactoryBuilder) m_bundleContext
						.getService(serviceReference);
				try {
					ObjectFactory factory = 
						builder.createObjectFactory(reference, environment);
		
					if (factory != null) {
						return factory;
					}
				}
				catch (NamingException namingException) {
					// catch exception, allow iteration to continue
				}
			}
		}
		
		return null;
	}
	
	
	/**
	 * Examines a Reference to determine if a StringRefAddr of type 
	 * 'URL' is associated with the Reference.  If so, the Factory Manager
	 * will attempt to locate a URL context factory that can be used to 
	 * resolve the reference.  
	 * 
	 * @param reference to be resolved
	 * @return a URL Context Factory (of type ObjectFactory) that can 
	 *         resolve the given reference, or null if no matching URL
	 *         context factory was found. 
	 */
	private ObjectFactory getURLContextFactoryFromReference(final Reference reference) {
		Enumeration refAddresses = reference.getAll();
		while(refAddresses.hasMoreElements()) {
			RefAddr address = (RefAddr)refAddresses.nextElement();
			if((address instanceof StringRefAddr) && (address.getType().equals("URL"))) {
				String urlContent = (String)address.getContent();
				try {
					URI uri = new URI(urlContent);
					ObjectFactory objectFactory = 
						getURLContextFactory(uri.getScheme());
					if(objectFactory != null) {
						return objectFactory;
					}
				}
				catch (URISyntaxException e) {
					m_logger.log(Level.FINEST, 
							     "Exception thrown while parsing URL.  This URL reference address will be skipped.",
							     e);
	
				}
				catch (Exception e) {
					m_logger.log(Level.FINEST, 
						     "Exception thrown while parsing URL.  This URL reference address will be skipped.",
						     e);
				}
			}
		}
		
		return null;
	}
	
	
	private Object getObjectToResolve(Object obj) throws NamingException {
		Object objToResolve;
		if(obj instanceof Referenceable) {
			// obtain the Reference before proceeding
			Referenceable referenceable = (Referenceable)obj;
			objToResolve = referenceable.getReference();
		} else {
			// this object is either a Reference or another type
			objToResolve = obj;
		}
		return objToResolve;
	}
	
	
	/**
	 * Utility method for creating the set of environment properties from the 
	 * following sources (in order of priority):
	 * 
	 * 1. User-defined properties
	 * 2. Properties defined in a jndi.properties file in the caller's archive (if it exists)
	 * 
	 * 
	 * @param userEnvironment original environment passed in by the caller 
	 * @return a Hashtable representing the combined JNDI environment for this context
	 */
	private Hashtable getCombinedEnvironment(Hashtable userEnvironment) {
		// create a copy of the user-defined environment settings
		Hashtable combinedEnvironment = new Hashtable();
		combinedEnvironment.putAll(userEnvironment);
		
		// obtain environment properties defined in the calling bundle's archive
		Properties fileDefinedEnvironment = 
			getFileDefinedJndiProperties();
		if(fileDefinedEnvironment != null) {
			Enumeration keyEnum = fileDefinedEnvironment.keys();
			while(keyEnum.hasMoreElements()) {
				final String key = (String) keyEnum.nextElement();
				if(!combinedEnvironment.containsKey(key)) {
					// add the file-defined property to the combined environment
					// only add keys that do not exist, since the user-defined
					// environment takes precedence over the file-defined
					combinedEnvironment.put(key, fileDefinedEnvironment.get(key));
				}
			}
		}
		
		return combinedEnvironment;
	}

	
	/**
	 * Checks the thread context classloader in order to search for a 
	 * jndi.properties file.  
	 * 
	 * If the classloader supports BundleReference, this loader
	 * represents a client bundle.
	 * 
	 * Check the client's bundle for a jndi.properties file in the archive
	 * 
	 * @return a Properties instance that contains the properties defined in
	 *         a jndi.properties file for the caller's archive, or null if none exists
	 */
	private static Properties getFileDefinedJndiProperties() {
		ClassLoader threadContextClassloader = Thread.currentThread().getContextClassLoader();
		if ((threadContextClassloader != null)
				&& (threadContextClassloader instanceof BundleReference)) {
			BundleReference bundleRef = (BundleReference) threadContextClassloader;
			if (bundleRef.getBundle() != null) {
				Bundle bundle = bundleRef.getBundle();
				try {
					URL propertiesURL = bundle.getResource(JNDI_PROPERTIES_FILE_NAME);
					if (propertiesURL != null) {
						File jndiPropertiesFile = (File) propertiesURL.getContent();
						try {
							FileInputStream userDefinedPropertiesStream = 
								new FileInputStream(jndiPropertiesFile);
							Properties fileDefinedJndiProperties = new Properties();
							fileDefinedJndiProperties.load(userDefinedPropertiesStream);
							return fileDefinedJndiProperties; 
						}
						catch (FileNotFoundException e) {
							// this exception should never occur, since the File has
							// already been tested to be available
							m_logger.log(Level.FINEST, "Exception encountered while trying to locate a jndi.properties file.", e);
						}
					}
				}
				catch (IOException e) {
					m_logger.log(Level.FINEST, 
							     "Exception encounted while trying to load a jndi.properties file",
							     e);
				}
			}
		}
	
		return null;
	}



	/**
	 * Convenience method for creating ServiceTracker instances.
	 * 
	 * This method only creates tracker instances for a single interface type,
	 * and does not support customizing the tracker.
	 * 
	 * @param bundleContext the BundleContext to use to create the tracker
	 * @param serviceInterface the service interface name to use for this
	 *        tracker
	 * 
	 * @return a ServiceTracker instance for the given interface
	 */
	private static ServiceTracker createServiceTracker(
			BundleContext bundleContext, String serviceInterface) {
		return new ServiceTracker(bundleContext, serviceInterface, null);
	}

	/**
	 * Utility method to sort an array of ServiceReferences using the service
	 * ranking (if specified).
	 * 
	 * This utility should follow any service ranking rules already defined in
	 * the OSGi spec.
	 * 
	 * @param serviceTracker tracker to use to provide the initial array to sort
	 * @return sorted array of ServiceReferences, or a zero-length array if no
	 *         matching services were found
	 */
	private static ServiceReference[] sortServiceReferences(
			ServiceTracker serviceTracker) {
		final ServiceReference[] serviceReferences = serviceTracker
				.getServiceReferences();
		if (serviceReferences == null) {
			return new ServiceReference[0];
		}

		Arrays.sort(serviceReferences, new Comparator() {
			public int compare(Object objectOne, Object objectTwo) {
				ServiceReference serviceReferenceOne = (ServiceReference) objectOne;
				ServiceReference serviceReferenceTwo = (ServiceReference) objectTwo;
				return serviceReferenceTwo.compareTo(serviceReferenceOne);
			}
		});

		return serviceReferences;
	}
	
	
	private static final class ReturnReferenceInfoObjectFactory implements ObjectFactory {
		private final Object m_refInfo;
		
		public ReturnReferenceInfoObjectFactory(Object refInfo) {
			m_refInfo = refInfo;
		}
		
		public Object getObjectInstance(Object var0, Name var1,
				Context var2, Hashtable var3) throws Exception {
				return m_refInfo;
		}
	}
	
	
}
