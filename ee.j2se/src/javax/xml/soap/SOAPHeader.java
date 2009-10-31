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
public interface SOAPHeader extends javax.xml.soap.SOAPElement {
	javax.xml.soap.SOAPHeaderElement addHeaderElement(javax.xml.namespace.QName var0) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPHeaderElement addHeaderElement(javax.xml.soap.Name var0) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPHeaderElement addNotUnderstoodHeaderElement(javax.xml.namespace.QName var0) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPHeaderElement addUpgradeHeaderElement(java.lang.String var0) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPHeaderElement addUpgradeHeaderElement(java.util.Iterator var0) throws javax.xml.soap.SOAPException;
	javax.xml.soap.SOAPHeaderElement addUpgradeHeaderElement(java.lang.String[] var0) throws javax.xml.soap.SOAPException;
	java.util.Iterator examineAllHeaderElements();
	java.util.Iterator examineHeaderElements(java.lang.String var0);
	java.util.Iterator examineMustUnderstandHeaderElements(java.lang.String var0);
	java.util.Iterator extractAllHeaderElements();
	java.util.Iterator extractHeaderElements(java.lang.String var0);
}

