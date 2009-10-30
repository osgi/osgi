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

package javax.imageio.stream;
public interface ImageInputStream extends java.io.DataInput {
	void close() throws java.io.IOException;
	void flush() throws java.io.IOException;
	void flushBefore(long var0) throws java.io.IOException;
	int getBitOffset() throws java.io.IOException;
	java.nio.ByteOrder getByteOrder();
	long getFlushedPosition();
	long getStreamPosition() throws java.io.IOException;
	boolean isCached();
	boolean isCachedFile();
	boolean isCachedMemory();
	long length() throws java.io.IOException;
	void mark();
	int read() throws java.io.IOException;
	int read(byte[] var0) throws java.io.IOException;
	int read(byte[] var0, int var1, int var2) throws java.io.IOException;
	int readBit() throws java.io.IOException;
	long readBits(int var0) throws java.io.IOException;
	void readBytes(javax.imageio.stream.IIOByteBuffer var0, int var1) throws java.io.IOException;
	void readFully(char[] var0, int var1, int var2) throws java.io.IOException;
	void readFully(double[] var0, int var1, int var2) throws java.io.IOException;
	void readFully(float[] var0, int var1, int var2) throws java.io.IOException;
	void readFully(int[] var0, int var1, int var2) throws java.io.IOException;
	void readFully(long[] var0, int var1, int var2) throws java.io.IOException;
	void readFully(short[] var0, int var1, int var2) throws java.io.IOException;
	long readUnsignedInt() throws java.io.IOException;
	void reset() throws java.io.IOException;
	void seek(long var0) throws java.io.IOException;
	void setBitOffset(int var0) throws java.io.IOException;
	void setByteOrder(java.nio.ByteOrder var0);
	long skipBytes(long var0) throws java.io.IOException;
}

