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

import javax.naming.*;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;

/**
 * A Decorated Context class that will allow the JNDI Framework to handle
 * requests for URL context factory lookups.
 * 
 */
class ContextWrapperImpl implements Context, ContextWrapper {

	//TODO, move this to a common interface/class
	private static final String OSGI_BUNDLE_CONTEXT_LOOKUP = "osgi:framework/bundleContext";
	
	private final Context			m_context;
	private final FactoryManager	m_factoryManager;

	public ContextWrapperImpl(Context context, FactoryManager factoryManager) {
		m_context = context;
		m_factoryManager = factoryManager;
	}

	public Context getWrappedContext() {
		return m_context;
	}

	public Object lookup(Name name) throws NamingException {
		return m_context.lookup(name);
	}

	public Object lookup(String name) throws NamingException {
		if (isURLRequest(name)) {
			//TODO, consider using service factory for osgi URL
			if(name.equals(OSGI_BUNDLE_CONTEXT_LOOKUP)) {
				return m_factoryManager.getBundleContext();
			}
			
			// attempt to find a URL Context Factory to satisfy this lookup
			// request
			ObjectFactory objectFactory = 
				m_factoryManager.getURLContextFactory(getScheme(name));
			
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

				NamingException namingException = new NamingException(
						"Exception occurred during URL Context Factory Resolution for name = "
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

	public void bind(Name name, Object obj) throws NamingException {
		m_context.bind(name, obj);
	}

	public void bind(String name, Object obj) throws NamingException {
		m_context.bind(name, obj);
	}

	public void rebind(Name name, Object obj) throws NamingException {
		m_context.rebind(name, obj);
	}

	public void rebind(String name, Object obj) throws NamingException {
		m_context.rebind(name, obj);
	}

	public void unbind(Name name) throws NamingException {
		m_context.unbind(name);
	}

	public void unbind(String name) throws NamingException {
		m_context.unbind(name);
	}

	public void rename(Name oldName, Name newName) throws NamingException {
		m_context.rename(oldName, newName);
	}

	public void rename(String oldName, String newName) throws NamingException {
		m_context.rename(oldName, newName);
	}

	public NamingEnumeration list(Name name) throws NamingException {
		return m_context.list(name);
	}

	public NamingEnumeration list(String name) throws NamingException {
		return m_context.list(name);
	}

	public NamingEnumeration listBindings(Name name) throws NamingException {
		return m_context.listBindings(name);
	}

	public NamingEnumeration listBindings(String name) throws NamingException {
		return m_context.listBindings(name);
	}

	public void destroySubcontext(Name name) throws NamingException {
		m_context.destroySubcontext(name);
	}

	public void destroySubcontext(String name) throws NamingException {
		m_context.destroySubcontext(name);
	}

	public Context createSubcontext(Name name) throws NamingException {
		return m_context.createSubcontext(name);
	}

	public Context createSubcontext(String name) throws NamingException {
		return m_context.createSubcontext(name);
	}

	public Object lookupLink(Name name) throws NamingException {
		return m_context.lookupLink(name);
	}

	public Object lookupLink(String name) throws NamingException {
		return m_context.lookupLink(name);
	}

	public NameParser getNameParser(Name name) throws NamingException {
		return m_context.getNameParser(name);
	}

	public NameParser getNameParser(String name) throws NamingException {
		return m_context.getNameParser(name);
	}

	public Name composeName(Name name, Name prefix) throws NamingException {
		return m_context.composeName(name, prefix);
	}

	public String composeName(String name, String prefix)
			throws NamingException {
		return m_context.composeName(name, prefix);
	}

	public Object addToEnvironment(String propName, Object propVal)
			throws NamingException {
		return m_context.addToEnvironment(propName, propVal);
	}

	public Object removeFromEnvironment(String propName) throws NamingException {
		return m_context.removeFromEnvironment(propName);
	}

	public Hashtable getEnvironment() throws NamingException {
		return m_context.getEnvironment();
	}

	public void close() throws NamingException {
		m_context.close();
	}

	public String getNameInNamespace() throws NamingException {
		return m_context.getNameInNamespace();
	}

	private static boolean isURLRequest(String name) {
		int indexOfColon = name.indexOf(":");
		return (indexOfColon != -1);
	}

	private static String getScheme(String name) {
		int indexOfColon = name.indexOf(":");
		if (indexOfColon != -1) {
			return name.substring(0, indexOfColon);
		}

		return null;
	}
}
