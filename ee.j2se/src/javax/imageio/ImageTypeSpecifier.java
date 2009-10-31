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

package javax.imageio;
public class ImageTypeSpecifier {
	protected java.awt.image.ColorModel colorModel;
	protected java.awt.image.SampleModel sampleModel;
	public ImageTypeSpecifier(java.awt.image.ColorModel var0, java.awt.image.SampleModel var1) { } 
	public ImageTypeSpecifier(java.awt.image.RenderedImage var0) { } 
	public static javax.imageio.ImageTypeSpecifier createBanded(java.awt.color.ColorSpace var0, int[] var1, int[] var2, int var3, boolean var4, boolean var5) { return null; }
	public java.awt.image.BufferedImage createBufferedImage(int var0, int var1) { return null; }
	public static javax.imageio.ImageTypeSpecifier createFromBufferedImageType(int var0) { return null; }
	public static javax.imageio.ImageTypeSpecifier createFromRenderedImage(java.awt.image.RenderedImage var0) { return null; }
	public static javax.imageio.ImageTypeSpecifier createGrayscale(int var0, int var1, boolean var2) { return null; }
	public static javax.imageio.ImageTypeSpecifier createGrayscale(int var0, int var1, boolean var2, boolean var3) { return null; }
	public static javax.imageio.ImageTypeSpecifier createIndexed(byte[] var0, byte[] var1, byte[] var2, byte[] var3, int var4, int var5) { return null; }
	public static javax.imageio.ImageTypeSpecifier createInterleaved(java.awt.color.ColorSpace var0, int[] var1, int var2, boolean var3, boolean var4) { return null; }
	public static javax.imageio.ImageTypeSpecifier createPacked(java.awt.color.ColorSpace var0, int var1, int var2, int var3, int var4, int var5, boolean var6) { return null; }
	public int getBitsPerBand(int var0) { return 0; }
	public int getBufferedImageType() { return 0; }
	public java.awt.image.ColorModel getColorModel() { return null; }
	public int getNumBands() { return 0; }
	public int getNumComponents() { return 0; }
	public java.awt.image.SampleModel getSampleModel() { return null; }
	public java.awt.image.SampleModel getSampleModel(int var0, int var1) { return null; }
}

