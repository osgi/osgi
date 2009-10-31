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
public interface DigestMethod extends javax.xml.crypto.AlgorithmMethod, javax.xml.crypto.XMLStructure {
	public final static java.lang.String RIPEMD160 = "http://www.w3.org/2001/04/xmlenc#ripemd160";
	public final static java.lang.String SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
	public final static java.lang.String SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
	public final static java.lang.String SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
}

