/*
 * Copyright 2010 Oracle Corporation
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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

class ServiceInvocationHandler implements InvocationHandler {
	
	private static final Logger logger = Logger.getLogger(ServiceInvocationHandler.class.getName());
	
	private final BundleContext m_callerBundleContext;
	
	/* backing OSGi service */
	private Object m_osgiService;

	/* service tracker for the backing service */
	protected ServiceTracker m_serviceTracker;
	
	/* ServiceReference for the backing service */
	private ServiceReference m_serviceReference;

	/* the URL information used to rebind the backing service if necessary */
	private final OSGiURLParser m_urlParser;
	
	
	ServiceInvocationHandler(BundleContext callerBundleContext, ServiceReference serviceReference, OSGiURLParser urlParser, Object osgiService) {
		m_callerBundleContext = callerBundleContext;
		// initialize backing service 
		m_osgiService = osgiService;
		m_serviceReference = serviceReference;
		m_urlParser = urlParser;
		
		// open a tracker for just this service
		m_serviceTracker = 
			new ServiceTracker(m_callerBundleContext, m_serviceReference, null);
		m_serviceTracker.open();
	}
	
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (isServiceAvailable()) {
			return invokeMethodOnService(method, args);
		} else {
			// attempt to obtain another service reference to match this interface
			if(obtainService()) {
				return invokeMethodOnService(method, args);
			}
		}
		
		throw new ServiceException("Backing service is not available for invocation", 
				                    ServiceException.UNREGISTERED);
	}


	private Object invokeMethodOnService(Method method, Object[] args) throws Throwable {
		try {
			return ReflectionUtils.invokeMethodOnObject(method, m_osgiService, args);
		}
		catch (IllegalAccessException illegalAccessException) {
			throw new ServiceException("An error occurred while trying to invoke on this service, please verify that this service's interface is public", illegalAccessException);
		}
	}
	
	protected void close() {
		try {
			m_callerBundleContext.ungetService(m_serviceReference);
		}
		catch (Throwable throwable) {
			logger.log(Level.FINER, 
					   "An Exception occurred while trying to unget the backing OSGi service",
					   throwable);
		}
		
		m_serviceTracker.close();
	}
	
	
	
	protected void finalize() throws Throwable {
		close();
	}


	private boolean isServiceAvailable() {
		return m_serviceTracker.size() == 1;
	}
	
	protected boolean obtainService() {
		m_serviceTracker.close();
		try {
			ServiceReference[] serviceReferences = 
				m_callerBundleContext.getServiceReferences(m_urlParser.getServiceInterface(),
							                               m_urlParser.getFilter());
			if (serviceReferences != null) {
				final ServiceReference[] sortedServiceReferences = 
					ServiceUtils.sortServiceReferences(serviceReferences);
				
				// reset the tracker
				return resetBackingService(sortedServiceReferences[0]);
			 } else {
				 // attempt to locate service using service name property
				 ServiceReference[] serviceReferencesByName = 
					ServiceUtils.getServiceReferencesByServiceName(m_callerBundleContext, m_urlParser);
				if (serviceReferencesByName != null) {
					ServiceReference[] sortedServiceReferences = 
						ServiceUtils.sortServiceReferences(serviceReferencesByName);
					// reset the tracker
					return resetBackingService(sortedServiceReferences[0]);
				}
				
			 }
		}
		catch (InvalidSyntaxException invalidSyntaxException) {
			logger.log(Level.SEVERE, 
					   "An error in the filter syntax for this OSGi lookup has occurred.",
					   invalidSyntaxException);
		}
		
		return false;
   }


	private boolean resetBackingService(ServiceReference serviceReference) {
		m_serviceTracker = 
			new ServiceTracker(m_callerBundleContext, serviceReference, null);
		m_serviceTracker.open();
		
		// reset the service
		m_osgiService = m_serviceTracker.getService();
		if(m_osgiService!= null) {
			return true;
		}
		
		return false;
	}

}