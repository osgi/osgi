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
public abstract class Graphics2D extends java.awt.Graphics {
	protected Graphics2D() { }
	public abstract void addRenderingHints(java.util.Map var0);
	public abstract void clip(java.awt.Shape var0);
	public abstract void draw(java.awt.Shape var0);
	public abstract void drawGlyphVector(java.awt.font.GlyphVector var0, float var1, float var2);
	public abstract boolean drawImage(java.awt.Image var0, java.awt.geom.AffineTransform var1, java.awt.image.ImageObserver var2);
	public abstract void drawImage(java.awt.image.BufferedImage var0, java.awt.image.BufferedImageOp var1, int var2, int var3);
	public abstract void drawRenderableImage(java.awt.image.renderable.RenderableImage var0, java.awt.geom.AffineTransform var1);
	public abstract void drawRenderedImage(java.awt.image.RenderedImage var0, java.awt.geom.AffineTransform var1);
	public abstract void drawString(java.lang.String var0, float var1, float var2);
	public abstract void drawString(java.text.AttributedCharacterIterator var0, float var1, float var2);
	public abstract void fill(java.awt.Shape var0);
	public abstract java.awt.Color getBackground();
	public abstract java.awt.Composite getComposite();
	public abstract java.awt.GraphicsConfiguration getDeviceConfiguration();
	public abstract java.awt.font.FontRenderContext getFontRenderContext();
	public abstract java.awt.Paint getPaint();
	public abstract java.lang.Object getRenderingHint(java.awt.RenderingHints.Key var0);
	public abstract java.awt.RenderingHints getRenderingHints();
	public abstract java.awt.Stroke getStroke();
	public abstract java.awt.geom.AffineTransform getTransform();
	public abstract boolean hit(java.awt.Rectangle var0, java.awt.Shape var1, boolean var2);
	public abstract void rotate(double var0);
	public abstract void rotate(double var0, double var1, double var2);
	public abstract void scale(double var0, double var1);
	public abstract void setBackground(java.awt.Color var0);
	public abstract void setComposite(java.awt.Composite var0);
	public abstract void setPaint(java.awt.Paint var0);
	public abstract void setRenderingHint(java.awt.RenderingHints.Key var0, java.lang.Object var1);
	public abstract void setRenderingHints(java.util.Map var0);
	public abstract void setStroke(java.awt.Stroke var0);
	public abstract void setTransform(java.awt.geom.AffineTransform var0);
	public abstract void shear(double var0, double var1);
	public abstract void transform(java.awt.geom.AffineTransform var0);
	public abstract void translate(double var0, double var1);
}

