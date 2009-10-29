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

package org.w3c.dom;
public abstract interface Node {
	public abstract org.w3c.dom.Node appendChild(org.w3c.dom.Node var0);
	public abstract org.w3c.dom.Node cloneNode(boolean var0);
	public abstract org.w3c.dom.NamedNodeMap getAttributes();
	public abstract org.w3c.dom.NodeList getChildNodes();
	public abstract org.w3c.dom.Node getFirstChild();
	public abstract org.w3c.dom.Node getLastChild();
	public abstract java.lang.String getLocalName();
	public abstract java.lang.String getNamespaceURI();
	public abstract org.w3c.dom.Node getNextSibling();
	public abstract java.lang.String getNodeName();
	public abstract short getNodeType();
	public abstract java.lang.String getNodeValue();
	public abstract org.w3c.dom.Document getOwnerDocument();
	public abstract org.w3c.dom.Node getParentNode();
	public abstract java.lang.String getPrefix();
	public abstract org.w3c.dom.Node getPreviousSibling();
	public abstract boolean hasAttributes();
	public abstract boolean hasChildNodes();
	public abstract org.w3c.dom.Node insertBefore(org.w3c.dom.Node var0, org.w3c.dom.Node var1);
	public abstract boolean isSupported(java.lang.String var0, java.lang.String var1);
	public abstract void normalize();
	public abstract org.w3c.dom.Node removeChild(org.w3c.dom.Node var0);
	public abstract org.w3c.dom.Node replaceChild(org.w3c.dom.Node var0, org.w3c.dom.Node var1);
	public abstract void setNodeValue(java.lang.String var0);
	public abstract void setPrefix(java.lang.String var0);
	public final static short ATTRIBUTE_NODE = 2;
	public final static short CDATA_SECTION_NODE = 4;
	public final static short COMMENT_NODE = 8;
	public final static short DOCUMENT_FRAGMENT_NODE = 11;
	public final static short DOCUMENT_NODE = 9;
	public final static short DOCUMENT_TYPE_NODE = 10;
	public final static short ELEMENT_NODE = 1;
	public final static short ENTITY_NODE = 6;
	public final static short ENTITY_REFERENCE_NODE = 5;
	public final static short NOTATION_NODE = 12;
	public final static short PROCESSING_INSTRUCTION_NODE = 7;
	public final static short TEXT_NODE = 3;
}

