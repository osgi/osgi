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
public class FlowLayout implements java.awt.LayoutManager, java.io.Serializable {
	public final static int CENTER = 1;
	public final static int LEADING = 3;
	public final static int LEFT = 0;
	public final static int RIGHT = 2;
	public final static int TRAILING = 4;
	public FlowLayout() { } 
	public FlowLayout(int var0) { } 
	public FlowLayout(int var0, int var1, int var2) { } 
	public void addLayoutComponent(java.lang.String var0, java.awt.Component var1) { }
	public boolean getAlignOnBaseline() { return false; }
	public int getAlignment() { return 0; }
	public int getHgap() { return 0; }
	public int getVgap() { return 0; }
	public void layoutContainer(java.awt.Container var0) { }
	public java.awt.Dimension minimumLayoutSize(java.awt.Container var0) { return null; }
	public java.awt.Dimension preferredLayoutSize(java.awt.Container var0) { return null; }
	public void removeLayoutComponent(java.awt.Component var0) { }
	public void setAlignOnBaseline(boolean var0) { }
	public void setAlignment(int var0) { }
	public void setHgap(int var0) { }
	public void setVgap(int var0) { }
}

