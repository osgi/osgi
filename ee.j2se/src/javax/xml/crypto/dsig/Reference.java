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
public interface Reference extends javax.xml.crypto.URIReference, javax.xml.crypto.XMLStructure {
	byte[] getCalculatedDigestValue();
	javax.xml.crypto.Data getDereferencedData();
	java.io.InputStream getDigestInputStream();
	javax.xml.crypto.dsig.DigestMethod getDigestMethod();
	byte[] getDigestValue();
	java.lang.String getId();
	java.util.List getTransforms();
	boolean validate(javax.xml.crypto.dsig.XMLValidateContext var0) throws javax.xml.crypto.dsig.XMLSignatureException;
}

