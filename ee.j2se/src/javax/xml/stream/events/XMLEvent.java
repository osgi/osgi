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

package javax.xml.stream.events;
public interface XMLEvent extends javax.xml.stream.XMLStreamConstants {
	javax.xml.stream.events.Characters asCharacters();
	javax.xml.stream.events.EndElement asEndElement();
	javax.xml.stream.events.StartElement asStartElement();
	int getEventType();
	javax.xml.stream.Location getLocation();
	javax.xml.namespace.QName getSchemaType();
	boolean isAttribute();
	boolean isCharacters();
	boolean isEndDocument();
	boolean isEndElement();
	boolean isEntityReference();
	boolean isNamespace();
	boolean isProcessingInstruction();
	boolean isStartDocument();
	boolean isStartElement();
	void writeAsEncodedUnicode(java.io.Writer var0) throws javax.xml.stream.XMLStreamException;
}

