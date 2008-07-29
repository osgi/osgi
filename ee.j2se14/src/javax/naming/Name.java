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

package javax.naming;
public abstract interface Name extends java.io.Serializable, java.lang.Cloneable {
	public abstract javax.naming.Name add(int var0, java.lang.String var1) throws javax.naming.InvalidNameException;
	public abstract javax.naming.Name add(java.lang.String var0) throws javax.naming.InvalidNameException;
	public abstract javax.naming.Name addAll(int var0, javax.naming.Name var1) throws javax.naming.InvalidNameException;
	public abstract javax.naming.Name addAll(javax.naming.Name var0) throws javax.naming.InvalidNameException;
	public abstract java.lang.Object clone();
	public abstract int compareTo(java.lang.Object var0);
	public abstract boolean endsWith(javax.naming.Name var0);
	public abstract java.lang.String get(int var0);
	public abstract java.util.Enumeration getAll();
	public abstract javax.naming.Name getPrefix(int var0);
	public abstract javax.naming.Name getSuffix(int var0);
	public abstract boolean isEmpty();
	public abstract java.lang.Object remove(int var0) throws javax.naming.InvalidNameException;
	public abstract int size();
	public abstract boolean startsWith(javax.naming.Name var0);
}

