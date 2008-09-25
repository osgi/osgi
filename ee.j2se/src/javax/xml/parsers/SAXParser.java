/*
 * $Revision$
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
public abstract class SAXParser {
	protected SAXParser() { }
	public abstract org.xml.sax.Parser getParser() throws org.xml.sax.SAXException;
	public abstract java.lang.Object getProperty(java.lang.String var0) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException;
	public abstract org.xml.sax.XMLReader getXMLReader() throws org.xml.sax.SAXException;
	public abstract boolean isNamespaceAware();
	public abstract boolean isValidating();
	public void parse(java.io.File var0, org.xml.sax.HandlerBase var1) throws java.io.IOException, org.xml.sax.SAXException { }
	public void parse(java.io.File var0, org.xml.sax.helpers.DefaultHandler var1) throws java.io.IOException, org.xml.sax.SAXException { }
	public void parse(java.io.InputStream var0, org.xml.sax.HandlerBase var1) throws java.io.IOException, org.xml.sax.SAXException { }
	public void parse(java.io.InputStream var0, org.xml.sax.HandlerBase var1, java.lang.String var2) throws java.io.IOException, org.xml.sax.SAXException { }
	public void parse(java.io.InputStream var0, org.xml.sax.helpers.DefaultHandler var1) throws java.io.IOException, org.xml.sax.SAXException { }
	public void parse(java.io.InputStream var0, org.xml.sax.helpers.DefaultHandler var1, java.lang.String var2) throws java.io.IOException, org.xml.sax.SAXException { }
	public void parse(java.lang.String var0, org.xml.sax.HandlerBase var1) throws java.io.IOException, org.xml.sax.SAXException { }
	public void parse(java.lang.String var0, org.xml.sax.helpers.DefaultHandler var1) throws java.io.IOException, org.xml.sax.SAXException { }
	public void parse(org.xml.sax.InputSource var0, org.xml.sax.HandlerBase var1) throws java.io.IOException, org.xml.sax.SAXException { }
	public void parse(org.xml.sax.InputSource var0, org.xml.sax.helpers.DefaultHandler var1) throws java.io.IOException, org.xml.sax.SAXException { }
	public abstract void setProperty(java.lang.String var0, java.lang.Object var1) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException;
}

