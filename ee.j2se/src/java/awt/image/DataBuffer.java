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

package java.awt.image;
public abstract class DataBuffer {
	protected DataBuffer(int var0, int var1) { }
	protected DataBuffer(int var0, int var1, int var2) { }
	protected DataBuffer(int var0, int var1, int var2, int var3) { }
	protected DataBuffer(int var0, int var1, int var2, int[] var3) { }
	public int getDataType() { return 0; }
	public static int getDataTypeSize(int var0) { return 0; }
	public int getElem(int var0) { return 0; }
	public abstract int getElem(int var0, int var1);
	public double getElemDouble(int var0) { return 0.0d; }
	public double getElemDouble(int var0, int var1) { return 0.0d; }
	public float getElemFloat(int var0) { return 0.0f; }
	public float getElemFloat(int var0, int var1) { return 0.0f; }
	public int getNumBanks() { return 0; }
	public int getOffset() { return 0; }
	public int[] getOffsets() { return null; }
	public int getSize() { return 0; }
	public void setElem(int var0, int var1) { }
	public abstract void setElem(int var0, int var1, int var2);
	public void setElemDouble(int var0, double var1) { }
	public void setElemDouble(int var0, int var1, double var2) { }
	public void setElemFloat(int var0, float var1) { }
	public void setElemFloat(int var0, int var1, float var2) { }
	public final static int TYPE_BYTE = 0;
	public final static int TYPE_DOUBLE = 5;
	public final static int TYPE_FLOAT = 4;
	public final static int TYPE_INT = 3;
	public final static int TYPE_SHORT = 2;
	public final static int TYPE_UNDEFINED = 32;
	public final static int TYPE_USHORT = 1;
	protected int banks;
	protected int dataType;
	protected int offset;
	protected int[] offsets;
	protected int size;
}

