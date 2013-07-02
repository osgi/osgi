/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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

package javax.swing.plaf.nimbus;
public abstract class AbstractRegionPainter implements javax.swing.Painter<javax.swing.JComponent> {
	protected static class PaintContext {
		protected enum CacheMode {
			FIXED_SIZES,
			NINE_SQUARE_SCALE,
			NO_CACHING;
		}
		public PaintContext(java.awt.Insets var0, java.awt.Dimension var1, boolean var2) { } 
		public PaintContext(java.awt.Insets var0, java.awt.Dimension var1, boolean var2, javax.swing.plaf.nimbus.AbstractRegionPainter.PaintContext.CacheMode var3, double var4, double var5) { } 
	}
	protected AbstractRegionPainter() { } 
	protected void configureGraphics(java.awt.Graphics2D var0) { }
	protected final float decodeAnchorX(float var0, float var1) { return 0.0f; }
	protected final float decodeAnchorY(float var0, float var1) { return 0.0f; }
	protected final java.awt.Color decodeColor(java.awt.Color var0, java.awt.Color var1, float var2) { return null; }
	protected final java.awt.Color decodeColor(java.lang.String var0, float var1, float var2, float var3, int var4) { return null; }
	protected final java.awt.LinearGradientPaint decodeGradient(float var0, float var1, float var2, float var3, float[] var4, java.awt.Color[] var5) { return null; }
	protected final java.awt.RadialGradientPaint decodeRadialGradient(float var0, float var1, float var2, float[] var3, java.awt.Color[] var4) { return null; }
	protected final float decodeX(float var0) { return 0.0f; }
	protected final float decodeY(float var0) { return 0.0f; }
	protected abstract void doPaint(java.awt.Graphics2D var0, javax.swing.JComponent var1, int var2, int var3, java.lang.Object[] var4);
	protected final java.awt.Color getComponentColor(javax.swing.JComponent var0, java.lang.String var1, java.awt.Color var2, float var3, float var4, int var5) { return null; }
	protected java.lang.Object[] getExtendedCacheKeys(javax.swing.JComponent var0) { return null; }
	protected abstract javax.swing.plaf.nimbus.AbstractRegionPainter.PaintContext getPaintContext();
	public final void paint(java.awt.Graphics2D var0, javax.swing.JComponent var1, int var2, int var3) { }
}

