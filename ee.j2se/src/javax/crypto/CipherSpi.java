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
public abstract class CipherSpi {
	public CipherSpi() { }
	protected abstract byte[] engineDoFinal(byte[] var0, int var1, int var2) throws javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException;
	protected abstract int engineDoFinal(byte[] var0, int var1, int var2, byte[] var3, int var4) throws javax.crypto.BadPaddingException, javax.crypto.IllegalBlockSizeException, javax.crypto.ShortBufferException;
	protected abstract int engineGetBlockSize();
	protected abstract byte[] engineGetIV();
	protected int engineGetKeySize(java.security.Key var0) throws java.security.InvalidKeyException { return 0; }
	protected abstract int engineGetOutputSize(int var0);
	protected abstract java.security.AlgorithmParameters engineGetParameters();
	protected abstract void engineInit(int var0, java.security.Key var1, java.security.AlgorithmParameters var2, java.security.SecureRandom var3) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException;
	protected abstract void engineInit(int var0, java.security.Key var1, java.security.SecureRandom var2) throws java.security.InvalidKeyException;
	protected abstract void engineInit(int var0, java.security.Key var1, java.security.spec.AlgorithmParameterSpec var2, java.security.SecureRandom var3) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException;
	protected abstract void engineSetMode(java.lang.String var0) throws java.security.NoSuchAlgorithmException;
	protected abstract void engineSetPadding(java.lang.String var0) throws javax.crypto.NoSuchPaddingException;
	protected java.security.Key engineUnwrap(byte[] var0, java.lang.String var1, int var2) throws java.security.InvalidKeyException, java.security.NoSuchAlgorithmException { return null; }
	protected abstract byte[] engineUpdate(byte[] var0, int var1, int var2);
	protected abstract int engineUpdate(byte[] var0, int var1, int var2, byte[] var3, int var4) throws javax.crypto.ShortBufferException;
	protected byte[] engineWrap(java.security.Key var0) throws java.security.InvalidKeyException, javax.crypto.IllegalBlockSizeException { return null; }
}

