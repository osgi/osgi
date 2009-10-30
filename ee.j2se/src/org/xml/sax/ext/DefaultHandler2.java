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

package org.xml.sax.ext;
public class DefaultHandler2 extends org.xml.sax.helpers.DefaultHandler implements org.xml.sax.ext.DeclHandler, org.xml.sax.ext.EntityResolver2, org.xml.sax.ext.LexicalHandler {
	public DefaultHandler2() { } 
	public void attributeDecl(java.lang.String var0, java.lang.String var1, java.lang.String var2, java.lang.String var3, java.lang.String var4) throws org.xml.sax.SAXException { }
	public void comment(char[] var0, int var1, int var2) throws org.xml.sax.SAXException { }
	public void elementDecl(java.lang.String var0, java.lang.String var1) throws org.xml.sax.SAXException { }
	public void endCDATA() throws org.xml.sax.SAXException { }
	public void endDTD() throws org.xml.sax.SAXException { }
	public void endEntity(java.lang.String var0) throws org.xml.sax.SAXException { }
	public void externalEntityDecl(java.lang.String var0, java.lang.String var1, java.lang.String var2) throws org.xml.sax.SAXException { }
	public org.xml.sax.InputSource getExternalSubset(java.lang.String var0, java.lang.String var1) throws java.io.IOException, org.xml.sax.SAXException { return null; }
	public void internalEntityDecl(java.lang.String var0, java.lang.String var1) throws org.xml.sax.SAXException { }
	public org.xml.sax.InputSource resolveEntity(java.lang.String var0, java.lang.String var1, java.lang.String var2, java.lang.String var3) throws java.io.IOException, org.xml.sax.SAXException { return null; }
	public void startCDATA() throws org.xml.sax.SAXException { }
	public void startDTD(java.lang.String var0, java.lang.String var1, java.lang.String var2) throws org.xml.sax.SAXException { }
	public void startEntity(java.lang.String var0) throws org.xml.sax.SAXException { }
}

