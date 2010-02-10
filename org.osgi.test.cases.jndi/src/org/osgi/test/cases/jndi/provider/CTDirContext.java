/*
 * Copyright (c) IBM Corporation (2010). All Rights Reserved.
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
import java.util.Map;

import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

/**
 * @version $Revision$ $Date$
 */
public class CTDirContext extends CTContext implements DirContext {
	
	protected static Map attributeMap = new HashMap();

	public CTDirContext() throws NamingException {
		super();
	}
	
	public CTDirContext(Map env) throws NamingException {
		super();
	}
	public void bind(String name, Object obj, Attributes attrs)	throws NamingException {
		bind(new CompositeName(name), obj, attrs);
	}

	public void bind(Name name, Object obj, Attributes attrs) throws NamingException {
		bind(name, obj);
		attributeMap.put(name.toString(), attrs);
	}

	public DirContext createSubcontext(String name, Attributes attrs) throws NamingException {
		return createSubcontext(new CompositeName(name), attrs);
	}

	public DirContext createSubcontext(Name name, Attributes attrs)	throws NamingException {
		throw new OperationNotSupportedException("Subcontexts are not supported");
	}

	public Attributes getAttributes(String name) throws NamingException {
		return getAttributes(new CompositeName(name));
	}

	public Attributes getAttributes(Name name) throws NamingException {
		return (Attributes) attributeMap.get(name.toString());
	}

	public Attributes getAttributes(String name, String[] attrIds) throws NamingException {
		return getAttributes(new CompositeName(name), attrIds);
	}

	public Attributes getAttributes(Name name, String[] attrIds) throws NamingException {
		Attributes retrievedAttributes = (Attributes) attributeMap.get(name.toString());
		Attributes selectedAttributes = new BasicAttributes();
		
		for (int i=0; i < attrIds.length; i++) {
			selectedAttributes.put(retrievedAttributes.get(attrIds[i]));
		}
		
		return selectedAttributes;
	}

	public DirContext getSchema(String name) throws NamingException {
		return getSchema(new CompositeName(name));
	}

	public DirContext getSchema(Name name) throws NamingException {
		throw new OperationNotSupportedException("Schema are not supported");
	}

	public DirContext getSchemaClassDefinition(String name)	throws NamingException {
		return getSchemaClassDefinition(new CompositeName(name));
	}

	public DirContext getSchemaClassDefinition(Name name) throws NamingException {
		throw new OperationNotSupportedException("Schema are not supported");
	}

	public void modifyAttributes(String var0, ModificationItem[] var1) throws NamingException {
		throw new OperationNotSupportedException("modifyAttributes is not supported");
	}

	public void modifyAttributes(Name var0, ModificationItem[] var1) throws NamingException {
		throw new OperationNotSupportedException("modifyAttributes is not supported");
	}

	public void modifyAttributes(String var0, int var1, Attributes var2) throws NamingException {
		throw new OperationNotSupportedException("modifyAttributes is not supported");
	}

	public void modifyAttributes(Name var0, int var1, Attributes var2) throws NamingException {
		throw new OperationNotSupportedException("modifyAttributes is not supported");
	}

	public void rebind(String var0, Object var1, Attributes var2) throws NamingException {
		throw new OperationNotSupportedException("rebind is not supported");
	}

	public void rebind(Name var0, Object var1, Attributes var2)	throws NamingException {
		throw new OperationNotSupportedException("rebind is not supported");
	}

	public NamingEnumeration search(String var0, Attributes var1)	throws NamingException {
		throw new OperationNotSupportedException("search is not supported");
	}

	public NamingEnumeration search(Name var0, Attributes var1) throws NamingException {
		throw new OperationNotSupportedException("search is not supported");
	}

	public NamingEnumeration search(String var0, String var1, SearchControls var2) throws NamingException {
		throw new OperationNotSupportedException("search is not supported");
	}

	public NamingEnumeration search(String var0, Attributes var1, String[] var2) throws NamingException {
		throw new OperationNotSupportedException("search is not supported");
	}

	public NamingEnumeration search(Name var0, String var1, SearchControls var2) throws NamingException {
		throw new OperationNotSupportedException("search is not supported");
	}

	public NamingEnumeration search(Name var0, Attributes var1, String[] var2) throws NamingException {
		throw new OperationNotSupportedException("search is not supported");
	}

	public NamingEnumeration search(String var0, String var1,	Object[] var2, SearchControls var3) throws NamingException {
		throw new OperationNotSupportedException("search is not supported");
	}

	public NamingEnumeration search(Name var0, String var1, Object[] var2, SearchControls var3) throws NamingException {
		throw new OperationNotSupportedException("search is not supported");
	}

}
