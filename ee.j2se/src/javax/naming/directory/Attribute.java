/*
 * $Revision$
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
public abstract interface Attribute extends java.io.Serializable, java.lang.Cloneable {
	public abstract void add(int var0, java.lang.Object var1);
	public abstract boolean add(java.lang.Object var0);
	public abstract void clear();
	public abstract java.lang.Object clone();
	public abstract boolean contains(java.lang.Object var0);
	public abstract java.lang.Object get() throws javax.naming.NamingException;
	public abstract java.lang.Object get(int var0) throws javax.naming.NamingException;
	public abstract javax.naming.NamingEnumeration getAll() throws javax.naming.NamingException;
	public abstract javax.naming.directory.DirContext getAttributeDefinition() throws javax.naming.NamingException;
	public abstract javax.naming.directory.DirContext getAttributeSyntaxDefinition() throws javax.naming.NamingException;
	public abstract java.lang.String getID();
	public abstract boolean isOrdered();
	public abstract java.lang.Object remove(int var0);
	public abstract boolean remove(java.lang.Object var0);
	public abstract java.lang.Object set(int var0, java.lang.Object var1);
	public abstract int size();
	public final static long serialVersionUID = 8707690322213556804l;
}

