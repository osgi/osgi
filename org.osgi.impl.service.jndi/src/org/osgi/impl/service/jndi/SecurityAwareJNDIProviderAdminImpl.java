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

import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.directory.Attributes;


/**
 * Decorator for the JNDIProviderAdmin that can handle invoking methods on the 
 * underlying JNDIProviderAdmin implementation in a doPrivileged() Action.
 *
 * 
 * @author $Id$
 */
class SecurityAwareJNDIProviderAdminImpl implements CloseableJNDIProviderAdmin {

	private static final Logger logger = 
		Logger.getLogger(SecurityAwareJNDIProviderAdminImpl.class.getName());
	
	final CloseableJNDIProviderAdmin	m_jndiProviderAdmin;
	
	public SecurityAwareJNDIProviderAdminImpl(CloseableJNDIProviderAdmin jndiProviderAdmin) {
		m_jndiProviderAdmin = jndiProviderAdmin;
	}
		
	@Override
	public Object getObjectInstance(Object refInfo, Name name, Context context,
			Map<String, ? > environment) throws Exception {
		PrivilegedExceptionAction< ? > action =
			new GetObjectInstanceAction(refInfo, name, context, environment);
		return invokePrivilegedAction(action);
	}

	@Override
	public Object getObjectInstance(Object refInfo, Name name, Context context,
			Map<String, ? > environment, Attributes attributes)
			throws Exception {
		PrivilegedExceptionAction< ? > action =
			new GetObjectInstanceActionWithAttributes(refInfo, name, context, environment, attributes);
		return invokePrivilegedAction(action);
	}
	
	@Override
	public void close() {
		try {
			SecurityUtils.invokePrivilegedActionNoReturn(new CloseAction());
		}
		catch (Exception exception) {
			logger.log(Level.FINE, 
					   "Exception occurred while trying to close this JNDIProviderAdmin implementation",
					   exception);
		}
	}
	
	private static Object invokePrivilegedAction(
			final PrivilegedExceptionAction< ? > action) throws Exception {
		return SecurityUtils.invokePrivilegedAction(action);
	}
	
	
	private class GetObjectInstanceAction
			implements PrivilegedExceptionAction<Object> {
		protected final Object m_refInfo;
		protected final Name m_name;
		protected final Context m_context;
		protected final Map<String, ? >	m_environment;
		
		GetObjectInstanceAction(Object refInfo, Name name, Context context,
				Map<String, ? > environment) {
			m_refInfo = refInfo;
			m_name = name;
			m_context = context;
			m_environment = environment;
		}
		
		@Override
		public Object run() throws Exception {
			return m_jndiProviderAdmin.getObjectInstance(m_refInfo, 
					                                     m_name, 
					                                     m_context, 
					                                     m_environment);
		}
		
	}
	
	private class GetObjectInstanceActionWithAttributes extends GetObjectInstanceAction {
		private final Attributes m_attributes;
		
		GetObjectInstanceActionWithAttributes(Object refInfo, Name name,
				Context context, Map<String, ? > environment,
				Attributes attributes) {
			super(refInfo, name, context, environment);
			m_attributes = attributes;
		}
		
		
		@Override
		public Object run() throws Exception {
			return m_jndiProviderAdmin.getObjectInstance(m_refInfo, 
					                                     m_name, 
					                                     m_context, 
					                                     m_environment, 
					                                     m_attributes);
		}
		
	}
	
	
	private class CloseAction implements PrivilegedExceptionAction<Void> {
		CloseAction() {
			super();
		}

		@Override
		public Void run() throws Exception {
			m_jndiProviderAdmin.close();
			return null;
		}
		
	}
}
