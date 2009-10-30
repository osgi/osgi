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
public class Rdn implements java.io.Serializable, java.lang.Comparable<java.lang.Object> {
	public Rdn(java.lang.String var0) throws javax.naming.InvalidNameException { } 
	public Rdn(java.lang.String var0, java.lang.Object var1) throws javax.naming.InvalidNameException { } 
	public Rdn(javax.naming.directory.Attributes var0) throws javax.naming.InvalidNameException { } 
	public Rdn(javax.naming.ldap.Rdn var0) { } 
	public int compareTo(java.lang.Object var0) { return 0; }
	public static java.lang.String escapeValue(java.lang.Object var0) { return null; }
	public java.lang.String getType() { return null; }
	public java.lang.Object getValue() { return null; }
	public int hashCode() { return 0; }
	public int size() { return 0; }
	public javax.naming.directory.Attributes toAttributes() { return null; }
	public static java.lang.Object unescapeValue(java.lang.String var0) { return null; }
}

