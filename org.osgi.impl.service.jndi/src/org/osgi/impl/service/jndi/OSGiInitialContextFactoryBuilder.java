/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.impl.service.jndi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import javax.naming.directory.Attributes;
import javax.naming.spi.DirObjectFactory;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ObjectFactoryBuilder;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jndi.JNDIConstants;
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

	private static Logger logger = 
		Logger.getLogger(OSGiInitialContextFactoryBuilder.class.getName());
	

	private static final String	JNDI_PROPERTIES_FILE_NAME	= "jndi.properties";

	private final static String NO_CONTEXT_FACTORIES_MSG = "No JNDI implementations available";
	
	/* calling JNDI Client's BundleContext */
	private final BundleContext	m_callerBundleContext;
	
	/* JNDI implementation bundle's BundleContext */
	final BundleContext																	m_implBundleContext;

	private ServiceTracker<InitialContextFactory,InitialContextFactory>	m_contextFactoryServiceTracker			= null;
	private ServiceTracker<InitialContextFactoryBuilder,InitialContextFactoryBuilder>	m_contextFactoryBuilderServiceTracker	= null;
	ServiceTracker<ObjectFactory,ObjectFactory>											m_objectFactoryServiceTracker			= null;
	private ServiceTracker<ObjectFactoryBuilder,ObjectFactoryBuilder>					m_objectFactoryBuilderServiceTracker	= null;
	private ServiceTracker<ObjectFactory,ObjectFactory>									m_urlContextFactoryServiceTracker		= null;
	ServiceTracker<DirObjectFactory,DirObjectFactory>									m_dirObjectFactoryServiceTracker		= null;

	
	/* 
	 * Map of OSGi services to a List of Contexts created by that service.  
	 * Each service services as a key to a list of Context implementations.  
	 */
	final Map<Object,List<Context>>														m_mapOfServicesToContexts				= Collections
			.synchronizedMap(new HashMap<>());
	

	public OSGiInitialContextFactoryBuilder(BundleContext callerBundleContext, BundleContext implBundleContext) {
		m_callerBundleContext = callerBundleContext;
		m_implBundleContext = implBundleContext;
		
		try {
			// create the service trackers inside a doPrivileged() block
			// since this code is the only interaction with a BundleContext
			// context not covered by the security-aware wrapper interfaces
			SecurityUtils.invokePrivilegedActionNoReturn(
					new PrivilegedExceptionAction<Void>() {
				@Override
						public Void run() throws Exception {
					createServiceTrackers(m_implBundleContext);
					return null;
				}
			});
		} catch (Exception e) {
			logger.log(Level.FINE, 
					   "Exception occurred while creating ServiceTracker implementations for JNDI Provider Services",
					   e);
		}
	}

	/**
	 * This builder implementation uses the OSGi service registry to find
	 * matching JNDI service providers.
	 */
	@Override
	public InitialContextFactory createInitialContextFactory(
			Hashtable< ? , ? > environment) throws NamingException {
		// check for valid tracker setup
		if (m_contextFactoryServiceTracker == null) {
			throw new NoInitialContextException(NO_CONTEXT_FACTORIES_MSG);
		}
		
		return getInitialContextFactoryInternal(getCombinedEnvironment(environment));
	}

	private InitialContextFactory getInitialContextFactoryInternal(
			Hashtable< ? , ? > environment) throws NoInitialContextException {
		if (environment.get(Context.INITIAL_CONTEXT_FACTORY) != null) {
			final String initialContextFactoryName = 
				(String) environment.get(Context.INITIAL_CONTEXT_FACTORY);
			Object factory = 
				obtainFactoryService(initialContextFactoryName, m_contextFactoryServiceTracker);
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
			
			throw new NoInitialContextException(NO_CONTEXT_FACTORIES_MSG); 
		}
		else {
			// query known builders to see if any can support
			// the given environment
			InitialContextFactory contextFactory = getContextFactoryFromBuilder(environment);
			if (contextFactory != null) {
				return new InitialContextFactoryWrapper(contextFactory, this);
			}

			// return the first valid context factory if one exists
			try {
				InitialContextFactory defaultContextFactory = getDefaultInitialContextFactory(environment);
				if (defaultContextFactory == null) {
					// return a wrapper to support URL-based lookups only
					return new InitialContextFactoryWrapper(new DefaultInitialContextFactory(), this);
				}
				else {
					return new InitialContextFactoryWrapper(defaultContextFactory, this);
				}
			}
			catch (NamingException namingException) {
				NoInitialContextException noInitialContextException = 
					new NoInitialContextException("Exception occured while iterating over the default InitialContextFactory services");
				noInitialContextException.setRootCause(namingException);
				throw noInitialContextException;
			}

			
		}
	}


	/**
	 * This Builder implementation uses the OSGi Service registry to find
	 * matching JNDI service providers for resolving references.
	 * 
	 */
	@Override
	public ObjectFactory createObjectFactory(Object obj,
			Hashtable< ? , ? > environment) throws NamingException {
		if (m_objectFactoryServiceTracker == null) {
			throw new NoInitialContextException("No Object factories available");
		}

		return new ReturnReferenceInfoObjectFactory(createInnerObjectFactory(obj));
	}
	
	public DirObjectFactory getDirObjectFactory(Object obj,
			Hashtable< ? , ? > environment) throws NamingException {
		if (m_dirObjectFactoryServiceTracker == null) {
			throw new NamingException("No DirObjectFactories available");
		}
		
		return new ReturnReferenceInfoDirObjectFactory(createInnerDirObjectFactory(obj)) ;
	}

	

		
	/**
	 * Returns a URL Context Factory implementation that is published to support
	 * the provided URL scheme.
	 * 
	 * @param urlScheme the URL scheme that the URL context factory must support
	 * @return a javax.naming.spi.ObjectFactory instance that supports the
	 *         requested URL scheme, or null if no matching factory was found
	 */
	@Override
	public ObjectFactory getURLContextFactory(String urlScheme) {
		if (m_urlContextFactoryServiceTracker.getServiceReferences() != null) {
			ServiceReference<ObjectFactory>[] serviceReferences = ServiceUtils
					.sortServiceTrackerReferences(m_urlContextFactoryServiceTracker);
			for (int i = 0; i < serviceReferences.length; i++) {
				ServiceReference<ObjectFactory> serviceReference = serviceReferences[i];
				if (serviceReference.getProperty(JNDIConstants.JNDI_URLSCHEME).equals(urlScheme)) {
					return m_callerBundleContext.getService(serviceReference);
				}
			}
		}
		return null;
	}
	
	@Override
	public void associateFactoryService(Object factory, Context createdContext) {
		if(m_mapOfServicesToContexts.containsKey(factory)) {
			List<Context> listOfContexts =
				m_mapOfServicesToContexts.get(factory);
			listOfContexts.add(createdContext);
			m_mapOfServicesToContexts.put(factory, listOfContexts);
		} else {
			List<Context> listOfContexts = new LinkedList<>();
			listOfContexts.add(createdContext);
			m_mapOfServicesToContexts.put(factory, listOfContexts);
		}
		
	}

	@Override
	public boolean isFactoryServiceActive(Object factory) {
		return m_mapOfServicesToContexts.containsKey(factory);
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

	
	final void createServiceTrackers(BundleContext bundleContext) {
		// create trackers
		m_contextFactoryServiceTracker = 
				new ContextFactoryServiceTracker<>(bundleContext,
						InitialContextFactory.class);

		m_contextFactoryBuilderServiceTracker = 
				new ContextFactoryServiceTracker<>(bundleContext,
						InitialContextFactoryBuilder.class);
		
		m_objectFactoryServiceTracker = 
				new ObjectFactoryServiceTracker<>(bundleContext,
						ObjectFactory.class);
		
		m_dirObjectFactoryServiceTracker = 
				new ObjectFactoryServiceTracker<>(bundleContext,
						DirObjectFactory.class);
		
		m_objectFactoryBuilderServiceTracker = 
				createServiceTracker(bundleContext, ObjectFactoryBuilder.class);

		m_urlContextFactoryServiceTracker = 
				new URLContextFactoryServiceTracker<>(bundleContext,
						ObjectFactory.class);


		// open trackers
		m_contextFactoryServiceTracker.open();
		m_contextFactoryBuilderServiceTracker.open();
		m_objectFactoryServiceTracker.open();
		m_objectFactoryBuilderServiceTracker.open();
		m_dirObjectFactoryServiceTracker.open();
		m_urlContextFactoryServiceTracker.open();
	}

	<T> T obtainFactoryService(String factoryServiceInterface,
			ServiceTracker<T,T> serviceTracker) {
		ServiceReference<T>[] serviceReferences = ServiceUtils
				.sortServiceTrackerReferences(serviceTracker);
		for (int i = 0; i < serviceReferences.length; i++) {
			ServiceReference<T> serviceReference = serviceReferences[i];
			String[] serviceInterfaces = (String[]) serviceReference
					.getProperty(Constants.OBJECTCLASS);
			List<String> interfaceList = Arrays.asList(serviceInterfaces);
			if (interfaceList.contains(factoryServiceInterface)) {
				return m_callerBundleContext.getService(serviceReference);
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
			Hashtable< ? , ? > environment) {
		if (m_contextFactoryBuilderServiceTracker.getServiceReferences() != null) {
			final ServiceReference<InitialContextFactoryBuilder>[] serviceReferences = ServiceUtils
					.sortServiceTrackerReferences(
							m_contextFactoryBuilderServiceTracker);
			for (int i = 0; i < serviceReferences.length; i++) {
				ServiceReference<InitialContextFactoryBuilder> serviceReference = serviceReferences[i];
				InitialContextFactoryBuilder builder = 
					m_callerBundleContext.getService(serviceReference);
				try {
					// if builder is null, then service is not available
					if (builder != null) {
						InitialContextFactory contextFactory = builder
								.createInitialContextFactory(environment);
						// the first builder to return a non-null result is
						// given precedence as per Section 5.2.1.1 of RFC
						// 142
						if (contextFactory != null) {
							return new DefaultBuilderSupportedInitialContextFactory(
									contextFactory, builder);
						}
					}
				}
				catch (NamingException namingException) {
					// catch exception, allow iteration to continue
					logger.log(Level.FINE, 
							     "NamingException occurred while invoking on an InitialContextFactoryBuilder",
							     namingException);
				}
			}

		}

		return null;
	}
	
	
	/**
	 * Convenience method for obtaining the "default" InitialContextFactory.  
	 * This method takes the list of known InitialContextFactory implementations, 
	 * in service ranking order, and attempts to use each to create a JNDI Context
	 * given the environment passed in.  The first service to return a non-null
	 * result is returned.  
	 * 
	 * @param environment the JNDI environment to use when creating the Context
	 * @return the first InitialContextFactory service that can support this environment, 
	 *         or null if no matching service was found. 
	 * @throws NamingException any NamingException thrown by an InitialContextFactory
	 *         service is thrown back to the caller.  
	 */
	private InitialContextFactory getDefaultInitialContextFactory(
			Hashtable< ? , ? > environment) throws NamingException {
		if (m_contextFactoryServiceTracker.getServiceReferences() != null) {
			ServiceReference<InitialContextFactory>[] serviceReferences = ServiceUtils
					.sortServiceTrackerReferences(m_contextFactoryServiceTracker);
			for (int i = 0; i < serviceReferences.length; i++) {
				ServiceReference<InitialContextFactory> serviceReference = serviceReferences[i];
				InitialContextFactory factoryService = 
					m_callerBundleContext.getService(serviceReference);
				if(factoryService.getInitialContext(environment) != null) {
					return factoryService;
				} else {
					m_callerBundleContext.ungetService(serviceReference);
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
	 * @param refInfo the Object to resolve
	 * @return an ObjectFactory instance that matches this Reference, 
	 *         or null if no match can be found. 
	 */
	private ObjectFactory getObjectFactoryFromBuilder(
			Hashtable< ? , ? > environment, Object refInfo) {
		if (m_objectFactoryBuilderServiceTracker.getServiceReferences() != null) {
			final ServiceReference<ObjectFactoryBuilder>[] serviceReferences = ServiceUtils
					.sortServiceTrackerReferences(
							m_objectFactoryBuilderServiceTracker);
			for (int i = 0; i < serviceReferences.length; i++) {
				ServiceReference<ObjectFactoryBuilder> serviceReference = serviceReferences[i];
				ObjectFactoryBuilder builder = m_callerBundleContext
						.getService(serviceReference);
				try {
					ObjectFactory factory = 
						builder.createObjectFactory(refInfo, environment);
		
					if (factory != null) {
						return factory;
					}
				}
				catch (NamingException namingException) {
					// catch exception, allow iteration to continue
					logger.log(Level.FINE, 
						     "NamingException occurred while invoking on an ObjectFactoryBuilder",
						     namingException);
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
	 * @return an object resolved from a URL Context Factory, or null if no URL
	 * 	       Context Factory could resolve the reference.   
	 */
	Object getObjectFromURLContextFactoryFromReference(
			final Reference reference, Hashtable< ? , ? > environment) {
		Enumeration<RefAddr> refAddresses = reference.getAll();
		while(refAddresses.hasMoreElements()) {
			RefAddr address = refAddresses.nextElement();
			if((address instanceof StringRefAddr) && (address.getType().equals("URL"))) {
				String urlContent = (String)address.getContent();
				try {
					URI uri = new URI(urlContent);
					ObjectFactory objectFactory = 
						getURLContextFactory(uri.getScheme());
					if(objectFactory != null) {
						Object objToReturn = objectFactory.getObjectInstance(urlContent, null, null, environment);
						if(objToReturn != null) {
							// return the first object that is constructed from a URL string reference address
							return objToReturn;
						}
					}
				}
				catch (URISyntaxException e) {
					logger.log(Level.FINEST, 
							     "Exception thrown while parsing URL.  This URL reference address will be skipped.",
							     e);
	
				}
				catch (Exception e) {
					logger.log(Level.FINEST, 
						     "Exception thrown while parsing URL.  This URL reference address will be skipped.",
						     e);
				}
			}
		}
		
		return null;
	}
	
	
	Object getObjectToResolve(Object obj) throws NamingException {
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
	
	
	private ObjectFactory createInnerObjectFactory(Object obj) throws NamingException {
		final Object objToResolve = getObjectToResolve(obj);
		if (objToResolve instanceof Reference) {
			final Reference reference = (Reference) objToResolve;
			if (reference.getFactoryClassName() != null) {
				return new FactoryNameSpecifiedObjectFactory();
			}
			else {
				return new NoFactoryNameSpecifiedObjectFactory();
			}
		}
		else {
			return new NoReferenceObjectFactory();
		}
	}
	
	
	private DirObjectFactory createInnerDirObjectFactory(Object obj) throws NamingException {
		final Object objToResolve = getObjectToResolve(obj);
		if (objToResolve instanceof Reference) {
			final Reference reference = (Reference) objToResolve;
			if (reference.getFactoryClassName() != null) {
				return new FactoryNameSpecifiedDirObjectFactory();
			}
			else {
				return new NoFactoryNameSpecifiedDirObjectFactory();
			}
		}
		else {
			return new NoReferenceDirObjectFactory();
		}
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
	private Hashtable< ? , ? > getCombinedEnvironment(
			Hashtable< ? , ? > userEnvironment) {
		// create a copy of the user-defined environment settings
		Hashtable<Object,Object> combinedEnvironment = new Hashtable<>();
		combinedEnvironment.putAll(userEnvironment);
		
		// obtain environment properties defined in the calling bundle's archive
		Properties fileDefinedEnvironment = 
			getFileDefinedJndiProperties(m_callerBundleContext);
		if(fileDefinedEnvironment != null) {
			Enumeration<Object> keyEnum = fileDefinedEnvironment.keys();
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

	

	Object resolveObjectUsingBuilders(Object objectToResolve, Name name,
			Context context, 
			Hashtable< ? , ? > environment)
			throws Exception {
		ObjectFactory objectFactory = 
			getObjectFactoryFromBuilder(environment, objectToResolve);
		if(objectFactory != null) {
			Object resolvedObject = objectFactory.getObjectInstance(objectToResolve, name, context, environment);
			if(resolvedObject != null) {
				return resolvedObject;
			}
		}
		
		return null;
	}
	
	Object resolveObjectUsingObjectFactories(Object objectToResolve, Name name,
			Context context, Hashtable< ? , ? > environment)
			throws NamingException {
		if (m_objectFactoryServiceTracker.getServiceReferences() != null) {
			final ServiceReference<ObjectFactory>[] serviceReferences = ServiceUtils
					.sortServiceTrackerReferences(m_objectFactoryServiceTracker);
			for (int i = 0; i < serviceReferences.length; i++) {
				ServiceReference<ObjectFactory> serviceReference = serviceReferences[i];
				ObjectFactory factory = 
					m_callerBundleContext.getService(serviceReference);
				try {
					Object result = 
						factory.getObjectInstance(objectToResolve, name, context, environment);

					// release the service for this factory
					m_callerBundleContext.ungetService(serviceReference);

					if (result != null) {
						// return resolved object
						return result;
					} 
				}
				catch (Exception exception) {
					NamingException namingException = new NamingException("Exception occurred while trying to resolve object using ObjectFactory search");
					namingException.setRootCause(exception);
					throw namingException;
				}
			}
		}
		
		return null;
	}
	
	
	Object resolveObjectUsingDirObjectFactories(Object objectToResolve,
			Name name, Context context, Hashtable< ? , ? > environment,
			Attributes attributes) throws NamingException {
		if (m_dirObjectFactoryServiceTracker.getServiceReferences() != null) {
			final ServiceReference<DirObjectFactory>[] serviceReferences = ServiceUtils
					.sortServiceTrackerReferences(m_dirObjectFactoryServiceTracker);
			for (int i = 0; i < serviceReferences.length; i++) {
				ServiceReference<DirObjectFactory> serviceReference = serviceReferences[i];
				DirObjectFactory factory = 
					m_callerBundleContext.getService(serviceReference);
				try {
					Object result = 
						factory.getObjectInstance(objectToResolve, name, context, environment, attributes);
		
					// release the service reference
					m_callerBundleContext.ungetService(serviceReference);
					
					if (result != null) {
						// return the resolved object 
						return result;
					} 
				}
				catch (Exception exception) {
					NamingException namingException = new NamingException("Exception occurred while trying to resolve object using ObjectFactory search");
					namingException.setRootCause(exception);
					throw namingException;
				}
			}
		}
		
		return null;
	}
	
	
	Object resolveDirObjectUsingBuilders(Object objectToResolve, Name name,
			Context context, Hashtable< ? , ? > environment,
			Attributes attributes)
			throws Exception {
		ObjectFactory objectFactory = 
			getObjectFactoryFromBuilder(environment, objectToResolve);
		if((objectFactory != null) && (objectFactory instanceof DirObjectFactory)) {
			DirObjectFactory dirObjectFactory = (DirObjectFactory)objectFactory;
			Object resolvedObject = dirObjectFactory.getObjectInstance(objectToResolve, name, context, environment, attributes);
			if(resolvedObject != null) {
				return resolvedObject;
			}
		}
		
		return null;
	}

	/**
	 * Checks the calling Bundle in order to search for a 
	 * jndi.properties file.  
	 * 
	 * Check the client's bundle for a jndi.properties file in the archive
	 * 
	 * @return a Properties instance that contains the properties defined in
	 *         a jndi.properties file for the caller's archive, or null if none exists
	 */
	private static Properties getFileDefinedJndiProperties(BundleContext callerBundleContext) {
		if (callerBundleContext.getBundle() != null) {
			Bundle bundle = callerBundleContext.getBundle();
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
						logger.log(Level.FINEST, "Exception encountered while trying to locate a jndi.properties file.", e);
					}
				}
			}
			catch (IOException e) {
				logger.log(Level.FINEST, 
						"Exception encounted while trying to load a jndi.properties file",
						e);
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
	private static <T> ServiceTracker<T,T> createServiceTracker(
			BundleContext bundleContext, Class<T> serviceInterface) {
		return new ServiceTracker<>(bundleContext, serviceInterface, null);
	}

	
	private static final class URLContextFactoryServiceTracker<T>
			extends ServiceTracker<T,T> {
		URLContextFactoryServiceTracker(BundleContext context, Class<T> clazz) {
			super(context, clazz, null);
		}

		@Override
		public T addingService(ServiceReference<T> serviceReference) {
			if (serviceReference.getProperty(JNDIConstants.JNDI_URLSCHEME) != null) {
				return super.addingService(serviceReference);
			}

			return null;
		}
	}



	private static final class ObjectFactoryServiceTracker<T>
			extends ServiceTracker<T,T> {
		ObjectFactoryServiceTracker(BundleContext context, Class<T> clazz) {
			super(context, clazz, null);
		}

		@Override
		public T addingService(ServiceReference<T> serviceReference) {
			if (serviceReference.getProperty(JNDIConstants.JNDI_URLSCHEME) == null) {
				return super.addingService(serviceReference);
			}

			return null;
		}
	}



	private final class ContextFactoryServiceTracker<T>
			extends ServiceTracker<T,T> {
		ContextFactoryServiceTracker(BundleContext context, Class<T> clazz) {
			super(context, clazz, null);
		}

		@Override
		public void removedService(ServiceReference<T> reference, T service) {
			handleRemovedService(reference, service);
		}

		@Override
		public T addingService(ServiceReference<T> reference) {
			return handleAddingService(reference);
		}

		private void handleRemovedService(ServiceReference<T> reference,
				T service) {
			super.removedService(reference, service);
			m_mapOfServicesToContexts.remove(service);
		}
		
		private T handleAddingService(ServiceReference<T> reference) {
			return super.addingService(reference);
		}
	}



	/**
	 *  Query the inner ObjectFactory instance to see if that factory
	 *  can resolve the object.  
	 *  
	 *  If no matching ObjectFactory services exist that can resolve the object,
	 *  return a specialized ObjectFactory implementation that
	 *  merely returns the reference passed in. This allows the Factory 
	 *  Manager to more closely comply with the behavior specified in the 
	 *  javadoc for NamingManger.getObjectInstance()
	 *
	 * 
	 * @author $Id$
	 */
	private static class ReturnReferenceInfoObjectFactory implements ObjectFactory {
		private final ObjectFactory m_objectFactory;
		
		
		public ReturnReferenceInfoObjectFactory(ObjectFactory objectFactory) {
			m_objectFactory = objectFactory;
		}
		
		@Override
		public Object getObjectInstance(Object refInfo, Name name,
				Context context, Hashtable< ? , ? > environment)
				throws Exception {

			if (m_objectFactory != null) {
				Object resolvedObject = 
					m_objectFactory.getObjectInstance(refInfo, name, 
							                          context, environment);
				if (resolvedObject != null) {
					return resolvedObject;
				}
			}

			// in all other cases return refInfo
			return refInfo;
		}
	}
	
	
	
	/**
	 *  Query the inner DirObjectFactory instance to see if that factory
	 *  can resolve the object.  
	 *  
	 *  If no matching DirObjectFactory services exist that can resolve the object,
	 *  return a specialized ObjectFactory implementation that
	 *  merely returns the reference passed in. This allows the Factory 
	 *  Manager to more closely comply with the behavior specified in the 
	 *  javadoc for DirectoryManger.getObjectInstance()
	 *
	 * 
	 * @author $Id$
	 */
	private static final class ReturnReferenceInfoDirObjectFactory extends ReturnReferenceInfoObjectFactory implements DirObjectFactory {
		private final DirObjectFactory m_dirObjectFactory;
		
		
		public ReturnReferenceInfoDirObjectFactory(DirObjectFactory dirObjectFactory) {
			super(dirObjectFactory);
			m_dirObjectFactory = dirObjectFactory;
		}
		

		@Override
		public Object getObjectInstance(Object refInfo, Name name, 
				Context context, Hashtable< ? , ? > environment,
				Attributes attributes) throws Exception {
			if (m_dirObjectFactory != null) {
				Object resolvedObject = 
					m_dirObjectFactory.getObjectInstance(refInfo, name, 
							                          context, environment, attributes);
				if (resolvedObject != null) {
					return resolvedObject;
				}
			}

			// in all other cases return refInfo
			return refInfo;
		}
	}
	
	
	
	

	/**
	 * Inner ObjectFactory implementation used to handle cases where a Reference
	 * is specified, but the Reference does not indicate which ObjectFactory should be used
	 * to resolve the object.  
	 * 
	 * This factory will dynamically attempt to use a URL context factory to 
	 * resolve the object (if a Reference Address of type "URL" is detected), 
	 * and will also consult the known ObjectFactoryBuilder services if no other 
	 * way to resolve the reference exists.  
	 *
	 * 
	 * @author $Id$
	 */
	private final class NoFactoryNameSpecifiedObjectFactory implements ObjectFactory {

		NoFactoryNameSpecifiedObjectFactory() {
			super();
		}

		@Override
		public Object getObjectInstance(Object refInfo, Name name,
				Context context, Hashtable< ? , ? > environment)
				throws Exception {
			if(refInfo == null) {
				return null;
			}
			
			Object objectToResolve = getObjectToResolve(refInfo);
			if(objectToResolve instanceof Reference) {
				Reference reference = (Reference)objectToResolve;
				Object resultFromURLContextFactories = 
					getObjectFromURLContextFactoryFromReference(reference, environment);
				if (resultFromURLContextFactories != null) {
					return resultFromURLContextFactories;
				}
				
				Object resultFromBuilders = resolveObjectUsingBuilders(objectToResolve, name, context, environment);
				if(resultFromBuilders != null) {
					return resultFromBuilders;
				}
				
				Object resultFromObjectFactories = 
					resolveObjectUsingObjectFactories(objectToResolve, name, context, environment);
				
				if(resultFromObjectFactories != null) {
					return resultFromObjectFactories;
				}
				
			}

			return null;
		}
		
	}
	
	private final class NoFactoryNameSpecifiedDirObjectFactory implements DirObjectFactory {

		NoFactoryNameSpecifiedDirObjectFactory() {
			super();
		}

		@Override
		public Object getObjectInstance(Object refInfo, Name name,
				Context context, Hashtable< ? , ? > environment,
				Attributes attributes) throws Exception {
			if(refInfo == null) {
				return null;
			}
			
			Object objectToResolve = getObjectToResolve(refInfo);
			if(objectToResolve instanceof Reference) {
				Reference reference = (Reference)objectToResolve;
				Object resultFromURLContextFactories = 
					getObjectFromURLContextFactoryFromReference(reference, environment);
				if (resultFromURLContextFactories != null) {
					return resultFromURLContextFactories;
				}
				
				Object resultFromBuilders = resolveDirObjectUsingBuilders(objectToResolve, name, context, environment, attributes);
				if(resultFromBuilders != null) {
					return resultFromBuilders;
				}
				
				Object resultFromDirObjectFactories = 
					resolveObjectUsingDirObjectFactories(objectToResolve, name, context, environment, attributes);
				if(resultFromDirObjectFactories != null) {
					return resultFromDirObjectFactories;
				}
			}

			return null;
		}

		@Override
		public Object getObjectInstance(Object refInfo, Name name,
				Context context, Hashtable< ? , ? > environment)
				throws Exception {
			// no-op for this DirObjectFactory
			return null;
		}
		
	}
	
	
	
	/**
	 * Inner ObjectFactory implementation used to handle cases where a Reference
	 * is specified, and the Reference indicates which ObjectFactory should be used
	 * to resolve the object.  
	 * 
	 * This factory will dynamically attempt to use locate the specified 
	 * factory in the OSGi service registry, and will also consult the known 
	 * ObjectFactoryBuilder services if no other way to resolve the reference 
	 * exists.  
	 *
	 * 
	 * @author $Id$
	 */
	private final class FactoryNameSpecifiedObjectFactory implements ObjectFactory {

		FactoryNameSpecifiedObjectFactory() {
			super();
		}

		@Override
		public Object getObjectInstance(Object refInfo, Name name,
				Context context, Hashtable< ? , ? > environment)
				throws Exception {
			Object objectToResolve = getObjectToResolve(refInfo);
			if(objectToResolve instanceof Reference) {
				// if a factory class name is specified, look through the list
				// of known ObjectFactories, and try to find a service published
				// that also supports the custom interface.
				Reference reference = (Reference)objectToResolve;
				Object factory = 
					obtainFactoryService(reference.getFactoryClassName(), m_objectFactoryServiceTracker);
				if (factory != null) {
					ObjectFactory objectFactory = (ObjectFactory)factory;
					Object resolvedObject = 
						objectFactory.getObjectInstance(objectToResolve, name, context, environment);
					if(resolvedObject != null) {
						return resolvedObject;
					}
				} else {
					return resolveObjectUsingBuilders(objectToResolve, name, context, environment);					
				}
			}
			
			return null;
		}
		
	}
	
	private final class FactoryNameSpecifiedDirObjectFactory implements DirObjectFactory {

		FactoryNameSpecifiedDirObjectFactory() {
			super();
		}

		@Override
		public Object getObjectInstance(Object refInfo, Name name,
				Context context, Hashtable< ? , ? > environment,
				Attributes attributes) throws Exception {
			Object objectToResolve = getObjectToResolve(refInfo);
			if(objectToResolve instanceof Reference) {
				// if a factory class name is specified, look through the list
				// of known ObjectFactories, and try to find a service published
				// that also supports the custom interface.
				Reference reference = (Reference)objectToResolve;
				Object factory = 
					obtainFactoryService(reference.getFactoryClassName(), m_dirObjectFactoryServiceTracker);
				if (factory != null) {
					DirObjectFactory dirObjectFactory = (DirObjectFactory)factory;
					Object resolvedObject = 
						dirObjectFactory.getObjectInstance(objectToResolve, name, context, environment, attributes);
					if(resolvedObject != null) {
						return resolvedObject;
					}
				} else {
					return resolveDirObjectUsingBuilders(objectToResolve, name, context, environment, attributes);					
				}
			}
			
			return null;
		}

		@Override
		public Object getObjectInstance(Object refInfo, Name name,
				Context context, Hashtable< ? , ? > environment)
				throws Exception {
			// always return null, since this DirObjectFactory is a wrapper type
			return null;
		}
		
	}
	
	
	private class NoReferenceObjectFactory implements ObjectFactory {
		NoReferenceObjectFactory() {
			super();
		}

		@Override
		public Object getObjectInstance(Object refInfo, Name name,
				Context context, Hashtable< ? , ? > environment)
				throws Exception {
			// first query all known ObjectFactoryBuilder services to resolve this reference
			Object resultFromBuilders = 
				resolveObjectUsingBuilders(refInfo, name, context, environment);
			
			if(resultFromBuilders != null) {
				return resultFromBuilders;
			}
			
			// as a last resort, query all known ObjectFactory services to attempt to resolve this reference
			Object resultFromObjectFactories = 
				resolveObjectUsingObjectFactories(refInfo, name, context, environment);
			
			if(resultFromObjectFactories != null) {
				return resultFromObjectFactories;
			}
			
			return null;
		}
		
	}
	
	private class NoReferenceDirObjectFactory implements DirObjectFactory {

		NoReferenceDirObjectFactory() {
			super();
		}

		@Override
		public Object getObjectInstance(Object refInfo, Name name,
				Context context, Hashtable< ? , ? > environment,
				Attributes attributes) throws Exception {
			final Object resultFromBuilders = resolveDirObjectUsingBuilders(refInfo, name, context, environment, attributes);
			if(resultFromBuilders != null) {
				return resultFromBuilders;
			}
			
			Object resultFromDirObjectFactories = 
				resolveObjectUsingDirObjectFactories(refInfo, name, context, environment, attributes);
			if(resultFromDirObjectFactories != null) {
				return resultFromDirObjectFactories;
			}
			
			return null;
		}

		@Override
		public Object getObjectInstance(Object refInfo, Name name,
				Context context, Hashtable< ? , ? > environment)
				throws Exception {
			// no-op for this DirObjectFactory
			return null;
		}
		
	}
	
	private static class DefaultBuilderSupportedInitialContextFactory implements BuilderSupportedInitialContextFactory {
		
		private final InitialContextFactory m_factory;
		private final InitialContextFactoryBuilder m_builder;
		
		DefaultBuilderSupportedInitialContextFactory(InitialContextFactory factory, InitialContextFactoryBuilder builder) {
			m_factory = factory;
			m_builder = builder;
		}

		@Override
		public InitialContextFactoryBuilder getBuilder() {
			return m_builder;
		}

		@Override
		public Context getInitialContext(Hashtable< ? , ? > environment)
				throws NamingException {
			return m_factory.getInitialContext(environment);
		}
	}

	
	
}
