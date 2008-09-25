/*
 * $Revision$
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
public class Mac implements java.lang.Cloneable {
	protected Mac(javax.crypto.MacSpi var0, java.security.Provider var1, java.lang.String var2) { }
	public final java.lang.Object clone() throws java.lang.CloneNotSupportedException { return null; }
	public final byte[] doFinal() { return null; }
	public final byte[] doFinal(byte[] var0) { return null; }
	public final void doFinal(byte[] var0, int var1) throws javax.crypto.ShortBufferException { }
	public final java.lang.String getAlgorithm() { return null; }
	public final static javax.crypto.Mac getInstance(java.lang.String var0) throws java.security.NoSuchAlgorithmException { return null; }
	public final static javax.crypto.Mac getInstance(java.lang.String var0, java.lang.String var1) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException { return null; }
	public final static javax.crypto.Mac getInstance(java.lang.String var0, java.security.Provider var1) throws java.security.NoSuchAlgorithmException { return null; }
	public final int getMacLength() { return 0; }
	public final java.security.Provider getProvider() { return null; }
	public final void init(java.security.Key var0) throws java.security.InvalidKeyException { }
	public final void init(java.security.Key var0, java.security.spec.AlgorithmParameterSpec var1) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException { }
	public final void reset() { }
	public final void update(byte var0) { }
	public final void update(byte[] var0) { }
	public final void update(byte[] var0, int var1, int var2) { }
}

