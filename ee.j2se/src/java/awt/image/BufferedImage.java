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
public class BufferedImage extends java.awt.Image implements java.awt.image.WritableRenderedImage {
	public BufferedImage(int var0, int var1, int var2) { }
	public BufferedImage(int var0, int var1, int var2, java.awt.image.IndexColorModel var3) { }
	public BufferedImage(java.awt.image.ColorModel var0, java.awt.image.WritableRaster var1, boolean var2, java.util.Hashtable var3) { }
	public void addTileObserver(java.awt.image.TileObserver var0) { }
	public void coerceData(boolean var0) { }
	public java.awt.image.WritableRaster copyData(java.awt.image.WritableRaster var0) { return null; }
	public java.awt.Graphics2D createGraphics() { return null; }
	public void flush() { }
	public java.awt.image.WritableRaster getAlphaRaster() { return null; }
	public java.awt.image.ColorModel getColorModel() { return null; }
	public java.awt.image.Raster getData() { return null; }
	public java.awt.image.Raster getData(java.awt.Rectangle var0) { return null; }
	public java.awt.Graphics getGraphics() { return null; }
	public int getHeight() { return 0; }
	public int getHeight(java.awt.image.ImageObserver var0) { return 0; }
	public int getMinTileX() { return 0; }
	public int getMinTileY() { return 0; }
	public int getMinX() { return 0; }
	public int getMinY() { return 0; }
	public int getNumXTiles() { return 0; }
	public int getNumYTiles() { return 0; }
	public java.lang.Object getProperty(java.lang.String var0) { return null; }
	public java.lang.Object getProperty(java.lang.String var0, java.awt.image.ImageObserver var1) { return null; }
	public java.lang.String[] getPropertyNames() { return null; }
	public int getRGB(int var0, int var1) { return 0; }
	public int[] getRGB(int var0, int var1, int var2, int var3, int[] var4, int var5, int var6) { return null; }
	public java.awt.image.WritableRaster getRaster() { return null; }
	public java.awt.image.SampleModel getSampleModel() { return null; }
	public java.awt.image.ImageProducer getSource() { return null; }
	public java.util.Vector getSources() { return null; }
	public java.awt.image.BufferedImage getSubimage(int var0, int var1, int var2, int var3) { return null; }
	public java.awt.image.Raster getTile(int var0, int var1) { return null; }
	public int getTileGridXOffset() { return 0; }
	public int getTileGridYOffset() { return 0; }
	public int getTileHeight() { return 0; }
	public int getTileWidth() { return 0; }
	public int getType() { return 0; }
	public int getWidth() { return 0; }
	public int getWidth(java.awt.image.ImageObserver var0) { return 0; }
	public java.awt.image.WritableRaster getWritableTile(int var0, int var1) { return null; }
	public java.awt.Point[] getWritableTileIndices() { return null; }
	public boolean hasTileWriters() { return false; }
	public boolean isAlphaPremultiplied() { return false; }
	public boolean isTileWritable(int var0, int var1) { return false; }
	public void releaseWritableTile(int var0, int var1) { }
	public void removeTileObserver(java.awt.image.TileObserver var0) { }
	public void setData(java.awt.image.Raster var0) { }
	public void setRGB(int var0, int var1, int var2) { }
	public void setRGB(int var0, int var1, int var2, int var3, int[] var4, int var5, int var6) { }
	public final static int TYPE_3BYTE_BGR = 5;
	public final static int TYPE_4BYTE_ABGR = 6;
	public final static int TYPE_4BYTE_ABGR_PRE = 7;
	public final static int TYPE_BYTE_BINARY = 12;
	public final static int TYPE_BYTE_GRAY = 10;
	public final static int TYPE_BYTE_INDEXED = 13;
	public final static int TYPE_CUSTOM = 0;
	public final static int TYPE_INT_ARGB = 2;
	public final static int TYPE_INT_ARGB_PRE = 3;
	public final static int TYPE_INT_BGR = 4;
	public final static int TYPE_INT_RGB = 1;
	public final static int TYPE_USHORT_555_RGB = 9;
	public final static int TYPE_USHORT_565_RGB = 8;
	public final static int TYPE_USHORT_GRAY = 11;
}

