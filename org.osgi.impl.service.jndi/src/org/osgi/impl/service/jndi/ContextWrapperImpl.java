/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.impl.service.jndi;

import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

/**
 * A Decorated Context class that will allow the JNDI Framework to handle
 * requests for URL context factory lookups.
 * 
 */
class ContextWrapperImpl implements Context {

	private static Logger logger = Logger.getLogger(ContextWrapperImpl.class.getName());
	
	private final Context			m_context;
	private final FactoryManager	m_factoryManager;
	
	public ContextWrapperImpl(Context context, FactoryManager factoryManager) {
		m_context = context;
		m_factoryManager = factoryManager;
	}


	@Override
	public Object lookup(Name name) throws NamingException {
		return m_context.lookup(name);
	}

	@Override
	public Object lookup(String name) throws NamingException {
		if (isURLRequest(name)) {
			// attempt to find a URL Context Factory to satisfy this lookup
			// request
			ObjectFactory objectFactory = null;
			try {
				// obtain URL Context Factory in a doPrivilieged() block
				objectFactory = SecurityUtils.invokePrivilegedAction(new GetObjectFactoryAction(m_factoryManager, name));
			} catch (Exception e) {
				logger.log(Level.FINE, 
						   "Exception occurred while trying to obtain a reference to a URL Context Factory.",
						   e);
			}
			
			if (objectFactory == null) {
				throw new NameNotFoundException(
						"Name: "
								+ name
								+ " was not found.  A URL Context Factory was not registered to handle "
								+ "this URL scheme");
			}

			try {
				Context context = 
					(Context) objectFactory.getObjectInstance(null, null, 
															  null, m_context.getEnvironment());
				if (context != null) {
					return context.lookup(name);
				}
				else {
					throw new NamingException("Name = " + name
							+ "was not found using the URL Context factory.");
				}
			}
			catch (Exception e) {
				if (e instanceof NamingException) {
					// re-throw naming exceptions
					throw (NamingException) e;
				}

				NamingException namingException = 
					new NameNotFoundException("Exception occurred during URL Context Factory Resolution for name = "
								              + name);
				namingException.initCause(e);
				throw namingException;
			}
		}
		else {
			// treat this lookup as a normal lookup
			return m_context.lookup(name);
		}

	}


	

	@Override
	public void bind(Name name, Object obj) throws NamingException {
		m_context.bind(name, obj);
	}

	@Override
	public void bind(String name, Object obj) throws NamingException {
		m_context.bind(name, obj);
	}

	@Override
	public void rebind(Name name, Object obj) throws NamingException {
		m_context.rebind(name, obj);
	}

	@Override
	public void rebind(String name, Object obj) throws NamingException {
		m_context.rebind(name, obj);
	}

	@Override
	public void unbind(Name name) throws NamingException {
		m_context.unbind(name);
	}

	@Override
	public void unbind(String name) throws NamingException {
		m_context.unbind(name);
	}

	@Override
	public void rename(Name oldName, Name newName) throws NamingException {
		m_context.rename(oldName, newName);
	}

	@Override
	public void rename(String oldName, String newName) throws NamingException {
		m_context.rename(oldName, newName);
	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name name)
			throws NamingException {
		return m_context.list(name);
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String name)
			throws NamingException {
		return m_context.list(name);
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name name)
			throws NamingException {
		return m_context.listBindings(name);
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String name)
			throws NamingException {
		return m_context.listBindings(name);
	}

	@Override
	public void destroySubcontext(Name name) throws NamingException {
		m_context.destroySubcontext(name);
	}

	@Override
	public void destroySubcontext(String name) throws NamingException {
		m_context.destroySubcontext(name);
	}

	@Override
	public Context createSubcontext(Name name) throws NamingException {
		return m_context.createSubcontext(name);
	}

	@Override
	public Context createSubcontext(String name) throws NamingException {
		return m_context.createSubcontext(name);
	}

	@Override
	public Object lookupLink(Name name) throws NamingException {
		return m_context.lookupLink(name);
	}

	@Override
	public Object lookupLink(String name) throws NamingException {
		return m_context.lookupLink(name);
	}

	@Override
	public NameParser getNameParser(Name name) throws NamingException {
		return m_context.getNameParser(name);
	}

	@Override
	public NameParser getNameParser(String name) throws NamingException {
		return m_context.getNameParser(name);
	}

	@Override
	public Name composeName(Name name, Name prefix) throws NamingException {
		return m_context.composeName(name, prefix);
	}

	@Override
	public String composeName(String name, String prefix)
			throws NamingException {
		return m_context.composeName(name, prefix);
	}

	@Override
	public Object addToEnvironment(String propName, Object propVal)
			throws NamingException {
		return m_context.addToEnvironment(propName, propVal);
	}

	@Override
	public Object removeFromEnvironment(String propName) throws NamingException {
		return m_context.removeFromEnvironment(propName);
	}

	@Override
	public Hashtable< ? , ? > getEnvironment() throws NamingException {
		return m_context.getEnvironment();
	}

	@Override
	public void close() throws NamingException {
		m_context.close();
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		return m_context.getNameInNamespace();
	}

	private static boolean isURLRequest(String name) {
		int indexOfColon = name.indexOf(":");
		return (indexOfColon != -1);
	}

	static String getScheme(String name) {
		int indexOfColon = name.indexOf(":");
		if (indexOfColon != -1) {
			return name.substring(0, indexOfColon);
		}

		return null;
	}
	
	private static class GetObjectFactoryAction
			implements PrivilegedExceptionAction<ObjectFactory> {
		private final FactoryManager m_factoryManager;
		private final String m_name;
		
		GetObjectFactoryAction(FactoryManager factoryManager, String name) {
			m_factoryManager = factoryManager;
			m_name = name;
		}

		@Override
		public ObjectFactory run() throws Exception {
			return obtainObjectFactory(m_name);
		}
		
		private ObjectFactory obtainObjectFactory(String name) {
			ObjectFactory objectFactory;
			synchronized (m_factoryManager) {
				objectFactory = m_factoryManager.getURLContextFactory(getScheme(name));
			}
			return objectFactory;
		}
	}
}
