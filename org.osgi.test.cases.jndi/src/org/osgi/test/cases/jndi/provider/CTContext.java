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


package org.osgi.test.cases.jndi.provider;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.spi.NamingManager;

/**
 * @version $Revision$ $Date$
 */
public class CTContext implements Context {
	
	protected static Map storage = new HashMap();
	private Map env 	= new HashMap();
	private static int invokeCount = 0;
	private boolean closed = false;
	
	private static final NameParser parser = new CTNameParser();

	public CTContext() throws NamingException {
		invokeCount++;
	}
	
	public CTContext(Map env) throws NamingException {
		this.env = env;
		invokeCount++;
	}

	public Object addToEnvironment(String propName, Object propVal)
			throws NamingException {
		if (closed) {
			throw new OperationNotSupportedException("This context has been closed.");
		}
		Object previousValue = env.get(propName);
		env.put(propName, propVal);
		return previousValue;
	}

	public void bind(String name, Object obj) throws NamingException {
		bind(new CompositeName(name), obj);
	}

	public void bind(Name name, Object obj) throws NamingException {
		if (closed) {
			throw new OperationNotSupportedException("This context has been closed.");
		}
		if (name == null) {
			throw new NamingException("Unable to bind object to null name");
		} else if (obj == null) {
			throw new NamingException("Unable to bind, object is null");
		}
		
		if ( obj instanceof Referenceable) {
			obj = ((Referenceable) obj).getReference();
		}
		
		storage.put(name.toString(), obj);
	}

	public void close() throws NamingException {
		storage.clear();
		env.clear();
		invokeCount--;
		closed = true;
	}

	public String composeName(String first, String second) throws NamingException {
		return composeName(new CompositeName(first), new CompositeName(second)).toString();
	}

	public Name composeName(Name first, Name second) throws NamingException {
		throw new OperationNotSupportedException("Namespace has no hierarchy");
	}

	public Context createSubcontext(String name) throws NamingException {
		return createSubcontext(new CompositeName(name));
	}

	public Context createSubcontext(Name name) throws NamingException {
		throw new OperationNotSupportedException("Subcontexts are not supported");
	}

	public void destroySubcontext(String name) throws NamingException {
		destroySubcontext(new CompositeName(name));
	}

	public void destroySubcontext(Name name) throws NamingException {
		throw new OperationNotSupportedException("Subcontexts are not supported");
	}

	public Hashtable getEnvironment() throws NamingException {
		return new Hashtable(env);
	}

	public String getNameInNamespace() throws NamingException {
		throw new OperationNotSupportedException("Namespace has no hierarchy");
	}

	public NameParser getNameParser(String name) throws NamingException {
		return getNameParser(new CompositeName(name));
	}

	public NameParser getNameParser(Name name) throws NamingException {
		return parser;
	}

	public NamingEnumeration list(String name) throws NamingException {
		return list(new CompositeName(name));
	}

	public NamingEnumeration list(Name name) throws NamingException {
		if (closed) {
			throw new OperationNotSupportedException("This context has been closed.");
		}
		CTNameClassPairEnumeration nameClassPairs = new CTNameClassPairEnumeration(CTContext.storage);
		return nameClassPairs;
	}

	public NamingEnumeration listBindings(String name) throws NamingException {
		return listBindings(new CompositeName(name));
	}

	public NamingEnumeration listBindings(Name name) throws NamingException {
		if (closed) {
			throw new OperationNotSupportedException("This context has been closed.");
		}
		CTBindingEnumeration bindings = new CTBindingEnumeration(CTContext.storage);
		return bindings;
	}

	public Object lookup(String name) throws NamingException {
		return lookup(new CompositeName(name));
	}

	public Object lookup(Name name) throws NamingException {
		if (closed) {
			throw new OperationNotSupportedException("This context has been closed.");
		}
		if (name.isEmpty()) {
			return new CTContext();
		}
		
		Object obj = storage.get(name.toString());
		
		if (obj instanceof Reference || obj instanceof Referenceable) {		
			try {
				return NamingManager.getObjectInstance(obj, null, null, null);
			} catch (Exception ex) {
				throw new NamingException("Unable to retrieve reference");
			}
		} else if (obj != null) {
			return obj;
		} else {
			throw new NamingException("Unable to find " + name.toString());
		}	
	}

	public Object lookupLink(String name) throws NamingException {
		return lookupLink(new CompositeName(name));
	}

	public Object lookupLink(Name name) throws NamingException {
		return lookup(name);
	}

	public void rebind(String name, Object obj) throws NamingException {
		rebind(new CompositeName(name), obj);
	}

	public void rebind(Name name, Object obj) throws NamingException {
		bind(name,obj);
	}

	public Object removeFromEnvironment(String propName) throws NamingException {
		if (closed) {
			throw new OperationNotSupportedException("This context has been closed.");
		}
		Object previousValue = env.get(propName);
		env.remove(propName);
		return previousValue;
	}

	public void rename(String old, String current) throws NamingException {
		rename(new CompositeName(old), new CompositeName(current));
	}

	public void rename(Name old, Name current) throws NamingException {
		if (closed) {
			throw new OperationNotSupportedException("This context has been closed.");
		}
		try {
			Object value = storage.get(old.toString());
			storage.put(current.toString(), value);
			storage.remove(old.toString());
		} catch (Exception ex) {
			throw new NamingException("Unable to rename from " + old.toString() + "to " + current.toString());
		}
	}

	public void unbind(String name) throws NamingException {
		unbind(new CompositeName(name));		
	}

	public void unbind(Name name) throws NamingException {
		if (closed) {
			throw new OperationNotSupportedException("This context has been closed.");
		}
		try {
			storage.remove(name.toString());
		} catch (Exception ex) {
			throw new NamingException("Unable to unbind " + name);
		}
		
	}
	
	public static int getInvokeCount() {
		return invokeCount;
	}

}
