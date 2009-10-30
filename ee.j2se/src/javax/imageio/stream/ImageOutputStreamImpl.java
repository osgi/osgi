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
public abstract class ImageOutputStreamImpl extends javax.imageio.stream.ImageInputStreamImpl implements javax.imageio.stream.ImageOutputStream {
	public ImageOutputStreamImpl() { } 
	protected final void flushBits() throws java.io.IOException { }
	public void write(byte[] var0) throws java.io.IOException { }
	public void writeBit(int var0) throws java.io.IOException { }
	public void writeBits(long var0, int var1) throws java.io.IOException { }
	public void writeBoolean(boolean var0) throws java.io.IOException { }
	public void writeByte(int var0) throws java.io.IOException { }
	public void writeBytes(java.lang.String var0) throws java.io.IOException { }
	public void writeChar(int var0) throws java.io.IOException { }
	public void writeChars(java.lang.String var0) throws java.io.IOException { }
	public void writeChars(char[] var0, int var1, int var2) throws java.io.IOException { }
	public void writeDouble(double var0) throws java.io.IOException { }
	public void writeDoubles(double[] var0, int var1, int var2) throws java.io.IOException { }
	public void writeFloat(float var0) throws java.io.IOException { }
	public void writeFloats(float[] var0, int var1, int var2) throws java.io.IOException { }
	public void writeInt(int var0) throws java.io.IOException { }
	public void writeInts(int[] var0, int var1, int var2) throws java.io.IOException { }
	public void writeLong(long var0) throws java.io.IOException { }
	public void writeLongs(long[] var0, int var1, int var2) throws java.io.IOException { }
	public void writeShort(int var0) throws java.io.IOException { }
	public void writeShorts(short[] var0, int var1, int var2) throws java.io.IOException { }
	public void writeUTF(java.lang.String var0) throws java.io.IOException { }
}

