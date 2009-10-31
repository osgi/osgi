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
public class List extends java.awt.Component implements java.awt.ItemSelectable, javax.accessibility.Accessible {
	protected class AccessibleAWTList extends java.awt.Component.AccessibleAWTComponent implements java.awt.event.ActionListener, java.awt.event.ItemListener, javax.accessibility.AccessibleSelection {
		protected class AccessibleAWTListChild extends java.awt.Component.AccessibleAWTComponent implements javax.accessibility.Accessible {
			public AccessibleAWTListChild(java.awt.List var0, int var1) { } 
			public javax.accessibility.AccessibleContext getAccessibleContext() { return null; }
		}
		public AccessibleAWTList() { } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
		public void addAccessibleSelection(int var0) { }
		public void clearAccessibleSelection() { }
		public javax.accessibility.Accessible getAccessibleSelection(int var0) { return null; }
		public int getAccessibleSelectionCount() { return 0; }
		public boolean isAccessibleChildSelected(int var0) { return false; }
		public void itemStateChanged(java.awt.event.ItemEvent var0) { }
		public void removeAccessibleSelection(int var0) { }
		public void selectAllAccessibleSelection() { }
	}
	public List() { } 
	public List(int var0) { } 
	public List(int var0, boolean var1) { } 
	public void add(java.lang.String var0) { }
	public void add(java.lang.String var0, int var1) { }
	public void addActionListener(java.awt.event.ActionListener var0) { }
	/** @deprecated */
	@java.lang.Deprecated
	public void addItem(java.lang.String var0) { }
	/** @deprecated */
	@java.lang.Deprecated
	public void addItem(java.lang.String var0, int var1) { }
	public void addItemListener(java.awt.event.ItemListener var0) { }
	/** @deprecated */
	@java.lang.Deprecated
	public boolean allowsMultipleSelections() { return false; }
	/** @deprecated */
	@java.lang.Deprecated
	public void clear() { }
	/** @deprecated */
	@java.lang.Deprecated
	public int countItems() { return 0; }
	/** @deprecated */
	@java.lang.Deprecated
	public void delItem(int var0) { }
	/** @deprecated */
	@java.lang.Deprecated
	public void delItems(int var0, int var1) { }
	public void deselect(int var0) { }
	public java.awt.event.ActionListener[] getActionListeners() { return null; }
	public java.lang.String getItem(int var0) { return null; }
	public int getItemCount() { return 0; }
	public java.awt.event.ItemListener[] getItemListeners() { return null; }
	public java.lang.String[] getItems() { return null; }
	public java.awt.Dimension getMinimumSize(int var0) { return null; }
	public java.awt.Dimension getPreferredSize(int var0) { return null; }
	public int getRows() { return 0; }
	public int getSelectedIndex() { return 0; }
	public int[] getSelectedIndexes() { return null; }
	public java.lang.String getSelectedItem() { return null; }
	public java.lang.String[] getSelectedItems() { return null; }
	public java.lang.Object[] getSelectedObjects() { return null; }
	public int getVisibleIndex() { return 0; }
	public boolean isIndexSelected(int var0) { return false; }
	public boolean isMultipleMode() { return false; }
	/** @deprecated */
	@java.lang.Deprecated
	public boolean isSelected(int var0) { return false; }
	public void makeVisible(int var0) { }
	/** @deprecated */
	@java.lang.Deprecated
	public java.awt.Dimension minimumSize(int var0) { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	public java.awt.Dimension preferredSize(int var0) { return null; }
	protected void processActionEvent(java.awt.event.ActionEvent var0) { }
	protected void processItemEvent(java.awt.event.ItemEvent var0) { }
	public void remove(int var0) { }
	public void remove(java.lang.String var0) { }
	public void removeActionListener(java.awt.event.ActionListener var0) { }
	public void removeAll() { }
	public void removeItemListener(java.awt.event.ItemListener var0) { }
	public void replaceItem(java.lang.String var0, int var1) { }
	public void select(int var0) { }
	public void setMultipleMode(boolean var0) { }
	/** @deprecated */
	@java.lang.Deprecated
	public void setMultipleSelections(boolean var0) { }
}

