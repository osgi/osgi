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

import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * A URL context factory that supports lookups of OSGi services.
 * 
 * 
 */
class OSGiURLContextFactory implements ObjectFactory {

	private static final String OSGI_BUNDLE_CONTEXT_LOOKUP = "osgi:framework/bundleContext";

	private static final Logger logger = Logger.getLogger(OSGiURLContextFactory.class.getName());
	
	private final BundleContext	m_bundleContext;

	public OSGiURLContextFactory(BundleContext bundleContext) {
		m_bundleContext = bundleContext;
	}

	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment) throws Exception {
		return new OSGiURLContext(m_bundleContext);
	}

	/**
	 * The OSGi URL Context that is handed back to the JNDI Implementation.
	 * <p/>
	 * This URL Context only supports the Context.lookup(String) method.
	 * All other method invocations on this context will result in an
	 * OperationNotSupportedException being thrown.
	 */
	private static class OSGiURLContext extends NotSupportedContext {

		private final BundleContext	m_bundleContext;

		public OSGiURLContext(BundleContext bundleContext) {
			super("This operation is not supported by the OSGi URL Context");
			m_bundleContext = bundleContext;
		}
		

		public Object lookup(String name) throws NamingException {
			String osgiURL = name;
			try {
				if(osgiURL.equals(OSGI_BUNDLE_CONTEXT_LOOKUP)) {
					// return the caller's BundleContext
					return m_bundleContext;
				}
				
				Object requestedService = obtainService(osgiURL);
				if (requestedService != null) {
					return requestedService;
				}
			}
			catch (InvalidSyntaxException e) {
				NamingException namingException = new NamingException(
						"Error occurred while parsing the OSGi URL");
				namingException.initCause(e);
				throw namingException;
			}

			throw new NameNotFoundException(
					"The OSGi service referred to by the URL = "
							+ osgiURL
							+ " could not be located in the OSGi Service Registry");
		}
		

		/**
		 * Obtain the service requested in the "osgi" URL. Currently, this
		 * method uses the Factory Manager's bundle context.
		 * 
		 * @param osgiURL the URL for the OSGi service requested
		 * @return the OSGi Service requested, or null if the service cannot be
		 *         found
		 * @throws InvalidSyntaxException if an error occurs while parsing the
		 *         OSGi filer (if specified)
		 */
		private Object obtainService(String osgiURL)
				throws InvalidSyntaxException {
			OSGiURLParser urlParser = new OSGiURLParser(osgiURL);
			try {
				urlParser.parse();
			}
			catch (IllegalStateException stateException) {
				logger.log(Level.SEVERE, "An exception occurred while trying to parse this osgi URL", stateException);
				return null;
			}
			if (urlParser.getServiceInterface() == null) {
				return null;
			}

			return getService(m_bundleContext, urlParser);
		}

		private static Object getService(BundleContext bundleContext, OSGiURLParser urlParser) throws InvalidSyntaxException {
			ServiceReference[] serviceReferences = 
				bundleContext.getServiceReferences(urlParser.getServiceInterface(), 
						                           urlParser.getFilter());
			if (serviceReferences != null) {
				final ServiceReference[] sortedServiceReferences = 
					ServiceUtils.sortServiceReferences(serviceReferences);
				if (urlParser.isServiceListURL()) {
					// return a Context that can handle service.id lookups
					return new OSGiServiceListContext(bundleContext, sortedServiceReferences, urlParser);
				}
				else {
					return getProxyForSingleService(bundleContext, urlParser, sortedServiceReferences[0]);
				}
			}
			else {
				// service interface name may not map to a published name
				// check the registry for a service that supports the
				// osgi.jndi.serviceName property
				ServiceReference[] serviceReferencesByName = 
					ServiceUtils.getServiceReferencesByServiceName(bundleContext, urlParser);
				if (serviceReferencesByName != null) {
					final ServiceReference[] sortedServiceReferences = 
						ServiceUtils.sortServiceReferences(serviceReferencesByName);
					return getProxyForSingleService(bundleContext, urlParser, sortedServiceReferences[0]);
				}
			}

			return null;
		}


		private static Object getProxyForSingleService(BundleContext bundleContext, OSGiURLParser urlParser, ServiceReference serviceReference) {
			Object requestedService = 
				bundleContext.getService(serviceReference);
			ClassLoader tempLoader = 
				requestedService.getClass().getClassLoader();
			try {
				Class clazz = Class.forName(urlParser.getServiceInterface(), true, tempLoader);
				if (clazz.isInterface()) {
					ServiceInvocationHandler handler = 
						new ServiceInvocationHandler(bundleContext, serviceReference, 
								                     urlParser, requestedService);
					return Proxy.newProxyInstance(tempLoader, new Class[] {clazz}, handler);
				}
				else {
					// TODO,revisit this 
					return requestedService;
				}
			}
			catch (ClassNotFoundException classNotFoundException) {
				tempLoader = requestedService.getClass().getClassLoader();
				final Class[] interfaces = getInterfaces(serviceReference, bundleContext, tempLoader);
				if (interfaces.length > 0) {
					ServiceInvocationHandler handler = 
						new ServiceInvocationHandler(bundleContext, serviceReference, 
								                     urlParser, requestedService);
					return Proxy.newProxyInstance(tempLoader, interfaces, handler);
				}
				else {
					// TODO,revisit this, should probably throw an IllegalArgumentException here (Section 126.6.1 of the JNDI spec)
					return requestedService;
				}
			}
		}
	
		private static Class[] getInterfaces(ServiceReference serviceReference, BundleContext bundleContext, ClassLoader classLoader) {
			String[] objectClassValues = (String [])serviceReference.getProperty(Constants.OBJECTCLASS);
			List listOfClasses = new LinkedList();
			for(int i = 0; i < objectClassValues.length; i++) {
				try {
					Class clazz = 
						Class.forName(objectClassValues[i], true, classLoader);
					if(clazz.isInterface()) {
						if (isInterfacePublic(clazz)) {
							if (isAssignable(serviceReference, bundleContext, clazz)) {
								listOfClasses.add(clazz);
							}
						} else {
							logger.warning("Unable to generate proxy for non-public interface: " + 
									       clazz.getName() + ".  This interface will not be available to clients");
						}
					}
				}
				catch (ClassNotFoundException e) {
					// just continue
				}
				
			}
			
			if(listOfClasses.isEmpty()) {
				return new Class[0];
			} else {
				Class[] interfacesToReturn = new Class[listOfClasses.size()];
				for(int i = 0; i < listOfClasses.size(); i++) {
					interfacesToReturn[i] = (Class)listOfClasses.get(i);
				}
				
				return interfacesToReturn;
			}
		}


		private static boolean isAssignable(ServiceReference serviceReference, BundleContext bundleContext, Class clazz) {
			return serviceReference.isAssignableTo(bundleContext.getBundle(), clazz.getName());
		}


		private static boolean isInterfacePublic(Class clazz) {
			return Modifier.isPublic(clazz.getModifiers());
		}
	
	}
	
}
