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
public abstract class Point2D implements java.lang.Cloneable {
	public static class Double extends java.awt.geom.Point2D {
		public double x;
		public double y;
		public Double() { } 
		public Double(double var0, double var1) { } 
		public double getX() { return 0.0d; }
		public double getY() { return 0.0d; }
		public void setLocation(double var0, double var1) { }
	}
	public static class Float extends java.awt.geom.Point2D {
		public float x;
		public float y;
		public Float() { } 
		public Float(float var0, float var1) { } 
		public double getX() { return 0.0d; }
		public double getY() { return 0.0d; }
		public void setLocation(double var0, double var1) { }
		public void setLocation(float var0, float var1) { }
	}
	protected Point2D() { } 
	public java.lang.Object clone() { return null; }
	public double distance(double var0, double var1) { return 0.0d; }
	public static double distance(double var0, double var1, double var2, double var3) { return 0.0d; }
	public double distance(java.awt.geom.Point2D var0) { return 0.0d; }
	public double distanceSq(double var0, double var1) { return 0.0d; }
	public static double distanceSq(double var0, double var1, double var2, double var3) { return 0.0d; }
	public double distanceSq(java.awt.geom.Point2D var0) { return 0.0d; }
	public abstract double getX();
	public abstract double getY();
	public abstract void setLocation(double var0, double var1);
	public void setLocation(java.awt.geom.Point2D var0) { }
}

