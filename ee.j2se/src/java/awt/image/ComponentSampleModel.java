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

package java.awt.image;
public class ComponentSampleModel extends java.awt.image.SampleModel {
	protected int[] bandOffsets;
	protected int[] bankIndices;
	protected int numBands;
	protected int numBanks;
	protected int pixelStride;
	protected int scanlineStride;
	public ComponentSampleModel(int var0, int var1, int var2, int var3, int var4, int[] var5)  { super(0, 0, 0, 0); } 
	public ComponentSampleModel(int var0, int var1, int var2, int var3, int var4, int[] var5, int[] var6)  { super(0, 0, 0, 0); } 
	public java.awt.image.SampleModel createCompatibleSampleModel(int var0, int var1) { return null; }
	public java.awt.image.DataBuffer createDataBuffer() { return null; }
	public java.awt.image.SampleModel createSubsetSampleModel(int[] var0) { return null; }
	public final int[] getBandOffsets() { return null; }
	public final int[] getBankIndices() { return null; }
	public java.lang.Object getDataElements(int var0, int var1, java.lang.Object var2, java.awt.image.DataBuffer var3) { return null; }
	public final int getNumDataElements() { return 0; }
	public int getOffset(int var0, int var1) { return 0; }
	public int getOffset(int var0, int var1, int var2) { return 0; }
	public final int getPixelStride() { return 0; }
	public int getSample(int var0, int var1, int var2, java.awt.image.DataBuffer var3) { return 0; }
	public final int[] getSampleSize() { return null; }
	public final int getSampleSize(int var0) { return 0; }
	public final int getScanlineStride() { return 0; }
	public int hashCode() { return 0; }
	public void setDataElements(int var0, int var1, java.lang.Object var2, java.awt.image.DataBuffer var3) { }
	public void setSample(int var0, int var1, int var2, int var3, java.awt.image.DataBuffer var4) { }
}

