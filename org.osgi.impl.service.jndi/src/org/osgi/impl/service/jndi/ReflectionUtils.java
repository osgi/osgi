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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.PrivilegedExceptionAction;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

/**
 * Utility class for reflection calls made by the JNDI implementation
 *
 * 
 * @version $Id$
 */
class ReflectionUtils {

	private static Logger logger = Logger.getLogger(ReflectionUtils.class.getName());
	
	/**
	 * This method uses reflection to invoke the given Method
	 * on the passed in Context instance.  This method also
	 * catches the InvocationTargeException, in order to always
	 * re-throw the original exception from the Method invocation. 
	 * 
	 * @param method the Method to invoke
	 * @param contextToInvokeOn the Context to invoke the Method on
	 * @param args the arguments to the Method
	 * @return an Object representing the result of the Method call
	 * @throws Throwable
	 */
	static Object invokeMethodOnContext(Method method, Context contextToInvokeOn, Object[] args) throws Throwable {
		return invokeMethodOnObject(method, contextToInvokeOn, args);
	}

	
	
	/**
	 * This method uses reflection to invoke the given Method
	 * on the passed in Object instance.  This method also
	 * catches the InvocationTargeException, in order to always
	 * re-throw the original exception from the Method invocation. 
	 * 
	 * @param method the Method to invoke
	 * @param objectToInvokeOn the Object to invoke the Method on
	 * @param args the arguments to the Method
	 * @return an Object representing the result of the Method call
	 * @throws Throwable
	 */
	static Object invokeMethodOnObject(Method method, Object objectToInvokeOn, Object[] args) throws IllegalAccessException, Throwable {
		try {
			return method.invoke(objectToInvokeOn, args); 
		} catch (InvocationTargetException invocationException) {
			throw invocationException.getTargetException();
		}
	}



	/**
	 * Creates a dynamic proxy for the given service.  This method 
	 * installs an InvocationHandler that will manage the dynamics of the
	 * underlying OSGi service (un-bind and re-bind). 
	 * 
	 * @param bundleContext the BundleContext used to obtain this service
	 * @param urlParser the OSGiURLParser used for this service
	 * @param serviceReference the ServiceReference for the service to proxy
	 * @return a ServiceProxyInfo instance, which includes the proxy or underlying service.
	 */
	static ServiceProxyInfo getProxyForSingleService(BundleContext bundleContext, OSGiURLParser urlParser, ServiceReference serviceReference) {
		return getProxyForSingleService(bundleContext, 
				                        urlParser,
				                        serviceReference,
				                        new RetryInvocationHandlerFactory());
	}
	
	
	/**
	 * Creates a dynamic proxy for the given service.  This method 
	 * calls on an InvocationHandlerFactory to create the handler associated
	 * with this proxy.  This method allows callers to customize the type of 
	 * InvocationHandler desired to be used with the proxy.  
	 * 
	 * @param bundleContext the BundleContext used to obtain this service
	 * @param urlParser the OSGiURLParser used for this service
	 * @param serviceReference the ServiceReference for the service to proxy
	 * @param handlerFactory a factory method for creating the InvocationHandler to be
	 *                       associated with this service proxy
	 * @return a ServiceProxyInfo instance, which includes the proxy or underlying service.
	 */
	static ServiceProxyInfo getProxyForSingleService(BundleContext bundleContext, OSGiURLParser urlParser, ServiceReference serviceReference, InvocationHandlerFactory handlerFactory) {
		final Object requestedService = 
			bundleContext.getService(serviceReference);
		ClassLoader tempLoader = null;
		try {
			tempLoader = (ClassLoader)SecurityUtils.invokePrivilegedAction(new PrivilegedExceptionAction() {
				public Object run() throws Exception {
					return requestedService.getClass().getClassLoader();
				}
			});
		} catch (Exception e) {
			logger.log(Level.FINE, 
					   "Exception occurred while trying to obtain OSGi service's ClassLoader",
					   e);
		} 
			
		try {
			Class clazz = Class.forName(urlParser.getServiceInterface(), true, tempLoader);
			if (clazz.isInterface()) {
				InvocationHandler handler = 
					handlerFactory.create(bundleContext, serviceReference, urlParser, requestedService);
				final Object serviceProxy = Proxy.newProxyInstance(tempLoader, new Class[] {clazz}, handler);
				return new ServiceProxyInfo(serviceProxy, handler, true);
			}
			else {
				logger.log(Level.WARNING, 
						   "The service type " + clazz.getName() + 
						   " is not an interface.  The JNDI implementation cannot generate a proxy for this service.");
				return new ServiceProxyInfo(requestedService, null, false);
			}
		}
		catch (ClassNotFoundException classNotFoundException) {
			tempLoader = requestedService.getClass().getClassLoader();
			final Class[] interfaces = getInterfaces(serviceReference, bundleContext, tempLoader);
			if (interfaces.length > 0) {
				InvocationHandler handler = 
					handlerFactory.create(bundleContext, serviceReference, 
							              urlParser, requestedService);
				final Object serviceProxy = Proxy.newProxyInstance(tempLoader, interfaces, handler);
				return new ServiceProxyInfo(serviceProxy, handler, true);
			}
			else {
				logger.log(Level.WARNING,
						   "No compatible interfaces could be found for this OSGi service, type = " +
						   requestedService.getClass().getName() + ".  The JNDI implementation cannot generate a proxy for this service.");

				throw new IllegalArgumentException("No compatible interfaces could be found for this OSGi service, type = " +
						   urlParser.getServiceInterface() + " (probably a JNDI Service Name)" + ".  The JNDI implementation cannot generate a proxy for this service.");
			}
		}
	}
	
	
	private static boolean isAssignable(ServiceReference serviceReference, BundleContext bundleContext, Class clazz) {
		return serviceReference.isAssignableTo(bundleContext.getBundle(), clazz.getName());
	}



	private static boolean isInterfacePublic(Class clazz) {
		return Modifier.isPublic(clazz.getModifiers());
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
	
	private static class RetryInvocationHandlerFactory implements InvocationHandlerFactory {
		public InvocationHandler create(BundleContext bundleContext, ServiceReference serviceReference, OSGiURLParser urlParser, Object osgiService) {
			return new ServiceInvocationHandler(bundleContext, 
					                            serviceReference, 
					                            urlParser, 
					                            osgiService);
		}
		
	}
}
