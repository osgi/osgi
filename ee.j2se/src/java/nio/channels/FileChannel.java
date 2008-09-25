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

package java.nio.channels;
public abstract class FileChannel extends java.nio.channels.spi.AbstractInterruptibleChannel implements java.nio.channels.ByteChannel, java.nio.channels.GatheringByteChannel, java.nio.channels.ScatteringByteChannel {
	protected FileChannel() { }
	public abstract void force(boolean var0) throws java.io.IOException;
	public final java.nio.channels.FileLock lock() throws java.io.IOException { return null; }
	public abstract java.nio.channels.FileLock lock(long var0, long var1, boolean var2) throws java.io.IOException;
	public abstract java.nio.MappedByteBuffer map(java.nio.channels.FileChannel.MapMode var0, long var1, long var2) throws java.io.IOException;
	public abstract long position() throws java.io.IOException;
	public abstract java.nio.channels.FileChannel position(long var0) throws java.io.IOException;
	public abstract int read(java.nio.ByteBuffer var0) throws java.io.IOException;
	public abstract int read(java.nio.ByteBuffer var0, long var1) throws java.io.IOException;
	public final long read(java.nio.ByteBuffer[] var0) throws java.io.IOException { return 0l; }
	public abstract long size() throws java.io.IOException;
	public abstract long transferFrom(java.nio.channels.ReadableByteChannel var0, long var1, long var2) throws java.io.IOException;
	public abstract long transferTo(long var0, long var1, java.nio.channels.WritableByteChannel var2) throws java.io.IOException;
	public abstract java.nio.channels.FileChannel truncate(long var0) throws java.io.IOException;
	public final java.nio.channels.FileLock tryLock() throws java.io.IOException { return null; }
	public abstract java.nio.channels.FileLock tryLock(long var0, long var1, boolean var2) throws java.io.IOException;
	public abstract int write(java.nio.ByteBuffer var0) throws java.io.IOException;
	public abstract int write(java.nio.ByteBuffer var0, long var1) throws java.io.IOException;
	public final long write(java.nio.ByteBuffer[] var0) throws java.io.IOException { return 0l; }
	public static class MapMode {
		public final static java.nio.channels.FileChannel.MapMode PRIVATE; static { PRIVATE = null; }
		public final static java.nio.channels.FileChannel.MapMode READ_ONLY; static { READ_ONLY = null; }
		public final static java.nio.channels.FileChannel.MapMode READ_WRITE; static { READ_WRITE = null; }
		private MapMode() { } /* generated constructor to prevent compiler adding default public constructor */
	}
}

