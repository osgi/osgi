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

package javax.imageio.stream;
public abstract interface ImageInputStream extends java.io.DataInput {
	public abstract void close() throws java.io.IOException;
	public abstract void flush() throws java.io.IOException;
	public abstract void flushBefore(long var0) throws java.io.IOException;
	public abstract int getBitOffset() throws java.io.IOException;
	public abstract java.nio.ByteOrder getByteOrder();
	public abstract long getFlushedPosition();
	public abstract long getStreamPosition() throws java.io.IOException;
	public abstract boolean isCached();
	public abstract boolean isCachedFile();
	public abstract boolean isCachedMemory();
	public abstract long length() throws java.io.IOException;
	public abstract void mark();
	public abstract int read() throws java.io.IOException;
	public abstract int read(byte[] var0) throws java.io.IOException;
	public abstract int read(byte[] var0, int var1, int var2) throws java.io.IOException;
	public abstract int readBit() throws java.io.IOException;
	public abstract long readBits(int var0) throws java.io.IOException;
	public abstract void readBytes(javax.imageio.stream.IIOByteBuffer var0, int var1) throws java.io.IOException;
	public abstract void readFully(char[] var0, int var1, int var2) throws java.io.IOException;
	public abstract void readFully(double[] var0, int var1, int var2) throws java.io.IOException;
	public abstract void readFully(float[] var0, int var1, int var2) throws java.io.IOException;
	public abstract void readFully(int[] var0, int var1, int var2) throws java.io.IOException;
	public abstract void readFully(long[] var0, int var1, int var2) throws java.io.IOException;
	public abstract void readFully(short[] var0, int var1, int var2) throws java.io.IOException;
	public abstract long readUnsignedInt() throws java.io.IOException;
	public abstract void reset() throws java.io.IOException;
	public abstract void seek(long var0) throws java.io.IOException;
	public abstract void setBitOffset(int var0) throws java.io.IOException;
	public abstract void setByteOrder(java.nio.ByteOrder var0);
	public abstract long skipBytes(long var0) throws java.io.IOException;
}

