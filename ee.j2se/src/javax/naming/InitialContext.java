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
public class InitialContext implements javax.naming.Context {
	public InitialContext() throws javax.naming.NamingException { }
	public InitialContext(java.util.Hashtable var0) throws javax.naming.NamingException { }
	protected InitialContext(boolean var0) throws javax.naming.NamingException { }
	public java.lang.Object addToEnvironment(java.lang.String var0, java.lang.Object var1) throws javax.naming.NamingException { return null; }
	public void bind(java.lang.String var0, java.lang.Object var1) throws javax.naming.NamingException { }
	public void bind(javax.naming.Name var0, java.lang.Object var1) throws javax.naming.NamingException { }
	public void close() throws javax.naming.NamingException { }
	public java.lang.String composeName(java.lang.String var0, java.lang.String var1) throws javax.naming.NamingException { return null; }
	public javax.naming.Name composeName(javax.naming.Name var0, javax.naming.Name var1) throws javax.naming.NamingException { return null; }
	public javax.naming.Context createSubcontext(java.lang.String var0) throws javax.naming.NamingException { return null; }
	public javax.naming.Context createSubcontext(javax.naming.Name var0) throws javax.naming.NamingException { return null; }
	public void destroySubcontext(java.lang.String var0) throws javax.naming.NamingException { }
	public void destroySubcontext(javax.naming.Name var0) throws javax.naming.NamingException { }
	protected javax.naming.Context getDefaultInitCtx() throws javax.naming.NamingException { return null; }
	public java.util.Hashtable getEnvironment() throws javax.naming.NamingException { return null; }
	public java.lang.String getNameInNamespace() throws javax.naming.NamingException { return null; }
	public javax.naming.NameParser getNameParser(java.lang.String var0) throws javax.naming.NamingException { return null; }
	public javax.naming.NameParser getNameParser(javax.naming.Name var0) throws javax.naming.NamingException { return null; }
	protected javax.naming.Context getURLOrDefaultInitCtx(java.lang.String var0) throws javax.naming.NamingException { return null; }
	protected javax.naming.Context getURLOrDefaultInitCtx(javax.naming.Name var0) throws javax.naming.NamingException { return null; }
	protected void init(java.util.Hashtable var0) throws javax.naming.NamingException { }
	public javax.naming.NamingEnumeration list(java.lang.String var0) throws javax.naming.NamingException { return null; }
	public javax.naming.NamingEnumeration list(javax.naming.Name var0) throws javax.naming.NamingException { return null; }
	public javax.naming.NamingEnumeration listBindings(java.lang.String var0) throws javax.naming.NamingException { return null; }
	public javax.naming.NamingEnumeration listBindings(javax.naming.Name var0) throws javax.naming.NamingException { return null; }
	public java.lang.Object lookup(java.lang.String var0) throws javax.naming.NamingException { return null; }
	public java.lang.Object lookup(javax.naming.Name var0) throws javax.naming.NamingException { return null; }
	public java.lang.Object lookupLink(java.lang.String var0) throws javax.naming.NamingException { return null; }
	public java.lang.Object lookupLink(javax.naming.Name var0) throws javax.naming.NamingException { return null; }
	public void rebind(java.lang.String var0, java.lang.Object var1) throws javax.naming.NamingException { }
	public void rebind(javax.naming.Name var0, java.lang.Object var1) throws javax.naming.NamingException { }
	public java.lang.Object removeFromEnvironment(java.lang.String var0) throws javax.naming.NamingException { return null; }
	public void rename(java.lang.String var0, java.lang.String var1) throws javax.naming.NamingException { }
	public void rename(javax.naming.Name var0, javax.naming.Name var1) throws javax.naming.NamingException { }
	public void unbind(java.lang.String var0) throws javax.naming.NamingException { }
	public void unbind(javax.naming.Name var0) throws javax.naming.NamingException { }
	protected javax.naming.Context defaultInitCtx;
	protected boolean gotDefault;
	protected java.util.Hashtable myProps;
}

