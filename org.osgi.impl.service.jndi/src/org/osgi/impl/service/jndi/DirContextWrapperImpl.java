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

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

class DirContextWrapperImpl extends ContextWrapperImpl implements DirContext {

	private final DirContext m_dirContext;
	
	DirContextWrapperImpl(DirContext dirContext, FactoryManager factoryManager) {
		super(dirContext, factoryManager);
		m_dirContext = dirContext;
	}
	
	@Override
	public void bind(String name, Object obj, Attributes attributes)
			throws NamingException {
		m_dirContext.bind(name, obj, attributes);

	}

	@Override
	public void bind(Name name, Object obj, Attributes attributes)
			throws NamingException {
		m_dirContext.bind(name, obj, attributes);
	}

	@Override
	public DirContext createSubcontext(String name, Attributes attributes)
			throws NamingException {
		return m_dirContext.createSubcontext(name, attributes);
	}

	@Override
	public DirContext createSubcontext(Name name, Attributes attributes)
			throws NamingException {
		return m_dirContext.createSubcontext(name, attributes);
	}

	@Override
	public Attributes getAttributes(String name) throws NamingException {
		return m_dirContext.getAttributes(name);
	}

	@Override
	public Attributes getAttributes(Name name) throws NamingException {
		return m_dirContext.getAttributes(name);
	}

	@Override
	public Attributes getAttributes(String name, String[] values)
			throws NamingException {
		return m_dirContext.getAttributes(name, values);
	}

	@Override
	public Attributes getAttributes(Name name, String[] values)
			throws NamingException {
		return m_dirContext.getAttributes(name, values);
	}

	@Override
	public DirContext getSchema(String name) throws NamingException {
		return m_dirContext.getSchema(name);
	}

	@Override
	public DirContext getSchema(Name name) throws NamingException {
		return m_dirContext.getSchema(name);
	}

	@Override
	public DirContext getSchemaClassDefinition(String name)
			throws NamingException {
		return m_dirContext.getSchemaClassDefinition(name);
	}

	@Override
	public DirContext getSchemaClassDefinition(Name name)
			throws NamingException {
		return m_dirContext.getSchemaClassDefinition(name);
	}

	@Override
	public void modifyAttributes(String name, ModificationItem[] values)
			throws NamingException {
		m_dirContext.modifyAttributes(name, values);
	}

	@Override
	public void modifyAttributes(Name name, ModificationItem[] values)
			throws NamingException {
		m_dirContext.modifyAttributes(name, values);
	}

	@Override
	public void modifyAttributes(String name, int index, Attributes attributes)
			throws NamingException {
		m_dirContext.modifyAttributes(name, index, attributes);
	}

	@Override
	public void modifyAttributes(Name name, int index, Attributes attributes)
			throws NamingException {
		m_dirContext.modifyAttributes(name, index, attributes);
	}

	@Override
	public void rebind(String name, Object obj, Attributes attributes)
			throws NamingException {
		m_dirContext.rebind(name, obj, attributes);
	}

	@Override
	public void rebind(Name name, Object obj, Attributes attributes)
			throws NamingException {
		m_dirContext.rebind(name, obj, attributes);
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name,
			Attributes attributes)
			throws NamingException {
		return m_dirContext.search(name, attributes);
	}

	@Override
	public NamingEnumeration<SearchResult> search(Name name,
			Attributes attributes)
			throws NamingException {
		return m_dirContext.search(name, attributes);
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name, String filter,
			SearchControls searchControls) throws NamingException {
		return m_dirContext.search(name, filter, searchControls);
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name,
			Attributes attributes, String[] attributesToReturn)
			throws NamingException {
		return m_dirContext.search(name, attributes, attributesToReturn);
	}

	@Override
	public NamingEnumeration<SearchResult> search(Name name, String filter,
			SearchControls searchControls)
			throws NamingException {
		return m_dirContext.search(name, filter, searchControls);
	}

	@Override
	public NamingEnumeration<SearchResult> search(Name name,
			Attributes attributes, String[] attributesToReturn)
			throws NamingException {
		return m_dirContext.search(name, attributes, attributesToReturn);
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name, String filter,
			Object[] filterArgs, SearchControls searchControls)
			throws NamingException {
		return m_dirContext.search(name, filter, filterArgs, searchControls);
	}

	@Override
	public NamingEnumeration<SearchResult> search(Name name, String filter,
			Object[] filterArgs, SearchControls searchControls)
			throws NamingException {
		return m_dirContext.search(name, filter, filterArgs, searchControls);
	}

}
