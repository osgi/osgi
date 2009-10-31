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
public class MBeanOperationInfo extends javax.management.MBeanFeatureInfo implements java.lang.Cloneable {
	public final static int ACTION = 1;
	public final static int ACTION_INFO = 2;
	public final static int INFO = 0;
	public final static int UNKNOWN = 3;
	public MBeanOperationInfo(java.lang.String var0, java.lang.String var1, javax.management.MBeanParameterInfo[] var2, java.lang.String var3, int var4)  { super((java.lang.String) null, (java.lang.String) null, (javax.management.Descriptor) null); } 
	public MBeanOperationInfo(java.lang.String var0, java.lang.String var1, javax.management.MBeanParameterInfo[] var2, java.lang.String var3, int var4, javax.management.Descriptor var5)  { super((java.lang.String) null, (java.lang.String) null, (javax.management.Descriptor) null); } 
	public MBeanOperationInfo(java.lang.String var0, java.lang.reflect.Method var1)  { super((java.lang.String) null, (java.lang.String) null, (javax.management.Descriptor) null); } 
	public java.lang.Object clone() { return null; }
	public int getImpact() { return 0; }
	public java.lang.String getReturnType() { return null; }
	public javax.management.MBeanParameterInfo[] getSignature() { return null; }
}

