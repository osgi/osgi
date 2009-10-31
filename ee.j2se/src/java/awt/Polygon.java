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

package java.awt;
public class Polygon implements java.awt.Shape, java.io.Serializable {
	protected java.awt.Rectangle bounds;
	public int npoints;
	public int[] xpoints;
	public int[] ypoints;
	public Polygon() { } 
	public Polygon(int[] var0, int[] var1, int var2) { } 
	public void addPoint(int var0, int var1) { }
	public boolean contains(double var0, double var1) { return false; }
	public boolean contains(double var0, double var1, double var2, double var3) { return false; }
	public boolean contains(int var0, int var1) { return false; }
	public boolean contains(java.awt.Point var0) { return false; }
	public boolean contains(java.awt.geom.Point2D var0) { return false; }
	public boolean contains(java.awt.geom.Rectangle2D var0) { return false; }
	/** @deprecated */
	@java.lang.Deprecated
	public java.awt.Rectangle getBoundingBox() { return null; }
	public java.awt.Rectangle getBounds() { return null; }
	public java.awt.geom.Rectangle2D getBounds2D() { return null; }
	public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0) { return null; }
	public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0, double var1) { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	public boolean inside(int var0, int var1) { return false; }
	public boolean intersects(double var0, double var1, double var2, double var3) { return false; }
	public boolean intersects(java.awt.geom.Rectangle2D var0) { return false; }
	public void invalidate() { }
	public void reset() { }
	public void translate(int var0, int var1) { }
}

