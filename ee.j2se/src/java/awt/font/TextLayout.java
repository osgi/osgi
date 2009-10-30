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
public final class TextLayout implements java.lang.Cloneable {
	public static class CaretPolicy {
		public CaretPolicy() { } 
		public java.awt.font.TextHitInfo getStrongCaret(java.awt.font.TextHitInfo var0, java.awt.font.TextHitInfo var1, java.awt.font.TextLayout var2) { return null; }
	}
	public final static java.awt.font.TextLayout.CaretPolicy DEFAULT_CARET_POLICY; static { DEFAULT_CARET_POLICY = null; }
	public TextLayout(java.lang.String var0, java.awt.Font var1, java.awt.font.FontRenderContext var2) { } 
	public TextLayout(java.lang.String var0, java.util.Map<? extends java.text.AttributedCharacterIterator.Attribute,?> var1, java.awt.font.FontRenderContext var2) { } 
	public TextLayout(java.text.AttributedCharacterIterator var0, java.awt.font.FontRenderContext var1) { } 
	protected java.lang.Object clone() { return null; }
	public void draw(java.awt.Graphics2D var0, float var1, float var2) { }
	public boolean equals(java.awt.font.TextLayout var0) { return false; }
	public float getAdvance() { return 0.0f; }
	public float getAscent() { return 0.0f; }
	public byte getBaseline() { return 0; }
	public float[] getBaselineOffsets() { return null; }
	public java.awt.Shape getBlackBoxBounds(int var0, int var1) { return null; }
	public java.awt.geom.Rectangle2D getBounds() { return null; }
	public float[] getCaretInfo(java.awt.font.TextHitInfo var0) { return null; }
	public float[] getCaretInfo(java.awt.font.TextHitInfo var0, java.awt.geom.Rectangle2D var1) { return null; }
	public java.awt.Shape getCaretShape(java.awt.font.TextHitInfo var0) { return null; }
	public java.awt.Shape getCaretShape(java.awt.font.TextHitInfo var0, java.awt.geom.Rectangle2D var1) { return null; }
	public java.awt.Shape[] getCaretShapes(int var0) { return null; }
	public java.awt.Shape[] getCaretShapes(int var0, java.awt.geom.Rectangle2D var1) { return null; }
	public java.awt.Shape[] getCaretShapes(int var0, java.awt.geom.Rectangle2D var1, java.awt.font.TextLayout.CaretPolicy var2) { return null; }
	public int getCharacterCount() { return 0; }
	public byte getCharacterLevel(int var0) { return 0; }
	public float getDescent() { return 0.0f; }
	public java.awt.font.TextLayout getJustifiedLayout(float var0) { return null; }
	public float getLeading() { return 0.0f; }
	public java.awt.Shape getLogicalHighlightShape(int var0, int var1) { return null; }
	public java.awt.Shape getLogicalHighlightShape(int var0, int var1, java.awt.geom.Rectangle2D var2) { return null; }
	public int[] getLogicalRangesForVisualSelection(java.awt.font.TextHitInfo var0, java.awt.font.TextHitInfo var1) { return null; }
	public java.awt.font.TextHitInfo getNextLeftHit(int var0) { return null; }
	public java.awt.font.TextHitInfo getNextLeftHit(int var0, java.awt.font.TextLayout.CaretPolicy var1) { return null; }
	public java.awt.font.TextHitInfo getNextLeftHit(java.awt.font.TextHitInfo var0) { return null; }
	public java.awt.font.TextHitInfo getNextRightHit(int var0) { return null; }
	public java.awt.font.TextHitInfo getNextRightHit(int var0, java.awt.font.TextLayout.CaretPolicy var1) { return null; }
	public java.awt.font.TextHitInfo getNextRightHit(java.awt.font.TextHitInfo var0) { return null; }
	public java.awt.Shape getOutline(java.awt.geom.AffineTransform var0) { return null; }
	public float getVisibleAdvance() { return 0.0f; }
	public java.awt.Shape getVisualHighlightShape(java.awt.font.TextHitInfo var0, java.awt.font.TextHitInfo var1) { return null; }
	public java.awt.Shape getVisualHighlightShape(java.awt.font.TextHitInfo var0, java.awt.font.TextHitInfo var1, java.awt.geom.Rectangle2D var2) { return null; }
	public java.awt.font.TextHitInfo getVisualOtherHit(java.awt.font.TextHitInfo var0) { return null; }
	protected void handleJustify(float var0) { }
	public int hashCode() { return 0; }
	public java.awt.font.TextHitInfo hitTestChar(float var0, float var1) { return null; }
	public java.awt.font.TextHitInfo hitTestChar(float var0, float var1, java.awt.geom.Rectangle2D var2) { return null; }
	public boolean isLeftToRight() { return false; }
	public boolean isVertical() { return false; }
}

