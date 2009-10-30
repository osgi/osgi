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

package javax.swing;
public class SpringLayout implements java.awt.LayoutManager2 {
	public static class Constraints {
		public Constraints() { } 
		public Constraints(java.awt.Component var0) { } 
		public Constraints(javax.swing.Spring var0, javax.swing.Spring var1) { } 
		public Constraints(javax.swing.Spring var0, javax.swing.Spring var1, javax.swing.Spring var2, javax.swing.Spring var3) { } 
		public javax.swing.Spring getConstraint(java.lang.String var0) { return null; }
		public javax.swing.Spring getHeight() { return null; }
		public javax.swing.Spring getWidth() { return null; }
		public javax.swing.Spring getX() { return null; }
		public javax.swing.Spring getY() { return null; }
		public void setConstraint(java.lang.String var0, javax.swing.Spring var1) { }
		public void setHeight(javax.swing.Spring var0) { }
		public void setWidth(javax.swing.Spring var0) { }
		public void setX(javax.swing.Spring var0) { }
		public void setY(javax.swing.Spring var0) { }
	}
	public final static java.lang.String EAST = "East";
	public final static java.lang.String NORTH = "North";
	public final static java.lang.String SOUTH = "South";
	public final static java.lang.String WEST = "West";
	public SpringLayout() { } 
	public void addLayoutComponent(java.awt.Component var0, java.lang.Object var1) { }
	public void addLayoutComponent(java.lang.String var0, java.awt.Component var1) { }
	public javax.swing.Spring getConstraint(java.lang.String var0, java.awt.Component var1) { return null; }
	public javax.swing.SpringLayout.Constraints getConstraints(java.awt.Component var0) { return null; }
	public float getLayoutAlignmentX(java.awt.Container var0) { return 0.0f; }
	public float getLayoutAlignmentY(java.awt.Container var0) { return 0.0f; }
	public void invalidateLayout(java.awt.Container var0) { }
	public void layoutContainer(java.awt.Container var0) { }
	public java.awt.Dimension maximumLayoutSize(java.awt.Container var0) { return null; }
	public java.awt.Dimension minimumLayoutSize(java.awt.Container var0) { return null; }
	public java.awt.Dimension preferredLayoutSize(java.awt.Container var0) { return null; }
	public void putConstraint(java.lang.String var0, java.awt.Component var1, int var2, java.lang.String var3, java.awt.Component var4) { }
	public void putConstraint(java.lang.String var0, java.awt.Component var1, javax.swing.Spring var2, java.lang.String var3, java.awt.Component var4) { }
	public void removeLayoutComponent(java.awt.Component var0) { }
}

