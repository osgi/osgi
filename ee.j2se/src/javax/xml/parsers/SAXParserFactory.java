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

package javax.xml.parsers;
public abstract class SAXParserFactory {
	protected SAXParserFactory() { }
	public abstract boolean getFeature(java.lang.String var0) throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException;
	public boolean isNamespaceAware() { return false; }
	public boolean isValidating() { return false; }
	public static javax.xml.parsers.SAXParserFactory newInstance() throws javax.xml.parsers.FactoryConfigurationError { return null; }
	public abstract javax.xml.parsers.SAXParser newSAXParser() throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException;
	public abstract void setFeature(java.lang.String var0, boolean var1) throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException;
	public void setNamespaceAware(boolean var0) { }
	public void setValidating(boolean var0) { }
}

