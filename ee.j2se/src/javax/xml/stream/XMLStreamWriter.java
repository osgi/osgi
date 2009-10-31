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
public interface XMLStreamWriter {
	void close() throws javax.xml.stream.XMLStreamException;
	void flush() throws javax.xml.stream.XMLStreamException;
	javax.xml.namespace.NamespaceContext getNamespaceContext();
	java.lang.String getPrefix(java.lang.String var0) throws javax.xml.stream.XMLStreamException;
	java.lang.Object getProperty(java.lang.String var0);
	void setDefaultNamespace(java.lang.String var0) throws javax.xml.stream.XMLStreamException;
	void setNamespaceContext(javax.xml.namespace.NamespaceContext var0) throws javax.xml.stream.XMLStreamException;
	void setPrefix(java.lang.String var0, java.lang.String var1) throws javax.xml.stream.XMLStreamException;
	void writeAttribute(java.lang.String var0, java.lang.String var1) throws javax.xml.stream.XMLStreamException;
	void writeAttribute(java.lang.String var0, java.lang.String var1, java.lang.String var2) throws javax.xml.stream.XMLStreamException;
	void writeAttribute(java.lang.String var0, java.lang.String var1, java.lang.String var2, java.lang.String var3) throws javax.xml.stream.XMLStreamException;
	void writeCData(java.lang.String var0) throws javax.xml.stream.XMLStreamException;
	void writeCharacters(java.lang.String var0) throws javax.xml.stream.XMLStreamException;
	void writeCharacters(char[] var0, int var1, int var2) throws javax.xml.stream.XMLStreamException;
	void writeComment(java.lang.String var0) throws javax.xml.stream.XMLStreamException;
	void writeDTD(java.lang.String var0) throws javax.xml.stream.XMLStreamException;
	void writeDefaultNamespace(java.lang.String var0) throws javax.xml.stream.XMLStreamException;
	void writeEmptyElement(java.lang.String var0) throws javax.xml.stream.XMLStreamException;
	void writeEmptyElement(java.lang.String var0, java.lang.String var1) throws javax.xml.stream.XMLStreamException;
	void writeEmptyElement(java.lang.String var0, java.lang.String var1, java.lang.String var2) throws javax.xml.stream.XMLStreamException;
	void writeEndDocument() throws javax.xml.stream.XMLStreamException;
	void writeEndElement() throws javax.xml.stream.XMLStreamException;
	void writeEntityRef(java.lang.String var0) throws javax.xml.stream.XMLStreamException;
	void writeNamespace(java.lang.String var0, java.lang.String var1) throws javax.xml.stream.XMLStreamException;
	void writeProcessingInstruction(java.lang.String var0) throws javax.xml.stream.XMLStreamException;
	void writeProcessingInstruction(java.lang.String var0, java.lang.String var1) throws javax.xml.stream.XMLStreamException;
	void writeStartDocument() throws javax.xml.stream.XMLStreamException;
	void writeStartDocument(java.lang.String var0) throws javax.xml.stream.XMLStreamException;
	void writeStartDocument(java.lang.String var0, java.lang.String var1) throws javax.xml.stream.XMLStreamException;
	void writeStartElement(java.lang.String var0) throws javax.xml.stream.XMLStreamException;
	void writeStartElement(java.lang.String var0, java.lang.String var1) throws javax.xml.stream.XMLStreamException;
	void writeStartElement(java.lang.String var0, java.lang.String var1, java.lang.String var2) throws javax.xml.stream.XMLStreamException;
}

