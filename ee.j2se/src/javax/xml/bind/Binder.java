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
public abstract class Binder<XmlNode> {
	public Binder() { } 
	public abstract javax.xml.bind.ValidationEventHandler getEventHandler() throws javax.xml.bind.JAXBException;
	public abstract java.lang.Object getJAXBNode(XmlNode var0);
	public abstract java.lang.Object getProperty(java.lang.String var0) throws javax.xml.bind.PropertyException;
	public abstract javax.xml.validation.Schema getSchema();
	public abstract XmlNode getXMLNode(java.lang.Object var0);
	public abstract void marshal(java.lang.Object var0, XmlNode var1) throws javax.xml.bind.JAXBException;
	public abstract void setEventHandler(javax.xml.bind.ValidationEventHandler var0) throws javax.xml.bind.JAXBException;
	public abstract void setProperty(java.lang.String var0, java.lang.Object var1) throws javax.xml.bind.PropertyException;
	public abstract void setSchema(javax.xml.validation.Schema var0);
	public abstract java.lang.Object unmarshal(XmlNode var0) throws javax.xml.bind.JAXBException;
	public abstract <T> javax.xml.bind.JAXBElement<T> unmarshal(XmlNode var0, java.lang.Class<T> var1) throws javax.xml.bind.JAXBException;
	public abstract java.lang.Object updateJAXB(XmlNode var0) throws javax.xml.bind.JAXBException;
	public abstract XmlNode updateXML(java.lang.Object var0) throws javax.xml.bind.JAXBException;
	public abstract XmlNode updateXML(java.lang.Object var0, XmlNode var1) throws javax.xml.bind.JAXBException;
}

