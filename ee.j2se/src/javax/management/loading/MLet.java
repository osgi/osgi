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

package javax.management.loading;
public class MLet extends java.net.URLClassLoader implements java.io.Externalizable, javax.management.MBeanRegistration, javax.management.loading.MLetMBean {
	public MLet()  { super((java.net.URL[]) null, (java.lang.ClassLoader) null, (java.net.URLStreamHandlerFactory) null); } 
	public MLet(java.net.URL[] var0)  { super((java.net.URL[]) null, (java.lang.ClassLoader) null, (java.net.URLStreamHandlerFactory) null); } 
	public MLet(java.net.URL[] var0, java.lang.ClassLoader var1)  { super((java.net.URL[]) null, (java.lang.ClassLoader) null, (java.net.URLStreamHandlerFactory) null); } 
	public MLet(java.net.URL[] var0, java.lang.ClassLoader var1, java.net.URLStreamHandlerFactory var2)  { super((java.net.URL[]) null, (java.lang.ClassLoader) null, (java.net.URLStreamHandlerFactory) null); } 
	public MLet(java.net.URL[] var0, java.lang.ClassLoader var1, java.net.URLStreamHandlerFactory var2, boolean var3)  { super((java.net.URL[]) null, (java.lang.ClassLoader) null, (java.net.URLStreamHandlerFactory) null); } 
	public MLet(java.net.URL[] var0, java.lang.ClassLoader var1, boolean var2)  { super((java.net.URL[]) null, (java.lang.ClassLoader) null, (java.net.URLStreamHandlerFactory) null); } 
	public MLet(java.net.URL[] var0, boolean var1)  { super((java.net.URL[]) null, (java.lang.ClassLoader) null, (java.net.URLStreamHandlerFactory) null); } 
	public void addURL(java.lang.String var0) throws javax.management.ServiceNotFoundException { }
	public void addURL(java.net.URL var0) { }
	protected java.net.URL check(java.lang.String var0, java.net.URL var1, java.lang.String var2, javax.management.loading.MLetContent var3) throws java.lang.Exception { return null; }
	public java.lang.String getLibraryDirectory() { return null; }
	public java.util.Set<java.lang.Object> getMBeansFromURL(java.lang.String var0) throws javax.management.ServiceNotFoundException { return null; }
	public java.util.Set<java.lang.Object> getMBeansFromURL(java.net.URL var0) throws javax.management.ServiceNotFoundException { return null; }
	public java.lang.Class<?> loadClass(java.lang.String var0, javax.management.loading.ClassLoaderRepository var1) throws java.lang.ClassNotFoundException { return null; }
	public void postDeregister() { }
	public void postRegister(java.lang.Boolean var0) { }
	public void preDeregister() throws java.lang.Exception { }
	public javax.management.ObjectName preRegister(javax.management.MBeanServer var0, javax.management.ObjectName var1) throws java.lang.Exception { return null; }
	public void readExternal(java.io.ObjectInput var0) throws java.io.IOException, java.lang.ClassNotFoundException { }
	public void setLibraryDirectory(java.lang.String var0) { }
	public void writeExternal(java.io.ObjectOutput var0) throws java.io.IOException { }
}

