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

package javax.management.remote.rmi;
public abstract class RMIServerImpl implements java.io.Closeable, javax.management.remote.rmi.RMIServer {
	public RMIServerImpl(java.util.Map<java.lang.String,?> var0) { } 
	protected void clientClosed(javax.management.remote.rmi.RMIConnection var0) throws java.io.IOException { }
	public void close() throws java.io.IOException { }
	protected abstract void closeClient(javax.management.remote.rmi.RMIConnection var0) throws java.io.IOException;
	protected abstract void closeServer() throws java.io.IOException;
	protected abstract void export() throws java.io.IOException;
	public java.lang.ClassLoader getDefaultClassLoader() { return null; }
	public javax.management.MBeanServer getMBeanServer() { return null; }
	protected abstract java.lang.String getProtocol();
	public java.lang.String getVersion() { return null; }
	protected abstract javax.management.remote.rmi.RMIConnection makeClient(java.lang.String var0, javax.security.auth.Subject var1) throws java.io.IOException;
	public javax.management.remote.rmi.RMIConnection newClient(java.lang.Object var0) throws java.io.IOException { return null; }
	public void setDefaultClassLoader(java.lang.ClassLoader var0) { }
	public void setMBeanServer(javax.management.MBeanServer var0) { }
	public abstract java.rmi.Remote toStub() throws java.io.IOException;
}

