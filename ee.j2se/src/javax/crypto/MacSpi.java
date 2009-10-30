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
public abstract class MacSpi {
	public MacSpi() { } 
	public java.lang.Object clone() throws java.lang.CloneNotSupportedException { return null; }
	protected abstract byte[] engineDoFinal();
	protected abstract int engineGetMacLength();
	protected abstract void engineInit(java.security.Key var0, java.security.spec.AlgorithmParameterSpec var1) throws java.security.InvalidAlgorithmParameterException, java.security.InvalidKeyException;
	protected abstract void engineReset();
	protected abstract void engineUpdate(byte var0);
	protected void engineUpdate(java.nio.ByteBuffer var0) { }
	protected abstract void engineUpdate(byte[] var0, int var1, int var2);
}

