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
public class Area implements java.awt.Shape, java.lang.Cloneable {
	public Area() { }
	public Area(java.awt.Shape var0) { }
	public void add(java.awt.geom.Area var0) { }
	public java.lang.Object clone() { return null; }
	public boolean contains(double var0, double var1) { return false; }
	public boolean contains(double var0, double var1, double var2, double var3) { return false; }
	public boolean contains(java.awt.geom.Point2D var0) { return false; }
	public boolean contains(java.awt.geom.Rectangle2D var0) { return false; }
	public java.awt.geom.Area createTransformedArea(java.awt.geom.AffineTransform var0) { return null; }
	public boolean equals(java.awt.geom.Area var0) { return false; }
	public void exclusiveOr(java.awt.geom.Area var0) { }
	public java.awt.Rectangle getBounds() { return null; }
	public java.awt.geom.Rectangle2D getBounds2D() { return null; }
	public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0) { return null; }
	public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0, double var1) { return null; }
	public void intersect(java.awt.geom.Area var0) { }
	public boolean intersects(double var0, double var1, double var2, double var3) { return false; }
	public boolean intersects(java.awt.geom.Rectangle2D var0) { return false; }
	public boolean isEmpty() { return false; }
	public boolean isPolygonal() { return false; }
	public boolean isRectangular() { return false; }
	public boolean isSingular() { return false; }
	public void reset() { }
	public void subtract(java.awt.geom.Area var0) { }
	public void transform(java.awt.geom.AffineTransform var0) { }
}

