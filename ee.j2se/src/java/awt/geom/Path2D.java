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

package java.awt.geom;
public abstract class Path2D implements java.awt.Shape, java.lang.Cloneable {
	public static class Double extends java.awt.geom.Path2D implements java.io.Serializable {
		public Double() { } 
		public Double(int var0) { } 
		public Double(int var0, int var1) { } 
		public Double(java.awt.Shape var0) { } 
		public Double(java.awt.Shape var0, java.awt.geom.AffineTransform var1) { } 
		public final void append(java.awt.geom.PathIterator var0, boolean var1) { }
		public final java.lang.Object clone() { return null; }
		public final void curveTo(double var0, double var1, double var2, double var3, double var4, double var5) { }
		public final java.awt.geom.Rectangle2D getBounds2D() { return null; }
		public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0) { return null; }
		public final void lineTo(double var0, double var1) { }
		public final void moveTo(double var0, double var1) { }
		public final void quadTo(double var0, double var1, double var2, double var3) { }
		public final void transform(java.awt.geom.AffineTransform var0) { }
	}
	public static class Float extends java.awt.geom.Path2D implements java.io.Serializable {
		public Float() { } 
		public Float(int var0) { } 
		public Float(int var0, int var1) { } 
		public Float(java.awt.Shape var0) { } 
		public Float(java.awt.Shape var0, java.awt.geom.AffineTransform var1) { } 
		public final void append(java.awt.geom.PathIterator var0, boolean var1) { }
		public final java.lang.Object clone() { return null; }
		public final void curveTo(double var0, double var1, double var2, double var3, double var4, double var5) { }
		public final void curveTo(float var0, float var1, float var2, float var3, float var4, float var5) { }
		public final java.awt.geom.Rectangle2D getBounds2D() { return null; }
		public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0) { return null; }
		public final void lineTo(double var0, double var1) { }
		public final void lineTo(float var0, float var1) { }
		public final void moveTo(double var0, double var1) { }
		public final void moveTo(float var0, float var1) { }
		public final void quadTo(double var0, double var1, double var2, double var3) { }
		public final void quadTo(float var0, float var1, float var2, float var3) { }
		public final void transform(java.awt.geom.AffineTransform var0) { }
	}
	public final static int WIND_EVEN_ODD = 0;
	public final static int WIND_NON_ZERO = 1;
	public final void append(java.awt.Shape var0, boolean var1) { }
	public abstract void append(java.awt.geom.PathIterator var0, boolean var1);
	public abstract java.lang.Object clone();
	public final void closePath() { }
	public final boolean contains(double var0, double var1) { return false; }
	public final boolean contains(double var0, double var1, double var2, double var3) { return false; }
	public static boolean contains(java.awt.geom.PathIterator var0, double var1, double var2) { return false; }
	public static boolean contains(java.awt.geom.PathIterator var0, double var1, double var2, double var3, double var4) { return false; }
	public static boolean contains(java.awt.geom.PathIterator var0, java.awt.geom.Point2D var1) { return false; }
	public static boolean contains(java.awt.geom.PathIterator var0, java.awt.geom.Rectangle2D var1) { return false; }
	public final boolean contains(java.awt.geom.Point2D var0) { return false; }
	public final boolean contains(java.awt.geom.Rectangle2D var0) { return false; }
	public final java.awt.Shape createTransformedShape(java.awt.geom.AffineTransform var0) { return null; }
	public abstract void curveTo(double var0, double var1, double var2, double var3, double var4, double var5);
	public final java.awt.Rectangle getBounds() { return null; }
	public final java.awt.geom.Point2D getCurrentPoint() { return null; }
	public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0, double var1) { return null; }
	public final int getWindingRule() { return 0; }
	public final boolean intersects(double var0, double var1, double var2, double var3) { return false; }
	public static boolean intersects(java.awt.geom.PathIterator var0, double var1, double var2, double var3, double var4) { return false; }
	public static boolean intersects(java.awt.geom.PathIterator var0, java.awt.geom.Rectangle2D var1) { return false; }
	public final boolean intersects(java.awt.geom.Rectangle2D var0) { return false; }
	public abstract void lineTo(double var0, double var1);
	public abstract void moveTo(double var0, double var1);
	public abstract void quadTo(double var0, double var1, double var2, double var3);
	public final void reset() { }
	public final void setWindingRule(int var0) { }
	public abstract void transform(java.awt.geom.AffineTransform var0);
	Path2D() { } /* generated constructor to prevent compiler adding default public constructor */
}

