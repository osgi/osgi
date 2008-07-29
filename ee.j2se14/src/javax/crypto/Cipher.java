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
public class Cipher {
	protected Cipher(javax.crypto.CipherSpi var0, java.security.Provider var1, java.lang.String var2) { }
	public final byte[] doFinal() throws javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException { return null; }
	public final byte[] doFinal(byte[] var0) throws javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException { return null; }
	public final int doFinal(byte[] var0, int var1) throws javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException, javax.crypto.ShortBufferException { return 0; }
	public final byte[] doFinal(byte[] var0, int var1, int var2) throws javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException { return null; }
	public final int doFinal(byte[] var0, int var1, int var2, byte[] var3) throws javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException, javax.crypto.ShortBufferException { return 0; }
	public final int doFinal(byte[] var0, int var1, int var2, byte[] var3, int var4) throws javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException, javax.crypto.ShortBufferException { return 0; }
	public final java.lang.String getAlgorithm() { return null; }
	public final int getBlockSize() { return 0; }
	public final javax.crypto.ExemptionMechanism getExemptionMechanism() { return null; }
	public final byte[] getIV() { return null; }
	public final static javax.crypto.Cipher getInstance(java.lang.String var0) throws java.security.NoSuchAlgorithmException, javax.crypto.NoSuchPaddingException { return null; }
	public final static javax.crypto.Cipher getInstance(java.lang.String var0, java.lang.String var1) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException, javax.crypto.NoSuchPaddingException { return null; }
	public final static javax.crypto.Cipher getInstance(java.lang.String var0, java.security.Provider var1) throws java.security.NoSuchAlgorithmException, javax.crypto.NoSuchPaddingException { return null; }
	public final int getOutputSize(int var0) { return 0; }
	public final java.security.AlgorithmParameters getParameters() { return null; }
	public final java.security.Provider getProvider() { return null; }
	public final void init(int var0, java.security.Key var1) throws java.security.InvalidKeyException { }
	public final void init(int var0, java.security.Key var1, java.security.AlgorithmParameters var2) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException { }
	public final void init(int var0, java.security.Key var1, java.security.AlgorithmParameters var2, java.security.SecureRandom var3) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException { }
	public final void init(int var0, java.security.Key var1, java.security.SecureRandom var2) throws java.security.InvalidKeyException { }
	public final void init(int var0, java.security.Key var1, java.security.spec.AlgorithmParameterSpec var2) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException { }
	public final void init(int var0, java.security.Key var1, java.security.spec.AlgorithmParameterSpec var2, java.security.SecureRandom var3) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException { }
	public final void init(int var0, java.security.cert.Certificate var1) throws java.security.InvalidKeyException { }
	public final void init(int var0, java.security.cert.Certificate var1, java.security.SecureRandom var2) throws java.security.InvalidKeyException { }
	public final java.security.Key unwrap(byte[] var0, java.lang.String var1, int var2) throws java.security.InvalidKeyException, java.security.NoSuchAlgorithmException { return null; }
	public final byte[] update(byte[] var0) { return null; }
	public final byte[] update(byte[] var0, int var1, int var2) { return null; }
	public final int update(byte[] var0, int var1, int var2, byte[] var3) throws javax.crypto.ShortBufferException { return 0; }
	public final int update(byte[] var0, int var1, int var2, byte[] var3, int var4) throws javax.crypto.ShortBufferException { return 0; }
	public final byte[] wrap(java.security.Key var0) throws java.security.InvalidKeyException, javax.crypto.IllegalBlockSizeException { return null; }
	public final static int DECRYPT_MODE = 2;
	public final static int ENCRYPT_MODE = 1;
	public final static int PRIVATE_KEY = 2;
	public final static int PUBLIC_KEY = 1;
	public final static int SECRET_KEY = 3;
	public final static int UNWRAP_MODE = 4;
	public final static int WRAP_MODE = 3;
}

