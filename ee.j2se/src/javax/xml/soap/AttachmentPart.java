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
public abstract class AttachmentPart {
	public AttachmentPart() { } 
	public abstract void addMimeHeader(java.lang.String var0, java.lang.String var1);
	public abstract void clearContent();
	public abstract java.util.Iterator getAllMimeHeaders();
	public abstract java.io.InputStream getBase64Content() throws javax.xml.soap.SOAPException;
	public abstract java.lang.Object getContent() throws javax.xml.soap.SOAPException;
	public java.lang.String getContentId() { return null; }
	public java.lang.String getContentLocation() { return null; }
	public java.lang.String getContentType() { return null; }
	public abstract javax.activation.DataHandler getDataHandler() throws javax.xml.soap.SOAPException;
	public abstract java.util.Iterator getMatchingMimeHeaders(java.lang.String[] var0);
	public abstract java.lang.String[] getMimeHeader(java.lang.String var0);
	public abstract java.util.Iterator getNonMatchingMimeHeaders(java.lang.String[] var0);
	public abstract java.io.InputStream getRawContent() throws javax.xml.soap.SOAPException;
	public abstract byte[] getRawContentBytes() throws javax.xml.soap.SOAPException;
	public abstract int getSize() throws javax.xml.soap.SOAPException;
	public abstract void removeAllMimeHeaders();
	public abstract void removeMimeHeader(java.lang.String var0);
	public abstract void setBase64Content(java.io.InputStream var0, java.lang.String var1) throws javax.xml.soap.SOAPException;
	public abstract void setContent(java.lang.Object var0, java.lang.String var1);
	public void setContentId(java.lang.String var0) { }
	public void setContentLocation(java.lang.String var0) { }
	public void setContentType(java.lang.String var0) { }
	public abstract void setDataHandler(javax.activation.DataHandler var0);
	public abstract void setMimeHeader(java.lang.String var0, java.lang.String var1);
	public abstract void setRawContent(java.io.InputStream var0, java.lang.String var1) throws javax.xml.soap.SOAPException;
	public abstract void setRawContentBytes(byte[] var0, int var1, int var2, java.lang.String var3) throws javax.xml.soap.SOAPException;
}

