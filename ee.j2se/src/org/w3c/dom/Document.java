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
public abstract interface Document extends org.w3c.dom.Node {
	public abstract org.w3c.dom.Attr createAttribute(java.lang.String var0);
	public abstract org.w3c.dom.Attr createAttributeNS(java.lang.String var0, java.lang.String var1);
	public abstract org.w3c.dom.CDATASection createCDATASection(java.lang.String var0);
	public abstract org.w3c.dom.Comment createComment(java.lang.String var0);
	public abstract org.w3c.dom.DocumentFragment createDocumentFragment();
	public abstract org.w3c.dom.Element createElement(java.lang.String var0);
	public abstract org.w3c.dom.Element createElementNS(java.lang.String var0, java.lang.String var1);
	public abstract org.w3c.dom.EntityReference createEntityReference(java.lang.String var0);
	public abstract org.w3c.dom.ProcessingInstruction createProcessingInstruction(java.lang.String var0, java.lang.String var1);
	public abstract org.w3c.dom.Text createTextNode(java.lang.String var0);
	public abstract org.w3c.dom.DocumentType getDoctype();
	public abstract org.w3c.dom.Element getDocumentElement();
	public abstract org.w3c.dom.Element getElementById(java.lang.String var0);
	public abstract org.w3c.dom.NodeList getElementsByTagName(java.lang.String var0);
	public abstract org.w3c.dom.NodeList getElementsByTagNameNS(java.lang.String var0, java.lang.String var1);
	public abstract org.w3c.dom.DOMImplementation getImplementation();
	public abstract org.w3c.dom.Node importNode(org.w3c.dom.Node var0, boolean var1);
}

