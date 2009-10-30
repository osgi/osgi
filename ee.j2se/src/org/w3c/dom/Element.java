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

package org.w3c.dom;
public interface Element extends org.w3c.dom.Node {
	java.lang.String getAttribute(java.lang.String var0);
	java.lang.String getAttributeNS(java.lang.String var0, java.lang.String var1);
	org.w3c.dom.Attr getAttributeNode(java.lang.String var0);
	org.w3c.dom.Attr getAttributeNodeNS(java.lang.String var0, java.lang.String var1);
	org.w3c.dom.NodeList getElementsByTagName(java.lang.String var0);
	org.w3c.dom.NodeList getElementsByTagNameNS(java.lang.String var0, java.lang.String var1);
	org.w3c.dom.TypeInfo getSchemaTypeInfo();
	java.lang.String getTagName();
	boolean hasAttribute(java.lang.String var0);
	boolean hasAttributeNS(java.lang.String var0, java.lang.String var1);
	void removeAttribute(java.lang.String var0);
	void removeAttributeNS(java.lang.String var0, java.lang.String var1);
	org.w3c.dom.Attr removeAttributeNode(org.w3c.dom.Attr var0);
	void setAttribute(java.lang.String var0, java.lang.String var1);
	void setAttributeNS(java.lang.String var0, java.lang.String var1, java.lang.String var2);
	org.w3c.dom.Attr setAttributeNode(org.w3c.dom.Attr var0);
	org.w3c.dom.Attr setAttributeNodeNS(org.w3c.dom.Attr var0);
	void setIdAttribute(java.lang.String var0, boolean var1);
	void setIdAttributeNS(java.lang.String var0, java.lang.String var1, boolean var2);
	void setIdAttributeNode(org.w3c.dom.Attr var0, boolean var1);
}

