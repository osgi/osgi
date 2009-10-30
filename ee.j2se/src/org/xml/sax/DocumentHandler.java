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

package org.xml.sax;
/** @deprecated */ public interface DocumentHandler {
	void characters(char[] var0, int var1, int var2) throws org.xml.sax.SAXException;
	void endDocument() throws org.xml.sax.SAXException;
	void endElement(java.lang.String var0) throws org.xml.sax.SAXException;
	void ignorableWhitespace(char[] var0, int var1, int var2) throws org.xml.sax.SAXException;
	void processingInstruction(java.lang.String var0, java.lang.String var1) throws org.xml.sax.SAXException;
	void setDocumentLocator(org.xml.sax.Locator var0);
	void startDocument() throws org.xml.sax.SAXException;
	void startElement(java.lang.String var0, org.xml.sax.AttributeList var1) throws org.xml.sax.SAXException;
}

