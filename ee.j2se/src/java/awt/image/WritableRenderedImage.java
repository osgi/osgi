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
public abstract interface WritableRenderedImage extends java.awt.image.RenderedImage {
	public abstract void addTileObserver(java.awt.image.TileObserver var0);
	public abstract java.awt.image.WritableRaster getWritableTile(int var0, int var1);
	public abstract java.awt.Point[] getWritableTileIndices();
	public abstract boolean hasTileWriters();
	public abstract boolean isTileWritable(int var0, int var1);
	public abstract void releaseWritableTile(int var0, int var1);
	public abstract void removeTileObserver(java.awt.image.TileObserver var0);
	public abstract void setData(java.awt.image.Raster var0);
}

