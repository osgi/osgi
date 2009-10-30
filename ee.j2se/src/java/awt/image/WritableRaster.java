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
public class WritableRaster extends java.awt.image.Raster {
	protected WritableRaster(java.awt.image.SampleModel var0, java.awt.Point var1)  { super((java.awt.image.SampleModel) null, (java.awt.image.DataBuffer) null, (java.awt.Rectangle) null, (java.awt.Point) null, (java.awt.image.Raster) null); } 
	protected WritableRaster(java.awt.image.SampleModel var0, java.awt.image.DataBuffer var1, java.awt.Point var2)  { super((java.awt.image.SampleModel) null, (java.awt.image.DataBuffer) null, (java.awt.Rectangle) null, (java.awt.Point) null, (java.awt.image.Raster) null); } 
	protected WritableRaster(java.awt.image.SampleModel var0, java.awt.image.DataBuffer var1, java.awt.Rectangle var2, java.awt.Point var3, java.awt.image.WritableRaster var4)  { super((java.awt.image.SampleModel) null, (java.awt.image.DataBuffer) null, (java.awt.Rectangle) null, (java.awt.Point) null, (java.awt.image.Raster) null); } 
	public java.awt.image.WritableRaster createWritableChild(int var0, int var1, int var2, int var3, int var4, int var5, int[] var6) { return null; }
	public java.awt.image.WritableRaster createWritableTranslatedChild(int var0, int var1) { return null; }
	public java.awt.image.WritableRaster getWritableParent() { return null; }
	public void setDataElements(int var0, int var1, int var2, int var3, java.lang.Object var4) { }
	public void setDataElements(int var0, int var1, java.awt.image.Raster var2) { }
	public void setDataElements(int var0, int var1, java.lang.Object var2) { }
	public void setPixel(int var0, int var1, double[] var2) { }
	public void setPixel(int var0, int var1, float[] var2) { }
	public void setPixel(int var0, int var1, int[] var2) { }
	public void setPixels(int var0, int var1, int var2, int var3, double[] var4) { }
	public void setPixels(int var0, int var1, int var2, int var3, float[] var4) { }
	public void setPixels(int var0, int var1, int var2, int var3, int[] var4) { }
	public void setRect(int var0, int var1, java.awt.image.Raster var2) { }
	public void setRect(java.awt.image.Raster var0) { }
	public void setSample(int var0, int var1, int var2, double var3) { }
	public void setSample(int var0, int var1, int var2, float var3) { }
	public void setSample(int var0, int var1, int var2, int var3) { }
	public void setSamples(int var0, int var1, int var2, int var3, int var4, double[] var5) { }
	public void setSamples(int var0, int var1, int var2, int var3, int var4, float[] var5) { }
	public void setSamples(int var0, int var1, int var2, int var3, int var4, int[] var5) { }
}

