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
public abstract class Rectangle2D extends java.awt.geom.RectangularShape {
	protected Rectangle2D() { }
	public void add(double var0, double var1) { }
	public void add(java.awt.geom.Point2D var0) { }
	public void add(java.awt.geom.Rectangle2D var0) { }
	public boolean contains(double var0, double var1) { return false; }
	public boolean contains(double var0, double var1, double var2, double var3) { return false; }
	public abstract java.awt.geom.Rectangle2D createIntersection(java.awt.geom.Rectangle2D var0);
	public abstract java.awt.geom.Rectangle2D createUnion(java.awt.geom.Rectangle2D var0);
	public java.awt.geom.Rectangle2D getBounds2D() { return null; }
	public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0) { return null; }
	public int hashCode() { return 0; }
	public static void intersect(java.awt.geom.Rectangle2D var0, java.awt.geom.Rectangle2D var1, java.awt.geom.Rectangle2D var2) { }
	public boolean intersects(double var0, double var1, double var2, double var3) { return false; }
	public boolean intersectsLine(double var0, double var1, double var2, double var3) { return false; }
	public boolean intersectsLine(java.awt.geom.Line2D var0) { return false; }
	public abstract int outcode(double var0, double var1);
	public int outcode(java.awt.geom.Point2D var0) { return 0; }
	public void setFrame(double var0, double var1, double var2, double var3) { }
	public abstract void setRect(double var0, double var1, double var2, double var3);
	public void setRect(java.awt.geom.Rectangle2D var0) { }
	public static void union(java.awt.geom.Rectangle2D var0, java.awt.geom.Rectangle2D var1, java.awt.geom.Rectangle2D var2) { }
	public final static int OUT_BOTTOM = 8;
	public final static int OUT_LEFT = 1;
	public final static int OUT_RIGHT = 4;
	public final static int OUT_TOP = 2;
	public static class Double extends java.awt.geom.Rectangle2D {
		public Double() { }
		public Double(double var0, double var1, double var2, double var3) { }
		public java.awt.geom.Rectangle2D createIntersection(java.awt.geom.Rectangle2D var0) { return null; }
		public java.awt.geom.Rectangle2D createUnion(java.awt.geom.Rectangle2D var0) { return null; }
		public double getHeight() { return 0.0d; }
		public double getWidth() { return 0.0d; }
		public double getX() { return 0.0d; }
		public double getY() { return 0.0d; }
		public boolean isEmpty() { return false; }
		public int outcode(double var0, double var1) { return 0; }
		public void setRect(double var0, double var1, double var2, double var3) { }
		public double height;
		public double width;
		public double x;
		public double y;
	}
	public static class Float extends java.awt.geom.Rectangle2D {
		public Float() { }
		public Float(float var0, float var1, float var2, float var3) { }
		public java.awt.geom.Rectangle2D createIntersection(java.awt.geom.Rectangle2D var0) { return null; }
		public java.awt.geom.Rectangle2D createUnion(java.awt.geom.Rectangle2D var0) { return null; }
		public double getHeight() { return 0.0d; }
		public double getWidth() { return 0.0d; }
		public double getX() { return 0.0d; }
		public double getY() { return 0.0d; }
		public boolean isEmpty() { return false; }
		public int outcode(double var0, double var1) { return 0; }
		public void setRect(double var0, double var1, double var2, double var3) { }
		public void setRect(float var0, float var1, float var2, float var3) { }
		public float height;
		public float width;
		public float x;
		public float y;
	}
}

