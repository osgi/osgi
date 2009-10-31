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
public class StandardMBean implements javax.management.DynamicMBean, javax.management.MBeanRegistration {
	protected StandardMBean(java.lang.Class<?> var0) throws javax.management.NotCompliantMBeanException { } 
	protected StandardMBean(java.lang.Class<?> var0, boolean var1) { } 
	public <T> StandardMBean(T var0, java.lang.Class<T> var1) throws javax.management.NotCompliantMBeanException { } 
	public <T> StandardMBean(T var0, java.lang.Class<T> var1, boolean var2) { } 
	protected void cacheMBeanInfo(javax.management.MBeanInfo var0) { }
	public java.lang.Object getAttribute(java.lang.String var0) throws javax.management.AttributeNotFoundException, javax.management.MBeanException, javax.management.ReflectionException { return null; }
	public javax.management.AttributeList getAttributes(java.lang.String[] var0) { return null; }
	protected javax.management.MBeanInfo getCachedMBeanInfo() { return null; }
	protected java.lang.String getClassName(javax.management.MBeanInfo var0) { return null; }
	protected javax.management.MBeanConstructorInfo[] getConstructors(javax.management.MBeanConstructorInfo[] var0, java.lang.Object var1) { return null; }
	protected java.lang.String getDescription(javax.management.MBeanAttributeInfo var0) { return null; }
	protected java.lang.String getDescription(javax.management.MBeanConstructorInfo var0) { return null; }
	protected java.lang.String getDescription(javax.management.MBeanConstructorInfo var0, javax.management.MBeanParameterInfo var1, int var2) { return null; }
	protected java.lang.String getDescription(javax.management.MBeanFeatureInfo var0) { return null; }
	protected java.lang.String getDescription(javax.management.MBeanInfo var0) { return null; }
	protected java.lang.String getDescription(javax.management.MBeanOperationInfo var0) { return null; }
	protected java.lang.String getDescription(javax.management.MBeanOperationInfo var0, javax.management.MBeanParameterInfo var1, int var2) { return null; }
	protected int getImpact(javax.management.MBeanOperationInfo var0) { return 0; }
	public java.lang.Object getImplementation() { return null; }
	public java.lang.Class<?> getImplementationClass() { return null; }
	public javax.management.MBeanInfo getMBeanInfo() { return null; }
	public final java.lang.Class<?> getMBeanInterface() { return null; }
	protected java.lang.String getParameterName(javax.management.MBeanConstructorInfo var0, javax.management.MBeanParameterInfo var1, int var2) { return null; }
	protected java.lang.String getParameterName(javax.management.MBeanOperationInfo var0, javax.management.MBeanParameterInfo var1, int var2) { return null; }
	public java.lang.Object invoke(java.lang.String var0, java.lang.Object[] var1, java.lang.String[] var2) throws javax.management.MBeanException, javax.management.ReflectionException { return null; }
	public void postDeregister() { }
	public void postRegister(java.lang.Boolean var0) { }
	public void preDeregister() throws java.lang.Exception { }
	public javax.management.ObjectName preRegister(javax.management.MBeanServer var0, javax.management.ObjectName var1) throws java.lang.Exception { return null; }
	public void setAttribute(javax.management.Attribute var0) throws javax.management.AttributeNotFoundException, javax.management.InvalidAttributeValueException, javax.management.MBeanException, javax.management.ReflectionException { }
	public javax.management.AttributeList setAttributes(javax.management.AttributeList var0) { return null; }
	public void setImplementation(java.lang.Object var0) throws javax.management.NotCompliantMBeanException { }
}

