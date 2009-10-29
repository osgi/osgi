/*
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
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

package javax.swing.beaninfo;
public abstract class SwingBeanInfo extends java.beans.SimpleBeanInfo {
	public SwingBeanInfo() { }
	public java.beans.BeanDescriptor createBeanDescriptor(java.lang.Class var0, java.lang.Object[] var1) { return null; }
	public java.beans.PropertyDescriptor createPropertyDescriptor(java.lang.Class var0, java.lang.String var1, java.lang.Object[] var2) { return null; }
	protected void throwError(java.lang.Exception var0, java.lang.String var1) { }
	public final static java.lang.String BOUND = "bound";
	public final static java.lang.String CONSTRAINED = "constrained";
	public final static java.lang.String CUSTOMIZERCLASS = "customizerClass";
	public final static java.lang.String DISPLAYNAME = "displayName";
	public final static java.lang.String EXPERT = "expert";
	public final static java.lang.String HIDDEN = "hidden";
	public final static java.lang.String PREFERRED = "preferred";
	public final static java.lang.String PROPERTYEDITORCLASS = "propertyEditorClass";
	public final static java.lang.String READMETHOD = "readMethod";
	public final static java.lang.String SHORTDESCRIPTION = "shortDescription";
	public final static java.lang.String WRITEMETHOD = "writeMethod";
}

