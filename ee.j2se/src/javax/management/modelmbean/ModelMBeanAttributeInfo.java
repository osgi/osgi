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
public class ModelMBeanAttributeInfo extends javax.management.MBeanAttributeInfo implements javax.management.DescriptorAccess {
	public ModelMBeanAttributeInfo(java.lang.String var0, java.lang.String var1, java.lang.String var2, boolean var3, boolean var4, boolean var5)  { super((java.lang.String) null, (java.lang.String) null, (java.lang.String) null, false, false, false, (javax.management.Descriptor) null); } 
	public ModelMBeanAttributeInfo(java.lang.String var0, java.lang.String var1, java.lang.String var2, boolean var3, boolean var4, boolean var5, javax.management.Descriptor var6)  { super((java.lang.String) null, (java.lang.String) null, (java.lang.String) null, false, false, false, (javax.management.Descriptor) null); } 
	public ModelMBeanAttributeInfo(java.lang.String var0, java.lang.String var1, java.lang.reflect.Method var2, java.lang.reflect.Method var3) throws javax.management.IntrospectionException  { super((java.lang.String) null, (java.lang.String) null, (java.lang.reflect.Method) null, (java.lang.reflect.Method) null); } 
	public ModelMBeanAttributeInfo(java.lang.String var0, java.lang.String var1, java.lang.reflect.Method var2, java.lang.reflect.Method var3, javax.management.Descriptor var4) throws javax.management.IntrospectionException  { super((java.lang.String) null, (java.lang.String) null, (java.lang.reflect.Method) null, (java.lang.reflect.Method) null); } 
	public ModelMBeanAttributeInfo(javax.management.modelmbean.ModelMBeanAttributeInfo var0)  { super((java.lang.String) null, (java.lang.String) null, (java.lang.String) null, false, false, false, (javax.management.Descriptor) null); } 
	public void setDescriptor(javax.management.Descriptor var0) { }
}

