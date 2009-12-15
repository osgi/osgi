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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.spi.ObjectFactory;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jndi.JndiConstants;

/**
 * A URL context factory that supports lookups of OSGi services.
 * 
 */
public class OSGiURLContextFactory implements ObjectFactory {

	private final BundleContext	m_bundleContext;

	public OSGiURLContextFactory(BundleContext bundleContext) {
		m_bundleContext = bundleContext;
	}

	public Object getObjectInstance(Object obj, Name name, Context nameCtx,
			Hashtable environment) throws Exception {
		return Proxy.newProxyInstance(this.getClass().getClassLoader(),
				new Class[] {Context.class}, new OSGiContextInvocationHandler(
						m_bundleContext));
	}

	/**
	 * InvocationHandler representing the OSGi URL Context that is handed back
	 * to the Factory Manager.
	 * <p/>
	 * Currently, this proxy only supports the Context.lookup(String) method.
	 * All other method invocations on this context will result in an
	 * OperationNotSupportedException being thrown.
	 */
	private static class OSGiContextInvocationHandler implements InvocationHandler {

		private final BundleContext	m_bundleContext;

		public OSGiContextInvocationHandler(BundleContext bundleContext) {
			m_bundleContext = bundleContext;
		}

		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			if (method.getName().equals("lookup")) {
				if ((args.length == 1) && (args[0] instanceof String)) {
					String osgiURL = (String) args[0];
					try {
						Object requestedService = obtainService(osgiURL);
						if (requestedService != null) {
							// TODO, eventually add proxy code to track service
							// usage
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
			}

			throw new OperationNotSupportedException(
					"This operation is not supported by the OSGi URL Context");
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
				// TODO, use a logger for this eventually
				stateException.printStackTrace();
				return null;
			}
			if (urlParser.getServiceInterface() == null) {
				return null;
			}

			// obtain the thread context classloader
			ClassLoader threadContextClassloader = Thread.currentThread()
					.getContextClassLoader();
			if ((threadContextClassloader != null)
					&& (threadContextClassloader instanceof BundleReference)) {
				// if the classloader supports BundleReference, this loader
				// represents a client bundle
				// making a request for an OSGi service. The client's bundle
				// context must be used to
				// obtain the OSGi service.
				BundleReference bundleRef = (BundleReference) threadContextClassloader;
				if (bundleRef.getBundle() != null) {
					BundleContext clientBundleContext = 
						bundleRef.getBundle().getBundleContext();
					return getService(clientBundleContext, urlParser);
				}
			}

			// default to Factory Manager's BundleContext
			return getService(m_bundleContext, urlParser);
		}

		private static Object getService(BundleContext bundleContext,
				OSGiURLParser urlParser) throws InvalidSyntaxException {
			
			ServiceReference[] serviceReferences = 
				bundleContext.getServiceReferences(urlParser.getServiceInterface(),
							                       urlParser.getFilter());
			if (serviceReferences != null) {
				final ServiceReference[] sortedServiceReferences = 
					ServiceUtils.sortServiceReferences(serviceReferences);
				if (urlParser.isServiceListURL()) {
					// return a Context that can handle service.id lookups
					return new OSGiServiceListContext(bundleContext, sortedServiceReferences);
				}
				else {
					return bundleContext.getService(sortedServiceReferences[0]);
				}
			}
			else {
				// service interface name may not map to a published name
				// check the registry for a service that supports the
				// osgi.jndi.serviceName property
				final String serviceNameFilter = "("
						+ JndiConstants.JNDI_SERVICENAME + "="
						+ urlParser.getServiceInterface() + ")";
				ServiceReference[] serviceReferencesByName = 
					bundleContext.getServiceReferences(null, serviceNameFilter);
				if (serviceReferencesByName != null) {
					final ServiceReference[] sortedServiceReferences = 
						ServiceUtils.sortServiceReferences(serviceReferencesByName);
					return bundleContext.getService(sortedServiceReferences[0]);
				}
			}

			return null;
		}
	}

}
