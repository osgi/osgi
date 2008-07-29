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

package javax.crypto.spec;
public class DESKeySpec implements java.security.spec.KeySpec {
	public DESKeySpec(byte[] var0) throws java.security.InvalidKeyException { }
	public DESKeySpec(byte[] var0, int var1) throws java.security.InvalidKeyException { }
	public byte[] getKey() { return null; }
	public static boolean isParityAdjusted(byte[] var0, int var1) throws java.security.InvalidKeyException { return false; }
	public static boolean isWeak(byte[] var0, int var1) throws java.security.InvalidKeyException { return false; }
	public final static int DES_KEY_LEN = 8;
}

