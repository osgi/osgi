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
public abstract class KeyAgreementSpi {
	public KeyAgreementSpi() { }
	protected abstract java.security.Key engineDoPhase(java.security.Key var0, boolean var1) throws java.security.InvalidKeyException;
	protected abstract byte[] engineGenerateSecret();
	protected abstract javax.crypto.SecretKey engineGenerateSecret(java.lang.String var0) throws java.security.InvalidKeyException, java.security.NoSuchAlgorithmException;
	protected abstract int engineGenerateSecret(byte[] var0, int var1) throws javax.crypto.ShortBufferException;
	protected abstract void engineInit(java.security.Key var0, java.security.SecureRandom var1) throws java.security.InvalidKeyException;
	protected abstract void engineInit(java.security.Key var0, java.security.spec.AlgorithmParameterSpec var1, java.security.SecureRandom var2) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException;
}

