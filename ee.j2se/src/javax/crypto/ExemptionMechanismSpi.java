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
public abstract class ExemptionMechanismSpi {
	public ExemptionMechanismSpi() { }
	protected abstract byte[] engineGenExemptionBlob() throws javax.crypto.ExemptionMechanismException;
	protected abstract int engineGenExemptionBlob(byte[] var0, int var1) throws javax.crypto.ExemptionMechanismException, javax.crypto.ShortBufferException;
	protected abstract int engineGetOutputSize(int var0);
	protected abstract void engineInit(java.security.Key var0) throws java.security.InvalidKeyException, javax.crypto.ExemptionMechanismException;
	protected abstract void engineInit(java.security.Key var0, java.security.AlgorithmParameters var1) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException, javax.crypto.ExemptionMechanismException;
	protected abstract void engineInit(java.security.Key var0, java.security.spec.AlgorithmParameterSpec var1) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException, javax.crypto.ExemptionMechanismException;
}

