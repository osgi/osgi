/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
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


package org.osgi.test.cases.jndi.provider.ct;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;

import org.osgi.test.cases.jndi.provider.CTContext;
import org.osgi.test.cases.jndi.provider.CTNameParser;

/**
 * @author $Id$
 */
public class ctURLContext implements Context {
	
	private Map<String,Object>		env		= new HashMap<>();
	private static final NameParser parser = new CTNameParser();
	
	public ctURLContext() {
		
	}
	
	public ctURLContext(Map<String,Object> env) {
		this.env = env;
	}

	@Override
	public Object addToEnvironment(String propName, Object propVal) throws NamingException {
		Object previousValue = env.get(propName);
		env.put(propName, propVal);
		return previousValue;
	}

	@Override
	public void bind(String name, Object obj) throws NamingException {
		Context ctx = new CTContext(env);
		String bindName = removeURL(name);
		ctx.bind(bindName, obj);
	}

	@Override
	public void bind(Name name, Object obj) throws NamingException {
		if (name.size() != 1) {
			throw new InvalidNameException("Composite names not supported");
		} else {
			bind(name.get(0), obj);
		}
	}

	@Override
	public void close() throws NamingException {
		env.clear();
	}

	@Override
	public String composeName(String first, String second) throws NamingException {
		return composeName(new CompositeName(first), new CompositeName(second)).toString();
	}

	@Override
	public Name composeName(Name first, Name second) throws NamingException {
		throw new OperationNotSupportedException("Namespace has no hierarchy");
	}

	@Override
	public Context createSubcontext(String name) throws NamingException {
		return createSubcontext(new CompositeName(name));
	}

	@Override
	public Context createSubcontext(Name name) throws NamingException {
		throw new OperationNotSupportedException("Subcontexts are not supported");
	}

	@Override
	public void destroySubcontext(String name) throws NamingException {
		destroySubcontext(new CompositeName(name));
	}

	@Override
	public void destroySubcontext(Name name) throws NamingException {
		throw new OperationNotSupportedException("Subcontexts are not supported");
	}

	@Override
	public Hashtable< ? , ? > getEnvironment() throws NamingException {
		return new Hashtable<>(env);
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		throw new OperationNotSupportedException("Namespace has no hierarchy");
	}

	@Override
	public NameParser getNameParser(String name) throws NamingException {
		return getNameParser(new CompositeName(name));
	}

	@Override
	public NameParser getNameParser(Name name) throws NamingException {
		return parser;
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String name)
			throws NamingException {
		Context ctx = new CTContext(env);
		String bindName = removeURL(name);
		return ctx.list(bindName);
	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name name)
			throws NamingException {
		if (name.size() != 1) {
			throw new InvalidNameException("Composite names not supported");
		} else {
			return list(name.get(0));
		}
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String name)
			throws NamingException {
		Context ctx = new CTContext(env);
		String bindName = removeURL(name);
		return ctx.listBindings(bindName);
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name name)
			throws NamingException {
		if (name.size() != 1) {
			throw new InvalidNameException("Composite names not supported");
		} else {
			return listBindings(name.get(0));
		}
	}

	@Override
	public Object lookup(String name) throws NamingException {
		Context ctx = new CTContext(env);
		String bindName = removeURL(name);
		return ctx.lookup(bindName);	
	}

	@Override
	public Object lookup(Name name) throws NamingException {
		if (name.size() != 1) {
			throw new InvalidNameException("Composite names not supported");
		} else {
			return lookup(name.get(0));
		}
	}

	@Override
	public Object lookupLink(String name) throws NamingException {
		Context ctx = new CTContext(env);
		String bindName = removeURL(name);
		return ctx.lookupLink(bindName);
	}

	@Override
	public Object lookupLink(Name name) throws NamingException {
		if (name.size() != 1) {
			throw new InvalidNameException("Composite names not supported");
		} else {
			return lookupLink(name.get(0));
		}
	}

	@Override
	public void rebind(String name, Object obj) throws NamingException {
		Context ctx = new CTContext(env);
		String bindName = removeURL(name);
		ctx.rebind(bindName, obj);
	}

	@Override
	public void rebind(Name name, Object obj) throws NamingException {
		if (name.size() != 1) {
			throw new InvalidNameException("Composite names not supported");
		} else {
			rebind(name.get(0), obj);
		}

	}

	@Override
	public Object removeFromEnvironment(String propName) throws NamingException {
		Object previousValue = env.get(propName);
		env.remove(propName);
		return previousValue;
	}

	@Override
	public void rename(String name, String newName) throws NamingException {
		Context ctx = new CTContext(env);
		String bindName = removeURL(name);
		String newBindName = removeURL(newName);
		ctx.rename(bindName, newBindName);
	}

	@Override
	public void rename(Name name, Name newName) throws NamingException {
		if (name.size() != 1 && newName.size() != 1) {
			throw new InvalidNameException("Composite names not supported");
		} else {
			rename(name.get(0), newName.get(0));
		}

	}

	@Override
	public void unbind(String name) throws NamingException {
		Context ctx = new CTContext(env);
		String bindName = removeURL(name);
		ctx.unbind(bindName);
	}

	@Override
	public void unbind(Name name) throws NamingException {
		if (name.size() != 1) {
			throw new InvalidNameException("Composite names not supported");
		} else {
			unbind(name.get(0));
		}
	}
	
	public String removeURL(String name) {
		if (!name.startsWith("ct://")) {
			throw new IllegalArgumentException("URL is not of type ct://");
		}
		// We do not support hierarchical contexts, so if there's more slashes than required for the url, we'll just take whatever is at the end.
	    return name.substring(name.lastIndexOf('/') + 1, name.length());
	}

}
