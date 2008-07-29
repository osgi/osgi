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

package java.awt;
public class Font implements java.io.Serializable {
	public Font(java.lang.String var0, int var1, int var2) { }
	public Font(java.util.Map var0) { }
	public boolean canDisplay(char var0) { return false; }
	public int canDisplayUpTo(java.lang.String var0) { return 0; }
	public int canDisplayUpTo(java.text.CharacterIterator var0, int var1, int var2) { return 0; }
	public int canDisplayUpTo(char[] var0, int var1, int var2) { return 0; }
	public static java.awt.Font createFont(int var0, java.io.InputStream var1) throws java.awt.FontFormatException, java.io.IOException { return null; }
	public java.awt.font.GlyphVector createGlyphVector(java.awt.font.FontRenderContext var0, java.lang.String var1) { return null; }
	public java.awt.font.GlyphVector createGlyphVector(java.awt.font.FontRenderContext var0, java.text.CharacterIterator var1) { return null; }
	public java.awt.font.GlyphVector createGlyphVector(java.awt.font.FontRenderContext var0, char[] var1) { return null; }
	public java.awt.font.GlyphVector createGlyphVector(java.awt.font.FontRenderContext var0, int[] var1) { return null; }
	public static java.awt.Font decode(java.lang.String var0) { return null; }
	public java.awt.Font deriveFont(float var0) { return null; }
	public java.awt.Font deriveFont(int var0) { return null; }
	public java.awt.Font deriveFont(int var0, float var1) { return null; }
	public java.awt.Font deriveFont(int var0, java.awt.geom.AffineTransform var1) { return null; }
	public java.awt.Font deriveFont(java.awt.geom.AffineTransform var0) { return null; }
	public java.awt.Font deriveFont(java.util.Map var0) { return null; }
	public java.util.Map getAttributes() { return null; }
	public java.text.AttributedCharacterIterator.Attribute[] getAvailableAttributes() { return null; }
	public byte getBaselineFor(char var0) { return 0; }
	public java.lang.String getFamily() { return null; }
	public java.lang.String getFamily(java.util.Locale var0) { return null; }
	public static java.awt.Font getFont(java.lang.String var0) { return null; }
	public static java.awt.Font getFont(java.lang.String var0, java.awt.Font var1) { return null; }
	public static java.awt.Font getFont(java.util.Map var0) { return null; }
	public java.lang.String getFontName() { return null; }
	public java.lang.String getFontName(java.util.Locale var0) { return null; }
	public float getItalicAngle() { return 0.0f; }
	public java.awt.font.LineMetrics getLineMetrics(java.lang.String var0, int var1, int var2, java.awt.font.FontRenderContext var3) { return null; }
	public java.awt.font.LineMetrics getLineMetrics(java.lang.String var0, java.awt.font.FontRenderContext var1) { return null; }
	public java.awt.font.LineMetrics getLineMetrics(java.text.CharacterIterator var0, int var1, int var2, java.awt.font.FontRenderContext var3) { return null; }
	public java.awt.font.LineMetrics getLineMetrics(char[] var0, int var1, int var2, java.awt.font.FontRenderContext var3) { return null; }
	public java.awt.geom.Rectangle2D getMaxCharBounds(java.awt.font.FontRenderContext var0) { return null; }
	public int getMissingGlyphCode() { return 0; }
	public java.lang.String getName() { return null; }
	public int getNumGlyphs() { return 0; }
	public java.lang.String getPSName() { return null; }
	/** @deprecated */ public java.awt.peer.FontPeer getPeer() { return null; }
	public int getSize() { return 0; }
	public float getSize2D() { return 0.0f; }
	public java.awt.geom.Rectangle2D getStringBounds(java.lang.String var0, int var1, int var2, java.awt.font.FontRenderContext var3) { return null; }
	public java.awt.geom.Rectangle2D getStringBounds(java.lang.String var0, java.awt.font.FontRenderContext var1) { return null; }
	public java.awt.geom.Rectangle2D getStringBounds(java.text.CharacterIterator var0, int var1, int var2, java.awt.font.FontRenderContext var3) { return null; }
	public java.awt.geom.Rectangle2D getStringBounds(char[] var0, int var1, int var2, java.awt.font.FontRenderContext var3) { return null; }
	public int getStyle() { return 0; }
	public java.awt.geom.AffineTransform getTransform() { return null; }
	public boolean hasUniformLineMetrics() { return false; }
	public int hashCode() { return 0; }
	public boolean isBold() { return false; }
	public boolean isItalic() { return false; }
	public boolean isPlain() { return false; }
	public boolean isTransformed() { return false; }
	public java.awt.font.GlyphVector layoutGlyphVector(java.awt.font.FontRenderContext var0, char[] var1, int var2, int var3, int var4) { return null; }
	public final static int BOLD = 1;
	public final static int CENTER_BASELINE = 1;
	public final static int HANGING_BASELINE = 2;
	public final static int ITALIC = 2;
	public final static int LAYOUT_LEFT_TO_RIGHT = 0;
	public final static int LAYOUT_NO_LIMIT_CONTEXT = 4;
	public final static int LAYOUT_NO_START_CONTEXT = 2;
	public final static int LAYOUT_RIGHT_TO_LEFT = 1;
	public final static int PLAIN = 0;
	public final static int ROMAN_BASELINE = 0;
	public final static int TRUETYPE_FONT = 0;
	protected java.lang.String name;
	protected float pointSize;
	protected int size;
	protected int style;
}

