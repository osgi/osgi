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
public abstract class Line2D implements java.awt.Shape, java.lang.Cloneable {
	public static class Double extends java.awt.geom.Line2D {
		public double x1;
		public double x2;
		public double y1;
		public double y2;
		public Double() { } 
		public Double(double var0, double var1, double var2, double var3) { } 
		public Double(java.awt.geom.Point2D var0, java.awt.geom.Point2D var1) { } 
		public java.awt.geom.Rectangle2D getBounds2D() { return null; }
		public java.awt.geom.Point2D getP1() { return null; }
		public java.awt.geom.Point2D getP2() { return null; }
		public double getX1() { return 0.0d; }
		public double getX2() { return 0.0d; }
		public double getY1() { return 0.0d; }
		public double getY2() { return 0.0d; }
		public void setLine(double var0, double var1, double var2, double var3) { }
	}
	public static class Float extends java.awt.geom.Line2D {
		public float x1;
		public float x2;
		public float y1;
		public float y2;
		public Float() { } 
		public Float(float var0, float var1, float var2, float var3) { } 
		public Float(java.awt.geom.Point2D var0, java.awt.geom.Point2D var1) { } 
		public java.awt.geom.Rectangle2D getBounds2D() { return null; }
		public java.awt.geom.Point2D getP1() { return null; }
		public java.awt.geom.Point2D getP2() { return null; }
		public double getX1() { return 0.0d; }
		public double getX2() { return 0.0d; }
		public double getY1() { return 0.0d; }
		public double getY2() { return 0.0d; }
		public void setLine(double var0, double var1, double var2, double var3) { }
		public void setLine(float var0, float var1, float var2, float var3) { }
	}
	protected Line2D() { } 
	public java.lang.Object clone() { return null; }
	public boolean contains(double var0, double var1) { return false; }
	public boolean contains(double var0, double var1, double var2, double var3) { return false; }
	public boolean contains(java.awt.geom.Point2D var0) { return false; }
	public boolean contains(java.awt.geom.Rectangle2D var0) { return false; }
	public java.awt.Rectangle getBounds() { return null; }
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
	public boolean intersectsLine(double var0, double var1, double var2, double var3) { return false; }
	public boolean intersectsLine(java.awt.geom.Line2D var0) { return false; }
	public static boolean linesIntersect(double var0, double var1, double var2, double var3, double var4, double var5, double var6, double var7) { return false; }
	public double ptLineDist(double var0, double var1) { return 0.0d; }
	public static double ptLineDist(double var0, double var1, double var2, double var3, double var4, double var5) { return 0.0d; }
	public double ptLineDist(java.awt.geom.Point2D var0) { return 0.0d; }
	public double ptLineDistSq(double var0, double var1) { return 0.0d; }
	public static double ptLineDistSq(double var0, double var1, double var2, double var3, double var4, double var5) { return 0.0d; }
	public double ptLineDistSq(java.awt.geom.Point2D var0) { return 0.0d; }
	public double ptSegDist(double var0, double var1) { return 0.0d; }
	public static double ptSegDist(double var0, double var1, double var2, double var3, double var4, double var5) { return 0.0d; }
	public double ptSegDist(java.awt.geom.Point2D var0) { return 0.0d; }
	public double ptSegDistSq(double var0, double var1) { return 0.0d; }
	public static double ptSegDistSq(double var0, double var1, double var2, double var3, double var4, double var5) { return 0.0d; }
	public double ptSegDistSq(java.awt.geom.Point2D var0) { return 0.0d; }
	public int relativeCCW(double var0, double var1) { return 0; }
	public static int relativeCCW(double var0, double var1, double var2, double var3, double var4, double var5) { return 0; }
	public int relativeCCW(java.awt.geom.Point2D var0) { return 0; }
	public abstract void setLine(double var0, double var1, double var2, double var3);
	public void setLine(java.awt.geom.Line2D var0) { }
	public void setLine(java.awt.geom.Point2D var0, java.awt.geom.Point2D var1) { }
}

