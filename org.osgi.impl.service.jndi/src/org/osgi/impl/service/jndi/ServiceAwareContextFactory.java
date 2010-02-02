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
import java.lang.reflect.Proxy;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.directory.DirContext;
import javax.naming.spi.InitialContextFactory;

public class ServiceAwareContextFactory {
	
	private static Logger logger = Logger.getLogger(ServiceAwareContextFactory.class.getName());
	
	/* private constructor to disallow creation of this class */
	private ServiceAwareContextFactory() {}
	
	public static Context createServiceAwareContextWrapper(InitialContextFactory factory, Context internalContext, FactoryManager manager) {
		return (Context) Proxy.newProxyInstance(ServiceAwareContextFactory.class.getClassLoader(),
                								new Class[] {Context.class}, 
                								new DefaultServiceAwareInvocationHandler(factory, internalContext, manager));
	}
	
	public static DirContext createServiceAwareDirContextWrapper(InitialContextFactory factory, DirContext internalContext, FactoryManager manager) {
		return (DirContext) Proxy.newProxyInstance(ServiceAwareContextFactory.class.getClassLoader(),
												   new Class[] {DirContext.class}, 
												   new DefaultServiceAwareInvocationHandler(factory, internalContext, manager));
	}

	private static class DefaultServiceAwareInvocationHandler implements InvocationHandler {

		private InitialContextFactory m_factory;
		private Context m_context;
		private final FactoryManager m_manager;
		private boolean m_isOpen;
		
		DefaultServiceAwareInvocationHandler(InitialContextFactory factory, Context context, FactoryManager manager) {
			m_factory = factory;
			m_context = context;
			m_manager = manager;
			m_isOpen = true;
		}
		
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			synchronized (this) {
				synchronized (m_manager) {
					try {
						return SecurityUtils.invokePrivilegedAction(new ContextInvokeAction(method, args));
					}
					catch (Exception exception) {
						if(exception instanceof NamingException) {
							throw (NamingException)exception;
						} 
						
						logger.log(Level.FINE, 
								   "Exception occurred during a doPrivileged call",
								   exception);
						// if the cause was not a NamingException, wrap the 
						// cause in NamingException and throw back to caller
						NamingException namingException = new NamingException("Exception occured during a Context method invocation");
						namingException.setRootCause(exception);
						throw namingException;
					}
				}
			}
		}

		private Object invokeContextMethod(Method method, Object[] args) throws Throwable {
			if (m_isOpen) {
				if (isFactoryServiceActive()
						|| method.getName().equals("close")) {

					if (method.getName().equals("close")) {
						m_isOpen = false;
					}

					return ReflectionUtils.invokeMethodOnContext(method, m_context, args);
				}
				else {
					// make copy of existing context's environment
					Hashtable newContextEnvironment = new Hashtable();
					if (m_context.getEnvironment() != null) {
						newContextEnvironment.putAll(m_context
								.getEnvironment());
					}
					// attempt to recreate the required factory and context
					try {
						InitialContextFactory newFactory = m_manager
								.createInitialContextFactory(newContextEnvironment);
						if (newFactory != null) {
							m_factory = newFactory;
							Context newInternalContext = m_factory
									.getInitialContext(newContextEnvironment);
							if (newInternalContext != null) {
								m_context = newInternalContext;
								return ReflectionUtils.invokeMethodOnContext(method, m_context, args);
							}
						}
					}
					catch (NoInitialContextException noContextException) {
						logger.log(Level.SEVERE,
								   "An exception occurred while attempting to rebind the JNDI Provider service for this Context",
								   noContextException);
					}

					// if no InitialContextFactory service can handle this request, throw exception
					throw new NoInitialContextException(
							"The service that created this JNDI Context is not available");
				}
			} else {
				// if context is already closed, do not try to 
				// rebind the backing service
				// simply forward the call to the underlying context implementation
				return ReflectionUtils.invokeMethodOnContext(method, m_context, args);
			}
		}
		

		/**
		 * Query to see if the IntialContextFactory used
		 * to create this context is still active
		 * 
		 * @return true if factory service is still active
		 *         false if factory service is no longer active
		 */
		private boolean isFactoryServiceActive() {
			if(m_factory instanceof BuilderSupportedInitialContextFactory) {
				return m_manager.isFactoryServiceActive(((BuilderSupportedInitialContextFactory)m_factory).getBuilder());
			} else {
				return m_manager.isFactoryServiceActive(m_factory);
			}
		}
		
		private class ContextInvokeAction extends ReflectiveInvokeAction {
			
			ContextInvokeAction(Method method, Object[] args) {
				super(method, args);
			}

			public Object invokeMethod(Method method, Object[] args) throws Throwable {
				return invokeContextMethod(method, args);
			}
		}
	}
}

