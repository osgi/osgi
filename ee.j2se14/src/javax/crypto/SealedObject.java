/*
 * $Date$
 *
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
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

package javax.crypto;
public class SealedObject implements java.io.Serializable {
	public SealedObject(java.io.Serializable var0, javax.crypto.Cipher var1) throws java.io.IOException, javax.crypto.IllegalBlockSizeException { }
	protected SealedObject(javax.crypto.SealedObject var0) { }
	public final java.lang.String getAlgorithm() { return null; }
	public final java.lang.Object getObject(java.security.Key var0) throws java.io.IOException, java.lang.ClassNotFoundException, java.security.InvalidKeyException, java.security.NoSuchAlgorithmException { return null; }
	public final java.lang.Object getObject(java.security.Key var0, java.lang.String var1) throws java.io.IOException, java.lang.ClassNotFoundException, java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException { return null; }
	public final java.lang.Object getObject(javax.crypto.Cipher var0) throws java.io.IOException, java.lang.ClassNotFoundException, javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException { return null; }
	protected byte[] encodedParams;
}

