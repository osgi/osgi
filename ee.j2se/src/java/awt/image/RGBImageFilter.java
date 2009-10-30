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
public abstract class RGBImageFilter extends java.awt.image.ImageFilter {
	protected boolean canFilterIndexColorModel;
	protected java.awt.image.ColorModel newmodel;
	protected java.awt.image.ColorModel origmodel;
	public RGBImageFilter() { } 
	public java.awt.image.IndexColorModel filterIndexColorModel(java.awt.image.IndexColorModel var0) { return null; }
	public abstract int filterRGB(int var0, int var1, int var2);
	public void filterRGBPixels(int var0, int var1, int var2, int var3, int[] var4, int var5, int var6) { }
	public void substituteColorModel(java.awt.image.ColorModel var0, java.awt.image.ColorModel var1) { }
}

