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

package java.awt.image;
public class PixelGrabber implements java.awt.image.ImageConsumer {
	public PixelGrabber(java.awt.Image var0, int var1, int var2, int var3, int var4, boolean var5) { }
	public PixelGrabber(java.awt.Image var0, int var1, int var2, int var3, int var4, int[] var5, int var6, int var7) { }
	public PixelGrabber(java.awt.image.ImageProducer var0, int var1, int var2, int var3, int var4, int[] var5, int var6, int var7) { }
	public void abortGrabbing() { }
	public java.awt.image.ColorModel getColorModel() { return null; }
	public int getHeight() { return 0; }
	public java.lang.Object getPixels() { return null; }
	public int getStatus() { return 0; }
	public int getWidth() { return 0; }
	public boolean grabPixels() throws java.lang.InterruptedException { return false; }
	public boolean grabPixels(long var0) throws java.lang.InterruptedException { return false; }
	public void imageComplete(int var0) { }
	public void setColorModel(java.awt.image.ColorModel var0) { }
	public void setDimensions(int var0, int var1) { }
	public void setHints(int var0) { }
	public void setPixels(int var0, int var1, int var2, int var3, java.awt.image.ColorModel var4, byte[] var5, int var6, int var7) { }
	public void setPixels(int var0, int var1, int var2, int var3, java.awt.image.ColorModel var4, int[] var5, int var6, int var7) { }
	public void setProperties(java.util.Hashtable var0) { }
	public void startGrabbing() { }
	public int status() { return 0; }
}

