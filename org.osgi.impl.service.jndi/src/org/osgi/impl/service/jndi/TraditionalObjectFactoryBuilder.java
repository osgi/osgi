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
import javax.naming.directory.Attributes;
import javax.naming.spi.DirObjectFactory;
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
	
	private static final String NAMING_MANAGER_CLASSNAME = 
		NamingManager.class.getName();
	
	private static final String DIRECTORY_MANAGER_CLASSNAME = 
		DirectoryManager.class.getName();
	

	public TraditionalObjectFactoryBuilder() {
	}
	
	public ObjectFactory createObjectFactory(Object obj, Hashtable environment) throws NamingException {
		// if the call came from NamingManager
		BundleContext clientBundleContext = 
			BuilderUtils.getBundleContext(environment, NAMING_MANAGER_CLASSNAME);
		
		// if the call came from DirectoryManager
		if(clientBundleContext == null) {
			clientBundleContext = 
				BuilderUtils.getBundleContext(environment, DIRECTORY_MANAGER_CLASSNAME);
		}
		
		return new TraditionalObjectFactory(clientBundleContext);
	}
	
	private class TraditionalObjectFactory implements DirObjectFactory {

		private final BundleContext m_clientBundleContext;
		
		TraditionalObjectFactory(BundleContext clientBundleContext) {
			m_clientBundleContext = clientBundleContext;
		}
		
		public Object getObjectInstance(Object refInfo, Name name, Context context, Hashtable environment) throws Exception {
			ProviderAdminAction providerAdminAction = 
				new NamingManagerAction(refInfo, name, context, environment);
			return resolveObjectWithProviderAdmin(providerAdminAction);
		}
		

		public Object getObjectInstance(Object refInfo, Name name, Context context, Hashtable environment, Attributes attributes) throws Exception {
			ProviderAdminAction providerAdminAction = 
				new DirectoryManagerAction(refInfo, name, context, environment, attributes);
			return resolveObjectWithProviderAdmin(providerAdminAction);
		}
		
		
		/**
		 * Utility method used to keep the code for obtaining the JNDIProviderAdmin service in a common place.  This allows
		 * for simpler managing of service references.  
		 * @param providerAdminAction the action to perform on the JNDIProviderAdmin service
		 * @return the result Object of the call to the JNDIProviderAdmin service
		 * @throws Exception 
		 */
		private Object resolveObjectWithProviderAdmin(ProviderAdminAction providerAdminAction) throws Exception {
			if(m_clientBundleContext == null) {
				throw new NamingException("Error in obtaining client's BundleContext");
			} else {
				ServiceReference serviceReference = 
					m_clientBundleContext.getServiceReference(JNDI_PROVIDER_ADMIN_INTERFACE);
				if(serviceReference == null) {
					throw new NamingException("JNDIProviderAdmin service not available, cannot resolve object at this time");
				} else {
					JNDIProviderAdmin providerAdmin = 
						(JNDIProviderAdmin)m_clientBundleContext.getService(serviceReference);
					if(providerAdmin == null) {
						throw new NamingException("JNDIProviderAdmin service not available, cannot resolve object at this time");
					} else {
						final Object resolvedObject = providerAdminAction.runProviderAdminAction(providerAdmin);
						// clean up reference to the provider admin service
						m_clientBundleContext.ungetService(serviceReference);
						// return result
						return resolvedObject;
					}
				}
			}
		}
		
		
	}
	
	/**
	 * Internal interface meant to represent a generic action on the JNDIProviderAdmin service.  
	 *
	 * @version $Revision$
	 */
	private interface ProviderAdminAction {
		Object runProviderAdminAction(JNDIProviderAdmin providerAdmin) throws Exception;
	}
	
	/**
	 * A ProviderAdminAction implementation that follows the behavior of 
	 * NamingManager.getObjectInstance().  
	 *
	 * @version $Revision$
	 */
	private static class NamingManagerAction implements ProviderAdminAction {
		protected final Object m_refInfo;
		protected final Name m_name;
		protected final Context m_context;
		protected final Hashtable m_environment;
		
		NamingManagerAction(Object refInfo, Name name, Context context, Hashtable environment) {
			m_refInfo = refInfo;
			m_name = name;
			m_context = context;
			m_environment = environment;
		}
		
		public Object runProviderAdminAction(JNDIProviderAdmin providerAdmin) throws Exception {
			return providerAdmin.getObjectInstance(m_refInfo, m_name, m_context, m_environment);
		}
	}
	
	/**
	 * A ProviderAdminAction implementation that follows the behavior of 
	 * DirectoryManager.getObjectInstance().  
	 *
	 * @version $Revision$
	 */
	private static class DirectoryManagerAction extends NamingManagerAction {
		private final Attributes m_attributes;
		
		DirectoryManagerAction(Object refInfo, Name name, Context context, Hashtable environment, Attributes attributes) {
			super(refInfo, name, context, environment);
			m_attributes = attributes;
		}

		public Object runProviderAdminAction(JNDIProviderAdmin providerAdmin) throws Exception {
			return providerAdmin.getObjectInstance(m_refInfo, m_name, m_context, m_environment, m_attributes);
		}
	}
	
}
