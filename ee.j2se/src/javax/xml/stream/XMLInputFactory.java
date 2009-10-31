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
public abstract class XMLInputFactory {
	public final static java.lang.String ALLOCATOR = "javax.xml.stream.allocator";
	public final static java.lang.String IS_COALESCING = "javax.xml.stream.isCoalescing";
	public final static java.lang.String IS_NAMESPACE_AWARE = "javax.xml.stream.isNamespaceAware";
	public final static java.lang.String IS_REPLACING_ENTITY_REFERENCES = "javax.xml.stream.isReplacingEntityReferences";
	public final static java.lang.String IS_SUPPORTING_EXTERNAL_ENTITIES = "javax.xml.stream.isSupportingExternalEntities";
	public final static java.lang.String IS_VALIDATING = "javax.xml.stream.isValidating";
	public final static java.lang.String REPORTER = "javax.xml.stream.reporter";
	public final static java.lang.String RESOLVER = "javax.xml.stream.resolver";
	public final static java.lang.String SUPPORT_DTD = "javax.xml.stream.supportDTD";
	protected XMLInputFactory() { } 
	public abstract javax.xml.stream.XMLEventReader createFilteredReader(javax.xml.stream.XMLEventReader var0, javax.xml.stream.EventFilter var1) throws javax.xml.stream.XMLStreamException;
	public abstract javax.xml.stream.XMLStreamReader createFilteredReader(javax.xml.stream.XMLStreamReader var0, javax.xml.stream.StreamFilter var1) throws javax.xml.stream.XMLStreamException;
	public abstract javax.xml.stream.XMLEventReader createXMLEventReader(java.io.InputStream var0) throws javax.xml.stream.XMLStreamException;
	public abstract javax.xml.stream.XMLEventReader createXMLEventReader(java.io.InputStream var0, java.lang.String var1) throws javax.xml.stream.XMLStreamException;
	public abstract javax.xml.stream.XMLEventReader createXMLEventReader(java.io.Reader var0) throws javax.xml.stream.XMLStreamException;
	public abstract javax.xml.stream.XMLEventReader createXMLEventReader(java.lang.String var0, java.io.InputStream var1) throws javax.xml.stream.XMLStreamException;
	public abstract javax.xml.stream.XMLEventReader createXMLEventReader(java.lang.String var0, java.io.Reader var1) throws javax.xml.stream.XMLStreamException;
	public abstract javax.xml.stream.XMLEventReader createXMLEventReader(javax.xml.stream.XMLStreamReader var0) throws javax.xml.stream.XMLStreamException;
	public abstract javax.xml.stream.XMLEventReader createXMLEventReader(javax.xml.transform.Source var0) throws javax.xml.stream.XMLStreamException;
	public abstract javax.xml.stream.XMLStreamReader createXMLStreamReader(java.io.InputStream var0) throws javax.xml.stream.XMLStreamException;
	public abstract javax.xml.stream.XMLStreamReader createXMLStreamReader(java.io.InputStream var0, java.lang.String var1) throws javax.xml.stream.XMLStreamException;
	public abstract javax.xml.stream.XMLStreamReader createXMLStreamReader(java.io.Reader var0) throws javax.xml.stream.XMLStreamException;
	public abstract javax.xml.stream.XMLStreamReader createXMLStreamReader(java.lang.String var0, java.io.InputStream var1) throws javax.xml.stream.XMLStreamException;
	public abstract javax.xml.stream.XMLStreamReader createXMLStreamReader(java.lang.String var0, java.io.Reader var1) throws javax.xml.stream.XMLStreamException;
	public abstract javax.xml.stream.XMLStreamReader createXMLStreamReader(javax.xml.transform.Source var0) throws javax.xml.stream.XMLStreamException;
	public abstract javax.xml.stream.util.XMLEventAllocator getEventAllocator();
	public abstract java.lang.Object getProperty(java.lang.String var0);
	public abstract javax.xml.stream.XMLReporter getXMLReporter();
	public abstract javax.xml.stream.XMLResolver getXMLResolver();
	public abstract boolean isPropertySupported(java.lang.String var0);
	public static javax.xml.stream.XMLInputFactory newInstance() throws javax.xml.stream.FactoryConfigurationError { return null; }
	public static javax.xml.stream.XMLInputFactory newInstance(java.lang.String var0, java.lang.ClassLoader var1) throws javax.xml.stream.FactoryConfigurationError { return null; }
	public abstract void setEventAllocator(javax.xml.stream.util.XMLEventAllocator var0);
	public abstract void setProperty(java.lang.String var0, java.lang.Object var1);
	public abstract void setXMLReporter(javax.xml.stream.XMLReporter var0);
	public abstract void setXMLResolver(javax.xml.stream.XMLResolver var0);
}

