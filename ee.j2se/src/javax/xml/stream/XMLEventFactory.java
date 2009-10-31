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
public abstract class XMLEventFactory {
	protected XMLEventFactory() { } 
	public abstract javax.xml.stream.events.Attribute createAttribute(java.lang.String var0, java.lang.String var1);
	public abstract javax.xml.stream.events.Attribute createAttribute(java.lang.String var0, java.lang.String var1, java.lang.String var2, java.lang.String var3);
	public abstract javax.xml.stream.events.Attribute createAttribute(javax.xml.namespace.QName var0, java.lang.String var1);
	public abstract javax.xml.stream.events.Characters createCData(java.lang.String var0);
	public abstract javax.xml.stream.events.Characters createCharacters(java.lang.String var0);
	public abstract javax.xml.stream.events.Comment createComment(java.lang.String var0);
	public abstract javax.xml.stream.events.DTD createDTD(java.lang.String var0);
	public abstract javax.xml.stream.events.EndDocument createEndDocument();
	public abstract javax.xml.stream.events.EndElement createEndElement(java.lang.String var0, java.lang.String var1, java.lang.String var2);
	public abstract javax.xml.stream.events.EndElement createEndElement(java.lang.String var0, java.lang.String var1, java.lang.String var2, java.util.Iterator var3);
	public abstract javax.xml.stream.events.EndElement createEndElement(javax.xml.namespace.QName var0, java.util.Iterator var1);
	public abstract javax.xml.stream.events.EntityReference createEntityReference(java.lang.String var0, javax.xml.stream.events.EntityDeclaration var1);
	public abstract javax.xml.stream.events.Characters createIgnorableSpace(java.lang.String var0);
	public abstract javax.xml.stream.events.Namespace createNamespace(java.lang.String var0);
	public abstract javax.xml.stream.events.Namespace createNamespace(java.lang.String var0, java.lang.String var1);
	public abstract javax.xml.stream.events.ProcessingInstruction createProcessingInstruction(java.lang.String var0, java.lang.String var1);
	public abstract javax.xml.stream.events.Characters createSpace(java.lang.String var0);
	public abstract javax.xml.stream.events.StartDocument createStartDocument();
	public abstract javax.xml.stream.events.StartDocument createStartDocument(java.lang.String var0);
	public abstract javax.xml.stream.events.StartDocument createStartDocument(java.lang.String var0, java.lang.String var1);
	public abstract javax.xml.stream.events.StartDocument createStartDocument(java.lang.String var0, java.lang.String var1, boolean var2);
	public abstract javax.xml.stream.events.StartElement createStartElement(java.lang.String var0, java.lang.String var1, java.lang.String var2);
	public abstract javax.xml.stream.events.StartElement createStartElement(java.lang.String var0, java.lang.String var1, java.lang.String var2, java.util.Iterator var3, java.util.Iterator var4);
	public abstract javax.xml.stream.events.StartElement createStartElement(java.lang.String var0, java.lang.String var1, java.lang.String var2, java.util.Iterator var3, java.util.Iterator var4, javax.xml.namespace.NamespaceContext var5);
	public abstract javax.xml.stream.events.StartElement createStartElement(javax.xml.namespace.QName var0, java.util.Iterator var1, java.util.Iterator var2);
	public static javax.xml.stream.XMLEventFactory newInstance() throws javax.xml.stream.FactoryConfigurationError { return null; }
	public static javax.xml.stream.XMLEventFactory newInstance(java.lang.String var0, java.lang.ClassLoader var1) throws javax.xml.stream.FactoryConfigurationError { return null; }
	public abstract void setLocation(javax.xml.stream.Location var0);
}

