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

package java.awt;
public class Rectangle extends java.awt.geom.Rectangle2D implements java.awt.Shape, java.io.Serializable {
	public Rectangle() { }
	public Rectangle(int var0, int var1) { }
	public Rectangle(int var0, int var1, int var2, int var3) { }
	public Rectangle(java.awt.Dimension var0) { }
	public Rectangle(java.awt.Point var0) { }
	public Rectangle(java.awt.Point var0, java.awt.Dimension var1) { }
	public Rectangle(java.awt.Rectangle var0) { }
	public void add(int var0, int var1) { }
	public void add(java.awt.Point var0) { }
	public void add(java.awt.Rectangle var0) { }
	public boolean contains(int var0, int var1) { return false; }
	public boolean contains(int var0, int var1, int var2, int var3) { return false; }
	public boolean contains(java.awt.Point var0) { return false; }
	public boolean contains(java.awt.Rectangle var0) { return false; }
	public java.awt.geom.Rectangle2D createIntersection(java.awt.geom.Rectangle2D var0) { return null; }
	public java.awt.geom.Rectangle2D createUnion(java.awt.geom.Rectangle2D var0) { return null; }
	public double getHeight() { return 0.0d; }
	public java.awt.Point getLocation() { return null; }
	public java.awt.Dimension getSize() { return null; }
	public double getWidth() { return 0.0d; }
	public double getX() { return 0.0d; }
	public double getY() { return 0.0d; }
	public void grow(int var0, int var1) { }
	/** @deprecated */ public boolean inside(int var0, int var1) { return false; }
	public java.awt.Rectangle intersection(java.awt.Rectangle var0) { return null; }
	public boolean intersects(java.awt.Rectangle var0) { return false; }
	public boolean isEmpty() { return false; }
	/** @deprecated */ public void move(int var0, int var1) { }
	public int outcode(double var0, double var1) { return 0; }
	/** @deprecated */ public void reshape(int var0, int var1, int var2, int var3) { }
	/** @deprecated */ public void resize(int var0, int var1) { }
	public void setBounds(int var0, int var1, int var2, int var3) { }
	public void setBounds(java.awt.Rectangle var0) { }
	public void setLocation(int var0, int var1) { }
	public void setLocation(java.awt.Point var0) { }
	public void setRect(double var0, double var1, double var2, double var3) { }
	public void setSize(int var0, int var1) { }
	public void setSize(java.awt.Dimension var0) { }
	public void translate(int var0, int var1) { }
	public java.awt.Rectangle union(java.awt.Rectangle var0) { return null; }
	public int height;
	public int width;
	public int x;
	public int y;
}

