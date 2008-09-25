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
public class Raster {
	protected Raster(java.awt.image.SampleModel var0, java.awt.Point var1) { }
	protected Raster(java.awt.image.SampleModel var0, java.awt.image.DataBuffer var1, java.awt.Point var2) { }
	protected Raster(java.awt.image.SampleModel var0, java.awt.image.DataBuffer var1, java.awt.Rectangle var2, java.awt.Point var3, java.awt.image.Raster var4) { }
	public static java.awt.image.WritableRaster createBandedRaster(int var0, int var1, int var2, int var3, java.awt.Point var4) { return null; }
	public static java.awt.image.WritableRaster createBandedRaster(int var0, int var1, int var2, int var3, int[] var4, int[] var5, java.awt.Point var6) { return null; }
	public static java.awt.image.WritableRaster createBandedRaster(java.awt.image.DataBuffer var0, int var1, int var2, int var3, int[] var4, int[] var5, java.awt.Point var6) { return null; }
	public java.awt.image.Raster createChild(int var0, int var1, int var2, int var3, int var4, int var5, int[] var6) { return null; }
	public java.awt.image.WritableRaster createCompatibleWritableRaster() { return null; }
	public java.awt.image.WritableRaster createCompatibleWritableRaster(int var0, int var1) { return null; }
	public java.awt.image.WritableRaster createCompatibleWritableRaster(int var0, int var1, int var2, int var3) { return null; }
	public java.awt.image.WritableRaster createCompatibleWritableRaster(java.awt.Rectangle var0) { return null; }
	public static java.awt.image.WritableRaster createInterleavedRaster(int var0, int var1, int var2, int var3, int var4, int[] var5, java.awt.Point var6) { return null; }
	public static java.awt.image.WritableRaster createInterleavedRaster(int var0, int var1, int var2, int var3, java.awt.Point var4) { return null; }
	public static java.awt.image.WritableRaster createInterleavedRaster(java.awt.image.DataBuffer var0, int var1, int var2, int var3, int var4, int[] var5, java.awt.Point var6) { return null; }
	public static java.awt.image.WritableRaster createPackedRaster(int var0, int var1, int var2, int var3, int var4, java.awt.Point var5) { return null; }
	public static java.awt.image.WritableRaster createPackedRaster(int var0, int var1, int var2, int[] var3, java.awt.Point var4) { return null; }
	public static java.awt.image.WritableRaster createPackedRaster(java.awt.image.DataBuffer var0, int var1, int var2, int var3, java.awt.Point var4) { return null; }
	public static java.awt.image.WritableRaster createPackedRaster(java.awt.image.DataBuffer var0, int var1, int var2, int var3, int[] var4, java.awt.Point var5) { return null; }
	public static java.awt.image.Raster createRaster(java.awt.image.SampleModel var0, java.awt.image.DataBuffer var1, java.awt.Point var2) { return null; }
	public java.awt.image.Raster createTranslatedChild(int var0, int var1) { return null; }
	public static java.awt.image.WritableRaster createWritableRaster(java.awt.image.SampleModel var0, java.awt.Point var1) { return null; }
	public static java.awt.image.WritableRaster createWritableRaster(java.awt.image.SampleModel var0, java.awt.image.DataBuffer var1, java.awt.Point var2) { return null; }
	public java.awt.Rectangle getBounds() { return null; }
	public java.awt.image.DataBuffer getDataBuffer() { return null; }
	public java.lang.Object getDataElements(int var0, int var1, int var2, int var3, java.lang.Object var4) { return null; }
	public java.lang.Object getDataElements(int var0, int var1, java.lang.Object var2) { return null; }
	public final int getHeight() { return 0; }
	public final int getMinX() { return 0; }
	public final int getMinY() { return 0; }
	public final int getNumBands() { return 0; }
	public final int getNumDataElements() { return 0; }
	public java.awt.image.Raster getParent() { return null; }
	public double[] getPixel(int var0, int var1, double[] var2) { return null; }
	public float[] getPixel(int var0, int var1, float[] var2) { return null; }
	public int[] getPixel(int var0, int var1, int[] var2) { return null; }
	public double[] getPixels(int var0, int var1, int var2, int var3, double[] var4) { return null; }
	public float[] getPixels(int var0, int var1, int var2, int var3, float[] var4) { return null; }
	public int[] getPixels(int var0, int var1, int var2, int var3, int[] var4) { return null; }
	public int getSample(int var0, int var1, int var2) { return 0; }
	public double getSampleDouble(int var0, int var1, int var2) { return 0.0d; }
	public float getSampleFloat(int var0, int var1, int var2) { return 0.0f; }
	public java.awt.image.SampleModel getSampleModel() { return null; }
	public final int getSampleModelTranslateX() { return 0; }
	public final int getSampleModelTranslateY() { return 0; }
	public double[] getSamples(int var0, int var1, int var2, int var3, int var4, double[] var5) { return null; }
	public float[] getSamples(int var0, int var1, int var2, int var3, int var4, float[] var5) { return null; }
	public int[] getSamples(int var0, int var1, int var2, int var3, int var4, int[] var5) { return null; }
	public final int getTransferType() { return 0; }
	public final int getWidth() { return 0; }
	protected java.awt.image.DataBuffer dataBuffer;
	protected int height;
	protected int minX;
	protected int minY;
	protected int numBands;
	protected int numDataElements;
	protected java.awt.image.Raster parent;
	protected java.awt.image.SampleModel sampleModel;
	protected int sampleModelTranslateX;
	protected int sampleModelTranslateY;
	protected int width;
}

