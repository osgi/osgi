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
public interface SOAPBody extends javax.xml.soap.SOAPElement {
	javax.xml.soap.SOAPBodyElement addBodyElement(javax.xml.namespace.QName var0) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPBodyElement addBodyElement(javax.xml.soap.Name var0) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPBodyElement addDocument(org.w3c.dom.Document var0) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPFault addFault() throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPFault addFault(javax.xml.namespace.QName var0, java.lang.String var1) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPFault addFault(javax.xml.namespace.QName var0, java.lang.String var1, java.util.Locale var2) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPFault addFault(javax.xml.soap.Name var0, java.lang.String var1) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPFault addFault(javax.xml.soap.Name var0, java.lang.String var1, java.util.Locale var2) throws javax.xml.soap.SOAPException;
	org.w3c.dom.Document extractContentAsDocument() throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPFault getFault();
	boolean hasFault();
}

