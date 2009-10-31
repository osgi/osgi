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

package javax.xml.stream;
public interface XMLStreamReader extends javax.xml.stream.XMLStreamConstants {
	void close() throws javax.xml.stream.XMLStreamException;
	int getAttributeCount();
	java.lang.String getAttributeLocalName(int var0);
	javax.xml.namespace.QName getAttributeName(int var0);
	java.lang.String getAttributeNamespace(int var0);
	java.lang.String getAttributePrefix(int var0);
	java.lang.String getAttributeType(int var0);
	java.lang.String getAttributeValue(int var0);
	java.lang.String getAttributeValue(java.lang.String var0, java.lang.String var1);
	java.lang.String getCharacterEncodingScheme();
	java.lang.String getElementText() throws javax.xml.stream.XMLStreamException;
	java.lang.String getEncoding();
	int getEventType();
	java.lang.String getLocalName();
	javax.xml.stream.Location getLocation();
	javax.xml.namespace.QName getName();
	javax.xml.namespace.NamespaceContext getNamespaceContext();
	int getNamespaceCount();
	java.lang.String getNamespacePrefix(int var0);
	java.lang.String getNamespaceURI();
	java.lang.String getNamespaceURI(int var0);
	java.lang.String getNamespaceURI(java.lang.String var0);
	java.lang.String getPIData();
	java.lang.String getPITarget();
	java.lang.String getPrefix();
	java.lang.Object getProperty(java.lang.String var0);
	java.lang.String getText();
	char[] getTextCharacters();
	int getTextCharacters(int var0, char[] var1, int var2, int var3) throws javax.xml.stream.XMLStreamException;
	int getTextLength();
	int getTextStart();
	java.lang.String getVersion();
	boolean hasName();
	boolean hasNext() throws javax.xml.stream.XMLStreamException;
	boolean hasText();
	boolean isAttributeSpecified(int var0);
	boolean isCharacters();
	boolean isEndElement();
	boolean isStandalone();
	boolean isStartElement();
	boolean isWhiteSpace();
	int next() throws javax.xml.stream.XMLStreamException;
	int nextTag() throws javax.xml.stream.XMLStreamException;
	void require(int var0, java.lang.String var1, java.lang.String var2) throws javax.xml.stream.XMLStreamException;
	boolean standaloneSet();
}

