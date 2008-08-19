/*
 * $Date$
 *
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

package org.w3c.dom;
public abstract interface Element extends org.w3c.dom.Node {
	public abstract java.lang.String getAttribute(java.lang.String var0);
	public abstract java.lang.String getAttributeNS(java.lang.String var0, java.lang.String var1);
	public abstract org.w3c.dom.Attr getAttributeNode(java.lang.String var0);
	public abstract org.w3c.dom.Attr getAttributeNodeNS(java.lang.String var0, java.lang.String var1);
	public abstract org.w3c.dom.NodeList getElementsByTagName(java.lang.String var0);
	public abstract org.w3c.dom.NodeList getElementsByTagNameNS(java.lang.String var0, java.lang.String var1);
	public abstract java.lang.String getTagName();
	public abstract boolean hasAttribute(java.lang.String var0);
	public abstract boolean hasAttributeNS(java.lang.String var0, java.lang.String var1);
	public abstract void removeAttribute(java.lang.String var0);
	public abstract void removeAttributeNS(java.lang.String var0, java.lang.String var1);
	public abstract org.w3c.dom.Attr removeAttributeNode(org.w3c.dom.Attr var0);
	public abstract void setAttribute(java.lang.String var0, java.lang.String var1);
	public abstract void setAttributeNS(java.lang.String var0, java.lang.String var1, java.lang.String var2);
	public abstract org.w3c.dom.Attr setAttributeNode(org.w3c.dom.Attr var0);
	public abstract org.w3c.dom.Attr setAttributeNodeNS(org.w3c.dom.Attr var0);
}

