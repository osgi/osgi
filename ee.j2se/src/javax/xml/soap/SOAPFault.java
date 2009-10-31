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
public interface SOAPFault extends javax.xml.soap.SOAPBodyElement {
	javax.xml.soap.Detail addDetail() throws javax.xml.soap.SOAPException;
	void addFaultReasonText(java.lang.String var0, java.util.Locale var1) throws javax.xml.soap.SOAPException;
	void appendFaultSubcode(javax.xml.namespace.QName var0) throws javax.xml.soap.SOAPException;
	javax.xml.soap.Detail getDetail();
	java.lang.String getFaultActor();
	java.lang.String getFaultCode();
	javax.xml.soap.Name getFaultCodeAsName();
	javax.xml.namespace.QName getFaultCodeAsQName();
	java.lang.String getFaultNode();
	java.util.Iterator getFaultReasonLocales() throws javax.xml.soap.SOAPException;
	java.lang.String getFaultReasonText(java.util.Locale var0) throws javax.xml.soap.SOAPException;
	java.util.Iterator getFaultReasonTexts() throws javax.xml.soap.SOAPException;
	java.lang.String getFaultRole();
	java.lang.String getFaultString();
	java.util.Locale getFaultStringLocale();
	java.util.Iterator getFaultSubcodes();
	boolean hasDetail();
	void removeAllFaultSubcodes();
	void setFaultActor(java.lang.String var0) throws javax.xml.soap.SOAPException;
	void setFaultCode(java.lang.String var0) throws javax.xml.soap.SOAPException;
	void setFaultCode(javax.xml.namespace.QName var0) throws javax.xml.soap.SOAPException;
	void setFaultCode(javax.xml.soap.Name var0) throws javax.xml.soap.SOAPException;
	void setFaultNode(java.lang.String var0) throws javax.xml.soap.SOAPException;
	void setFaultRole(java.lang.String var0) throws javax.xml.soap.SOAPException;
	void setFaultString(java.lang.String var0) throws javax.xml.soap.SOAPException;
	void setFaultString(java.lang.String var0, java.util.Locale var1) throws javax.xml.soap.SOAPException;
}

