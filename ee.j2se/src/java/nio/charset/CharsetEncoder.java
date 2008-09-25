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

package java.nio.charset;
public abstract class CharsetEncoder {
	protected CharsetEncoder(java.nio.charset.Charset var0, float var1, float var2) { }
	protected CharsetEncoder(java.nio.charset.Charset var0, float var1, float var2, byte[] var3) { }
	public final float averageBytesPerChar() { return 0.0f; }
	public boolean canEncode(char var0) { return false; }
	public boolean canEncode(java.lang.CharSequence var0) { return false; }
	public final java.nio.charset.Charset charset() { return null; }
	public final java.nio.ByteBuffer encode(java.nio.CharBuffer var0) throws java.nio.charset.CharacterCodingException { return null; }
	public final java.nio.charset.CoderResult encode(java.nio.CharBuffer var0, java.nio.ByteBuffer var1, boolean var2) { return null; }
	protected abstract java.nio.charset.CoderResult encodeLoop(java.nio.CharBuffer var0, java.nio.ByteBuffer var1);
	public final java.nio.charset.CoderResult flush(java.nio.ByteBuffer var0) { return null; }
	protected java.nio.charset.CoderResult implFlush(java.nio.ByteBuffer var0) { return null; }
	protected void implOnMalformedInput(java.nio.charset.CodingErrorAction var0) { }
	protected void implOnUnmappableCharacter(java.nio.charset.CodingErrorAction var0) { }
	protected void implReplaceWith(byte[] var0) { }
	protected void implReset() { }
	public boolean isLegalReplacement(byte[] var0) { return false; }
	public java.nio.charset.CodingErrorAction malformedInputAction() { return null; }
	public final float maxBytesPerChar() { return 0.0f; }
	public final java.nio.charset.CharsetEncoder onMalformedInput(java.nio.charset.CodingErrorAction var0) { return null; }
	public final java.nio.charset.CharsetEncoder onUnmappableCharacter(java.nio.charset.CodingErrorAction var0) { return null; }
	public final java.nio.charset.CharsetEncoder replaceWith(byte[] var0) { return null; }
	public final byte[] replacement() { return null; }
	public final java.nio.charset.CharsetEncoder reset() { return null; }
	public java.nio.charset.CodingErrorAction unmappableCharacterAction() { return null; }
}

