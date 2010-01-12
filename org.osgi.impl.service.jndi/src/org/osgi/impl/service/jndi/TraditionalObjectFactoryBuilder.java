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
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.DirectoryManager;
import javax.naming.spi.NamingManager;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ObjectFactoryBuilder;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jndi.JNDIProviderAdmin;

class TraditionalObjectFactoryBuilder implements ObjectFactoryBuilder {

	private static final String JNDI_PROVIDER_ADMIN_INTERFACE = 
		JNDIProviderAdmin.class.getName();
	
	/* BundleContext for the internal JNDI Implementation Bundle */
	private final BundleContext m_internalBundleContext;
	
	public TraditionalObjectFactoryBuilder(BundleContext internalBundleContext) {
		m_internalBundleContext = internalBundleContext;
	}
	
	public ObjectFactory createObjectFactory(Object obj, Hashtable environment)
			throws NamingException {
		return new TraditionalObjectFactory();
	}
	
	private class TraditionalObjectFactory implements ObjectFactory {

		public Object getObjectInstance(Object refInfo, Name name, Context context, Hashtable environment) throws Exception {
			// if the call came from NamingManager
			BundleContext clientBundleContext = 
				BuilderUtils.getBundleContext(environment, m_internalBundleContext, 
						                      NamingManager.class.getName());
			
			// if the call came from DirectoryManager
			if(clientBundleContext == null) {
				clientBundleContext = 
					BuilderUtils.getBundleContext(environment, m_internalBundleContext, 
		                      DirectoryManager.class.getName());
			}
			
			if(clientBundleContext == null) {
				throw new NamingException("Error in obtaining client's BundleContext");
			} else {
				ServiceReference serviceReference = 
					clientBundleContext.getServiceReference(JNDI_PROVIDER_ADMIN_INTERFACE);
				if(serviceReference == null) {
					throw new NamingException("JNDIProviderAdmin service not available, cannot resolve object at this time");
				} else {
					JNDIProviderAdmin providerAdmin = 
						(JNDIProviderAdmin)clientBundleContext.getService(serviceReference);
					if(providerAdmin == null) {
						throw new NamingException("JNDIProviderAdmin service not available, cannot resolve object at this time");
					} else {
						return providerAdmin.getObjectInstance(refInfo, name, context, environment);
					}
				}
			}
		}
		
	}

}
