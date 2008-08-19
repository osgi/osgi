/*
 * $Date$
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

package javax.imageio;
public class ImageWriteParam extends javax.imageio.IIOParam {
	protected ImageWriteParam() { }
	public ImageWriteParam(java.util.Locale var0) { }
	public boolean canOffsetTiles() { return false; }
	public boolean canWriteCompressed() { return false; }
	public boolean canWriteProgressive() { return false; }
	public boolean canWriteTiles() { return false; }
	public float getBitRate(float var0) { return 0.0f; }
	public int getCompressionMode() { return 0; }
	public float getCompressionQuality() { return 0.0f; }
	public java.lang.String[] getCompressionQualityDescriptions() { return null; }
	public float[] getCompressionQualityValues() { return null; }
	public java.lang.String getCompressionType() { return null; }
	public java.lang.String[] getCompressionTypes() { return null; }
	public java.util.Locale getLocale() { return null; }
	public java.lang.String getLocalizedCompressionTypeName() { return null; }
	public java.awt.Dimension[] getPreferredTileSizes() { return null; }
	public int getProgressiveMode() { return 0; }
	public int getTileGridXOffset() { return 0; }
	public int getTileGridYOffset() { return 0; }
	public int getTileHeight() { return 0; }
	public int getTileWidth() { return 0; }
	public int getTilingMode() { return 0; }
	public boolean isCompressionLossless() { return false; }
	public void setCompressionMode(int var0) { }
	public void setCompressionQuality(float var0) { }
	public void setCompressionType(java.lang.String var0) { }
	public void setProgressiveMode(int var0) { }
	public void setTiling(int var0, int var1, int var2, int var3) { }
	public void setTilingMode(int var0) { }
	public void unsetCompression() { }
	public void unsetTiling() { }
	public final static int MODE_COPY_FROM_METADATA = 3;
	public final static int MODE_DEFAULT = 1;
	public final static int MODE_DISABLED = 0;
	public final static int MODE_EXPLICIT = 2;
	protected boolean canOffsetTiles;
	protected boolean canWriteCompressed;
	protected boolean canWriteProgressive;
	protected boolean canWriteTiles;
	protected int compressionMode;
	protected float compressionQuality;
	protected java.lang.String compressionType;
	protected java.lang.String[] compressionTypes;
	protected java.util.Locale locale;
	protected java.awt.Dimension[] preferredTileSizes;
	protected int progressiveMode;
	protected int tileGridXOffset;
	protected int tileGridYOffset;
	protected int tileHeight;
	protected int tileWidth;
	protected int tilingMode;
	protected boolean tilingSet;
}

