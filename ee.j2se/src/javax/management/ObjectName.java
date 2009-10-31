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

package javax.management;
public class ObjectName implements java.lang.Comparable<javax.management.ObjectName>, javax.management.QueryExp {
	public final static javax.management.ObjectName WILDCARD; static { WILDCARD = null; }
	public ObjectName(java.lang.String var0) throws javax.management.MalformedObjectNameException { } 
	public ObjectName(java.lang.String var0, java.lang.String var1, java.lang.String var2) throws javax.management.MalformedObjectNameException { } 
	public ObjectName(java.lang.String var0, java.util.Hashtable<java.lang.String,java.lang.String> var1) throws javax.management.MalformedObjectNameException { } 
	public boolean apply(javax.management.ObjectName var0) { return false; }
	public int compareTo(javax.management.ObjectName var0) { return 0; }
	public java.lang.String getCanonicalKeyPropertyListString() { return null; }
	public java.lang.String getCanonicalName() { return null; }
	public java.lang.String getDomain() { return null; }
	public static javax.management.ObjectName getInstance(java.lang.String var0) throws javax.management.MalformedObjectNameException { return null; }
	public static javax.management.ObjectName getInstance(java.lang.String var0, java.lang.String var1, java.lang.String var2) throws javax.management.MalformedObjectNameException { return null; }
	public static javax.management.ObjectName getInstance(java.lang.String var0, java.util.Hashtable<java.lang.String,java.lang.String> var1) throws javax.management.MalformedObjectNameException { return null; }
	public static javax.management.ObjectName getInstance(javax.management.ObjectName var0) { return null; }
	public java.lang.String getKeyProperty(java.lang.String var0) { return null; }
	public java.util.Hashtable<java.lang.String,java.lang.String> getKeyPropertyList() { return null; }
	public java.lang.String getKeyPropertyListString() { return null; }
	public boolean isDomainPattern() { return false; }
	public boolean isPattern() { return false; }
	public boolean isPropertyListPattern() { return false; }
	public boolean isPropertyPattern() { return false; }
	public boolean isPropertyValuePattern() { return false; }
	public boolean isPropertyValuePattern(java.lang.String var0) { return false; }
	public static java.lang.String quote(java.lang.String var0) { return null; }
	public void setMBeanServer(javax.management.MBeanServer var0) { }
	public static java.lang.String unquote(java.lang.String var0) { return null; }
}

