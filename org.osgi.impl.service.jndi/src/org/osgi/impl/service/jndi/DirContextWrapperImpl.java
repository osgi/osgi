/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.osgi.impl.service.jndi;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

class DirContextWrapperImpl extends ContextWrapperImpl implements DirContext {

	private final DirContext m_dirContext;
	
	DirContextWrapperImpl(DirContext dirContext, FactoryManager factoryManager) {
		super(dirContext, factoryManager);
		m_dirContext = dirContext;
	}
	
	public void bind(String name, Object obj, Attributes attributes)
			throws NamingException {
		m_dirContext.bind(name, obj, attributes);

	}

	public void bind(Name name, Object obj, Attributes attributes)
			throws NamingException {
		m_dirContext.bind(name, obj, attributes);
	}

	public DirContext createSubcontext(String name, Attributes attributes)
			throws NamingException {
		return m_dirContext.createSubcontext(name, attributes);
	}

	public DirContext createSubcontext(Name name, Attributes attributes)
			throws NamingException {
		return m_dirContext.createSubcontext(name, attributes);
	}

	public Attributes getAttributes(String name) throws NamingException {
		return m_dirContext.getAttributes(name);
	}

	public Attributes getAttributes(Name name) throws NamingException {
		return m_dirContext.getAttributes(name);
	}

	public Attributes getAttributes(String name, String[] values)
			throws NamingException {
		return m_dirContext.getAttributes(name, values);
	}

	public Attributes getAttributes(Name name, String[] values)
			throws NamingException {
		return m_dirContext.getAttributes(name, values);
	}

	public DirContext getSchema(String name) throws NamingException {
		return m_dirContext.getSchema(name);
	}

	public DirContext getSchema(Name name) throws NamingException {
		return m_dirContext.getSchema(name);
	}

	public DirContext getSchemaClassDefinition(String name)
			throws NamingException {
		return m_dirContext.getSchemaClassDefinition(name);
	}

	public DirContext getSchemaClassDefinition(Name name)
			throws NamingException {
		return m_dirContext.getSchemaClassDefinition(name);
	}

	public void modifyAttributes(String name, ModificationItem[] values)
			throws NamingException {
		m_dirContext.modifyAttributes(name, values);
	}

	public void modifyAttributes(Name name, ModificationItem[] values)
			throws NamingException {
		m_dirContext.modifyAttributes(name, values);
	}

	public void modifyAttributes(String name, int index, Attributes attributes)
			throws NamingException {
		m_dirContext.modifyAttributes(name, index, attributes);
	}

	public void modifyAttributes(Name name, int index, Attributes attributes)
			throws NamingException {
		m_dirContext.modifyAttributes(name, index, attributes);
	}

	public void rebind(String name, Object obj, Attributes attributes)
			throws NamingException {
		m_dirContext.rebind(name, obj, attributes);
	}

	public void rebind(Name name, Object obj, Attributes attributes)
			throws NamingException {
		m_dirContext.rebind(name, obj, attributes);
	}

	public NamingEnumeration search(String name, Attributes attributes)
			throws NamingException {
		return m_dirContext.search(name, attributes);
	}

	public NamingEnumeration search(Name name, Attributes attributes)
			throws NamingException {
		return m_dirContext.search(name, attributes);
	}

	public NamingEnumeration search(String name, String filter, SearchControls searchControls) throws NamingException {
		return m_dirContext.search(name, filter, searchControls);
	}

	public NamingEnumeration search(String name, Attributes attributes, String[] attributesToReturn)
			throws NamingException {
		return m_dirContext.search(name, attributes, attributesToReturn);
	}

	public NamingEnumeration search(Name name, String filter, SearchControls searchControls)
			throws NamingException {
		return m_dirContext.search(name, filter, searchControls);
	}

	public NamingEnumeration search(Name name, Attributes attributes, String[] attributesToReturn) throws NamingException {
		return m_dirContext.search(name, attributes, attributesToReturn);
	}

	public NamingEnumeration search(String name, String filter, Object[] filterArgs, SearchControls searchControls) throws NamingException {
		return m_dirContext.search(name, filter, filterArgs, searchControls);
	}

	public NamingEnumeration search(Name name, String filter, Object[] filterArgs, SearchControls searchControls) throws NamingException {
		return m_dirContext.search(name, filter, filterArgs, searchControls);
	}

}
