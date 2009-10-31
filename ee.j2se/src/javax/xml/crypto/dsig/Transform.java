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
public interface Transform extends javax.xml.crypto.AlgorithmMethod, javax.xml.crypto.XMLStructure {
	public final static java.lang.String BASE64 = "http://www.w3.org/2000/09/xmldsig#base64";
	public final static java.lang.String ENVELOPED = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
	public final static java.lang.String XPATH = "http://www.w3.org/TR/1999/REC-xpath-19991116";
	public final static java.lang.String XPATH2 = "http://www.w3.org/2002/06/xmldsig-filter2";
	public final static java.lang.String XSLT = "http://www.w3.org/TR/1999/REC-xslt-19991116";
	javax.xml.crypto.Data transform(javax.xml.crypto.Data var0, javax.xml.crypto.XMLCryptoContext var1) throws javax.xml.crypto.dsig.TransformException;
	javax.xml.crypto.Data transform(javax.xml.crypto.Data var0, javax.xml.crypto.XMLCryptoContext var1, java.io.OutputStream var2) throws javax.xml.crypto.dsig.TransformException;
}

