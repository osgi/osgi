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
public class XMLReaderAdapter implements org.xml.sax.ContentHandler, org.xml.sax.Parser {
	public XMLReaderAdapter() throws org.xml.sax.SAXException { } 
	public XMLReaderAdapter(org.xml.sax.XMLReader var0) { } 
	public void characters(char[] var0, int var1, int var2) throws org.xml.sax.SAXException { }
	public void endDocument() throws org.xml.sax.SAXException { }
	public void endElement(java.lang.String var0, java.lang.String var1, java.lang.String var2) throws org.xml.sax.SAXException { }
	public void endPrefixMapping(java.lang.String var0) { }
	public void ignorableWhitespace(char[] var0, int var1, int var2) throws org.xml.sax.SAXException { }
	public void parse(java.lang.String var0) throws java.io.IOException, org.xml.sax.SAXException { }
	public void parse(org.xml.sax.InputSource var0) throws java.io.IOException, org.xml.sax.SAXException { }
	public void processingInstruction(java.lang.String var0, java.lang.String var1) throws org.xml.sax.SAXException { }
	public void setDTDHandler(org.xml.sax.DTDHandler var0) { }
	public void setDocumentHandler(org.xml.sax.DocumentHandler var0) { }
	public void setDocumentLocator(org.xml.sax.Locator var0) { }
	public void setEntityResolver(org.xml.sax.EntityResolver var0) { }
	public void setErrorHandler(org.xml.sax.ErrorHandler var0) { }
	public void setLocale(java.util.Locale var0) throws org.xml.sax.SAXException { }
	public void skippedEntity(java.lang.String var0) throws org.xml.sax.SAXException { }
	public void startDocument() throws org.xml.sax.SAXException { }
	public void startElement(java.lang.String var0, java.lang.String var1, java.lang.String var2, org.xml.sax.Attributes var3) throws org.xml.sax.SAXException { }
	public void startPrefixMapping(java.lang.String var0, java.lang.String var1) { }
}

