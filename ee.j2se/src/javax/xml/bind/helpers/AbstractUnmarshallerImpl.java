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
public abstract class AbstractUnmarshallerImpl implements javax.xml.bind.Unmarshaller {
	protected boolean validating;
	public AbstractUnmarshallerImpl() { } 
	protected javax.xml.bind.UnmarshalException createUnmarshalException(org.xml.sax.SAXException var0) { return null; }
	public <A extends javax.xml.bind.annotation.adapters.XmlAdapter> A getAdapter(java.lang.Class<A> var0) { return null; }
	public javax.xml.bind.attachment.AttachmentUnmarshaller getAttachmentUnmarshaller() { return null; }
	public javax.xml.bind.ValidationEventHandler getEventHandler() throws javax.xml.bind.JAXBException { return null; }
	public javax.xml.bind.Unmarshaller.Listener getListener() { return null; }
	public java.lang.Object getProperty(java.lang.String var0) throws javax.xml.bind.PropertyException { return null; }
	public javax.xml.validation.Schema getSchema() { return null; }
	protected org.xml.sax.XMLReader getXMLReader() throws javax.xml.bind.JAXBException { return null; }
	public boolean isValidating() throws javax.xml.bind.JAXBException { return false; }
	public <A extends javax.xml.bind.annotation.adapters.XmlAdapter> void setAdapter(java.lang.Class<A> var0, A var1) { }
	public void setAdapter(javax.xml.bind.annotation.adapters.XmlAdapter var0) { }
	public void setAttachmentUnmarshaller(javax.xml.bind.attachment.AttachmentUnmarshaller var0) { }
	public void setEventHandler(javax.xml.bind.ValidationEventHandler var0) throws javax.xml.bind.JAXBException { }
	public void setListener(javax.xml.bind.Unmarshaller.Listener var0) { }
	public void setProperty(java.lang.String var0, java.lang.Object var1) throws javax.xml.bind.PropertyException { }
	public void setSchema(javax.xml.validation.Schema var0) { }
	public void setValidating(boolean var0) throws javax.xml.bind.JAXBException { }
	public final java.lang.Object unmarshal(java.io.File var0) throws javax.xml.bind.JAXBException { return null; }
	public final java.lang.Object unmarshal(java.io.InputStream var0) throws javax.xml.bind.JAXBException { return null; }
	public final java.lang.Object unmarshal(java.io.Reader var0) throws javax.xml.bind.JAXBException { return null; }
	public final java.lang.Object unmarshal(java.net.URL var0) throws javax.xml.bind.JAXBException { return null; }
	public java.lang.Object unmarshal(javax.xml.stream.XMLEventReader var0) throws javax.xml.bind.JAXBException { return null; }
	public <T> javax.xml.bind.JAXBElement<T> unmarshal(javax.xml.stream.XMLEventReader var0, java.lang.Class<T> var1) throws javax.xml.bind.JAXBException { return null; }
	public java.lang.Object unmarshal(javax.xml.stream.XMLStreamReader var0) throws javax.xml.bind.JAXBException { return null; }
	public <T> javax.xml.bind.JAXBElement<T> unmarshal(javax.xml.stream.XMLStreamReader var0, java.lang.Class<T> var1) throws javax.xml.bind.JAXBException { return null; }
	public java.lang.Object unmarshal(javax.xml.transform.Source var0) throws javax.xml.bind.JAXBException { return null; }
	public <T> javax.xml.bind.JAXBElement<T> unmarshal(javax.xml.transform.Source var0, java.lang.Class<T> var1) throws javax.xml.bind.JAXBException { return null; }
	public <T> javax.xml.bind.JAXBElement<T> unmarshal(org.w3c.dom.Node var0, java.lang.Class<T> var1) throws javax.xml.bind.JAXBException { return null; }
	public final java.lang.Object unmarshal(org.xml.sax.InputSource var0) throws javax.xml.bind.JAXBException { return null; }
	protected abstract java.lang.Object unmarshal(org.xml.sax.XMLReader var0, org.xml.sax.InputSource var1) throws javax.xml.bind.JAXBException;
}

