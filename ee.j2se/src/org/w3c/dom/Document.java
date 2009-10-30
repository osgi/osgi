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
public interface Document extends org.w3c.dom.Node {
	org.w3c.dom.Node adoptNode(org.w3c.dom.Node var0);
	org.w3c.dom.Attr createAttribute(java.lang.String var0);
	org.w3c.dom.Attr createAttributeNS(java.lang.String var0, java.lang.String var1);
	org.w3c.dom.CDATASection createCDATASection(java.lang.String var0);
	org.w3c.dom.Comment createComment(java.lang.String var0);
	org.w3c.dom.DocumentFragment createDocumentFragment();
	org.w3c.dom.Element createElement(java.lang.String var0);
	org.w3c.dom.Element createElementNS(java.lang.String var0, java.lang.String var1);
	org.w3c.dom.EntityReference createEntityReference(java.lang.String var0);
	org.w3c.dom.ProcessingInstruction createProcessingInstruction(java.lang.String var0, java.lang.String var1);
	org.w3c.dom.Text createTextNode(java.lang.String var0);
	org.w3c.dom.DocumentType getDoctype();
	org.w3c.dom.Element getDocumentElement();
	java.lang.String getDocumentURI();
	org.w3c.dom.DOMConfiguration getDomConfig();
	org.w3c.dom.Element getElementById(java.lang.String var0);
	org.w3c.dom.NodeList getElementsByTagName(java.lang.String var0);
	org.w3c.dom.NodeList getElementsByTagNameNS(java.lang.String var0, java.lang.String var1);
	org.w3c.dom.DOMImplementation getImplementation();
	java.lang.String getInputEncoding();
	boolean getStrictErrorChecking();
	java.lang.String getXmlEncoding();
	boolean getXmlStandalone();
	java.lang.String getXmlVersion();
	org.w3c.dom.Node importNode(org.w3c.dom.Node var0, boolean var1);
	void normalizeDocument();
	org.w3c.dom.Node renameNode(org.w3c.dom.Node var0, java.lang.String var1, java.lang.String var2);
	void setDocumentURI(java.lang.String var0);
	void setStrictErrorChecking(boolean var0);
	void setXmlStandalone(boolean var0);
	void setXmlVersion(java.lang.String var0);
}

