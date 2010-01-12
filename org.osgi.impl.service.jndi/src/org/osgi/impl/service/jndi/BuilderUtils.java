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

import org.osgi.framework.Bundle;
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
	 * This utility method implements the process for obtaining the 
	 * JNDI client's BundleContext.  This method should only be used within
	 * the "traditional" method of JNDI support.  
	 * 
	 * This method will try the following methods to obtain the BundleContext:
	 *     1. check for the "osgi.service.jndi.bundleContext" environment property
	 *     2. check the thread context classloader to see if it supports BundleReference. If so, it uses
	 *      this classloader to obtain the caller's BundleContext. 
	 *     3. checks the current call stack to find the code that is invoking the naming function.  The BundleContext
	 *      that is associated with this code is returned.  
	 * 
	 * @param environment  the JNDI environment
	 * @param internalBundleContext BundleContext for the JNDI implementation
	 * @param namingClassType the class name of the type to be used to walk the call stack.  Typically, 
	 *        this value will be one of the following values:
	 *            "javax.naming.InitialContext" - to detect Context creation
	 *            "javax.naming.spi.NamingManager" - to detect Object resolution
	 *            "javax.naming.spi.DirectoryManager" - to detect Object resolution for directories
	 * @return
	 */
	static BundleContext getBundleContext(Hashtable environment, BundleContext internalBundleContext, String namingClassType) {
		if((environment != null) && (environment.containsKey(JNDIConstants.BUNDLE_CONTEXT))) {
			Object result = 
				environment.get(JNDIConstants.BUNDLE_CONTEXT);
			if(result instanceof BundleContext) {
				return (BundleContext)result;
			}
		} else {
			// obtain the thread context classloader
			ClassLoader threadContextClassloader = 
				Thread.currentThread().getContextClassLoader();
			if ((threadContextClassloader != null)
					&& (threadContextClassloader instanceof BundleReference)) {
				// if the classloader supports BundleReference, this loader
				// represents a client bundle
				// making a request for an OSGi service.
				BundleReference bundleRef = (BundleReference) threadContextClassloader;
				if (bundleRef.getBundle() != null) {
				    return bundleRef.getBundle().getBundleContext();
				}
			}
			
			
			// check call stack for matching classloader
			BundleContext result = getBundleContextFromCallStack(namingClassType, internalBundleContext);
			if(result != null) {
				return result;
			}
		}
		
		return null;
	}

	private static BundleContext getBundleContextFromCallStack(final String namingClassType, BundleContext internalBundleContext) {
		StackTraceElement[] callStack = new Throwable().getStackTrace();
		int indexOfConstructor = -1;
		for(int i = 0; i < callStack.length; i++) {
			//TODO, maybe check for method name as well
			if(callStack[i].getClassName().equals(namingClassType)) {
				indexOfConstructor = i;
			}
		}
		
		// the next stack frame should include the caller of the InitialContext constructor
		if ((indexOfConstructor >= 0)
				&& ((indexOfConstructor + 1) < callStack.length)) {
			StackTraceElement callerElement = callStack[indexOfConstructor + 1];
			final String callerClass = callerElement.getClassName();
			Bundle[] allBundles = internalBundleContext.getBundles();
			// try each known bundle, to see which can
			// load the caller of the InitialContext class
			for (int i = 0; i < allBundles.length; i++) {
				try {
					allBundles[i].loadClass(callerClass);
					// if load is successful, then use this BundleContext
					return allBundles[i].getBundleContext();
				}
				catch (Exception e) {
					// just continue on error
					continue;
				}
			}
		}
		
		return null;
	}
	
	
}
