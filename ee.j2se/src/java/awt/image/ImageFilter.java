/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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
public class ImageFilter implements java.awt.image.ImageConsumer, java.lang.Cloneable {
	protected java.awt.image.ImageConsumer consumer;
	public ImageFilter() { } 
	public java.lang.Object clone() { return null; }
	public java.awt.image.ImageFilter getFilterInstance(java.awt.image.ImageConsumer var0) { return null; }
	public void imageComplete(int var0) { }
	public void resendTopDownLeftRight(java.awt.image.ImageProducer var0) { }
	public void setColorModel(java.awt.image.ColorModel var0) { }
	public void setDimensions(int var0, int var1) { }
	public void setHints(int var0) { }
	public void setPixels(int var0, int var1, int var2, int var3, java.awt.image.ColorModel var4, byte[] var5, int var6, int var7) { }
	public void setPixels(int var0, int var1, int var2, int var3, java.awt.image.ColorModel var4, int[] var5, int var6, int var7) { }
	public void setProperties(java.util.Hashtable<?,?> var0) { }
}
