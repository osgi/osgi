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

package javax.management.openmbean;
public class OpenMBeanAttributeInfoSupport extends javax.management.MBeanAttributeInfo implements javax.management.openmbean.OpenMBeanAttributeInfo {
	public OpenMBeanAttributeInfoSupport(java.lang.String var0, java.lang.String var1, javax.management.openmbean.OpenType<?> var2, boolean var3, boolean var4, boolean var5)  { super((java.lang.String) null, (java.lang.String) null, (java.lang.String) null, false, false, false, (javax.management.Descriptor) null); } 
	public <T> OpenMBeanAttributeInfoSupport(java.lang.String var0, java.lang.String var1, javax.management.openmbean.OpenType<T> var2, boolean var3, boolean var4, boolean var5, T var6) throws javax.management.openmbean.OpenDataException  { super((java.lang.String) null, (java.lang.String) null, (java.lang.String) null, false, false, false, (javax.management.Descriptor) null); } 
	public <T> OpenMBeanAttributeInfoSupport(java.lang.String var0, java.lang.String var1, javax.management.openmbean.OpenType<T> var2, boolean var3, boolean var4, boolean var5, T var6, java.lang.Comparable<T> var7, java.lang.Comparable<T> var8) throws javax.management.openmbean.OpenDataException  { super((java.lang.String) null, (java.lang.String) null, (java.lang.String) null, false, false, false, (javax.management.Descriptor) null); } 
	public <T> OpenMBeanAttributeInfoSupport(java.lang.String var0, java.lang.String var1, javax.management.openmbean.OpenType<T> var2, boolean var3, boolean var4, boolean var5, T var6, T[] var7) throws javax.management.openmbean.OpenDataException  { super((java.lang.String) null, (java.lang.String) null, (java.lang.String) null, false, false, false, (javax.management.Descriptor) null); } 
	public OpenMBeanAttributeInfoSupport(java.lang.String var0, java.lang.String var1, javax.management.openmbean.OpenType<?> var2, boolean var3, boolean var4, boolean var5, javax.management.Descriptor var6)  { super((java.lang.String) null, (java.lang.String) null, (java.lang.String) null, false, false, false, (javax.management.Descriptor) null); } 
	public java.lang.Object getDefaultValue() { return null; }
	public java.util.Set<?> getLegalValues() { return null; }
	public java.lang.Comparable<?> getMaxValue() { return null; }
	public java.lang.Comparable<?> getMinValue() { return null; }
	public javax.management.openmbean.OpenType<?> getOpenType() { return null; }
	public boolean hasDefaultValue() { return false; }
	public boolean hasLegalValues() { return false; }
	public boolean hasMaxValue() { return false; }
	public boolean hasMinValue() { return false; }
	public boolean isValue(java.lang.Object var0) { return false; }
}

