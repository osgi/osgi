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

package java.awt.font;
public abstract class GlyphVector implements java.lang.Cloneable {
	public final static int FLAG_COMPLEX_GLYPHS = 8;
	public final static int FLAG_HAS_POSITION_ADJUSTMENTS = 2;
	public final static int FLAG_HAS_TRANSFORMS = 1;
	public final static int FLAG_MASK = 15;
	public final static int FLAG_RUN_RTL = 4;
	public GlyphVector() { } 
	public abstract boolean equals(java.awt.font.GlyphVector var0);
	public abstract java.awt.Font getFont();
	public abstract java.awt.font.FontRenderContext getFontRenderContext();
	public int getGlyphCharIndex(int var0) { return 0; }
	public int[] getGlyphCharIndices(int var0, int var1, int[] var2) { return null; }
	public abstract int getGlyphCode(int var0);
	public abstract int[] getGlyphCodes(int var0, int var1, int[] var2);
	public abstract java.awt.font.GlyphJustificationInfo getGlyphJustificationInfo(int var0);
	public abstract java.awt.Shape getGlyphLogicalBounds(int var0);
	public abstract java.awt.font.GlyphMetrics getGlyphMetrics(int var0);
	public abstract java.awt.Shape getGlyphOutline(int var0);
	public java.awt.Shape getGlyphOutline(int var0, float var1, float var2) { return null; }
	public java.awt.Rectangle getGlyphPixelBounds(int var0, java.awt.font.FontRenderContext var1, float var2, float var3) { return null; }
	public abstract java.awt.geom.Point2D getGlyphPosition(int var0);
	public abstract float[] getGlyphPositions(int var0, int var1, float[] var2);
	public abstract java.awt.geom.AffineTransform getGlyphTransform(int var0);
	public abstract java.awt.Shape getGlyphVisualBounds(int var0);
	public int getLayoutFlags() { return 0; }
	public abstract java.awt.geom.Rectangle2D getLogicalBounds();
	public abstract int getNumGlyphs();
	public abstract java.awt.Shape getOutline();
	public abstract java.awt.Shape getOutline(float var0, float var1);
	public java.awt.Rectangle getPixelBounds(java.awt.font.FontRenderContext var0, float var1, float var2) { return null; }
	public abstract java.awt.geom.Rectangle2D getVisualBounds();
	public abstract void performDefaultLayout();
	public abstract void setGlyphPosition(int var0, java.awt.geom.Point2D var1);
	public abstract void setGlyphTransform(int var0, java.awt.geom.AffineTransform var1);
}

