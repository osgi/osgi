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
public class MemoryImageSource implements java.awt.image.ImageProducer {
	public MemoryImageSource(int var0, int var1, java.awt.image.ColorModel var2, byte[] var3, int var4, int var5) { }
	public MemoryImageSource(int var0, int var1, java.awt.image.ColorModel var2, byte[] var3, int var4, int var5, java.util.Hashtable var6) { }
	public MemoryImageSource(int var0, int var1, java.awt.image.ColorModel var2, int[] var3, int var4, int var5) { }
	public MemoryImageSource(int var0, int var1, java.awt.image.ColorModel var2, int[] var3, int var4, int var5, java.util.Hashtable var6) { }
	public MemoryImageSource(int var0, int var1, int[] var2, int var3, int var4) { }
	public MemoryImageSource(int var0, int var1, int[] var2, int var3, int var4, java.util.Hashtable var5) { }
	public void addConsumer(java.awt.image.ImageConsumer var0) { }
	public boolean isConsumer(java.awt.image.ImageConsumer var0) { return false; }
	public void newPixels() { }
	public void newPixels(int var0, int var1, int var2, int var3) { }
	public void newPixels(int var0, int var1, int var2, int var3, boolean var4) { }
	public void newPixels(byte[] var0, java.awt.image.ColorModel var1, int var2, int var3) { }
	public void newPixels(int[] var0, java.awt.image.ColorModel var1, int var2, int var3) { }
	public void removeConsumer(java.awt.image.ImageConsumer var0) { }
	public void requestTopDownLeftRightResend(java.awt.image.ImageConsumer var0) { }
	public void setAnimated(boolean var0) { }
	public void setFullBufferUpdates(boolean var0) { }
	public void startProduction(java.awt.image.ImageConsumer var0) { }
}

