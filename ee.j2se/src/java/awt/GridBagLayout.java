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
public class GridBagLayout implements java.awt.LayoutManager2, java.io.Serializable {
	public GridBagLayout() { }
	protected void AdjustForGravity(java.awt.GridBagConstraints var0, java.awt.Rectangle var1) { }
	protected void ArrangeGrid(java.awt.Container var0) { }
	protected java.awt.GridBagLayoutInfo GetLayoutInfo(java.awt.Container var0, int var1) { return null; }
	protected java.awt.Dimension GetMinSize(java.awt.Container var0, java.awt.GridBagLayoutInfo var1) { return null; }
	public void addLayoutComponent(java.awt.Component var0, java.lang.Object var1) { }
	public void addLayoutComponent(java.lang.String var0, java.awt.Component var1) { }
	protected void adjustForGravity(java.awt.GridBagConstraints var0, java.awt.Rectangle var1) { }
	protected void arrangeGrid(java.awt.Container var0) { }
	public java.awt.GridBagConstraints getConstraints(java.awt.Component var0) { return null; }
	public float getLayoutAlignmentX(java.awt.Container var0) { return 0.0f; }
	public float getLayoutAlignmentY(java.awt.Container var0) { return 0.0f; }
	public int[][] getLayoutDimensions() { return null; }
	protected java.awt.GridBagLayoutInfo getLayoutInfo(java.awt.Container var0, int var1) { return null; }
	public java.awt.Point getLayoutOrigin() { return null; }
	public double[][] getLayoutWeights() { return null; }
	protected java.awt.Dimension getMinSize(java.awt.Container var0, java.awt.GridBagLayoutInfo var1) { return null; }
	public void invalidateLayout(java.awt.Container var0) { }
	public void layoutContainer(java.awt.Container var0) { }
	public java.awt.Point location(int var0, int var1) { return null; }
	protected java.awt.GridBagConstraints lookupConstraints(java.awt.Component var0) { return null; }
	public java.awt.Dimension maximumLayoutSize(java.awt.Container var0) { return null; }
	public java.awt.Dimension minimumLayoutSize(java.awt.Container var0) { return null; }
	public java.awt.Dimension preferredLayoutSize(java.awt.Container var0) { return null; }
	public void removeLayoutComponent(java.awt.Component var0) { }
	public void setConstraints(java.awt.Component var0, java.awt.GridBagConstraints var1) { }
	protected final static int MAXGRIDSIZE = 512;
	protected final static int MINSIZE = 1;
	protected final static int PREFERREDSIZE = 2;
	public double[] columnWeights;
	public int[] columnWidths;
	protected java.util.Hashtable comptable;
	protected java.awt.GridBagConstraints defaultConstraints;
	protected java.awt.GridBagLayoutInfo layoutInfo;
	public int[] rowHeights;
	public double[] rowWeights;
}

