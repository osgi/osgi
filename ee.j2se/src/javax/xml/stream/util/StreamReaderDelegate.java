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

package javax.xml.stream.util;
public class StreamReaderDelegate implements javax.xml.stream.XMLStreamReader {
	public StreamReaderDelegate() { } 
	public StreamReaderDelegate(javax.xml.stream.XMLStreamReader var0) { } 
	public void close() throws javax.xml.stream.XMLStreamException { }
	public int getAttributeCount() { return 0; }
	public java.lang.String getAttributeLocalName(int var0) { return null; }
	public javax.xml.namespace.QName getAttributeName(int var0) { return null; }
	public java.lang.String getAttributeNamespace(int var0) { return null; }
	public java.lang.String getAttributePrefix(int var0) { return null; }
	public java.lang.String getAttributeType(int var0) { return null; }
	public java.lang.String getAttributeValue(int var0) { return null; }
	public java.lang.String getAttributeValue(java.lang.String var0, java.lang.String var1) { return null; }
	public java.lang.String getCharacterEncodingScheme() { return null; }
	public java.lang.String getElementText() throws javax.xml.stream.XMLStreamException { return null; }
	public java.lang.String getEncoding() { return null; }
	public int getEventType() { return 0; }
	public java.lang.String getLocalName() { return null; }
	public javax.xml.stream.Location getLocation() { return null; }
	public javax.xml.namespace.QName getName() { return null; }
	public javax.xml.namespace.NamespaceContext getNamespaceContext() { return null; }
	public int getNamespaceCount() { return 0; }
	public java.lang.String getNamespacePrefix(int var0) { return null; }
	public java.lang.String getNamespaceURI() { return null; }
	public java.lang.String getNamespaceURI(int var0) { return null; }
	public java.lang.String getNamespaceURI(java.lang.String var0) { return null; }
	public java.lang.String getPIData() { return null; }
	public java.lang.String getPITarget() { return null; }
	public javax.xml.stream.XMLStreamReader getParent() { return null; }
	public java.lang.String getPrefix() { return null; }
	public java.lang.Object getProperty(java.lang.String var0) { return null; }
	public java.lang.String getText() { return null; }
	public char[] getTextCharacters() { return null; }
	public int getTextCharacters(int var0, char[] var1, int var2, int var3) throws javax.xml.stream.XMLStreamException { return 0; }
	public int getTextLength() { return 0; }
	public int getTextStart() { return 0; }
	public java.lang.String getVersion() { return null; }
	public boolean hasName() { return false; }
	public boolean hasNext() throws javax.xml.stream.XMLStreamException { return false; }
	public boolean hasText() { return false; }
	public boolean isAttributeSpecified(int var0) { return false; }
	public boolean isCharacters() { return false; }
	public boolean isEndElement() { return false; }
	public boolean isStandalone() { return false; }
	public boolean isStartElement() { return false; }
	public boolean isWhiteSpace() { return false; }
	public int next() throws javax.xml.stream.XMLStreamException { return 0; }
	public int nextTag() throws javax.xml.stream.XMLStreamException { return 0; }
	public void require(int var0, java.lang.String var1, java.lang.String var2) throws javax.xml.stream.XMLStreamException { }
	public void setParent(javax.xml.stream.XMLStreamReader var0) { }
	public boolean standaloneSet() { return false; }
}

