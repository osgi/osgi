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
public interface ModelMBeanInfo {
	java.lang.Object clone();
	javax.management.modelmbean.ModelMBeanAttributeInfo getAttribute(java.lang.String var0) throws javax.management.MBeanException;
	javax.management.MBeanAttributeInfo[] getAttributes();
	java.lang.String getClassName();
	javax.management.MBeanConstructorInfo[] getConstructors();
	java.lang.String getDescription();
	javax.management.Descriptor getDescriptor(java.lang.String var0, java.lang.String var1) throws javax.management.MBeanException;
	javax.management.Descriptor[] getDescriptors(java.lang.String var0) throws javax.management.MBeanException;
	javax.management.Descriptor getMBeanDescriptor() throws javax.management.MBeanException;
	javax.management.modelmbean.ModelMBeanNotificationInfo getNotification(java.lang.String var0) throws javax.management.MBeanException;
	javax.management.MBeanNotificationInfo[] getNotifications();
	javax.management.modelmbean.ModelMBeanOperationInfo getOperation(java.lang.String var0) throws javax.management.MBeanException;
	javax.management.MBeanOperationInfo[] getOperations();
	void setDescriptor(javax.management.Descriptor var0, java.lang.String var1) throws javax.management.MBeanException;
	void setDescriptors(javax.management.Descriptor[] var0) throws javax.management.MBeanException;
	void setMBeanDescriptor(javax.management.Descriptor var0) throws javax.management.MBeanException;
}

