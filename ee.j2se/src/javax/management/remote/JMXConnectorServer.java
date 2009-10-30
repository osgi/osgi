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
public abstract class JMXConnectorServer extends javax.management.NotificationBroadcasterSupport implements javax.management.MBeanRegistration, javax.management.remote.JMXConnectorServerMBean {
	public final static java.lang.String AUTHENTICATOR = "jmx.remote.authenticator";
	public JMXConnectorServer() { } 
	public JMXConnectorServer(javax.management.MBeanServer var0) { } 
	protected void connectionClosed(java.lang.String var0, java.lang.String var1, java.lang.Object var2) { }
	protected void connectionFailed(java.lang.String var0, java.lang.String var1, java.lang.Object var2) { }
	protected void connectionOpened(java.lang.String var0, java.lang.String var1, java.lang.Object var2) { }
	public java.lang.String[] getConnectionIds() { return null; }
	public javax.management.MBeanServer getMBeanServer() { return null; }
	public void postDeregister() { }
	public void postRegister(java.lang.Boolean var0) { }
	public void preDeregister() throws java.lang.Exception { }
	public javax.management.ObjectName preRegister(javax.management.MBeanServer var0, javax.management.ObjectName var1) { return null; }
	public void setMBeanServerForwarder(javax.management.remote.MBeanServerForwarder var0) { }
	public javax.management.remote.JMXConnector toJMXConnector(java.util.Map<java.lang.String,?> var0) throws java.io.IOException { return null; }
}

