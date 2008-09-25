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

package java.awt.geom;
public abstract class Arc2D extends java.awt.geom.RectangularShape {
	protected Arc2D(int var0) { }
	public boolean contains(double var0, double var1) { return false; }
	public boolean contains(double var0, double var1, double var2, double var3) { return false; }
	public boolean containsAngle(double var0) { return false; }
	public abstract double getAngleExtent();
	public abstract double getAngleStart();
	public int getArcType() { return 0; }
	public java.awt.geom.Rectangle2D getBounds2D() { return null; }
	public java.awt.geom.Point2D getEndPoint() { return null; }
	public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0) { return null; }
	public java.awt.geom.Point2D getStartPoint() { return null; }
	public boolean intersects(double var0, double var1, double var2, double var3) { return false; }
	protected abstract java.awt.geom.Rectangle2D makeBounds(double var0, double var1, double var2, double var3);
	public abstract void setAngleExtent(double var0);
	public abstract void setAngleStart(double var0);
	public void setAngleStart(java.awt.geom.Point2D var0) { }
	public void setAngles(double var0, double var1, double var2, double var3) { }
	public void setAngles(java.awt.geom.Point2D var0, java.awt.geom.Point2D var1) { }
	public abstract void setArc(double var0, double var1, double var2, double var3, double var4, double var5, int var6);
	public void setArc(java.awt.geom.Arc2D var0) { }
	public void setArc(java.awt.geom.Point2D var0, java.awt.geom.Dimension2D var1, double var2, double var3, int var4) { }
	public void setArc(java.awt.geom.Rectangle2D var0, double var1, double var2, int var3) { }
	public void setArcByCenter(double var0, double var1, double var2, double var3, double var4, int var5) { }
	public void setArcByTangent(java.awt.geom.Point2D var0, java.awt.geom.Point2D var1, java.awt.geom.Point2D var2, double var3) { }
	public void setArcType(int var0) { }
	public void setFrame(double var0, double var1, double var2, double var3) { }
	public final static int CHORD = 1;
	public final static int OPEN = 0;
	public final static int PIE = 2;
	public static class Double extends java.awt.geom.Arc2D {
		public Double() { super(0); }
		public Double(double var0, double var1, double var2, double var3, double var4, double var5, int var6) { super(0); }
		public Double(int var0) { super(0); }
		public Double(java.awt.geom.Rectangle2D var0, double var1, double var2, int var3) { super(0); }
		public double getAngleExtent() { return 0.0d; }
		public double getAngleStart() { return 0.0d; }
		public double getHeight() { return 0.0d; }
		public double getWidth() { return 0.0d; }
		public double getX() { return 0.0d; }
		public double getY() { return 0.0d; }
		public boolean isEmpty() { return false; }
		protected java.awt.geom.Rectangle2D makeBounds(double var0, double var1, double var2, double var3) { return null; }
		public void setAngleExtent(double var0) { }
		public void setAngleStart(double var0) { }
		public void setArc(double var0, double var1, double var2, double var3, double var4, double var5, int var6) { }
		public double extent;
		public double height;
		public double start;
		public double width;
		public double x;
		public double y;
	}
	public static class Float extends java.awt.geom.Arc2D {
		public Float() { super(0); }
		public Float(float var0, float var1, float var2, float var3, float var4, float var5, int var6) { super(0); }
		public Float(int var0) { super(0); }
		public Float(java.awt.geom.Rectangle2D var0, float var1, float var2, int var3) { super(0); }
		public double getAngleExtent() { return 0.0d; }
		public double getAngleStart() { return 0.0d; }
		public double getHeight() { return 0.0d; }
		public double getWidth() { return 0.0d; }
		public double getX() { return 0.0d; }
		public double getY() { return 0.0d; }
		public boolean isEmpty() { return false; }
		protected java.awt.geom.Rectangle2D makeBounds(double var0, double var1, double var2, double var3) { return null; }
		public void setAngleExtent(double var0) { }
		public void setAngleStart(double var0) { }
		public void setArc(double var0, double var1, double var2, double var3, double var4, double var5, int var6) { }
		public float extent;
		public float height;
		public float start;
		public float width;
		public float x;
		public float y;
	}
}

