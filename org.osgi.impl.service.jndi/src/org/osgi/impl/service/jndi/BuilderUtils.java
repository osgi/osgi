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

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.service.jndi.JNDIConstants;

/**
 * Utility methods for the "traditional" JNDI builder implementations.  
 *
 * 
 * @version $Revision$
 */
class BuilderUtils {
	
	/* private constructor, static utility class */
	private BuilderUtils() {}

	/**
	 * Array of strategies to use, in priority order, when
	 * attempting to find the caller's BundleContext. 
	 */
	private static final GetBundleContextStrategy[] getBundleContextStrategies = 
		{ new EnvironmentPropertyStrategyImpl(), 
		  new ThreadContextStrategyImpl(), 
		  new CallStackStrategyImpl() };
	
	
	/**
	 * This utility method implements the process for obtaining the 
	 * JNDI client's BundleContext.  This method should only be used within
	 * the "traditional" method of JNDI support.  
	 * 
	 * This method will try the following methods to obtain the BundleContext:
	 *     1. check for the "osgi.service.jndi.bundleContext" environment property
	 *     2. check the thread context ClassLoader to see if it supports BundleReference. If so, it uses
	 *      this ClassLoader to obtain the caller's BundleContext. 
	 *     3. checks the current call stack to find the code that is invoking the naming function.  The BundleContext
	 *      that is associated with this code is returned.  
	 * 
	 * @param environment  the JNDI environment
	 * @param namingClassType the class name of the type to be used to walk the call stack.  Typically, 
	 *        this value will be one of the following values:
	 *            "javax.naming.InitialContext" - to detect Context creation
	 *            "javax.naming.spi.NamingManager" - to detect Object resolution
	 *            "javax.naming.spi.DirectoryManager" - to detect Object resolution for directories
	 * 
	 * @return the BundleContext associated with the JNDI client, or 
	 *         null if no BundleContext could be found.  
	 */
	static BundleContext getBundleContext(Hashtable environment, String namingClassType) {
		// iterate over the existing strategies to attempt to find a BundleContext
		// for the calling Bundle
		for(int i = 0; i < getBundleContextStrategies.length; i++) {
			BundleContext clientBundleContext = 
				getBundleContextStrategies[i].getBundleContext(environment, namingClassType);
			if(clientBundleContext != null) {
				return clientBundleContext;
			}
		}
		
		// if a BundleContext isn't found at this point, return null
		return null;
	}
	
	
	
	
	/**
	 * Internal interface used to abstract the process of obtaining
	 * the caller's BundleContext.  
	 *
	 */
	private interface GetBundleContextStrategy {
		/**
		 * Obtain the caller's BundleContext.  
		 * 
		 * @param environment the JNDI environment properties
		 * @param namingClassType the name of the javax.naming class to use when
		 *                        searching for the calling Bundle.  
		 * @return the caller's BundleContext, or
		 *         null if none found. 
		 */
		public BundleContext getBundleContext(Hashtable environment, String namingClassType);
	}
	
	/**
	 * Strategy implementation that attempts to find the caller's
	 * BundleContext in the JNDI environment. 
	 *
	 */
	private static class EnvironmentPropertyStrategyImpl implements GetBundleContextStrategy {
		public BundleContext getBundleContext(Hashtable environment, String namingClassType) {
			if((environment != null) && (environment.containsKey(JNDIConstants.BUNDLE_CONTEXT))) {
				Object result = 
					environment.get(JNDIConstants.BUNDLE_CONTEXT);
				if(result instanceof BundleContext) {
					return (BundleContext)result;
				}
			}
			
			return null;
		}
	}
	
	
	/**
	 * Strategy implementation that attempts to find the 
	 * caller's BundleContext on the ThreadContext ClassLoader.
	 *
	 */
	private static class ThreadContextStrategyImpl implements GetBundleContextStrategy {
		public BundleContext getBundleContext(Hashtable environment, String namingClassType) {
			ClassLoader threadContextClassloader = 
				Thread.currentThread().getContextClassLoader();
			if ((threadContextClassloader != null)
					&& (threadContextClassloader instanceof BundleReference)) {
				BundleContext result = getBundleContextFromClassLoader(threadContextClassloader);
				if(result != null) {
					return result;
				}
			}
			
			return null;
		}
	}
	
	/**
	 * Strategy implementation that attempts to find the caller's 
	 * BundleContext on the call stack. 
	 * 
	 */
	private static class CallStackStrategyImpl implements GetBundleContextStrategy {
		public BundleContext getBundleContext(Hashtable environment, String namingClassType) {
			Class[] callStack = new CallStackSecurityManager().getClientCallStack();
			
			int indexOfConstructor = -1;
			for(int i = 0; i < callStack.length; i++) {
				if(callStack[i].getName().equals(namingClassType)) {
					indexOfConstructor = i;
				}
			}
			
			// the next stack frame should include the caller of the InitialContext constructor
			if ((indexOfConstructor >= 0)
					&& ((indexOfConstructor + 1) < callStack.length)) {
				Class clientClass = callStack[indexOfConstructor + 1];
				ClassLoader clientClassLoader = clientClass.getClassLoader();
				if(clientClassLoader instanceof BundleReference) {
					return getBundleContextFromClassLoader(clientClassLoader);
				}
			}
			
			return null;
		}
		
		private static class CallStackSecurityManager extends SecurityManager {
			public Class[] getClientCallStack() {
				return getClassContext();
			}
		}
	}
	
	
	/**
	 * Attempts to obtain the client's BundleContext from the 
	 * ClassLoader specified in the method call.  
	 * 
	 * If the ClassLoader supports BundleReference, this loader
	 * represents a client bundle making a request for an OSGi service.
	 * 
	 * @param classLoader
	 * @return BundleContext associated with this ClassLoader, or 
	 *         null if no BundleContext was associated with this ClassLoaer
	 */
	private static BundleContext getBundleContextFromClassLoader(ClassLoader classLoader) {
		BundleReference bundleRef = (BundleReference) classLoader;
		if (bundleRef.getBundle() != null) {
		    return bundleRef.getBundle().getBundleContext();
		}
		
		return null;
	}
}
