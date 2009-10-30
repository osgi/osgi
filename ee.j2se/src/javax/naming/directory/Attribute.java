/*
 * Copyright (c) OSGi Alliance (2001, 2009). All Rights Reserved.
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
public interface Attribute extends java.io.Serializable, java.lang.Cloneable {
	public final static long serialVersionUID = 8707690322213556804l;
	void add(int var0, java.lang.Object var1);
	boolean add(java.lang.Object var0);
	void clear();
	java.lang.Object clone();
	boolean contains(java.lang.Object var0);
	java.lang.Object get() throws javax.naming.NamingException;
	java.lang.Object get(int var0) throws javax.naming.NamingException;
	javax.naming.NamingEnumeration<?> getAll() throws javax.naming.NamingException;
	javax.naming.directory.DirContext getAttributeDefinition() throws javax.naming.NamingException;
	javax.naming.directory.DirContext getAttributeSyntaxDefinition() throws javax.naming.NamingException;
	java.lang.String getID();
	boolean isOrdered();
	java.lang.Object remove(int var0);
	boolean remove(java.lang.Object var0);
	java.lang.Object set(int var0, java.lang.Object var1);
	int size();
}

