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

package javax.xml.bind.helpers;
public class ValidationEventLocatorImpl implements javax.xml.bind.ValidationEventLocator {
	public ValidationEventLocatorImpl() { } 
	public ValidationEventLocatorImpl(java.lang.Object var0) { } 
	public ValidationEventLocatorImpl(org.w3c.dom.Node var0) { } 
	public ValidationEventLocatorImpl(org.xml.sax.Locator var0) { } 
	public ValidationEventLocatorImpl(org.xml.sax.SAXParseException var0) { } 
	public int getColumnNumber() { return 0; }
	public int getLineNumber() { return 0; }
	public org.w3c.dom.Node getNode() { return null; }
	public java.lang.Object getObject() { return null; }
	public int getOffset() { return 0; }
	public java.net.URL getURL() { return null; }
	public void setColumnNumber(int var0) { }
	public void setLineNumber(int var0) { }
	public void setNode(org.w3c.dom.Node var0) { }
	public void setObject(java.lang.Object var0) { }
	public void setOffset(int var0) { }
	public void setURL(java.net.URL var0) { }
}

