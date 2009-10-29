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

package javax.imageio.stream;
public abstract class ImageInputStreamImpl implements javax.imageio.stream.ImageInputStream {
	public ImageInputStreamImpl() { }
	protected final void checkClosed() throws java.io.IOException { }
	public void close() throws java.io.IOException { }
	public void flush() throws java.io.IOException { }
	public void flushBefore(long var0) throws java.io.IOException { }
	public int getBitOffset() throws java.io.IOException { return 0; }
	public java.nio.ByteOrder getByteOrder() { return null; }
	public long getFlushedPosition() { return 0l; }
	public long getStreamPosition() throws java.io.IOException { return 0l; }
	public boolean isCached() { return false; }
	public boolean isCachedFile() { return false; }
	public boolean isCachedMemory() { return false; }
	public long length() { return 0l; }
	public void mark() { }
	public int read(byte[] var0) throws java.io.IOException { return 0; }
	public int readBit() throws java.io.IOException { return 0; }
	public long readBits(int var0) throws java.io.IOException { return 0l; }
	public boolean readBoolean() throws java.io.IOException { return false; }
	public byte readByte() throws java.io.IOException { return 0; }
	public void readBytes(javax.imageio.stream.IIOByteBuffer var0, int var1) throws java.io.IOException { }
	public char readChar() throws java.io.IOException { return '\0'; }
	public double readDouble() throws java.io.IOException { return 0.0d; }
	public float readFloat() throws java.io.IOException { return 0.0f; }
	public void readFully(byte[] var0) throws java.io.IOException { }
	public void readFully(byte[] var0, int var1, int var2) throws java.io.IOException { }
	public void readFully(char[] var0, int var1, int var2) throws java.io.IOException { }
	public void readFully(double[] var0, int var1, int var2) throws java.io.IOException { }
	public void readFully(float[] var0, int var1, int var2) throws java.io.IOException { }
	public void readFully(int[] var0, int var1, int var2) throws java.io.IOException { }
	public void readFully(long[] var0, int var1, int var2) throws java.io.IOException { }
	public void readFully(short[] var0, int var1, int var2) throws java.io.IOException { }
	public int readInt() throws java.io.IOException { return 0; }
	public java.lang.String readLine() throws java.io.IOException { return null; }
	public long readLong() throws java.io.IOException { return 0l; }
	public short readShort() throws java.io.IOException { return 0; }
	public java.lang.String readUTF() throws java.io.IOException { return null; }
	public int readUnsignedByte() throws java.io.IOException { return 0; }
	public long readUnsignedInt() throws java.io.IOException { return 0l; }
	public int readUnsignedShort() throws java.io.IOException { return 0; }
	public void reset() throws java.io.IOException { }
	public void seek(long var0) throws java.io.IOException { }
	public void setBitOffset(int var0) throws java.io.IOException { }
	public void setByteOrder(java.nio.ByteOrder var0) { }
	public int skipBytes(int var0) throws java.io.IOException { return 0; }
	public long skipBytes(long var0) throws java.io.IOException { return 0l; }
	protected int bitOffset;
	protected java.nio.ByteOrder byteOrder;
	protected long flushedPos;
	protected long streamPos;
}

