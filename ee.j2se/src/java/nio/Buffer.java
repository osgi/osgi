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

package java.nio;
public abstract class Buffer {
	public abstract java.lang.Object array();
	public abstract int arrayOffset();
	public final int capacity() { return 0; }
	public final java.nio.Buffer clear() { return null; }
	public final java.nio.Buffer flip() { return null; }
	public abstract boolean hasArray();
	public final boolean hasRemaining() { return false; }
	public abstract boolean isDirect();
	public abstract boolean isReadOnly();
	public final int limit() { return 0; }
	public final java.nio.Buffer limit(int var0) { return null; }
	public final java.nio.Buffer mark() { return null; }
	public final int position() { return 0; }
	public final java.nio.Buffer position(int var0) { return null; }
	public final int remaining() { return 0; }
	public final java.nio.Buffer reset() { return null; }
	public final java.nio.Buffer rewind() { return null; }
	Buffer() { } /* generated constructor to prevent compiler adding default public constructor */
}

