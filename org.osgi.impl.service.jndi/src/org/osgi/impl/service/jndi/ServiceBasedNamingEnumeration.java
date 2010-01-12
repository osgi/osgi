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

import java.util.NoSuchElementException;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Abstract NamingEnumeration implementation that contains the 
 * basic logic for an enumeration over a set of NameClassPair objects.  
 * 
 * 
 *
 * 
 * @version $Revision$
 */
abstract class ServiceBasedNamingEnumeration implements NamingEnumeration {

	protected boolean				m_isOpen	= false;
	protected int					m_index	= -1;
	protected final BundleContext	m_bundleContext;
	protected final String			m_interfaceName;
	protected final ServiceReference[]	m_serviceReferences;
	protected NameClassPair[]		m_nameClassPairs;

	public ServiceBasedNamingEnumeration(BundleContext bundleContext, ServiceReference[] serviceReferences, String interfaceName) {
		m_bundleContext = bundleContext;
		if(interfaceName == null) {
			m_interfaceName = "";
		} else {
			m_interfaceName = interfaceName;
		}
		
		m_serviceReferences = serviceReferences;
		if(m_serviceReferences.length > 0) {
			m_isOpen = true;
			m_index = 0;
		}
	}

	public void close() throws NamingException {
		//TODO, consider closing any used service resources here
		m_isOpen = false;
	}

	public boolean hasMore() throws NamingException {
		checkIsOpen();
		return (isIndexValid());
	}

	public Object next() throws NamingException {
		checkIsOpen();
		return internalNextElement();
	}

	public boolean hasMoreElements() {
		if(!m_isOpen) {
			return false;
		} else {
			return (isIndexValid());
		}
		
	}

	public Object nextElement() {
		return internalNextElement();
	}

	private void checkIsOpen() throws NamingException {
		if (!m_isOpen) {
			throw new NamingException("Operation cannot complete, since this NamingEnumeration has been closed");
		}
	}

	private boolean isIndexValid() {
		return m_index < m_nameClassPairs.length;
	}

	private Object internalNextElement() {
		if(isIndexValid()) {
			return internalNextClassPair();
		} else {
			throw new NoSuchElementException("No additional elements exist in this NamingEnumeration");
		}
	}

	private NameClassPair internalNextClassPair() {
		return m_nameClassPairs[m_index++];
	}

}