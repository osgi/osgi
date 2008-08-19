/*
 * $Date$
 *
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

package javax.xml.parsers;
public abstract class DocumentBuilder {
	protected DocumentBuilder() { }
	public abstract org.w3c.dom.DOMImplementation getDOMImplementation();
	public abstract boolean isNamespaceAware();
	public abstract boolean isValidating();
	public abstract org.w3c.dom.Document newDocument();
	public org.w3c.dom.Document parse(java.io.File var0) throws java.io.IOException, org.xml.sax.SAXException { return null; }
	public org.w3c.dom.Document parse(java.io.InputStream var0) throws java.io.IOException, org.xml.sax.SAXException { return null; }
	public org.w3c.dom.Document parse(java.io.InputStream var0, java.lang.String var1) throws java.io.IOException, org.xml.sax.SAXException { return null; }
	public org.w3c.dom.Document parse(java.lang.String var0) throws java.io.IOException, org.xml.sax.SAXException { return null; }
	public abstract org.w3c.dom.Document parse(org.xml.sax.InputSource var0) throws java.io.IOException, org.xml.sax.SAXException;
	public abstract void setEntityResolver(org.xml.sax.EntityResolver var0);
	public abstract void setErrorHandler(org.xml.sax.ErrorHandler var0);
}

