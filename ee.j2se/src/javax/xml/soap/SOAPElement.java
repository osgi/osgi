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

package javax.xml.soap;
public interface SOAPElement extends javax.xml.soap.Node, org.w3c.dom.Element {
	javax.xml.soap.SOAPElement addAttribute(javax.xml.namespace.QName var0, java.lang.String var1) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPElement addAttribute(javax.xml.soap.Name var0, java.lang.String var1) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPElement addChildElement(java.lang.String var0) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPElement addChildElement(java.lang.String var0, java.lang.String var1) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPElement addChildElement(java.lang.String var0, java.lang.String var1, java.lang.String var2) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPElement addChildElement(javax.xml.namespace.QName var0) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPElement addChildElement(javax.xml.soap.Name var0) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPElement addChildElement(javax.xml.soap.SOAPElement var0) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPElement addNamespaceDeclaration(java.lang.String var0, java.lang.String var1) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPElement addTextNode(java.lang.String var0) throws javax.xml.soap.SOAPException;
	javax.xml.namespace.QName createQName(java.lang.String var0, java.lang.String var1) throws javax.xml.soap.SOAPException;
	java.util.Iterator getAllAttributes();
	java.util.Iterator getAllAttributesAsQNames();
	java.lang.String getAttributeValue(javax.xml.namespace.QName var0);
	java.lang.String getAttributeValue(javax.xml.soap.Name var0);
	java.util.Iterator getChildElements();
	java.util.Iterator getChildElements(javax.xml.namespace.QName var0);
	java.util.Iterator getChildElements(javax.xml.soap.Name var0);
	javax.xml.soap.Name getElementName();
	javax.xml.namespace.QName getElementQName();
	java.lang.String getEncodingStyle();
	java.util.Iterator getNamespacePrefixes();
	java.lang.String getNamespaceURI(java.lang.String var0);
	java.util.Iterator getVisibleNamespacePrefixes();
	boolean removeAttribute(javax.xml.namespace.QName var0);
	boolean removeAttribute(javax.xml.soap.Name var0);
	void removeContents();
	boolean removeNamespaceDeclaration(java.lang.String var0);
	javax.xml.soap.SOAPElement setElementQName(javax.xml.namespace.QName var0) throws javax.xml.soap.SOAPException;
	void setEncodingStyle(java.lang.String var0) throws javax.xml.soap.SOAPException;
}

