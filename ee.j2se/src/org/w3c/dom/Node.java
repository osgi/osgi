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
public interface Node {
	public final static short ATTRIBUTE_NODE = 2;
	public final static short CDATA_SECTION_NODE = 4;
	public final static short COMMENT_NODE = 8;
	public final static short DOCUMENT_FRAGMENT_NODE = 11;
	public final static short DOCUMENT_NODE = 9;
	public final static short DOCUMENT_POSITION_CONTAINED_BY = 16;
	public final static short DOCUMENT_POSITION_CONTAINS = 8;
	public final static short DOCUMENT_POSITION_DISCONNECTED = 1;
	public final static short DOCUMENT_POSITION_FOLLOWING = 4;
	public final static short DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC = 32;
	public final static short DOCUMENT_POSITION_PRECEDING = 2;
	public final static short DOCUMENT_TYPE_NODE = 10;
	public final static short ELEMENT_NODE = 1;
	public final static short ENTITY_NODE = 6;
	public final static short ENTITY_REFERENCE_NODE = 5;
	public final static short NOTATION_NODE = 12;
	public final static short PROCESSING_INSTRUCTION_NODE = 7;
	public final static short TEXT_NODE = 3;
	org.w3c.dom.Node appendChild(org.w3c.dom.Node var0);
	org.w3c.dom.Node cloneNode(boolean var0);
	short compareDocumentPosition(org.w3c.dom.Node var0);
	org.w3c.dom.NamedNodeMap getAttributes();
	java.lang.String getBaseURI();
	org.w3c.dom.NodeList getChildNodes();
	java.lang.Object getFeature(java.lang.String var0, java.lang.String var1);
	org.w3c.dom.Node getFirstChild();
	org.w3c.dom.Node getLastChild();
	java.lang.String getLocalName();
	java.lang.String getNamespaceURI();
	org.w3c.dom.Node getNextSibling();
	java.lang.String getNodeName();
	short getNodeType();
	java.lang.String getNodeValue();
	org.w3c.dom.Document getOwnerDocument();
	org.w3c.dom.Node getParentNode();
	java.lang.String getPrefix();
	org.w3c.dom.Node getPreviousSibling();
	java.lang.String getTextContent();
	java.lang.Object getUserData(java.lang.String var0);
	boolean hasAttributes();
	boolean hasChildNodes();
	org.w3c.dom.Node insertBefore(org.w3c.dom.Node var0, org.w3c.dom.Node var1);
	boolean isDefaultNamespace(java.lang.String var0);
	boolean isEqualNode(org.w3c.dom.Node var0);
	boolean isSameNode(org.w3c.dom.Node var0);
	boolean isSupported(java.lang.String var0, java.lang.String var1);
	java.lang.String lookupNamespaceURI(java.lang.String var0);
	java.lang.String lookupPrefix(java.lang.String var0);
	void normalize();
	org.w3c.dom.Node removeChild(org.w3c.dom.Node var0);
	org.w3c.dom.Node replaceChild(org.w3c.dom.Node var0, org.w3c.dom.Node var1);
	void setNodeValue(java.lang.String var0);
	void setPrefix(java.lang.String var0);
	void setTextContent(java.lang.String var0);
	java.lang.Object setUserData(java.lang.String var0, java.lang.Object var1, org.w3c.dom.UserDataHandler var2);
}

