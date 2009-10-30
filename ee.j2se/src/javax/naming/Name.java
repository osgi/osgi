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
public interface Name extends java.io.Serializable, java.lang.Cloneable, java.lang.Comparable<java.lang.Object> {
	public final static long serialVersionUID = -3617482732056931635l;
	javax.naming.Name add(int var0, java.lang.String var1) throws javax.naming.InvalidNameException;
	javax.naming.Name add(java.lang.String var0) throws javax.naming.InvalidNameException;
	javax.naming.Name addAll(int var0, javax.naming.Name var1) throws javax.naming.InvalidNameException;
	javax.naming.Name addAll(javax.naming.Name var0) throws javax.naming.InvalidNameException;
	java.lang.Object clone();
	int compareTo(java.lang.Object var0);
	boolean endsWith(javax.naming.Name var0);
	java.lang.String get(int var0);
	java.util.Enumeration<java.lang.String> getAll();
	javax.naming.Name getPrefix(int var0);
	javax.naming.Name getSuffix(int var0);
	boolean isEmpty();
	java.lang.Object remove(int var0) throws javax.naming.InvalidNameException;
	int size();
	boolean startsWith(javax.naming.Name var0);
}

