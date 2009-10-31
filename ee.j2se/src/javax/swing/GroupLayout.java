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
public class GroupLayout implements java.awt.LayoutManager2 {
	public enum Alignment {
		BASELINE,
		CENTER,
		LEADING,
		TRAILING;
	}
	public abstract class Group extends javax.swing.GroupLayout.Spring {
		public javax.swing.GroupLayout.Group addComponent(java.awt.Component var0) { return null; }
		public javax.swing.GroupLayout.Group addComponent(java.awt.Component var0, int var1, int var2, int var3) { return null; }
		public javax.swing.GroupLayout.Group addGap(int var0) { return null; }
		public javax.swing.GroupLayout.Group addGap(int var0, int var1, int var2) { return null; }
		public javax.swing.GroupLayout.Group addGroup(javax.swing.GroupLayout.Group var0) { return null; }
		Group() { } /* generated constructor to prevent compiler adding default public constructor */
	}
	public class ParallelGroup extends javax.swing.GroupLayout.Group {
		public javax.swing.GroupLayout.ParallelGroup addComponent(java.awt.Component var0) { return null; }
		public javax.swing.GroupLayout.ParallelGroup addComponent(java.awt.Component var0, int var1, int var2, int var3) { return null; }
		public javax.swing.GroupLayout.ParallelGroup addComponent(java.awt.Component var0, javax.swing.GroupLayout.Alignment var1) { return null; }
		public javax.swing.GroupLayout.ParallelGroup addComponent(java.awt.Component var0, javax.swing.GroupLayout.Alignment var1, int var2, int var3, int var4) { return null; }
		public javax.swing.GroupLayout.ParallelGroup addGap(int var0) { return null; }
		public javax.swing.GroupLayout.ParallelGroup addGap(int var0, int var1, int var2) { return null; }
		public javax.swing.GroupLayout.ParallelGroup addGroup(javax.swing.GroupLayout.Alignment var0, javax.swing.GroupLayout.Group var1) { return null; }
		public javax.swing.GroupLayout.ParallelGroup addGroup(javax.swing.GroupLayout.Group var0) { return null; }
		private ParallelGroup() { } /* generated constructor to prevent compiler adding default public constructor */
	}
	public class SequentialGroup extends javax.swing.GroupLayout.Group {
		public javax.swing.GroupLayout.SequentialGroup addComponent(java.awt.Component var0) { return null; }
		public javax.swing.GroupLayout.SequentialGroup addComponent(java.awt.Component var0, int var1, int var2, int var3) { return null; }
		public javax.swing.GroupLayout.SequentialGroup addComponent(boolean var0, java.awt.Component var1) { return null; }
		public javax.swing.GroupLayout.SequentialGroup addComponent(boolean var0, java.awt.Component var1, int var2, int var3, int var4) { return null; }
		public javax.swing.GroupLayout.SequentialGroup addContainerGap() { return null; }
		public javax.swing.GroupLayout.SequentialGroup addContainerGap(int var0, int var1) { return null; }
		public javax.swing.GroupLayout.SequentialGroup addGap(int var0) { return null; }
		public javax.swing.GroupLayout.SequentialGroup addGap(int var0, int var1, int var2) { return null; }
		public javax.swing.GroupLayout.SequentialGroup addGroup(javax.swing.GroupLayout.Group var0) { return null; }
		public javax.swing.GroupLayout.SequentialGroup addGroup(boolean var0, javax.swing.GroupLayout.Group var1) { return null; }
		public javax.swing.GroupLayout.SequentialGroup addPreferredGap(javax.swing.JComponent var0, javax.swing.JComponent var1, javax.swing.LayoutStyle.ComponentPlacement var2) { return null; }
		public javax.swing.GroupLayout.SequentialGroup addPreferredGap(javax.swing.JComponent var0, javax.swing.JComponent var1, javax.swing.LayoutStyle.ComponentPlacement var2, int var3, int var4) { return null; }
		public javax.swing.GroupLayout.SequentialGroup addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement var0) { return null; }
		public javax.swing.GroupLayout.SequentialGroup addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement var0, int var1, int var2) { return null; }
		private SequentialGroup() { } /* generated constructor to prevent compiler adding default public constructor */
	}
	abstract class Spring {
		Spring() { } /* generated constructor to prevent compiler adding default public constructor */
	}
	public final static int DEFAULT_SIZE = -1;
	public final static int PREFERRED_SIZE = -2;
	public GroupLayout(java.awt.Container var0) { } 
	public void addLayoutComponent(java.awt.Component var0, java.lang.Object var1) { }
	public void addLayoutComponent(java.lang.String var0, java.awt.Component var1) { }
	public javax.swing.GroupLayout.ParallelGroup createBaselineGroup(boolean var0, boolean var1) { return null; }
	public javax.swing.GroupLayout.ParallelGroup createParallelGroup() { return null; }
	public javax.swing.GroupLayout.ParallelGroup createParallelGroup(javax.swing.GroupLayout.Alignment var0) { return null; }
	public javax.swing.GroupLayout.ParallelGroup createParallelGroup(javax.swing.GroupLayout.Alignment var0, boolean var1) { return null; }
	public javax.swing.GroupLayout.SequentialGroup createSequentialGroup() { return null; }
	public boolean getAutoCreateContainerGaps() { return false; }
	public boolean getAutoCreateGaps() { return false; }
	public boolean getHonorsVisibility() { return false; }
	public float getLayoutAlignmentX(java.awt.Container var0) { return 0.0f; }
	public float getLayoutAlignmentY(java.awt.Container var0) { return 0.0f; }
	public javax.swing.LayoutStyle getLayoutStyle() { return null; }
	public void invalidateLayout(java.awt.Container var0) { }
	public void layoutContainer(java.awt.Container var0) { }
	public void linkSize(int var0, java.awt.Component... var1) { }
	public void linkSize(java.awt.Component... var0) { }
	public java.awt.Dimension maximumLayoutSize(java.awt.Container var0) { return null; }
	public java.awt.Dimension minimumLayoutSize(java.awt.Container var0) { return null; }
	public java.awt.Dimension preferredLayoutSize(java.awt.Container var0) { return null; }
	public void removeLayoutComponent(java.awt.Component var0) { }
	public void replace(java.awt.Component var0, java.awt.Component var1) { }
	public void setAutoCreateContainerGaps(boolean var0) { }
	public void setAutoCreateGaps(boolean var0) { }
	public void setHonorsVisibility(java.awt.Component var0, java.lang.Boolean var1) { }
	public void setHonorsVisibility(boolean var0) { }
	public void setHorizontalGroup(javax.swing.GroupLayout.Group var0) { }
	public void setLayoutStyle(javax.swing.LayoutStyle var0) { }
	public void setVerticalGroup(javax.swing.GroupLayout.Group var0) { }
}

