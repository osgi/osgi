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

package javax.xml.crypto.dsig;
public interface XMLSignature extends javax.xml.crypto.XMLStructure {
	public interface SignatureValue extends javax.xml.crypto.XMLStructure {
		java.lang.String getId();
		byte[] getValue();
		boolean validate(javax.xml.crypto.dsig.XMLValidateContext var0) throws javax.xml.crypto.dsig.XMLSignatureException;
	}
	public final static java.lang.String XMLNS = "http://www.w3.org/2000/09/xmldsig#";
	java.lang.String getId();
	javax.xml.crypto.dsig.keyinfo.KeyInfo getKeyInfo();
	javax.xml.crypto.KeySelectorResult getKeySelectorResult();
	java.util.List getObjects();
	javax.xml.crypto.dsig.XMLSignature.SignatureValue getSignatureValue();
	javax.xml.crypto.dsig.SignedInfo getSignedInfo();
	void sign(javax.xml.crypto.dsig.XMLSignContext var0) throws javax.xml.crypto.MarshalException, javax.xml.crypto.dsig.XMLSignatureException;
	boolean validate(javax.xml.crypto.dsig.XMLValidateContext var0) throws javax.xml.crypto.dsig.XMLSignatureException;
}

