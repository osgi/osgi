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

package javax.naming;
public class Reference implements java.io.Serializable, java.lang.Cloneable {
	public Reference(java.lang.String var0) { }
	public Reference(java.lang.String var0, java.lang.String var1, java.lang.String var2) { }
	public Reference(java.lang.String var0, javax.naming.RefAddr var1) { }
	public Reference(java.lang.String var0, javax.naming.RefAddr var1, java.lang.String var2, java.lang.String var3) { }
	public void add(int var0, javax.naming.RefAddr var1) { }
	public void add(javax.naming.RefAddr var0) { }
	public void clear() { }
	public java.lang.Object clone() { return null; }
	public javax.naming.RefAddr get(int var0) { return null; }
	public javax.naming.RefAddr get(java.lang.String var0) { return null; }
	public java.util.Enumeration getAll() { return null; }
	public java.lang.String getClassName() { return null; }
	public java.lang.String getFactoryClassLocation() { return null; }
	public java.lang.String getFactoryClassName() { return null; }
	public int hashCode() { return 0; }
	public java.lang.Object remove(int var0) { return null; }
	public int size() { return 0; }
	protected java.util.Vector addrs;
	protected java.lang.String classFactory;
	protected java.lang.String classFactoryLocation;
	protected java.lang.String className;
}

