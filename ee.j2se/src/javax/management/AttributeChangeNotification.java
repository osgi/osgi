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
public class AttributeChangeNotification extends javax.management.Notification {
	public final static java.lang.String ATTRIBUTE_CHANGE = "jmx.attribute.change";
	public AttributeChangeNotification(java.lang.Object var0, long var1, long var2, java.lang.String var3, java.lang.String var4, java.lang.String var5, java.lang.Object var6, java.lang.Object var7)  { super((java.lang.String) null, (java.lang.Object) null, 0l, (java.lang.String) null); } 
	public java.lang.String getAttributeName() { return null; }
	public java.lang.String getAttributeType() { return null; }
	public java.lang.Object getNewValue() { return null; }
	public java.lang.Object getOldValue() { return null; }
}

