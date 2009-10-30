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
public abstract class ColorModel implements java.awt.Transparency {
	protected int pixel_bits;
	protected int transferType;
	public ColorModel(int var0) { } 
	protected ColorModel(int var0, int[] var1, java.awt.color.ColorSpace var2, boolean var3, boolean var4, int var5, int var6) { } 
	public java.awt.image.ColorModel coerceData(java.awt.image.WritableRaster var0, boolean var1) { return null; }
	public java.awt.image.SampleModel createCompatibleSampleModel(int var0, int var1) { return null; }
	public java.awt.image.WritableRaster createCompatibleWritableRaster(int var0, int var1) { return null; }
	public void finalize() { }
	public abstract int getAlpha(int var0);
	public int getAlpha(java.lang.Object var0) { return 0; }
	public java.awt.image.WritableRaster getAlphaRaster(java.awt.image.WritableRaster var0) { return null; }
	public abstract int getBlue(int var0);
	public int getBlue(java.lang.Object var0) { return 0; }
	public final java.awt.color.ColorSpace getColorSpace() { return null; }
	public int[] getComponentSize() { return null; }
	public int getComponentSize(int var0) { return 0; }
	public int[] getComponents(int var0, int[] var1, int var2) { return null; }
	public int[] getComponents(java.lang.Object var0, int[] var1, int var2) { return null; }
	public int getDataElement(float[] var0, int var1) { return 0; }
	public int getDataElement(int[] var0, int var1) { return 0; }
	public java.lang.Object getDataElements(int var0, java.lang.Object var1) { return null; }
	public java.lang.Object getDataElements(float[] var0, int var1, java.lang.Object var2) { return null; }
	public java.lang.Object getDataElements(int[] var0, int var1, java.lang.Object var2) { return null; }
	public abstract int getGreen(int var0);
	public int getGreen(java.lang.Object var0) { return 0; }
	public float[] getNormalizedComponents(java.lang.Object var0, float[] var1, int var2) { return null; }
	public float[] getNormalizedComponents(int[] var0, int var1, float[] var2, int var3) { return null; }
	public int getNumColorComponents() { return 0; }
	public int getNumComponents() { return 0; }
	public int getPixelSize() { return 0; }
	public int getRGB(int var0) { return 0; }
	public int getRGB(java.lang.Object var0) { return 0; }
	public static java.awt.image.ColorModel getRGBdefault() { return null; }
	public abstract int getRed(int var0);
	public int getRed(java.lang.Object var0) { return 0; }
	public final int getTransferType() { return 0; }
	public int getTransparency() { return 0; }
	public int[] getUnnormalizedComponents(float[] var0, int var1, int[] var2, int var3) { return null; }
	public final boolean hasAlpha() { return false; }
	public int hashCode() { return 0; }
	public final boolean isAlphaPremultiplied() { return false; }
	public boolean isCompatibleRaster(java.awt.image.Raster var0) { return false; }
	public boolean isCompatibleSampleModel(java.awt.image.SampleModel var0) { return false; }
}

