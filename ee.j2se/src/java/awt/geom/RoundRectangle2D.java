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
public abstract class RoundRectangle2D extends java.awt.geom.RectangularShape {
	public static class Double extends java.awt.geom.RoundRectangle2D implements java.io.Serializable {
		public double archeight;
		public double arcwidth;
		public double height;
		public double width;
		public double x;
		public double y;
		public Double() { } 
		public Double(double var0, double var1, double var2, double var3, double var4, double var5) { } 
		public double getArcHeight() { return 0.0d; }
		public double getArcWidth() { return 0.0d; }
		public java.awt.geom.Rectangle2D getBounds2D() { return null; }
		public double getHeight() { return 0.0d; }
		public double getWidth() { return 0.0d; }
		public double getX() { return 0.0d; }
		public double getY() { return 0.0d; }
		public boolean isEmpty() { return false; }
		public void setRoundRect(double var0, double var1, double var2, double var3, double var4, double var5) { }
	}
	public static class Float extends java.awt.geom.RoundRectangle2D implements java.io.Serializable {
		public float archeight;
		public float arcwidth;
		public float height;
		public float width;
		public float x;
		public float y;
		public Float() { } 
		public Float(float var0, float var1, float var2, float var3, float var4, float var5) { } 
		public double getArcHeight() { return 0.0d; }
		public double getArcWidth() { return 0.0d; }
		public java.awt.geom.Rectangle2D getBounds2D() { return null; }
		public double getHeight() { return 0.0d; }
		public double getWidth() { return 0.0d; }
		public double getX() { return 0.0d; }
		public double getY() { return 0.0d; }
		public boolean isEmpty() { return false; }
		public void setRoundRect(double var0, double var1, double var2, double var3, double var4, double var5) { }
		public void setRoundRect(float var0, float var1, float var2, float var3, float var4, float var5) { }
	}
	protected RoundRectangle2D() { } 
	public boolean contains(double var0, double var1) { return false; }
	public boolean contains(double var0, double var1, double var2, double var3) { return false; }
	public abstract double getArcHeight();
	public abstract double getArcWidth();
	public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0) { return null; }
	public boolean intersects(double var0, double var1, double var2, double var3) { return false; }
	public void setFrame(double var0, double var1, double var2, double var3) { }
	public abstract void setRoundRect(double var0, double var1, double var2, double var3, double var4, double var5);
	public void setRoundRect(java.awt.geom.RoundRectangle2D var0) { }
}

