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
public class ExemptionMechanism {
	protected ExemptionMechanism(javax.crypto.ExemptionMechanismSpi var0, java.security.Provider var1, java.lang.String var2) { } 
	protected void finalize() { }
	public final byte[] genExemptionBlob() throws javax.crypto.ExemptionMechanismException { return null; }
	public final int genExemptionBlob(byte[] var0) throws javax.crypto.ExemptionMechanismException, javax.crypto.ShortBufferException { return 0; }
	public final int genExemptionBlob(byte[] var0, int var1) throws javax.crypto.ExemptionMechanismException, javax.crypto.ShortBufferException { return 0; }
	public final static javax.crypto.ExemptionMechanism getInstance(java.lang.String var0) throws java.security.NoSuchAlgorithmException { return null; }
	public final static javax.crypto.ExemptionMechanism getInstance(java.lang.String var0, java.lang.String var1) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException { return null; }
	public final static javax.crypto.ExemptionMechanism getInstance(java.lang.String var0, java.security.Provider var1) throws java.security.NoSuchAlgorithmException { return null; }
	public final java.lang.String getName() { return null; }
	public final int getOutputSize(int var0) { return 0; }
	public final java.security.Provider getProvider() { return null; }
	public final void init(java.security.Key var0) throws java.security.InvalidKeyException, javax.crypto.ExemptionMechanismException { }
	public final void init(java.security.Key var0, java.security.AlgorithmParameters var1) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException, javax.crypto.ExemptionMechanismException { }
	public final void init(java.security.Key var0, java.security.spec.AlgorithmParameterSpec var1) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException, javax.crypto.ExemptionMechanismException { }
	public final boolean isCryptoAllowed(java.security.Key var0) throws javax.crypto.ExemptionMechanismException { return false; }
}

