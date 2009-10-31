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
public abstract class SOAPFactory {
	public SOAPFactory() { } 
	public abstract javax.xml.soap.Detail createDetail() throws javax.xml.soap.SOAPException;
	public abstract javax.xml.soap.SOAPElement createElement(java.lang.String var0) throws javax.xml.soap.SOAPException;
	public abstract javax.xml.soap.SOAPElement createElement(java.lang.String var0, java.lang.String var1, java.lang.String var2) throws javax.xml.soap.SOAPException;
	public javax.xml.soap.SOAPElement createElement(javax.xml.namespace.QName var0) throws javax.xml.soap.SOAPException { return null; }
	public abstract javax.xml.soap.SOAPElement createElement(javax.xml.soap.Name var0) throws javax.xml.soap.SOAPException;
	public javax.xml.soap.SOAPElement createElement(org.w3c.dom.Element var0) throws javax.xml.soap.SOAPException { return null; }
	public abstract javax.xml.soap.SOAPFault createFault() throws javax.xml.soap.SOAPException;
	public abstract javax.xml.soap.SOAPFault createFault(java.lang.String var0, javax.xml.namespace.QName var1) throws javax.xml.soap.SOAPException;
	public abstract javax.xml.soap.Name createName(java.lang.String var0) throws javax.xml.soap.SOAPException;
	public abstract javax.xml.soap.Name createName(java.lang.String var0, java.lang.String var1, java.lang.String var2) throws javax.xml.soap.SOAPException;
	public static javax.xml.soap.SOAPFactory newInstance() throws javax.xml.soap.SOAPException { return null; }
	public static javax.xml.soap.SOAPFactory newInstance(java.lang.String var0) throws javax.xml.soap.SOAPException { return null; }
}

