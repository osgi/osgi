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
public abstract class AbstractMarshallerImpl implements javax.xml.bind.Marshaller {
	public AbstractMarshallerImpl() { } 
	public <A extends javax.xml.bind.annotation.adapters.XmlAdapter> A getAdapter(java.lang.Class<A> var0) { return null; }
	public javax.xml.bind.attachment.AttachmentMarshaller getAttachmentMarshaller() { return null; }
	protected java.lang.String getEncoding() { return null; }
	public javax.xml.bind.ValidationEventHandler getEventHandler() throws javax.xml.bind.JAXBException { return null; }
	protected java.lang.String getJavaEncoding(java.lang.String var0) throws java.io.UnsupportedEncodingException { return null; }
	public javax.xml.bind.Marshaller.Listener getListener() { return null; }
	protected java.lang.String getNoNSSchemaLocation() { return null; }
	public org.w3c.dom.Node getNode(java.lang.Object var0) throws javax.xml.bind.JAXBException { return null; }
	public java.lang.Object getProperty(java.lang.String var0) throws javax.xml.bind.PropertyException { return null; }
	public javax.xml.validation.Schema getSchema() { return null; }
	protected java.lang.String getSchemaLocation() { return null; }
	protected boolean isFormattedOutput() { return false; }
	protected boolean isFragment() { return false; }
	public void marshal(java.lang.Object var0, java.io.File var1) throws javax.xml.bind.JAXBException { }
	public final void marshal(java.lang.Object var0, java.io.OutputStream var1) throws javax.xml.bind.JAXBException { }
	public final void marshal(java.lang.Object var0, java.io.Writer var1) throws javax.xml.bind.JAXBException { }
	public void marshal(java.lang.Object var0, javax.xml.stream.XMLEventWriter var1) throws javax.xml.bind.JAXBException { }
	public void marshal(java.lang.Object var0, javax.xml.stream.XMLStreamWriter var1) throws javax.xml.bind.JAXBException { }
	public final void marshal(java.lang.Object var0, org.w3c.dom.Node var1) throws javax.xml.bind.JAXBException { }
	public final void marshal(java.lang.Object var0, org.xml.sax.ContentHandler var1) throws javax.xml.bind.JAXBException { }
	public <A extends javax.xml.bind.annotation.adapters.XmlAdapter> void setAdapter(java.lang.Class<A> var0, A var1) { }
	public void setAdapter(javax.xml.bind.annotation.adapters.XmlAdapter var0) { }
	public void setAttachmentMarshaller(javax.xml.bind.attachment.AttachmentMarshaller var0) { }
	protected void setEncoding(java.lang.String var0) { }
	public void setEventHandler(javax.xml.bind.ValidationEventHandler var0) throws javax.xml.bind.JAXBException { }
	protected void setFormattedOutput(boolean var0) { }
	protected void setFragment(boolean var0) { }
	public void setListener(javax.xml.bind.Marshaller.Listener var0) { }
	protected void setNoNSSchemaLocation(java.lang.String var0) { }
	public void setProperty(java.lang.String var0, java.lang.Object var1) throws javax.xml.bind.PropertyException { }
	public void setSchema(javax.xml.validation.Schema var0) { }
	protected void setSchemaLocation(java.lang.String var0) { }
}

