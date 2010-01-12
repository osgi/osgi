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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jndi.JNDIContextManager;

class TraditionalInitialContextFactoryBuilder implements
		InitialContextFactoryBuilder {

	private static final String JNDI_CONTEXT_MANAGER_CLASS = 
		JNDIContextManager.class.getName();
	
	/* BundleContext for the internal JNDI Implementation Bundle */
	private final BundleContext m_internalBundleContext;
	
	public TraditionalInitialContextFactoryBuilder(BundleContext internalBundleContext) {
		m_internalBundleContext = internalBundleContext;
	}
	
	public InitialContextFactory createInitialContextFactory(Hashtable environment)
			throws NamingException {
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
	private class TraditionalInitialContextFactory implements InitialContextFactory {

		public Context getInitialContext(Hashtable environment) throws NamingException {
			BundleContext clientBundleContext = 
				BuilderUtils.getBundleContext(environment, m_internalBundleContext, 
                                              InitialContext.class.getName());
			
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
						return contextManager.newInitialContext(environment);
					}
				}
			}
		}
	}

}
