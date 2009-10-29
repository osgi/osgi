/*
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
public class CoderResult {
	public boolean isError() { return false; }
	public boolean isMalformed() { return false; }
	public boolean isOverflow() { return false; }
	public boolean isUnderflow() { return false; }
	public boolean isUnmappable() { return false; }
	public int length() { return 0; }
	public static java.nio.charset.CoderResult malformedForLength(int var0) { return null; }
	public void throwException() throws java.nio.charset.CharacterCodingException { }
	public static java.nio.charset.CoderResult unmappableForLength(int var0) { return null; }
	public final static java.nio.charset.CoderResult OVERFLOW; static { OVERFLOW = null; }
	public final static java.nio.charset.CoderResult UNDERFLOW; static { UNDERFLOW = null; }
	private CoderResult() { } /* generated constructor to prevent compiler adding default public constructor */
}

