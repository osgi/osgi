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
public abstract interface ImageOutputStream extends java.io.DataOutput, javax.imageio.stream.ImageInputStream {
	public abstract void writeBit(int var0) throws java.io.IOException;
	public abstract void writeBits(long var0, int var1) throws java.io.IOException;
	public abstract void writeChars(char[] var0, int var1, int var2) throws java.io.IOException;
	public abstract void writeDoubles(double[] var0, int var1, int var2) throws java.io.IOException;
	public abstract void writeFloats(float[] var0, int var1, int var2) throws java.io.IOException;
	public abstract void writeInts(int[] var0, int var1, int var2) throws java.io.IOException;
	public abstract void writeLongs(long[] var0, int var1, int var2) throws java.io.IOException;
	public abstract void writeShorts(short[] var0, int var1, int var2) throws java.io.IOException;
}

