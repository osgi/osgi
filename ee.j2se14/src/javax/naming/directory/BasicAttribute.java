/*
 * $Date$
 *
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
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

package javax.naming.directory;
public class BasicAttribute implements javax.naming.directory.Attribute {
	public BasicAttribute(java.lang.String var0) { }
	public BasicAttribute(java.lang.String var0, java.lang.Object var1) { }
	public BasicAttribute(java.lang.String var0, java.lang.Object var1, boolean var2) { }
	public BasicAttribute(java.lang.String var0, boolean var1) { }
	public void add(int var0, java.lang.Object var1) { }
	public boolean add(java.lang.Object var0) { return false; }
	public void clear() { }
	public java.lang.Object clone() { return null; }
	public boolean contains(java.lang.Object var0) { return false; }
	public java.lang.Object get() throws javax.naming.NamingException { return null; }
	public java.lang.Object get(int var0) throws javax.naming.NamingException { return null; }
	public javax.naming.NamingEnumeration getAll() throws javax.naming.NamingException { return null; }
	public javax.naming.directory.DirContext getAttributeDefinition() throws javax.naming.NamingException { return null; }
	public javax.naming.directory.DirContext getAttributeSyntaxDefinition() throws javax.naming.NamingException { return null; }
	public java.lang.String getID() { return null; }
	public int hashCode() { return 0; }
	public boolean isOrdered() { return false; }
	public java.lang.Object remove(int var0) { return null; }
	public boolean remove(java.lang.Object var0) { return false; }
	public java.lang.Object set(int var0, java.lang.Object var1) { return null; }
	public int size() { return 0; }
	protected java.lang.String attrID;
	protected boolean ordered;
	protected java.util.Vector values;
}

