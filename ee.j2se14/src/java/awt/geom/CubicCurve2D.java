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

package java.awt.geom;
public abstract class CubicCurve2D implements java.awt.Shape, java.lang.Cloneable {
	protected CubicCurve2D() { }
	public java.lang.Object clone() { return null; }
	public boolean contains(double var0, double var1) { return false; }
	public boolean contains(double var0, double var1, double var2, double var3) { return false; }
	public boolean contains(java.awt.geom.Point2D var0) { return false; }
	public boolean contains(java.awt.geom.Rectangle2D var0) { return false; }
	public java.awt.Rectangle getBounds() { return null; }
	public abstract java.awt.geom.Point2D getCtrlP1();
	public abstract java.awt.geom.Point2D getCtrlP2();
	public abstract double getCtrlX1();
	public abstract double getCtrlX2();
	public abstract double getCtrlY1();
	public abstract double getCtrlY2();
	public double getFlatness() { return 0.0d; }
	public static double getFlatness(double var0, double var1, double var2, double var3, double var4, double var5, double var6, double var7) { return 0.0d; }
	public static double getFlatness(double[] var0, int var1) { return 0.0d; }
	public double getFlatnessSq() { return 0.0d; }
	public static double getFlatnessSq(double var0, double var1, double var2, double var3, double var4, double var5, double var6, double var7) { return 0.0d; }
	public static double getFlatnessSq(double[] var0, int var1) { return 0.0d; }
	public abstract java.awt.geom.Point2D getP1();
	public abstract java.awt.geom.Point2D getP2();
	public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0) { return null; }
	public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0, double var1) { return null; }
	public abstract double getX1();
	public abstract double getX2();
	public abstract double getY1();
	public abstract double getY2();
	public boolean intersects(double var0, double var1, double var2, double var3) { return false; }
	public boolean intersects(java.awt.geom.Rectangle2D var0) { return false; }
	public abstract void setCurve(double var0, double var1, double var2, double var3, double var4, double var5, double var6, double var7);
	public void setCurve(java.awt.geom.CubicCurve2D var0) { }
	public void setCurve(java.awt.geom.Point2D var0, java.awt.geom.Point2D var1, java.awt.geom.Point2D var2, java.awt.geom.Point2D var3) { }
	public void setCurve(double[] var0, int var1) { }
	public void setCurve(java.awt.geom.Point2D[] var0, int var1) { }
	public static int solveCubic(double[] var0) { return 0; }
	public static int solveCubic(double[] var0, double[] var1) { return 0; }
	public void subdivide(java.awt.geom.CubicCurve2D var0, java.awt.geom.CubicCurve2D var1) { }
	public static void subdivide(java.awt.geom.CubicCurve2D var0, java.awt.geom.CubicCurve2D var1, java.awt.geom.CubicCurve2D var2) { }
	public static void subdivide(double[] var0, int var1, double[] var2, int var3, double[] var4, int var5) { }
	public static class Double extends java.awt.geom.CubicCurve2D {
		public Double() { }
		public Double(double var0, double var1, double var2, double var3, double var4, double var5, double var6, double var7) { }
		public java.awt.geom.Rectangle2D getBounds2D() { return null; }
		public java.awt.geom.Point2D getCtrlP1() { return null; }
		public java.awt.geom.Point2D getCtrlP2() { return null; }
		public double getCtrlX1() { return 0.0d; }
		public double getCtrlX2() { return 0.0d; }
		public double getCtrlY1() { return 0.0d; }
		public double getCtrlY2() { return 0.0d; }
		public java.awt.geom.Point2D getP1() { return null; }
		public java.awt.geom.Point2D getP2() { return null; }
		public double getX1() { return 0.0d; }
		public double getX2() { return 0.0d; }
		public double getY1() { return 0.0d; }
		public double getY2() { return 0.0d; }
		public void setCurve(double var0, double var1, double var2, double var3, double var4, double var5, double var6, double var7) { }
		public double ctrlx1;
		public double ctrlx2;
		public double ctrly1;
		public double ctrly2;
		public double x1;
		public double x2;
		public double y1;
		public double y2;
	}
	public static class Float extends java.awt.geom.CubicCurve2D {
		public Float() { }
		public Float(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) { }
		public java.awt.geom.Rectangle2D getBounds2D() { return null; }
		public java.awt.geom.Point2D getCtrlP1() { return null; }
		public java.awt.geom.Point2D getCtrlP2() { return null; }
		public double getCtrlX1() { return 0.0d; }
		public double getCtrlX2() { return 0.0d; }
		public double getCtrlY1() { return 0.0d; }
		public double getCtrlY2() { return 0.0d; }
		public java.awt.geom.Point2D getP1() { return null; }
		public java.awt.geom.Point2D getP2() { return null; }
		public double getX1() { return 0.0d; }
		public double getX2() { return 0.0d; }
		public double getY1() { return 0.0d; }
		public double getY2() { return 0.0d; }
		public void setCurve(double var0, double var1, double var2, double var3, double var4, double var5, double var6, double var7) { }
		public void setCurve(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) { }
		public float ctrlx1;
		public float ctrlx2;
		public float ctrly1;
		public float ctrly2;
		public float x1;
		public float x2;
		public float y1;
		public float y2;
	}
}

