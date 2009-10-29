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

package org.xml.sax;
/** @deprecated */ public class HandlerBase implements org.xml.sax.DTDHandler, org.xml.sax.DocumentHandler, org.xml.sax.EntityResolver, org.xml.sax.ErrorHandler {
	public HandlerBase() { }
	public void characters(char[] var0, int var1, int var2) throws org.xml.sax.SAXException { }
	public void endDocument() throws org.xml.sax.SAXException { }
	public void endElement(java.lang.String var0) throws org.xml.sax.SAXException { }
	public void error(org.xml.sax.SAXParseException var0) throws org.xml.sax.SAXException { }
	public void fatalError(org.xml.sax.SAXParseException var0) throws org.xml.sax.SAXException { }
	public void ignorableWhitespace(char[] var0, int var1, int var2) throws org.xml.sax.SAXException { }
	public void notationDecl(java.lang.String var0, java.lang.String var1, java.lang.String var2) { }
	public void processingInstruction(java.lang.String var0, java.lang.String var1) throws org.xml.sax.SAXException { }
	public org.xml.sax.InputSource resolveEntity(java.lang.String var0, java.lang.String var1) throws org.xml.sax.SAXException { return null; }
	public void setDocumentLocator(org.xml.sax.Locator var0) { }
	public void startDocument() throws org.xml.sax.SAXException { }
	public void startElement(java.lang.String var0, org.xml.sax.AttributeList var1) throws org.xml.sax.SAXException { }
	public void unparsedEntityDecl(java.lang.String var0, java.lang.String var1, java.lang.String var2, java.lang.String var3) { }
	public void warning(org.xml.sax.SAXParseException var0) throws org.xml.sax.SAXException { }
}

