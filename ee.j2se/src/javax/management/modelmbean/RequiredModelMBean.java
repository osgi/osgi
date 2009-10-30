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

package javax.management.modelmbean;
public class RequiredModelMBean implements javax.management.MBeanRegistration, javax.management.NotificationEmitter, javax.management.modelmbean.ModelMBean {
	public RequiredModelMBean() throws javax.management.MBeanException { } 
	public RequiredModelMBean(javax.management.modelmbean.ModelMBeanInfo var0) throws javax.management.MBeanException { } 
	public void addAttributeChangeNotificationListener(javax.management.NotificationListener var0, java.lang.String var1, java.lang.Object var2) throws javax.management.MBeanException { }
	public void addNotificationListener(javax.management.NotificationListener var0, javax.management.NotificationFilter var1, java.lang.Object var2) { }
	public java.lang.Object getAttribute(java.lang.String var0) throws javax.management.AttributeNotFoundException, javax.management.MBeanException, javax.management.ReflectionException { return null; }
	public javax.management.AttributeList getAttributes(java.lang.String[] var0) { return null; }
	protected javax.management.loading.ClassLoaderRepository getClassLoaderRepository() { return null; }
	public javax.management.MBeanInfo getMBeanInfo() { return null; }
	public javax.management.MBeanNotificationInfo[] getNotificationInfo() { return null; }
	public java.lang.Object invoke(java.lang.String var0, java.lang.Object[] var1, java.lang.String[] var2) throws javax.management.MBeanException, javax.management.ReflectionException { return null; }
	public void load() throws javax.management.InstanceNotFoundException, javax.management.MBeanException { }
	public void postDeregister() { }
	public void postRegister(java.lang.Boolean var0) { }
	public void preDeregister() throws java.lang.Exception { }
	public javax.management.ObjectName preRegister(javax.management.MBeanServer var0, javax.management.ObjectName var1) throws java.lang.Exception { return null; }
	public void removeAttributeChangeNotificationListener(javax.management.NotificationListener var0, java.lang.String var1) throws javax.management.ListenerNotFoundException, javax.management.MBeanException { }
	public void removeNotificationListener(javax.management.NotificationListener var0) throws javax.management.ListenerNotFoundException { }
	public void removeNotificationListener(javax.management.NotificationListener var0, javax.management.NotificationFilter var1, java.lang.Object var2) throws javax.management.ListenerNotFoundException { }
	public void sendAttributeChangeNotification(javax.management.Attribute var0, javax.management.Attribute var1) throws javax.management.MBeanException { }
	public void sendAttributeChangeNotification(javax.management.AttributeChangeNotification var0) throws javax.management.MBeanException { }
	public void sendNotification(java.lang.String var0) throws javax.management.MBeanException { }
	public void sendNotification(javax.management.Notification var0) throws javax.management.MBeanException { }
	public void setAttribute(javax.management.Attribute var0) throws javax.management.AttributeNotFoundException, javax.management.InvalidAttributeValueException, javax.management.MBeanException, javax.management.ReflectionException { }
	public javax.management.AttributeList setAttributes(javax.management.AttributeList var0) { return null; }
	public void setManagedResource(java.lang.Object var0, java.lang.String var1) throws javax.management.InstanceNotFoundException, javax.management.MBeanException, javax.management.modelmbean.InvalidTargetObjectTypeException { }
	public void setModelMBeanInfo(javax.management.modelmbean.ModelMBeanInfo var0) throws javax.management.MBeanException { }
	public void store() throws javax.management.InstanceNotFoundException, javax.management.MBeanException { }
}

