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
public interface MBeanServerConnection {
	void addNotificationListener(javax.management.ObjectName var0, javax.management.NotificationListener var1, javax.management.NotificationFilter var2, java.lang.Object var3) throws java.io.IOException, javax.management.InstanceNotFoundException;
	void addNotificationListener(javax.management.ObjectName var0, javax.management.ObjectName var1, javax.management.NotificationFilter var2, java.lang.Object var3) throws java.io.IOException, javax.management.InstanceNotFoundException;
	javax.management.ObjectInstance createMBean(java.lang.String var0, javax.management.ObjectName var1) throws java.io.IOException, javax.management.InstanceAlreadyExistsException, javax.management.MBeanException, javax.management.NotCompliantMBeanException, javax.management.ReflectionException;
	javax.management.ObjectInstance createMBean(java.lang.String var0, javax.management.ObjectName var1, javax.management.ObjectName var2) throws java.io.IOException, javax.management.InstanceAlreadyExistsException, javax.management.InstanceNotFoundException, javax.management.MBeanException, javax.management.NotCompliantMBeanException, javax.management.ReflectionException;
	javax.management.ObjectInstance createMBean(java.lang.String var0, javax.management.ObjectName var1, javax.management.ObjectName var2, java.lang.Object[] var3, java.lang.String[] var4) throws java.io.IOException, javax.management.InstanceAlreadyExistsException, javax.management.InstanceNotFoundException, javax.management.MBeanException, javax.management.NotCompliantMBeanException, javax.management.ReflectionException;
	javax.management.ObjectInstance createMBean(java.lang.String var0, javax.management.ObjectName var1, java.lang.Object[] var2, java.lang.String[] var3) throws java.io.IOException, javax.management.InstanceAlreadyExistsException, javax.management.MBeanException, javax.management.NotCompliantMBeanException, javax.management.ReflectionException;
	java.lang.Object getAttribute(javax.management.ObjectName var0, java.lang.String var1) throws java.io.IOException, javax.management.AttributeNotFoundException, javax.management.InstanceNotFoundException, javax.management.MBeanException, javax.management.ReflectionException;
	javax.management.AttributeList getAttributes(javax.management.ObjectName var0, java.lang.String[] var1) throws java.io.IOException, javax.management.InstanceNotFoundException, javax.management.ReflectionException;
	java.lang.String getDefaultDomain() throws java.io.IOException;
	java.lang.String[] getDomains() throws java.io.IOException;
	java.lang.Integer getMBeanCount() throws java.io.IOException;
	javax.management.MBeanInfo getMBeanInfo(javax.management.ObjectName var0) throws java.io.IOException, javax.management.InstanceNotFoundException, javax.management.IntrospectionException, javax.management.ReflectionException;
	javax.management.ObjectInstance getObjectInstance(javax.management.ObjectName var0) throws java.io.IOException, javax.management.InstanceNotFoundException;
	java.lang.Object invoke(javax.management.ObjectName var0, java.lang.String var1, java.lang.Object[] var2, java.lang.String[] var3) throws java.io.IOException, javax.management.InstanceNotFoundException, javax.management.MBeanException, javax.management.ReflectionException;
	boolean isInstanceOf(javax.management.ObjectName var0, java.lang.String var1) throws java.io.IOException, javax.management.InstanceNotFoundException;
	boolean isRegistered(javax.management.ObjectName var0) throws java.io.IOException;
	java.util.Set queryMBeans(javax.management.ObjectName var0, javax.management.QueryExp var1) throws java.io.IOException;
	java.util.Set queryNames(javax.management.ObjectName var0, javax.management.QueryExp var1) throws java.io.IOException;
	void removeNotificationListener(javax.management.ObjectName var0, javax.management.NotificationListener var1) throws java.io.IOException, javax.management.InstanceNotFoundException, javax.management.ListenerNotFoundException;
	void removeNotificationListener(javax.management.ObjectName var0, javax.management.NotificationListener var1, javax.management.NotificationFilter var2, java.lang.Object var3) throws java.io.IOException, javax.management.InstanceNotFoundException, javax.management.ListenerNotFoundException;
	void removeNotificationListener(javax.management.ObjectName var0, javax.management.ObjectName var1) throws java.io.IOException, javax.management.InstanceNotFoundException, javax.management.ListenerNotFoundException;
	void removeNotificationListener(javax.management.ObjectName var0, javax.management.ObjectName var1, javax.management.NotificationFilter var2, java.lang.Object var3) throws java.io.IOException, javax.management.InstanceNotFoundException, javax.management.ListenerNotFoundException;
	void setAttribute(javax.management.ObjectName var0, javax.management.Attribute var1) throws java.io.IOException, javax.management.AttributeNotFoundException, javax.management.InstanceNotFoundException, javax.management.InvalidAttributeValueException, javax.management.MBeanException, javax.management.ReflectionException;
	javax.management.AttributeList setAttributes(javax.management.ObjectName var0, javax.management.AttributeList var1) throws java.io.IOException, javax.management.InstanceNotFoundException, javax.management.ReflectionException;
	void unregisterMBean(javax.management.ObjectName var0) throws java.io.IOException, javax.management.InstanceNotFoundException, javax.management.MBeanRegistrationException;
}

