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

package javax.swing.text;
public class GlyphView extends javax.swing.text.View implements java.lang.Cloneable, javax.swing.text.TabableView {
	public GlyphView(javax.swing.text.Element var0) { super((javax.swing.text.Element) null); }
	protected void checkPainter() { }
	protected final java.lang.Object clone() { return null; }
	public java.awt.Color getBackground() { return null; }
	public java.awt.Font getFont() { return null; }
	public java.awt.Color getForeground() { return null; }
	public javax.swing.text.GlyphView.GlyphPainter getGlyphPainter() { return null; }
	public float getPartialSpan(int var0, int var1) { return 0.0f; }
	public float getPreferredSpan(int var0) { return 0.0f; }
	public javax.swing.text.TabExpander getTabExpander() { return null; }
	public float getTabbedSpan(float var0, javax.swing.text.TabExpander var1) { return 0.0f; }
	public javax.swing.text.Segment getText(int var0, int var1) { return null; }
	public boolean isStrikeThrough() { return false; }
	public boolean isSubscript() { return false; }
	public boolean isSuperscript() { return false; }
	public boolean isUnderline() { return false; }
	public java.awt.Shape modelToView(int var0, java.awt.Shape var1, javax.swing.text.Position.Bias var2) throws javax.swing.text.BadLocationException { return null; }
	public void paint(java.awt.Graphics var0, java.awt.Shape var1) { }
	public void setGlyphPainter(javax.swing.text.GlyphView.GlyphPainter var0) { }
	public int viewToModel(float var0, float var1, java.awt.Shape var2, javax.swing.text.Position.Bias[] var3) { return 0; }
	public static abstract class GlyphPainter {
		public GlyphPainter() { }
		public abstract float getAscent(javax.swing.text.GlyphView var0);
		public abstract int getBoundedPosition(javax.swing.text.GlyphView var0, int var1, float var2, float var3);
		public abstract float getDescent(javax.swing.text.GlyphView var0);
		public abstract float getHeight(javax.swing.text.GlyphView var0);
		public int getNextVisualPositionFrom(javax.swing.text.GlyphView var0, int var1, javax.swing.text.Position.Bias var2, java.awt.Shape var3, int var4, javax.swing.text.Position.Bias[] var5) throws javax.swing.text.BadLocationException { return 0; }
		public javax.swing.text.GlyphView.GlyphPainter getPainter(javax.swing.text.GlyphView var0, int var1, int var2) { return null; }
		public abstract float getSpan(javax.swing.text.GlyphView var0, int var1, int var2, javax.swing.text.TabExpander var3, float var4);
		public abstract java.awt.Shape modelToView(javax.swing.text.GlyphView var0, int var1, javax.swing.text.Position.Bias var2, java.awt.Shape var3) throws javax.swing.text.BadLocationException;
		public abstract void paint(javax.swing.text.GlyphView var0, java.awt.Graphics var1, java.awt.Shape var2, int var3, int var4);
		public abstract int viewToModel(javax.swing.text.GlyphView var0, float var1, float var2, java.awt.Shape var3, javax.swing.text.Position.Bias[] var4);
	}
}

