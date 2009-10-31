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

package javax.xml.bind;
public interface Marshaller {
	public static abstract class Listener {
		public Listener() { } 
		public void afterMarshal(java.lang.Object var0) { }
		public void beforeMarshal(java.lang.Object var0) { }
	}
	public final static java.lang.String JAXB_ENCODING = "jaxb.encoding";
	public final static java.lang.String JAXB_FORMATTED_OUTPUT = "jaxb.formatted.output";
	public final static java.lang.String JAXB_FRAGMENT = "jaxb.fragment";
	public final static java.lang.String JAXB_NO_NAMESPACE_SCHEMA_LOCATION = "jaxb.noNamespaceSchemaLocation";
	public final static java.lang.String JAXB_SCHEMA_LOCATION = "jaxb.schemaLocation";
	<A extends javax.xml.bind.annotation.adapters.XmlAdapter> A getAdapter(java.lang.Class<A> var0);
	javax.xml.bind.attachment.AttachmentMarshaller getAttachmentMarshaller();
	javax.xml.bind.ValidationEventHandler getEventHandler() throws javax.xml.bind.JAXBException;
	javax.xml.bind.Marshaller.Listener getListener();
	org.w3c.dom.Node getNode(java.lang.Object var0) throws javax.xml.bind.JAXBException;
	java.lang.Object getProperty(java.lang.String var0) throws javax.xml.bind.PropertyException;
	javax.xml.validation.Schema getSchema();
	void marshal(java.lang.Object var0, java.io.File var1) throws javax.xml.bind.JAXBException;
	void marshal(java.lang.Object var0, java.io.OutputStream var1) throws javax.xml.bind.JAXBException;
	void marshal(java.lang.Object var0, java.io.Writer var1) throws javax.xml.bind.JAXBException;
	void marshal(java.lang.Object var0, javax.xml.stream.XMLEventWriter var1) throws javax.xml.bind.JAXBException;
	void marshal(java.lang.Object var0, javax.xml.stream.XMLStreamWriter var1) throws javax.xml.bind.JAXBException;
	void marshal(java.lang.Object var0, javax.xml.transform.Result var1) throws javax.xml.bind.JAXBException;
	void marshal(java.lang.Object var0, org.w3c.dom.Node var1) throws javax.xml.bind.JAXBException;
	void marshal(java.lang.Object var0, org.xml.sax.ContentHandler var1) throws javax.xml.bind.JAXBException;
	<A extends javax.xml.bind.annotation.adapters.XmlAdapter> void setAdapter(java.lang.Class<A> var0, A var1);
	void setAdapter(javax.xml.bind.annotation.adapters.XmlAdapter var0);
	void setAttachmentMarshaller(javax.xml.bind.attachment.AttachmentMarshaller var0);
	void setEventHandler(javax.xml.bind.ValidationEventHandler var0) throws javax.xml.bind.JAXBException;
	void setListener(javax.xml.bind.Marshaller.Listener var0);
	void setProperty(java.lang.String var0, java.lang.Object var1) throws javax.xml.bind.PropertyException;
	void setSchema(javax.xml.validation.Schema var0);
}

