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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.Reference;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ObjectFactoryBuilder;

import org.osgi.framework.BundleContext;
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
public class OSGiInitialContextFactoryBuilder implements
		InitialContextFactoryBuilder, ObjectFactoryBuilder, FactoryManager {
	private final BundleContext	m_bundleContext;

	private ServiceTracker		m_contextFactoryServiceTracker			= null;
	private ServiceTracker		m_contextFactoryBuilderServiceTracker	= null;
	private ServiceTracker		m_objectFactoryServiceTracker			= null;
	private ServiceTracker		m_objectFactoryBuilderServiceTracker	= null;
	private ServiceTracker		m_urlContextFactoryServiceTracker		= null;

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
			throw new NoInitialContextException(
					"No JNDI implementations available");
		}

		if (environment.get(Context.INITIAL_CONTEXT_FACTORY) != null) {
			final String initialContextFactoryName = (String) environment
					.get(Context.INITIAL_CONTEXT_FACTORY);
			Object factory = obtainFactoryService(initialContextFactoryName,
					m_contextFactoryServiceTracker);
			if (factory != null) {
				return new InitialContextFactoryWrapper(
						(InitialContextFactory) factory, this);
			}
			else {
				InitialContextFactory contextFactory = getContextFactoryFromBuilder(environment);
				if (contextFactory != null) {
					return new InitialContextFactoryWrapper(contextFactory,
							this);
				}
			}

			// services that are registered under a specific classname
			throw new NamingException(
					"No Context implementation available.  Specifying a context factory is not supported yet");
		}
		else {
			// query known builders to see if any can support
			// the given environment
			InitialContextFactory contextFactory = getContextFactoryFromBuilder(environment);
			if (contextFactory != null) {
				return new InitialContextFactoryWrapper(contextFactory, this);
			}

			// return the first context factory if one exists
			InitialContextFactory defaultContextFactory = (InitialContextFactory) m_contextFactoryServiceTracker
					.getService();

			if (defaultContextFactory == null) {
				throw new NoInitialContextException(
						"No JNDI implementations available");
			}
			else {
				return new InitialContextFactoryWrapper(defaultContextFactory,
						this);
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

		if (obj instanceof Reference) {
			Reference reference = (Reference) obj;
			if (reference.getFactoryClassName() != null) {
				// if a factory class name is specified, look through the list
				// of known ObjectFactories, and try to find a service published
				// that also supports the custom interface.
				Object factory = obtainFactoryService(reference
						.getFactoryClassName(), m_objectFactoryServiceTracker);
				if (factory != null) {
					return (ObjectFactory) factory;
				}

				// if a factory classname was specified, and no matching
				// services exist, this should be treated as an
				// error condition
				throw new NamingException(
						"No matching ObjectFactory implementations could be found for this reference binding");
			}
			else {
				// search for InitialContextFactoryBuilder implementations that
				// are published as OSGi services
				if (m_objectFactoryBuilderServiceTracker.getServiceReferences() != null) {
					final ServiceReference[] serviceReferences = sortServiceReferences(m_objectFactoryBuilderServiceTracker);
					for (int i = 0; i < serviceReferences.length; i++) {
						ServiceReference serviceReference = serviceReferences[i];
						ObjectFactoryBuilder builder = (ObjectFactoryBuilder) m_bundleContext
								.getService(serviceReference);
						try {
							ObjectFactory factory = builder
									.createObjectFactory(reference, environment);
							// the first builder to return a non-null result is
							// given precedence as per Section 5.2.1.1 of RFC
							// 142
							if (factory != null) {
								return factory;
							}
						}
						catch (NamingException namingException) {
							// catch exception, allow iteration to continue
						}
					}
				}
			}
		}

		ObjectFactory objectFactory = (ObjectFactory) m_objectFactoryServiceTracker
				.getService();
		if (objectFactory == null) {
			throw new NoInitialContextException(
					"No ObjectFactory implementations available");
		}
		else {
			return objectFactory;
		}
	}

	/**
	 * Returns a URL Context Factory implementation that is published to support
	 * the provided URL scheme.
	 * 
	 * @param urlScheme the URL scheme that the URL context factory must support
	 * @return a javax.naming.spi.ObjectFactory instance that supports the
	 *         requested URL scheme
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
}
