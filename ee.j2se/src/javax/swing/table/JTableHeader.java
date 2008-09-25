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

package javax.swing.table;
public class JTableHeader extends javax.swing.JComponent implements javax.accessibility.Accessible, javax.swing.event.TableColumnModelListener {
	public JTableHeader() { }
	public JTableHeader(javax.swing.table.TableColumnModel var0) { }
	public void columnAdded(javax.swing.event.TableColumnModelEvent var0) { }
	public int columnAtPoint(java.awt.Point var0) { return 0; }
	public void columnMarginChanged(javax.swing.event.ChangeEvent var0) { }
	public void columnMoved(javax.swing.event.TableColumnModelEvent var0) { }
	public void columnRemoved(javax.swing.event.TableColumnModelEvent var0) { }
	public void columnSelectionChanged(javax.swing.event.ListSelectionEvent var0) { }
	protected javax.swing.table.TableColumnModel createDefaultColumnModel() { return null; }
	protected javax.swing.table.TableCellRenderer createDefaultRenderer() { return null; }
	public javax.swing.table.TableColumnModel getColumnModel() { return null; }
	public javax.swing.table.TableCellRenderer getDefaultRenderer() { return null; }
	public javax.swing.table.TableColumn getDraggedColumn() { return null; }
	public int getDraggedDistance() { return 0; }
	public java.awt.Rectangle getHeaderRect(int var0) { return null; }
	public boolean getReorderingAllowed() { return false; }
	public boolean getResizingAllowed() { return false; }
	public javax.swing.table.TableColumn getResizingColumn() { return null; }
	public javax.swing.JTable getTable() { return null; }
	public javax.swing.plaf.TableHeaderUI getUI() { return null; }
	public boolean getUpdateTableInRealTime() { return false; }
	protected void initializeLocalVars() { }
	public void resizeAndRepaint() { }
	public void setColumnModel(javax.swing.table.TableColumnModel var0) { }
	public void setDefaultRenderer(javax.swing.table.TableCellRenderer var0) { }
	public void setDraggedColumn(javax.swing.table.TableColumn var0) { }
	public void setDraggedDistance(int var0) { }
	public void setReorderingAllowed(boolean var0) { }
	public void setResizingAllowed(boolean var0) { }
	public void setResizingColumn(javax.swing.table.TableColumn var0) { }
	public void setTable(javax.swing.JTable var0) { }
	public void setUI(javax.swing.plaf.TableHeaderUI var0) { }
	public void setUpdateTableInRealTime(boolean var0) { }
	protected javax.swing.table.TableColumnModel columnModel;
	protected javax.swing.table.TableColumn draggedColumn;
	protected int draggedDistance;
	protected boolean reorderingAllowed;
	protected boolean resizingAllowed;
	protected javax.swing.table.TableColumn resizingColumn;
	protected javax.swing.JTable table;
	protected boolean updateTableInRealTime;
	protected class AccessibleJTableHeader extends javax.swing.JComponent.AccessibleJComponent {
		protected AccessibleJTableHeader() { }
		protected class AccessibleJTableHeaderEntry extends javax.accessibility.AccessibleContext implements javax.accessibility.Accessible, javax.accessibility.AccessibleComponent {
			public AccessibleJTableHeaderEntry(int var0, javax.swing.table.JTableHeader var1, javax.swing.JTable var2) { }
			public void addFocusListener(java.awt.event.FocusListener var0) { }
			public boolean contains(java.awt.Point var0) { return false; }
			public javax.accessibility.Accessible getAccessibleAt(java.awt.Point var0) { return null; }
			public javax.accessibility.Accessible getAccessibleChild(int var0) { return null; }
			public int getAccessibleChildrenCount() { return 0; }
			public javax.accessibility.AccessibleContext getAccessibleContext() { return null; }
			public int getAccessibleIndexInParent() { return 0; }
			public javax.accessibility.AccessibleRole getAccessibleRole() { return null; }
			public javax.accessibility.AccessibleStateSet getAccessibleStateSet() { return null; }
			public java.awt.Color getBackground() { return null; }
			public java.awt.Rectangle getBounds() { return null; }
			public java.awt.Cursor getCursor() { return null; }
			public java.awt.Font getFont() { return null; }
			public java.awt.FontMetrics getFontMetrics(java.awt.Font var0) { return null; }
			public java.awt.Color getForeground() { return null; }
			public java.util.Locale getLocale() { return null; }
			public java.awt.Point getLocation() { return null; }
			public java.awt.Point getLocationOnScreen() { return null; }
			public java.awt.Dimension getSize() { return null; }
			public boolean isEnabled() { return false; }
			public boolean isFocusTraversable() { return false; }
			public boolean isShowing() { return false; }
			public boolean isVisible() { return false; }
			public void removeFocusListener(java.awt.event.FocusListener var0) { }
			public void requestFocus() { }
			public void setBackground(java.awt.Color var0) { }
			public void setBounds(java.awt.Rectangle var0) { }
			public void setCursor(java.awt.Cursor var0) { }
			public void setEnabled(boolean var0) { }
			public void setFont(java.awt.Font var0) { }
			public void setForeground(java.awt.Color var0) { }
			public void setLocation(java.awt.Point var0) { }
			public void setSize(java.awt.Dimension var0) { }
			public void setVisible(boolean var0) { }
		}
	}
}

