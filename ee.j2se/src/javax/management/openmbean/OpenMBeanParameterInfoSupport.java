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
public class OpenMBeanParameterInfoSupport extends javax.management.MBeanParameterInfo implements javax.management.openmbean.OpenMBeanParameterInfo {
	public OpenMBeanParameterInfoSupport(java.lang.String var0, java.lang.String var1, javax.management.openmbean.OpenType<?> var2)  { super((java.lang.String) null, (java.lang.String) null, (java.lang.String) null, (javax.management.Descriptor) null); } 
	public <T> OpenMBeanParameterInfoSupport(java.lang.String var0, java.lang.String var1, javax.management.openmbean.OpenType<T> var2, T var3) throws javax.management.openmbean.OpenDataException  { super((java.lang.String) null, (java.lang.String) null, (java.lang.String) null, (javax.management.Descriptor) null); } 
	public <T> OpenMBeanParameterInfoSupport(java.lang.String var0, java.lang.String var1, javax.management.openmbean.OpenType<T> var2, T var3, java.lang.Comparable<T> var4, java.lang.Comparable<T> var5) throws javax.management.openmbean.OpenDataException  { super((java.lang.String) null, (java.lang.String) null, (java.lang.String) null, (javax.management.Descriptor) null); } 
	public <T> OpenMBeanParameterInfoSupport(java.lang.String var0, java.lang.String var1, javax.management.openmbean.OpenType<T> var2, T var3, T[] var4) throws javax.management.openmbean.OpenDataException  { super((java.lang.String) null, (java.lang.String) null, (java.lang.String) null, (javax.management.Descriptor) null); } 
	public OpenMBeanParameterInfoSupport(java.lang.String var0, java.lang.String var1, javax.management.openmbean.OpenType<?> var2, javax.management.Descriptor var3)  { super((java.lang.String) null, (java.lang.String) null, (java.lang.String) null, (javax.management.Descriptor) null); } 
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

