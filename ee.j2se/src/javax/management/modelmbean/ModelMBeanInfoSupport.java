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
public class ModelMBeanInfoSupport extends javax.management.MBeanInfo implements javax.management.modelmbean.ModelMBeanInfo {
	public ModelMBeanInfoSupport(java.lang.String var0, java.lang.String var1, javax.management.modelmbean.ModelMBeanAttributeInfo[] var2, javax.management.modelmbean.ModelMBeanConstructorInfo[] var3, javax.management.modelmbean.ModelMBeanOperationInfo[] var4, javax.management.modelmbean.ModelMBeanNotificationInfo[] var5)  { super((java.lang.String) null, (java.lang.String) null, (javax.management.MBeanAttributeInfo[]) null, (javax.management.MBeanConstructorInfo[]) null, (javax.management.MBeanOperationInfo[]) null, (javax.management.MBeanNotificationInfo[]) null, (javax.management.Descriptor) null); } 
	public ModelMBeanInfoSupport(java.lang.String var0, java.lang.String var1, javax.management.modelmbean.ModelMBeanAttributeInfo[] var2, javax.management.modelmbean.ModelMBeanConstructorInfo[] var3, javax.management.modelmbean.ModelMBeanOperationInfo[] var4, javax.management.modelmbean.ModelMBeanNotificationInfo[] var5, javax.management.Descriptor var6)  { super((java.lang.String) null, (java.lang.String) null, (javax.management.MBeanAttributeInfo[]) null, (javax.management.MBeanConstructorInfo[]) null, (javax.management.MBeanOperationInfo[]) null, (javax.management.MBeanNotificationInfo[]) null, (javax.management.Descriptor) null); } 
	public ModelMBeanInfoSupport(javax.management.modelmbean.ModelMBeanInfo var0)  { super((java.lang.String) null, (java.lang.String) null, (javax.management.MBeanAttributeInfo[]) null, (javax.management.MBeanConstructorInfo[]) null, (javax.management.MBeanOperationInfo[]) null, (javax.management.MBeanNotificationInfo[]) null, (javax.management.Descriptor) null); } 
	public javax.management.modelmbean.ModelMBeanAttributeInfo getAttribute(java.lang.String var0) throws javax.management.MBeanException { return null; }
	public javax.management.modelmbean.ModelMBeanConstructorInfo getConstructor(java.lang.String var0) throws javax.management.MBeanException { return null; }
	public javax.management.Descriptor getDescriptor(java.lang.String var0) throws javax.management.MBeanException { return null; }
	public javax.management.Descriptor getDescriptor(java.lang.String var0, java.lang.String var1) throws javax.management.MBeanException { return null; }
	public javax.management.Descriptor[] getDescriptors(java.lang.String var0) throws javax.management.MBeanException { return null; }
	public javax.management.Descriptor getMBeanDescriptor() throws javax.management.MBeanException { return null; }
	public javax.management.modelmbean.ModelMBeanNotificationInfo getNotification(java.lang.String var0) throws javax.management.MBeanException { return null; }
	public javax.management.modelmbean.ModelMBeanOperationInfo getOperation(java.lang.String var0) throws javax.management.MBeanException { return null; }
	public void setDescriptor(javax.management.Descriptor var0, java.lang.String var1) throws javax.management.MBeanException { }
	public void setDescriptors(javax.management.Descriptor[] var0) throws javax.management.MBeanException { }
	public void setMBeanDescriptor(javax.management.Descriptor var0) throws javax.management.MBeanException { }
}

