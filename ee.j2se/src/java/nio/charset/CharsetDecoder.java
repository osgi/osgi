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
public abstract class CharsetDecoder {
	protected CharsetDecoder(java.nio.charset.Charset var0, float var1, float var2) { }
	public final float averageCharsPerByte() { return 0.0f; }
	public final java.nio.charset.Charset charset() { return null; }
	public final java.nio.CharBuffer decode(java.nio.ByteBuffer var0) throws java.nio.charset.CharacterCodingException { return null; }
	public final java.nio.charset.CoderResult decode(java.nio.ByteBuffer var0, java.nio.CharBuffer var1, boolean var2) { return null; }
	protected abstract java.nio.charset.CoderResult decodeLoop(java.nio.ByteBuffer var0, java.nio.CharBuffer var1);
	public java.nio.charset.Charset detectedCharset() { return null; }
	public final java.nio.charset.CoderResult flush(java.nio.CharBuffer var0) { return null; }
	protected java.nio.charset.CoderResult implFlush(java.nio.CharBuffer var0) { return null; }
	protected void implOnMalformedInput(java.nio.charset.CodingErrorAction var0) { }
	protected void implOnUnmappableCharacter(java.nio.charset.CodingErrorAction var0) { }
	protected void implReplaceWith(java.lang.String var0) { }
	protected void implReset() { }
	public boolean isAutoDetecting() { return false; }
	public boolean isCharsetDetected() { return false; }
	public java.nio.charset.CodingErrorAction malformedInputAction() { return null; }
	public final float maxCharsPerByte() { return 0.0f; }
	public final java.nio.charset.CharsetDecoder onMalformedInput(java.nio.charset.CodingErrorAction var0) { return null; }
	public final java.nio.charset.CharsetDecoder onUnmappableCharacter(java.nio.charset.CodingErrorAction var0) { return null; }
	public final java.nio.charset.CharsetDecoder replaceWith(java.lang.String var0) { return null; }
	public final java.lang.String replacement() { return null; }
	public final java.nio.charset.CharsetDecoder reset() { return null; }
	public java.nio.charset.CodingErrorAction unmappableCharacterAction() { return null; }
}

