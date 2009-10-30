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
public interface WritableRenderedImage extends java.awt.image.RenderedImage {
	void addTileObserver(java.awt.image.TileObserver var0);
	java.awt.image.WritableRaster getWritableTile(int var0, int var1);
	java.awt.Point[] getWritableTileIndices();
	boolean hasTileWriters();
	boolean isTileWritable(int var0, int var1);
	void releaseWritableTile(int var0, int var1);
	void removeTileObserver(java.awt.image.TileObserver var0);
	void setData(java.awt.image.Raster var0);
}

