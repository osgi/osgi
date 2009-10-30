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

package javax.crypto;
public class EncryptedPrivateKeyInfo {
	public EncryptedPrivateKeyInfo(java.lang.String var0, byte[] var1) throws java.security.NoSuchAlgorithmException { } 
	public EncryptedPrivateKeyInfo(java.security.AlgorithmParameters var0, byte[] var1) throws java.security.NoSuchAlgorithmException { } 
	public EncryptedPrivateKeyInfo(byte[] var0) throws java.io.IOException { } 
	public java.lang.String getAlgName() { return null; }
	public java.security.AlgorithmParameters getAlgParameters() { return null; }
	public byte[] getEncoded() throws java.io.IOException { return null; }
	public byte[] getEncryptedData() { return null; }
	public java.security.spec.PKCS8EncodedKeySpec getKeySpec(java.security.Key var0) throws java.security.InvalidKeyException, java.security.NoSuchAlgorithmException { return null; }
	public java.security.spec.PKCS8EncodedKeySpec getKeySpec(java.security.Key var0, java.lang.String var1) throws java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException { return null; }
	public java.security.spec.PKCS8EncodedKeySpec getKeySpec(java.security.Key var0, java.security.Provider var1) throws java.security.InvalidKeyException, java.security.NoSuchAlgorithmException { return null; }
	public java.security.spec.PKCS8EncodedKeySpec getKeySpec(javax.crypto.Cipher var0) throws java.security.spec.InvalidKeySpecException { return null; }
}

