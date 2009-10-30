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

package java.nio.channels;
public final class Channels {
	public static java.nio.channels.ReadableByteChannel newChannel(java.io.InputStream var0) { return null; }
	public static java.nio.channels.WritableByteChannel newChannel(java.io.OutputStream var0) { return null; }
	public static java.io.InputStream newInputStream(java.nio.channels.ReadableByteChannel var0) { return null; }
	public static java.io.OutputStream newOutputStream(java.nio.channels.WritableByteChannel var0) { return null; }
	public static java.io.Reader newReader(java.nio.channels.ReadableByteChannel var0, java.lang.String var1) { return null; }
	public static java.io.Reader newReader(java.nio.channels.ReadableByteChannel var0, java.nio.charset.CharsetDecoder var1, int var2) { return null; }
	public static java.io.Writer newWriter(java.nio.channels.WritableByteChannel var0, java.lang.String var1) { return null; }
	public static java.io.Writer newWriter(java.nio.channels.WritableByteChannel var0, java.nio.charset.CharsetEncoder var1, int var2) { return null; }
	private Channels() { } /* generated constructor to prevent compiler adding default public constructor */
}

