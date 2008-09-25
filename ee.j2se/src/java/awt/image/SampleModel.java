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
public abstract class SampleModel {
	public SampleModel(int var0, int var1, int var2, int var3) { }
	public abstract java.awt.image.SampleModel createCompatibleSampleModel(int var0, int var1);
	public abstract java.awt.image.DataBuffer createDataBuffer();
	public abstract java.awt.image.SampleModel createSubsetSampleModel(int[] var0);
	public java.lang.Object getDataElements(int var0, int var1, int var2, int var3, java.lang.Object var4, java.awt.image.DataBuffer var5) { return null; }
	public abstract java.lang.Object getDataElements(int var0, int var1, java.lang.Object var2, java.awt.image.DataBuffer var3);
	public final int getDataType() { return 0; }
	public final int getHeight() { return 0; }
	public final int getNumBands() { return 0; }
	public abstract int getNumDataElements();
	public double[] getPixel(int var0, int var1, double[] var2, java.awt.image.DataBuffer var3) { return null; }
	public float[] getPixel(int var0, int var1, float[] var2, java.awt.image.DataBuffer var3) { return null; }
	public int[] getPixel(int var0, int var1, int[] var2, java.awt.image.DataBuffer var3) { return null; }
	public double[] getPixels(int var0, int var1, int var2, int var3, double[] var4, java.awt.image.DataBuffer var5) { return null; }
	public float[] getPixels(int var0, int var1, int var2, int var3, float[] var4, java.awt.image.DataBuffer var5) { return null; }
	public int[] getPixels(int var0, int var1, int var2, int var3, int[] var4, java.awt.image.DataBuffer var5) { return null; }
	public abstract int getSample(int var0, int var1, int var2, java.awt.image.DataBuffer var3);
	public double getSampleDouble(int var0, int var1, int var2, java.awt.image.DataBuffer var3) { return 0.0d; }
	public float getSampleFloat(int var0, int var1, int var2, java.awt.image.DataBuffer var3) { return 0.0f; }
	public abstract int[] getSampleSize();
	public abstract int getSampleSize(int var0);
	public double[] getSamples(int var0, int var1, int var2, int var3, int var4, double[] var5, java.awt.image.DataBuffer var6) { return null; }
	public float[] getSamples(int var0, int var1, int var2, int var3, int var4, float[] var5, java.awt.image.DataBuffer var6) { return null; }
	public int[] getSamples(int var0, int var1, int var2, int var3, int var4, int[] var5, java.awt.image.DataBuffer var6) { return null; }
	public int getTransferType() { return 0; }
	public final int getWidth() { return 0; }
	public void setDataElements(int var0, int var1, int var2, int var3, java.lang.Object var4, java.awt.image.DataBuffer var5) { }
	public abstract void setDataElements(int var0, int var1, java.lang.Object var2, java.awt.image.DataBuffer var3);
	public void setPixel(int var0, int var1, double[] var2, java.awt.image.DataBuffer var3) { }
	public void setPixel(int var0, int var1, float[] var2, java.awt.image.DataBuffer var3) { }
	public void setPixel(int var0, int var1, int[] var2, java.awt.image.DataBuffer var3) { }
	public void setPixels(int var0, int var1, int var2, int var3, double[] var4, java.awt.image.DataBuffer var5) { }
	public void setPixels(int var0, int var1, int var2, int var3, float[] var4, java.awt.image.DataBuffer var5) { }
	public void setPixels(int var0, int var1, int var2, int var3, int[] var4, java.awt.image.DataBuffer var5) { }
	public void setSample(int var0, int var1, int var2, double var3, java.awt.image.DataBuffer var4) { }
	public void setSample(int var0, int var1, int var2, float var3, java.awt.image.DataBuffer var4) { }
	public abstract void setSample(int var0, int var1, int var2, int var3, java.awt.image.DataBuffer var4);
	public void setSamples(int var0, int var1, int var2, int var3, int var4, double[] var5, java.awt.image.DataBuffer var6) { }
	public void setSamples(int var0, int var1, int var2, int var3, int var4, float[] var5, java.awt.image.DataBuffer var6) { }
	public void setSamples(int var0, int var1, int var2, int var3, int var4, int[] var5, java.awt.image.DataBuffer var6) { }
	protected int dataType;
	protected int height;
	protected int numBands;
	protected int width;
}

