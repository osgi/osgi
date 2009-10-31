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

package javax.management.remote;
public interface JMXConnector extends java.io.Closeable {
	public final static java.lang.String CREDENTIALS = "jmx.remote.credentials";
	void addConnectionNotificationListener(javax.management.NotificationListener var0, javax.management.NotificationFilter var1, java.lang.Object var2);
	void connect() throws java.io.IOException;
	void connect(java.util.Map<java.lang.String,?> var0) throws java.io.IOException;
	java.lang.String getConnectionId() throws java.io.IOException;
	javax.management.MBeanServerConnection getMBeanServerConnection() throws java.io.IOException;
	javax.management.MBeanServerConnection getMBeanServerConnection(javax.security.auth.Subject var0) throws java.io.IOException;
	void removeConnectionNotificationListener(javax.management.NotificationListener var0) throws javax.management.ListenerNotFoundException;
	void removeConnectionNotificationListener(javax.management.NotificationListener var0, javax.management.NotificationFilter var1, java.lang.Object var2) throws javax.management.ListenerNotFoundException;
}

