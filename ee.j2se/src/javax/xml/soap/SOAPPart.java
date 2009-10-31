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
public abstract class SOAPPart implements javax.xml.soap.Node, org.w3c.dom.Document {
	public SOAPPart() { } 
	public abstract void addMimeHeader(java.lang.String var0, java.lang.String var1);
	public abstract java.util.Iterator getAllMimeHeaders();
	public abstract javax.xml.transform.Source getContent() throws javax.xml.soap.SOAPException;
	public java.lang.String getContentId() { return null; }
	public java.lang.String getContentLocation() { return null; }
	public abstract javax.xml.soap.SOAPEnvelope getEnvelope() throws javax.xml.soap.SOAPException;
	public abstract java.util.Iterator getMatchingMimeHeaders(java.lang.String[] var0);
	public abstract java.lang.String[] getMimeHeader(java.lang.String var0);
	public abstract java.util.Iterator getNonMatchingMimeHeaders(java.lang.String[] var0);
	public abstract void removeAllMimeHeaders();
	public abstract void removeMimeHeader(java.lang.String var0);
	public abstract void setContent(javax.xml.transform.Source var0) throws javax.xml.soap.SOAPException;
	public void setContentId(java.lang.String var0) { }
	public void setContentLocation(java.lang.String var0) { }
	public abstract void setMimeHeader(java.lang.String var0, java.lang.String var1);
}

