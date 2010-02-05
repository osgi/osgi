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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Binding;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

class OSGiServiceListContext extends NotSupportedContext {

	private static Logger logger = 
		Logger.getLogger(OSGiServiceListContext.class.getName());
	
	private final BundleContext m_bundleContext;
	
	private final ServiceReference[] m_serviceReferences;
	
	private final OSGiURLParser m_urlParser;
	
	/* map of service ids (Long) to ServiceReferences */
	private final Map m_mapOfServices = new HashMap(); 
	
	OSGiServiceListContext(BundleContext bundleContext, ServiceReference[] serviceReferences, OSGiURLParser urlParser) {
		super("This operation is not supported in an osgi:servicelist context");
		m_bundleContext = bundleContext;
		m_serviceReferences = serviceReferences;
		m_urlParser = urlParser;
		buildMapOfServices(m_mapOfServices, m_serviceReferences);
	}
	

	public void close() throws NamingException {
		// this operation is a no-op
	}


	public NamingEnumeration list(String name) throws NamingException {
		if(name.equals("")) {
			return new ListNamingEnumeration(m_bundleContext, 
					                                     m_serviceReferences, m_urlParser.getServiceInterface());
		}
		
		throw new OperationNotSupportedException("This NamingEnumeration cannot support list() operations for anything other than the empty string");
	}


	public NamingEnumeration listBindings(String name) throws NamingException {
		if(name.equals("")) {
			return new ListBindingsNamingEnumeration(m_bundleContext, 
					                                 m_serviceReferences, 
					                                 m_urlParser);
		}
		
		throw new OperationNotSupportedException("This NamingEnumeration cannot support list() operations for anything other than the empty string");
	}


	public Object lookup(String name) throws NamingException {
		Long serviceId = new Long(name);
		if(serviceId == null) {
			throw new NameNotFoundException("Service with the name = " + name + " does not exist in this context");
		} else {
			if(m_mapOfServices.containsKey(serviceId)) {
				ServiceReference serviceReference = (ServiceReference)m_mapOfServices.get(serviceId);
				// create a proxy for this service, and return the proxy to handle
				// service dynamics
				ServiceProxyInfo proxyInfo = 
					createNoRetryProxiedService(m_bundleContext, m_urlParser, serviceReference);
				
				if(!proxyInfo.isProxied()) {
					logger.log(Level.WARNING, 
							   "The service returned could not be proxied, OSGi lifecycle maintenance will not be handled by the Context Manager service");
				}

				return proxyInfo.getService();
			} else {
				throw new NameNotFoundException("Service with the name = " + name + " does not exist in this context");
			}
		}
	}
	
	
	private static ServiceProxyInfo createNoRetryProxiedService(BundleContext bundleContext, OSGiURLParser urlParser, final ServiceReference serviceReference) {
		return ReflectionUtils.getProxyForSingleService(bundleContext, 
				                                        urlParser, 
				                                        serviceReference,
				                                        new NoRetryInvocationHandlerFactory());
	}


	/**
	 * Convenience method for building a lookup map of service id's to services.  
	 * @param mapOfServices
	 * @param serviceReferences
	 */
	private static void buildMapOfServices(Map mapOfServices, ServiceReference[] serviceReferences) {
		for(int i = 0; i < serviceReferences.length; i++) {
			Long serviceId = (Long)serviceReferences[i].getProperty("service.id");
			mapOfServices.put(serviceId, serviceReferences[i]);
		}
	}
	
	
	/**
	 * NamingEnumeration that allows for iteration over a list of 
	 * NameClassPair structures.  This enumeration should be used 
	 * from the Context.list() operation.  
	 *
	 * 
	 * @version $Revision$
	 */
	private static class ListNamingEnumeration extends ServiceBasedNamingEnumeration {

		ListNamingEnumeration(BundleContext bundleContext, ServiceReference[] serviceReferences, String interfaceName) {
			super(bundleContext, serviceReferences, interfaceName);
			
			// create the NameClassPair structure
			m_nameClassPairs = new NameClassPair[m_serviceReferences.length];
			for(int i = 0; i < m_serviceReferences.length; i++) {
				Long serviceId = (Long)m_serviceReferences[i].getProperty(Constants.SERVICE_ID);
				m_nameClassPairs[i] = 
					new NameClassPair(serviceId.toString(), m_interfaceName);
			}
		}
	}
	

	/**
	 * NamingEnumeration that supports calls to Context.listBinding() 
	 * 
	 * This enumeration will contain a collection of javax.naming.Binding
	 * objects.  
	 * 
	 * The Binding for each service reference will contain:
	 * 
	 *    1. The service ID name
	 *    2. The service interface type (if specified)
	 *    3. The service object itself  
	 *    
	 * The responsibility for cleaning up the services obtained by 
	 * this NamingEnumeration lies with the enumeration itself.  The close() 
	 * implementation must unget each service reference.  
	 * 
	 * The caller of the NamingEnumeration is responsible for calling close()
	 * when the caller is finished with the services in the enumeration.  
	 *
	 * 
	 * @version $Revision$
	 */
	private static class ListBindingsNamingEnumeration extends ServiceBasedNamingEnumeration {
		
		private final List m_listOfHandlers = new LinkedList();
		
		ListBindingsNamingEnumeration(BundleContext bundleContext, ServiceReference[] serviceReferences, OSGiURLParser urlParser) {
			super(bundleContext, serviceReferences, urlParser.getServiceInterface());
			
			// setup a Binding object for each ServiceReference
			m_nameClassPairs = new Binding[m_serviceReferences.length];
			for(int i = 0; i < m_serviceReferences.length; i++) {
				Long serviceId = (Long)m_serviceReferences[i].getProperty(Constants.SERVICE_ID);
				final ServiceReference serviceReference = m_serviceReferences[i];
				ServiceProxyInfo proxyInfo = 
					createNoRetryProxiedService(bundleContext, urlParser, serviceReference);
				m_listOfHandlers.add(proxyInfo.getHandler());
				m_nameClassPairs[i] = 
					new Binding(serviceId.toString(), 
							    m_interfaceName, 
							    proxyInfo.getService());
			}
		}

		public void close() throws NamingException {
			super.close();
			
			for(int i = 0; i < m_serviceReferences.length; i++) {
				m_bundleContext.ungetService(m_serviceReferences[i]);
			}
			
			Iterator iterator = m_listOfHandlers.iterator();
			while(iterator.hasNext()) {
				NoRetryServiceInvocationHandler handler = 
					(NoRetryServiceInvocationHandler)iterator.next();
				handler.close();
			}
        }
	}
	
	
	private static class NoRetryServiceInvocationHandler extends ServiceInvocationHandler {
		NoRetryServiceInvocationHandler(BundleContext callerBundleContext, ServiceReference serviceReference, OSGiURLParser urlParser, Object osgiService) {
			super(callerBundleContext, serviceReference, urlParser, osgiService);
		}


		protected boolean obtainService() {
			m_serviceTracker.close();
			// always return false, since servicelist proxies must not rebind to a service
			return false;
		}
	}
	
	private static class NoRetryInvocationHandlerFactory implements InvocationHandlerFactory {
		public InvocationHandler create(BundleContext bundleContext, ServiceReference serviceReference, OSGiURLParser urlParser, Object osgiService) {
			return new NoRetryServiceInvocationHandler(bundleContext, serviceReference, urlParser, osgiService);
		}
		
	}

}
