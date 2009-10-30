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

package javax.naming.ldap;
public class LdapName implements javax.naming.Name {
	private final static long serialVersionUID = -1595520034788997356l;
	public LdapName(java.lang.String var0) throws javax.naming.InvalidNameException { } 
	public LdapName(java.util.List<javax.naming.ldap.Rdn> var0) { } 
	public javax.naming.Name add(int var0, java.lang.String var1) throws javax.naming.InvalidNameException { return null; }
	public javax.naming.Name add(int var0, javax.naming.ldap.Rdn var1) { return null; }
	public javax.naming.Name add(java.lang.String var0) throws javax.naming.InvalidNameException { return null; }
	public javax.naming.Name add(javax.naming.ldap.Rdn var0) { return null; }
	public javax.naming.Name addAll(int var0, java.util.List<javax.naming.ldap.Rdn> var1) { return null; }
	public javax.naming.Name addAll(int var0, javax.naming.Name var1) throws javax.naming.InvalidNameException { return null; }
	public javax.naming.Name addAll(java.util.List<javax.naming.ldap.Rdn> var0) { return null; }
	public javax.naming.Name addAll(javax.naming.Name var0) throws javax.naming.InvalidNameException { return null; }
	public java.lang.Object clone() { return null; }
	public int compareTo(java.lang.Object var0) { return 0; }
	public boolean endsWith(java.util.List<javax.naming.ldap.Rdn> var0) { return false; }
	public boolean endsWith(javax.naming.Name var0) { return false; }
	public java.lang.String get(int var0) { return null; }
	public java.util.Enumeration<java.lang.String> getAll() { return null; }
	public javax.naming.Name getPrefix(int var0) { return null; }
	public javax.naming.ldap.Rdn getRdn(int var0) { return null; }
	public java.util.List<javax.naming.ldap.Rdn> getRdns() { return null; }
	public javax.naming.Name getSuffix(int var0) { return null; }
	public int hashCode() { return 0; }
	public boolean isEmpty() { return false; }
	public java.lang.Object remove(int var0) throws javax.naming.InvalidNameException { return null; }
	public int size() { return 0; }
	public boolean startsWith(java.util.List<javax.naming.ldap.Rdn> var0) { return false; }
	public boolean startsWith(javax.naming.Name var0) { return false; }
}

