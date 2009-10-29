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
public abstract class RectangularShape implements java.awt.Shape, java.lang.Cloneable {
	protected RectangularShape() { }
	public java.lang.Object clone() { return null; }
	public boolean contains(java.awt.geom.Point2D var0) { return false; }
	public boolean contains(java.awt.geom.Rectangle2D var0) { return false; }
	public java.awt.Rectangle getBounds() { return null; }
	public double getCenterX() { return 0.0d; }
	public double getCenterY() { return 0.0d; }
	public java.awt.geom.Rectangle2D getFrame() { return null; }
	public abstract double getHeight();
	public double getMaxX() { return 0.0d; }
	public double getMaxY() { return 0.0d; }
	public double getMinX() { return 0.0d; }
	public double getMinY() { return 0.0d; }
	public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0, double var1) { return null; }
	public abstract double getWidth();
	public abstract double getX();
	public abstract double getY();
	public boolean intersects(java.awt.geom.Rectangle2D var0) { return false; }
	public abstract boolean isEmpty();
	public abstract void setFrame(double var0, double var1, double var2, double var3);
	public void setFrame(java.awt.geom.Point2D var0, java.awt.geom.Dimension2D var1) { }
	public void setFrame(java.awt.geom.Rectangle2D var0) { }
	public void setFrameFromCenter(double var0, double var1, double var2, double var3) { }
	public void setFrameFromCenter(java.awt.geom.Point2D var0, java.awt.geom.Point2D var1) { }
	public void setFrameFromDiagonal(double var0, double var1, double var2, double var3) { }
	public void setFrameFromDiagonal(java.awt.geom.Point2D var0, java.awt.geom.Point2D var1) { }
}

