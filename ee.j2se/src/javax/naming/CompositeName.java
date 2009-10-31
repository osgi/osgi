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

package javax.naming;
public class CompositeName implements javax.naming.Name {
	private final static long serialVersionUID = 1667768148915813118l;
	public CompositeName() { } 
	public CompositeName(java.lang.String var0) throws javax.naming.InvalidNameException { } 
	protected CompositeName(java.util.Enumeration<java.lang.String> var0) { } 
	public javax.naming.Name add(int var0, java.lang.String var1) throws javax.naming.InvalidNameException { return null; }
	public javax.naming.Name add(java.lang.String var0) throws javax.naming.InvalidNameException { return null; }
	public javax.naming.Name addAll(int var0, javax.naming.Name var1) throws javax.naming.InvalidNameException { return null; }
	public javax.naming.Name addAll(javax.naming.Name var0) throws javax.naming.InvalidNameException { return null; }
	public java.lang.Object clone() { return null; }
	public int compareTo(java.lang.Object var0) { return 0; }
	public boolean endsWith(javax.naming.Name var0) { return false; }
	public java.lang.String get(int var0) { return null; }
	public java.util.Enumeration<java.lang.String> getAll() { return null; }
	public javax.naming.Name getPrefix(int var0) { return null; }
	public javax.naming.Name getSuffix(int var0) { return null; }
	public boolean isEmpty() { return false; }
	public java.lang.Object remove(int var0) throws javax.naming.InvalidNameException { return null; }
	public int size() { return 0; }
	public boolean startsWith(javax.naming.Name var0) { return false; }
}

