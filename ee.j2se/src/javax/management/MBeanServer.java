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
public interface MBeanServer extends javax.management.MBeanServerConnection {
	void addNotificationListener(javax.management.ObjectName var0, javax.management.NotificationListener var1, javax.management.NotificationFilter var2, java.lang.Object var3) throws javax.management.InstanceNotFoundException;
	void addNotificationListener(javax.management.ObjectName var0, javax.management.ObjectName var1, javax.management.NotificationFilter var2, java.lang.Object var3) throws javax.management.InstanceNotFoundException;
	javax.management.ObjectInstance createMBean(java.lang.String var0, javax.management.ObjectName var1) throws javax.management.InstanceAlreadyExistsException, javax.management.MBeanException, javax.management.NotCompliantMBeanException, javax.management.ReflectionException;
	javax.management.ObjectInstance createMBean(java.lang.String var0, javax.management.ObjectName var1, javax.management.ObjectName var2) throws javax.management.InstanceAlreadyExistsException, javax.management.InstanceNotFoundException, javax.management.MBeanException, javax.management.NotCompliantMBeanException, javax.management.ReflectionException;
	javax.management.ObjectInstance createMBean(java.lang.String var0, javax.management.ObjectName var1, javax.management.ObjectName var2, java.lang.Object[] var3, java.lang.String[] var4) throws javax.management.InstanceAlreadyExistsException, javax.management.InstanceNotFoundException, javax.management.MBeanException, javax.management.NotCompliantMBeanException, javax.management.ReflectionException;
	javax.management.ObjectInstance createMBean(java.lang.String var0, javax.management.ObjectName var1, java.lang.Object[] var2, java.lang.String[] var3) throws javax.management.InstanceAlreadyExistsException, javax.management.MBeanException, javax.management.NotCompliantMBeanException, javax.management.ReflectionException;
	/** @deprecated */ java.io.ObjectInputStream deserialize(java.lang.String var0, javax.management.ObjectName var1, byte[] var2) throws javax.management.OperationsException, javax.management.ReflectionException;
	/** @deprecated */ java.io.ObjectInputStream deserialize(java.lang.String var0, byte[] var1) throws javax.management.OperationsException, javax.management.ReflectionException;
	/** @deprecated */ java.io.ObjectInputStream deserialize(javax.management.ObjectName var0, byte[] var1) throws javax.management.OperationsException;
	java.lang.Object getAttribute(javax.management.ObjectName var0, java.lang.String var1) throws javax.management.AttributeNotFoundException, javax.management.InstanceNotFoundException, javax.management.MBeanException, javax.management.ReflectionException;
	javax.management.AttributeList getAttributes(javax.management.ObjectName var0, java.lang.String[] var1) throws javax.management.InstanceNotFoundException, javax.management.ReflectionException;
	java.lang.ClassLoader getClassLoader(javax.management.ObjectName var0) throws javax.management.InstanceNotFoundException;
	java.lang.ClassLoader getClassLoaderFor(javax.management.ObjectName var0) throws javax.management.InstanceNotFoundException;
	javax.management.loading.ClassLoaderRepository getClassLoaderRepository();
	java.lang.String getDefaultDomain();
	java.lang.String[] getDomains();
	java.lang.Integer getMBeanCount();
	javax.management.MBeanInfo getMBeanInfo(javax.management.ObjectName var0) throws javax.management.InstanceNotFoundException, javax.management.IntrospectionException, javax.management.ReflectionException;
	javax.management.ObjectInstance getObjectInstance(javax.management.ObjectName var0) throws javax.management.InstanceNotFoundException;
	java.lang.Object instantiate(java.lang.String var0) throws javax.management.MBeanException, javax.management.ReflectionException;
	java.lang.Object instantiate(java.lang.String var0, javax.management.ObjectName var1) throws javax.management.InstanceNotFoundException, javax.management.MBeanException, javax.management.ReflectionException;
	java.lang.Object instantiate(java.lang.String var0, javax.management.ObjectName var1, java.lang.Object[] var2, java.lang.String[] var3) throws javax.management.InstanceNotFoundException, javax.management.MBeanException, javax.management.ReflectionException;
	java.lang.Object instantiate(java.lang.String var0, java.lang.Object[] var1, java.lang.String[] var2) throws javax.management.MBeanException, javax.management.ReflectionException;
	java.lang.Object invoke(javax.management.ObjectName var0, java.lang.String var1, java.lang.Object[] var2, java.lang.String[] var3) throws javax.management.InstanceNotFoundException, javax.management.MBeanException, javax.management.ReflectionException;
	boolean isInstanceOf(javax.management.ObjectName var0, java.lang.String var1) throws javax.management.InstanceNotFoundException;
	boolean isRegistered(javax.management.ObjectName var0);
	java.util.Set queryMBeans(javax.management.ObjectName var0, javax.management.QueryExp var1);
	java.util.Set queryNames(javax.management.ObjectName var0, javax.management.QueryExp var1);
	javax.management.ObjectInstance registerMBean(java.lang.Object var0, javax.management.ObjectName var1) throws javax.management.InstanceAlreadyExistsException, javax.management.MBeanRegistrationException, javax.management.NotCompliantMBeanException;
	void removeNotificationListener(javax.management.ObjectName var0, javax.management.NotificationListener var1) throws javax.management.InstanceNotFoundException, javax.management.ListenerNotFoundException;
	void removeNotificationListener(javax.management.ObjectName var0, javax.management.NotificationListener var1, javax.management.NotificationFilter var2, java.lang.Object var3) throws javax.management.InstanceNotFoundException, javax.management.ListenerNotFoundException;
	void removeNotificationListener(javax.management.ObjectName var0, javax.management.ObjectName var1) throws javax.management.InstanceNotFoundException, javax.management.ListenerNotFoundException;
	void removeNotificationListener(javax.management.ObjectName var0, javax.management.ObjectName var1, javax.management.NotificationFilter var2, java.lang.Object var3) throws javax.management.InstanceNotFoundException, javax.management.ListenerNotFoundException;
	void setAttribute(javax.management.ObjectName var0, javax.management.Attribute var1) throws javax.management.AttributeNotFoundException, javax.management.InstanceNotFoundException, javax.management.InvalidAttributeValueException, javax.management.MBeanException, javax.management.ReflectionException;
	javax.management.AttributeList setAttributes(javax.management.ObjectName var0, javax.management.AttributeList var1) throws javax.management.InstanceNotFoundException, javax.management.ReflectionException;
	void unregisterMBean(javax.management.ObjectName var0) throws javax.management.InstanceNotFoundException, javax.management.MBeanRegistrationException;
}

