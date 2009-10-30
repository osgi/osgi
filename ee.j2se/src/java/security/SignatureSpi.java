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

package java.security;
public abstract class SignatureSpi {
	protected java.security.SecureRandom appRandom;
	public SignatureSpi() { } 
	public java.lang.Object clone() throws java.lang.CloneNotSupportedException { return null; }
	/** @deprecated */ protected abstract java.lang.Object engineGetParameter(java.lang.String var0);
	protected java.security.AlgorithmParameters engineGetParameters() { return null; }
	protected abstract void engineInitSign(java.security.PrivateKey var0) throws java.security.InvalidKeyException;
	protected void engineInitSign(java.security.PrivateKey var0, java.security.SecureRandom var1) throws java.security.InvalidKeyException { }
	protected abstract void engineInitVerify(java.security.PublicKey var0) throws java.security.InvalidKeyException;
	/** @deprecated */ protected abstract void engineSetParameter(java.lang.String var0, java.lang.Object var1);
	protected void engineSetParameter(java.security.spec.AlgorithmParameterSpec var0) throws java.security.InvalidAlgorithmParameterException { }
	protected abstract byte[] engineSign() throws java.security.SignatureException;
	protected int engineSign(byte[] var0, int var1, int var2) throws java.security.SignatureException { return 0; }
	protected abstract void engineUpdate(byte var0) throws java.security.SignatureException;
	protected void engineUpdate(java.nio.ByteBuffer var0) { }
	protected abstract void engineUpdate(byte[] var0, int var1, int var2) throws java.security.SignatureException;
	protected abstract boolean engineVerify(byte[] var0) throws java.security.SignatureException;
	protected boolean engineVerify(byte[] var0, int var1, int var2) throws java.security.SignatureException { return false; }
}

