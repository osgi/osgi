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

package java.awt;
public abstract class FontMetrics implements java.io.Serializable {
	protected FontMetrics(java.awt.Font var0) { }
	public int bytesWidth(byte[] var0, int var1, int var2) { return 0; }
	public int charWidth(char var0) { return 0; }
	public int charWidth(int var0) { return 0; }
	public int charsWidth(char[] var0, int var1, int var2) { return 0; }
	public int getAscent() { return 0; }
	public int getDescent() { return 0; }
	public java.awt.Font getFont() { return null; }
	public int getHeight() { return 0; }
	public int getLeading() { return 0; }
	public java.awt.font.LineMetrics getLineMetrics(java.lang.String var0, int var1, int var2, java.awt.Graphics var3) { return null; }
	public java.awt.font.LineMetrics getLineMetrics(java.lang.String var0, java.awt.Graphics var1) { return null; }
	public java.awt.font.LineMetrics getLineMetrics(java.text.CharacterIterator var0, int var1, int var2, java.awt.Graphics var3) { return null; }
	public java.awt.font.LineMetrics getLineMetrics(char[] var0, int var1, int var2, java.awt.Graphics var3) { return null; }
	public int getMaxAdvance() { return 0; }
	public int getMaxAscent() { return 0; }
	public java.awt.geom.Rectangle2D getMaxCharBounds(java.awt.Graphics var0) { return null; }
	/** @deprecated */ public int getMaxDecent() { return 0; }
	public int getMaxDescent() { return 0; }
	public java.awt.geom.Rectangle2D getStringBounds(java.lang.String var0, int var1, int var2, java.awt.Graphics var3) { return null; }
	public java.awt.geom.Rectangle2D getStringBounds(java.lang.String var0, java.awt.Graphics var1) { return null; }
	public java.awt.geom.Rectangle2D getStringBounds(java.text.CharacterIterator var0, int var1, int var2, java.awt.Graphics var3) { return null; }
	public java.awt.geom.Rectangle2D getStringBounds(char[] var0, int var1, int var2, java.awt.Graphics var3) { return null; }
	public int[] getWidths() { return null; }
	public boolean hasUniformLineMetrics() { return false; }
	public int stringWidth(java.lang.String var0) { return 0; }
	protected java.awt.Font font;
}

