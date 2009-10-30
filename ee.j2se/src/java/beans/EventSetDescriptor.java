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

package java.beans;
public class EventSetDescriptor extends java.beans.FeatureDescriptor {
	public EventSetDescriptor(java.lang.Class<?> var0, java.lang.String var1, java.lang.Class<?> var2, java.lang.String var3) throws java.beans.IntrospectionException { } 
	public EventSetDescriptor(java.lang.Class<?> var0, java.lang.String var1, java.lang.Class<?> var2, java.lang.String[] var3, java.lang.String var4, java.lang.String var5) throws java.beans.IntrospectionException { } 
	public EventSetDescriptor(java.lang.Class<?> var0, java.lang.String var1, java.lang.Class<?> var2, java.lang.String[] var3, java.lang.String var4, java.lang.String var5, java.lang.String var6) throws java.beans.IntrospectionException { } 
	public EventSetDescriptor(java.lang.String var0, java.lang.Class<?> var1, java.beans.MethodDescriptor[] var2, java.lang.reflect.Method var3, java.lang.reflect.Method var4) throws java.beans.IntrospectionException { } 
	public EventSetDescriptor(java.lang.String var0, java.lang.Class<?> var1, java.lang.reflect.Method[] var2, java.lang.reflect.Method var3, java.lang.reflect.Method var4) throws java.beans.IntrospectionException { } 
	public EventSetDescriptor(java.lang.String var0, java.lang.Class<?> var1, java.lang.reflect.Method[] var2, java.lang.reflect.Method var3, java.lang.reflect.Method var4, java.lang.reflect.Method var5) throws java.beans.IntrospectionException { } 
	public java.lang.reflect.Method getAddListenerMethod() { return null; }
	public java.lang.reflect.Method getGetListenerMethod() { return null; }
	public java.beans.MethodDescriptor[] getListenerMethodDescriptors() { return null; }
	public java.lang.reflect.Method[] getListenerMethods() { return null; }
	public java.lang.Class<?> getListenerType() { return null; }
	public java.lang.reflect.Method getRemoveListenerMethod() { return null; }
	public boolean isInDefaultEventSet() { return false; }
	public boolean isUnicast() { return false; }
	public void setInDefaultEventSet(boolean var0) { }
	public void setUnicast(boolean var0) { }
}

