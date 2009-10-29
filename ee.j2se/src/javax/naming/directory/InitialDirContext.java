/*
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
public class InitialDirContext extends javax.naming.InitialContext implements javax.naming.directory.DirContext {
	public InitialDirContext() throws javax.naming.NamingException { }
	public InitialDirContext(java.util.Hashtable var0) throws javax.naming.NamingException { }
	protected InitialDirContext(boolean var0) throws javax.naming.NamingException { }
	public void bind(java.lang.String var0, java.lang.Object var1, javax.naming.directory.Attributes var2) throws javax.naming.NamingException { }
	public void bind(javax.naming.Name var0, java.lang.Object var1, javax.naming.directory.Attributes var2) throws javax.naming.NamingException { }
	public javax.naming.directory.DirContext createSubcontext(java.lang.String var0, javax.naming.directory.Attributes var1) throws javax.naming.NamingException { return null; }
	public javax.naming.directory.DirContext createSubcontext(javax.naming.Name var0, javax.naming.directory.Attributes var1) throws javax.naming.NamingException { return null; }
	public javax.naming.directory.Attributes getAttributes(java.lang.String var0) throws javax.naming.NamingException { return null; }
	public javax.naming.directory.Attributes getAttributes(java.lang.String var0, java.lang.String[] var1) throws javax.naming.NamingException { return null; }
	public javax.naming.directory.Attributes getAttributes(javax.naming.Name var0) throws javax.naming.NamingException { return null; }
	public javax.naming.directory.Attributes getAttributes(javax.naming.Name var0, java.lang.String[] var1) throws javax.naming.NamingException { return null; }
	public javax.naming.directory.DirContext getSchema(java.lang.String var0) throws javax.naming.NamingException { return null; }
	public javax.naming.directory.DirContext getSchema(javax.naming.Name var0) throws javax.naming.NamingException { return null; }
	public javax.naming.directory.DirContext getSchemaClassDefinition(java.lang.String var0) throws javax.naming.NamingException { return null; }
	public javax.naming.directory.DirContext getSchemaClassDefinition(javax.naming.Name var0) throws javax.naming.NamingException { return null; }
	public void modifyAttributes(java.lang.String var0, int var1, javax.naming.directory.Attributes var2) throws javax.naming.NamingException { }
	public void modifyAttributes(java.lang.String var0, javax.naming.directory.ModificationItem[] var1) throws javax.naming.NamingException { }
	public void modifyAttributes(javax.naming.Name var0, int var1, javax.naming.directory.Attributes var2) throws javax.naming.NamingException { }
	public void modifyAttributes(javax.naming.Name var0, javax.naming.directory.ModificationItem[] var1) throws javax.naming.NamingException { }
	public void rebind(java.lang.String var0, java.lang.Object var1, javax.naming.directory.Attributes var2) throws javax.naming.NamingException { }
	public void rebind(javax.naming.Name var0, java.lang.Object var1, javax.naming.directory.Attributes var2) throws javax.naming.NamingException { }
	public javax.naming.NamingEnumeration search(java.lang.String var0, java.lang.String var1, javax.naming.directory.SearchControls var2) throws javax.naming.NamingException { return null; }
	public javax.naming.NamingEnumeration search(java.lang.String var0, java.lang.String var1, java.lang.Object[] var2, javax.naming.directory.SearchControls var3) throws javax.naming.NamingException { return null; }
	public javax.naming.NamingEnumeration search(java.lang.String var0, javax.naming.directory.Attributes var1) throws javax.naming.NamingException { return null; }
	public javax.naming.NamingEnumeration search(java.lang.String var0, javax.naming.directory.Attributes var1, java.lang.String[] var2) throws javax.naming.NamingException { return null; }
	public javax.naming.NamingEnumeration search(javax.naming.Name var0, java.lang.String var1, javax.naming.directory.SearchControls var2) throws javax.naming.NamingException { return null; }
	public javax.naming.NamingEnumeration search(javax.naming.Name var0, java.lang.String var1, java.lang.Object[] var2, javax.naming.directory.SearchControls var3) throws javax.naming.NamingException { return null; }
	public javax.naming.NamingEnumeration search(javax.naming.Name var0, javax.naming.directory.Attributes var1) throws javax.naming.NamingException { return null; }
	public javax.naming.NamingEnumeration search(javax.naming.Name var0, javax.naming.directory.Attributes var1, java.lang.String[] var2) throws javax.naming.NamingException { return null; }
}

