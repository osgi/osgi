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
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jndi.JNDIContextManager;

class TraditionalInitialContextFactoryBuilder implements InitialContextFactoryBuilder {

	private static final String JNDI_CONTEXT_MANAGER_CLASS = 
		JNDIContextManager.class.getName();
	
	private static final String INITIAL_CONTEXT_CLASSNAME = 
		InitialContext.class.getName();
	
	private static final String INITIAL_DIR_CONTEXT_CLASSNAME = 
		InitialDirContext.class.getName();
	
	public TraditionalInitialContextFactoryBuilder() {
	}
	
	public InitialContextFactory createInitialContextFactory(Hashtable environment) throws NamingException {
		return new TraditionalInitialContextFactory();
	}
	
	
	
	/**
	 * An InitialContextFactory implementation that handles requests from 
	 * "traditional" clients (non-OSGi clients).  
	 * 
	 * This factory first attempts to obtain the client's BundleContext.  If this BundleContext
	 * cannot be located, a NoInitialContextException is thrown.  
	 *
	 * 
	 * @version $Revision$
	 */
	private static class TraditionalInitialContextFactory implements InitialContextFactory {

		public Context getInitialContext(Hashtable environment) throws NamingException {
			// try to find BundleContext, assuming a call to the InitialContext constructor
			BundleContext clientBundleContext = 
				BuilderUtils.getBundleContext(environment, INITIAL_CONTEXT_CLASSNAME);

			if(clientBundleContext == null) {
				// try to find BundleContext, assuming a call to the InitialDirContext constructor
				clientBundleContext = 
					BuilderUtils.getBundleContext(environment, INITIAL_DIR_CONTEXT_CLASSNAME);
			}
			
			
			if(clientBundleContext == null) {
				throw new NoInitialContextException("Client's BundleContext could not be located");
			} else {
				ServiceReference serviceRef = 
					clientBundleContext.getServiceReference(JNDI_CONTEXT_MANAGER_CLASS);
				
				// if service not available, throw exception back to caller
				if(serviceRef == null) {
					throw new NamingException("JNDIContextManager service not available yet, cannot create a new context");
				} else {
					JNDIContextManager contextManager = 
						(JNDIContextManager)clientBundleContext.getService(serviceRef);
					if(contextManager == null) {
						throw new NamingException("JNDIContextManager service not available yet, cannot create a new context");
					} else {
						// install a dynamic proxy to trap calls to Context.close()
						try {
							final Context newInitialContext = contextManager.newInitialContext(environment);
							final TraditionalContextInvocationHandler handler = 
								new TraditionalContextInvocationHandler(serviceRef, newInitialContext, clientBundleContext);
							// create the correct proxy
							if(newInitialContext instanceof DirContext) {
								return (DirContext)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {DirContext.class}, handler);
							} else {
								return (Context)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {Context.class}, handler);
							}
							
						}
						catch (NamingException namingException) {
							// clean up reference to JNDIContextManager service
							clientBundleContext.ungetService(serviceRef);
							// re-throw exception
							throw namingException;
						}
					}
				}
			}
		}
	}
	
	private static class TraditionalContextInvocationHandler implements InvocationHandler {

		private final ServiceReference m_referenceToContextManager;
		private final Context m_context;
		private final BundleContext m_bundleContext;
		
		TraditionalContextInvocationHandler(ServiceReference refToContextManager, Context context, BundleContext bundleContext) {
			m_referenceToContextManager = refToContextManager;
			m_context = context;
			m_bundleContext = bundleContext;
			
		}
		
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if(method.getName().equals("close")) {
				// clean up reference to JNDIContextManager
				m_bundleContext.ungetService(m_referenceToContextManager);
			}
			
			return ReflectionUtils.invokeMethodOnContext(method, m_context, args);
		}
	}

}
