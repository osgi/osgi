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
public abstract interface RenderedImage {
	public abstract java.awt.image.WritableRaster copyData(java.awt.image.WritableRaster var0);
	public abstract java.awt.image.ColorModel getColorModel();
	public abstract java.awt.image.Raster getData();
	public abstract java.awt.image.Raster getData(java.awt.Rectangle var0);
	public abstract int getHeight();
	public abstract int getMinTileX();
	public abstract int getMinTileY();
	public abstract int getMinX();
	public abstract int getMinY();
	public abstract int getNumXTiles();
	public abstract int getNumYTiles();
	public abstract java.lang.Object getProperty(java.lang.String var0);
	public abstract java.lang.String[] getPropertyNames();
	public abstract java.awt.image.SampleModel getSampleModel();
	public abstract java.util.Vector getSources();
	public abstract java.awt.image.Raster getTile(int var0, int var1);
	public abstract int getTileGridXOffset();
	public abstract int getTileGridYOffset();
	public abstract int getTileHeight();
	public abstract int getTileWidth();
	public abstract int getWidth();
}

