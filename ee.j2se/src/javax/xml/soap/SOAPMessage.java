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

package javax.xml.soap;
public abstract class SOAPMessage {
	public final static java.lang.String CHARACTER_SET_ENCODING = "javax.xml.soap.character-set-encoding";
	public final static java.lang.String WRITE_XML_DECLARATION = "javax.xml.soap.write-xml-declaration";
	public SOAPMessage() { } 
	public abstract void addAttachmentPart(javax.xml.soap.AttachmentPart var0);
	public abstract int countAttachments();
	public abstract javax.xml.soap.AttachmentPart createAttachmentPart();
	public javax.xml.soap.AttachmentPart createAttachmentPart(java.lang.Object var0, java.lang.String var1) { return null; }
	public javax.xml.soap.AttachmentPart createAttachmentPart(javax.activation.DataHandler var0) { return null; }
	public abstract javax.xml.soap.AttachmentPart getAttachment(javax.xml.soap.SOAPElement var0) throws javax.xml.soap.SOAPException;
	public abstract java.util.Iterator getAttachments();
	public abstract java.util.Iterator getAttachments(javax.xml.soap.MimeHeaders var0);
	public abstract java.lang.String getContentDescription();
	public abstract javax.xml.soap.MimeHeaders getMimeHeaders();
	public java.lang.Object getProperty(java.lang.String var0) throws javax.xml.soap.SOAPException { return null; }
	public javax.xml.soap.SOAPBody getSOAPBody() throws javax.xml.soap.SOAPException { return null; }
	public javax.xml.soap.SOAPHeader getSOAPHeader() throws javax.xml.soap.SOAPException { return null; }
	public abstract javax.xml.soap.SOAPPart getSOAPPart();
	public abstract void removeAllAttachments();
	public abstract void removeAttachments(javax.xml.soap.MimeHeaders var0);
	public abstract void saveChanges() throws javax.xml.soap.SOAPException;
	public abstract boolean saveRequired();
	public abstract void setContentDescription(java.lang.String var0);
	public void setProperty(java.lang.String var0, java.lang.Object var1) throws javax.xml.soap.SOAPException { }
	public abstract void writeTo(java.io.OutputStream var0) throws java.io.IOException, javax.xml.soap.SOAPException;
}

