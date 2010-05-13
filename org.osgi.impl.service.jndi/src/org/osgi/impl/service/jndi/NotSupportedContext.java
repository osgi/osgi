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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;

/**
 * This class implements an adapter for use in creating JNDI Context
 * implementations.  
 * 
 * This implementation throws the OperationNotSupportedException in each
 * method implementation for Context.  This allows subclasses to only override
 * the behavior that should be supported.  
 *
 * @version $Id$
 */
class NotSupportedContext implements Context {

	private final String m_exceptionMessage;
	
	public NotSupportedContext(String exceptionMessage) {
		m_exceptionMessage = exceptionMessage;
	}
	
	public Object addToEnvironment(String var0, Object var1) throws NamingException {
		operationNotSupported();
		return null;
	}

	public void bind(String var0, Object var1) throws NamingException {
		operationNotSupported();
	}

	public void bind(Name var0, Object var1) throws NamingException {
		operationNotSupported();
	}

	public void close() throws NamingException {
		operationNotSupported();
	}

	public String composeName(String var0, String var1) throws NamingException {
		operationNotSupported();
		return null;
	}

	public Name composeName(Name var0, Name var1) throws NamingException {
		operationNotSupported();
		return null;
	}

	public Context createSubcontext(String var0) throws NamingException {
		operationNotSupported();
		return null;
	}

	public Context createSubcontext(Name var0) throws NamingException {
		operationNotSupported();
		return null;
	}

	public void destroySubcontext(String var0) throws NamingException {
		operationNotSupported();
	}

	public void destroySubcontext(Name var0) throws NamingException {
		operationNotSupported();
	}

	public Hashtable getEnvironment() throws NamingException {
		operationNotSupported();
		return null;
	}

	public String getNameInNamespace() throws NamingException {
		operationNotSupported();
		return null;
	}

	public NameParser getNameParser(String var0) throws NamingException {
		operationNotSupported();
		return null;
	}

	public NameParser getNameParser(Name var0) throws NamingException {
		operationNotSupported();
		return null;
	}

	public NamingEnumeration list(String var0) throws NamingException {
		operationNotSupported();
		return null;
	}

	public NamingEnumeration list(Name var0) throws NamingException {
		operationNotSupported();
		return null;
	}

	public NamingEnumeration listBindings(String var0) throws NamingException {
		operationNotSupported();
		return null;
	}

	public NamingEnumeration listBindings(Name var0) throws NamingException {
		operationNotSupported();
		return null;
	}

	public Object lookup(String var0) throws NamingException {
		operationNotSupported();
		return null;
	}

	public Object lookup(Name var0) throws NamingException {
		operationNotSupported();
		return null;
	}

	public Object lookupLink(String var0) throws NamingException {
		operationNotSupported();
		return null;
	}

	public Object lookupLink(Name var0) throws NamingException {
		operationNotSupported();
		return null;
	}

	public void rebind(String var0, Object var1) throws NamingException {
		operationNotSupported();
	}

	public void rebind(Name var0, Object var1) throws NamingException {
		operationNotSupported();
	}

	public Object removeFromEnvironment(String var0) throws NamingException {
		operationNotSupported();
		return null;
	}

	public void rename(String var0, String var1) throws NamingException {
		operationNotSupported();
	}

	public void rename(Name var0, Name var1) throws NamingException {
		operationNotSupported();
	}

	public void unbind(String var0) throws NamingException {
		operationNotSupported();
	}

	public void unbind(Name var0) throws NamingException {
		operationNotSupported();
	}
	
	private void operationNotSupported() throws OperationNotSupportedException {
		throw new OperationNotSupportedException(m_exceptionMessage);
	}

}
