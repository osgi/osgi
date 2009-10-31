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
public class PropertyDescriptor extends java.beans.FeatureDescriptor {
	public PropertyDescriptor(java.lang.String var0, java.lang.Class<?> var1) throws java.beans.IntrospectionException { } 
	public PropertyDescriptor(java.lang.String var0, java.lang.Class<?> var1, java.lang.String var2, java.lang.String var3) throws java.beans.IntrospectionException { } 
	public PropertyDescriptor(java.lang.String var0, java.lang.reflect.Method var1, java.lang.reflect.Method var2) throws java.beans.IntrospectionException { } 
	public java.beans.PropertyEditor createPropertyEditor(java.lang.Object var0) { return null; }
	public java.lang.Class<?> getPropertyEditorClass() { return null; }
	public java.lang.Class<?> getPropertyType() { return null; }
	public java.lang.reflect.Method getReadMethod() { return null; }
	public java.lang.reflect.Method getWriteMethod() { return null; }
	public boolean isBound() { return false; }
	public boolean isConstrained() { return false; }
	public void setBound(boolean var0) { }
	public void setConstrained(boolean var0) { }
	public void setPropertyEditorClass(java.lang.Class<?> var0) { }
	public void setReadMethod(java.lang.reflect.Method var0) throws java.beans.IntrospectionException { }
	public void setWriteMethod(java.lang.reflect.Method var0) throws java.beans.IntrospectionException { }
}

