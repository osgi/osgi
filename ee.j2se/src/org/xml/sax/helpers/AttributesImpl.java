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

package org.xml.sax.helpers;
public class AttributesImpl implements org.xml.sax.Attributes {
	public AttributesImpl() { } 
	public AttributesImpl(org.xml.sax.Attributes var0) { } 
	public void addAttribute(java.lang.String var0, java.lang.String var1, java.lang.String var2, java.lang.String var3, java.lang.String var4) { }
	public void clear() { }
	public int getIndex(java.lang.String var0) { return 0; }
	public int getIndex(java.lang.String var0, java.lang.String var1) { return 0; }
	public int getLength() { return 0; }
	public java.lang.String getLocalName(int var0) { return null; }
	public java.lang.String getQName(int var0) { return null; }
	public java.lang.String getType(int var0) { return null; }
	public java.lang.String getType(java.lang.String var0) { return null; }
	public java.lang.String getType(java.lang.String var0, java.lang.String var1) { return null; }
	public java.lang.String getURI(int var0) { return null; }
	public java.lang.String getValue(int var0) { return null; }
	public java.lang.String getValue(java.lang.String var0) { return null; }
	public java.lang.String getValue(java.lang.String var0, java.lang.String var1) { return null; }
	public void removeAttribute(int var0) { }
	public void setAttribute(int var0, java.lang.String var1, java.lang.String var2, java.lang.String var3, java.lang.String var4, java.lang.String var5) { }
	public void setAttributes(org.xml.sax.Attributes var0) { }
	public void setLocalName(int var0, java.lang.String var1) { }
	public void setQName(int var0, java.lang.String var1) { }
	public void setType(int var0, java.lang.String var1) { }
	public void setURI(int var0, java.lang.String var1) { }
	public void setValue(int var0, java.lang.String var1) { }
}

