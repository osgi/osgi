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
public class BorderLayout implements java.awt.LayoutManager2, java.io.Serializable {
	public BorderLayout() { }
	public BorderLayout(int var0, int var1) { }
	public void addLayoutComponent(java.awt.Component var0, java.lang.Object var1) { }
	/** @deprecated */ public void addLayoutComponent(java.lang.String var0, java.awt.Component var1) { }
	public int getHgap() { return 0; }
	public float getLayoutAlignmentX(java.awt.Container var0) { return 0.0f; }
	public float getLayoutAlignmentY(java.awt.Container var0) { return 0.0f; }
	public int getVgap() { return 0; }
	public void invalidateLayout(java.awt.Container var0) { }
	public void layoutContainer(java.awt.Container var0) { }
	public java.awt.Dimension maximumLayoutSize(java.awt.Container var0) { return null; }
	public java.awt.Dimension minimumLayoutSize(java.awt.Container var0) { return null; }
	public java.awt.Dimension preferredLayoutSize(java.awt.Container var0) { return null; }
	public void removeLayoutComponent(java.awt.Component var0) { }
	public void setHgap(int var0) { }
	public void setVgap(int var0) { }
	public final static java.lang.String AFTER_LAST_LINE = "Last";
	public final static java.lang.String AFTER_LINE_ENDS = "After";
	public final static java.lang.String BEFORE_FIRST_LINE = "First";
	public final static java.lang.String BEFORE_LINE_BEGINS = "Before";
	public final static java.lang.String CENTER = "Center";
	public final static java.lang.String EAST = "East";
	public final static java.lang.String LINE_END = "After";
	public final static java.lang.String LINE_START = "Before";
	public final static java.lang.String NORTH = "North";
	public final static java.lang.String PAGE_END = "Last";
	public final static java.lang.String PAGE_START = "First";
	public final static java.lang.String SOUTH = "South";
	public final static java.lang.String WEST = "West";
}

