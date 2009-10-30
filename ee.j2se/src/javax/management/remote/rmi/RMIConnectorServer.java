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
public class RMIConnectorServer extends javax.management.remote.JMXConnectorServer {
	public final static java.lang.String JNDI_REBIND_ATTRIBUTE = "jmx.remote.jndi.rebind";
	public final static java.lang.String RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE = "jmx.remote.rmi.client.socket.factory";
	public final static java.lang.String RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE = "jmx.remote.rmi.server.socket.factory";
	public RMIConnectorServer(javax.management.remote.JMXServiceURL var0, java.util.Map<java.lang.String,?> var1) throws java.io.IOException { } 
	public RMIConnectorServer(javax.management.remote.JMXServiceURL var0, java.util.Map<java.lang.String,?> var1, javax.management.MBeanServer var2) throws java.io.IOException { } 
	public RMIConnectorServer(javax.management.remote.JMXServiceURL var0, java.util.Map<java.lang.String,?> var1, javax.management.remote.rmi.RMIServerImpl var2, javax.management.MBeanServer var3) throws java.io.IOException { } 
	public javax.management.remote.JMXServiceURL getAddress() { return null; }
	public java.util.Map<java.lang.String,?> getAttributes() { return null; }
	public boolean isActive() { return false; }
	public void start() throws java.io.IOException { }
	public void stop() throws java.io.IOException { }
}

