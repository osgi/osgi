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
public class RMIConnector implements java.io.Serializable, javax.management.remote.JMXAddressable, javax.management.remote.JMXConnector {
	public RMIConnector(javax.management.remote.JMXServiceURL var0, java.util.Map<java.lang.String,?> var1) { } 
	public RMIConnector(javax.management.remote.rmi.RMIServer var0, java.util.Map<java.lang.String,?> var1) { } 
	public void addConnectionNotificationListener(javax.management.NotificationListener var0, javax.management.NotificationFilter var1, java.lang.Object var2) { }
	public void close() throws java.io.IOException { }
	public void connect() throws java.io.IOException { }
	public void connect(java.util.Map<java.lang.String,?> var0) throws java.io.IOException { }
	public javax.management.remote.JMXServiceURL getAddress() { return null; }
	public java.lang.String getConnectionId() throws java.io.IOException { return null; }
	public javax.management.MBeanServerConnection getMBeanServerConnection() throws java.io.IOException { return null; }
	public javax.management.MBeanServerConnection getMBeanServerConnection(javax.security.auth.Subject var0) throws java.io.IOException { return null; }
	public void removeConnectionNotificationListener(javax.management.NotificationListener var0) throws javax.management.ListenerNotFoundException { }
	public void removeConnectionNotificationListener(javax.management.NotificationListener var0, javax.management.NotificationFilter var1, java.lang.Object var2) throws javax.management.ListenerNotFoundException { }
}

