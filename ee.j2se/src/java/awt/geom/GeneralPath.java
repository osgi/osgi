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

package java.awt.geom;
public final class GeneralPath implements java.awt.Shape, java.lang.Cloneable {
	public GeneralPath() { }
	public GeneralPath(int var0) { }
	public GeneralPath(int var0, int var1) { }
	public GeneralPath(java.awt.Shape var0) { }
	public void append(java.awt.Shape var0, boolean var1) { }
	public void append(java.awt.geom.PathIterator var0, boolean var1) { }
	public java.lang.Object clone() { return null; }
	public void closePath() { }
	public boolean contains(double var0, double var1) { return false; }
	public boolean contains(double var0, double var1, double var2, double var3) { return false; }
	public boolean contains(java.awt.geom.Point2D var0) { return false; }
	public boolean contains(java.awt.geom.Rectangle2D var0) { return false; }
	public java.awt.Shape createTransformedShape(java.awt.geom.AffineTransform var0) { return null; }
	public void curveTo(float var0, float var1, float var2, float var3, float var4, float var5) { }
	public java.awt.Rectangle getBounds() { return null; }
	public java.awt.geom.Rectangle2D getBounds2D() { return null; }
	public java.awt.geom.Point2D getCurrentPoint() { return null; }
	public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0) { return null; }
	public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0, double var1) { return null; }
	public int getWindingRule() { return 0; }
	public boolean intersects(double var0, double var1, double var2, double var3) { return false; }
	public boolean intersects(java.awt.geom.Rectangle2D var0) { return false; }
	public void lineTo(float var0, float var1) { }
	public void moveTo(float var0, float var1) { }
	public void quadTo(float var0, float var1, float var2, float var3) { }
	public void reset() { }
	public void setWindingRule(int var0) { }
	public void transform(java.awt.geom.AffineTransform var0) { }
	public final static int WIND_EVEN_ODD = 0;
	public final static int WIND_NON_ZERO = 1;
}

