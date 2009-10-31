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
public class JList extends javax.swing.JComponent implements javax.accessibility.Accessible, javax.swing.Scrollable {
	protected class AccessibleJList extends javax.swing.JComponent.AccessibleJComponent implements java.beans.PropertyChangeListener, javax.accessibility.AccessibleSelection, javax.swing.event.ListDataListener, javax.swing.event.ListSelectionListener {
		protected class AccessibleJListChild extends javax.accessibility.AccessibleContext implements javax.accessibility.Accessible, javax.accessibility.AccessibleComponent {
			public AccessibleJListChild(javax.swing.JList var0, int var1) { } 
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
		public AccessibleJList() { } 
		public void addAccessibleSelection(int var0) { }
		public void clearAccessibleSelection() { }
		public void contentsChanged(javax.swing.event.ListDataEvent var0) { }
		public javax.accessibility.Accessible getAccessibleSelection(int var0) { return null; }
		public int getAccessibleSelectionCount() { return 0; }
		public void intervalAdded(javax.swing.event.ListDataEvent var0) { }
		public void intervalRemoved(javax.swing.event.ListDataEvent var0) { }
		public boolean isAccessibleChildSelected(int var0) { return false; }
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
		public void removeAccessibleSelection(int var0) { }
		public void selectAllAccessibleSelection() { }
		public void valueChanged(javax.swing.event.ListSelectionEvent var0) { }
	}
	public static final class DropLocation extends javax.swing.TransferHandler.DropLocation {
		public int getIndex() { return 0; }
		public boolean isInsert() { return false; }
		private DropLocation()  { super((java.awt.Point) null); } /* generated constructor to prevent compiler adding default public constructor */
	}
	public final static int HORIZONTAL_WRAP = 2;
	public final static int VERTICAL = 0;
	public final static int VERTICAL_WRAP = 1;
	public JList() { } 
	public JList(java.util.Vector<?> var0) { } 
	public JList(javax.swing.ListModel var0) { } 
	public JList(java.lang.Object[] var0) { } 
	public void addListSelectionListener(javax.swing.event.ListSelectionListener var0) { }
	public void addSelectionInterval(int var0, int var1) { }
	public void clearSelection() { }
	protected javax.swing.ListSelectionModel createSelectionModel() { return null; }
	public void ensureIndexIsVisible(int var0) { }
	protected void fireSelectionValueChanged(int var0, int var1, boolean var2) { }
	public int getAnchorSelectionIndex() { return 0; }
	public java.awt.Rectangle getCellBounds(int var0, int var1) { return null; }
	public javax.swing.ListCellRenderer getCellRenderer() { return null; }
	public boolean getDragEnabled() { return false; }
	public final javax.swing.JList.DropLocation getDropLocation() { return null; }
	public final javax.swing.DropMode getDropMode() { return null; }
	public int getFirstVisibleIndex() { return 0; }
	public int getFixedCellHeight() { return 0; }
	public int getFixedCellWidth() { return 0; }
	public int getLastVisibleIndex() { return 0; }
	public int getLayoutOrientation() { return 0; }
	public int getLeadSelectionIndex() { return 0; }
	public javax.swing.event.ListSelectionListener[] getListSelectionListeners() { return null; }
	public int getMaxSelectionIndex() { return 0; }
	public int getMinSelectionIndex() { return 0; }
	public javax.swing.ListModel getModel() { return null; }
	public int getNextMatch(java.lang.String var0, int var1, javax.swing.text.Position.Bias var2) { return 0; }
	public java.awt.Dimension getPreferredScrollableViewportSize() { return null; }
	public java.lang.Object getPrototypeCellValue() { return null; }
	public int getScrollableBlockIncrement(java.awt.Rectangle var0, int var1, int var2) { return 0; }
	public boolean getScrollableTracksViewportHeight() { return false; }
	public boolean getScrollableTracksViewportWidth() { return false; }
	public int getScrollableUnitIncrement(java.awt.Rectangle var0, int var1, int var2) { return 0; }
	public int getSelectedIndex() { return 0; }
	public int[] getSelectedIndices() { return null; }
	public java.lang.Object getSelectedValue() { return null; }
	public java.lang.Object[] getSelectedValues() { return null; }
	public java.awt.Color getSelectionBackground() { return null; }
	public java.awt.Color getSelectionForeground() { return null; }
	public int getSelectionMode() { return 0; }
	public javax.swing.ListSelectionModel getSelectionModel() { return null; }
	public javax.swing.plaf.ListUI getUI() { return null; }
	public boolean getValueIsAdjusting() { return false; }
	public int getVisibleRowCount() { return 0; }
	public java.awt.Point indexToLocation(int var0) { return null; }
	public boolean isSelectedIndex(int var0) { return false; }
	public boolean isSelectionEmpty() { return false; }
	public int locationToIndex(java.awt.Point var0) { return 0; }
	public void removeListSelectionListener(javax.swing.event.ListSelectionListener var0) { }
	public void removeSelectionInterval(int var0, int var1) { }
	public void setCellRenderer(javax.swing.ListCellRenderer var0) { }
	public void setDragEnabled(boolean var0) { }
	public final void setDropMode(javax.swing.DropMode var0) { }
	public void setFixedCellHeight(int var0) { }
	public void setFixedCellWidth(int var0) { }
	public void setLayoutOrientation(int var0) { }
	public void setListData(java.util.Vector<?> var0) { }
	public void setListData(java.lang.Object[] var0) { }
	public void setModel(javax.swing.ListModel var0) { }
	public void setPrototypeCellValue(java.lang.Object var0) { }
	public void setSelectedIndex(int var0) { }
	public void setSelectedIndices(int[] var0) { }
	public void setSelectedValue(java.lang.Object var0, boolean var1) { }
	public void setSelectionBackground(java.awt.Color var0) { }
	public void setSelectionForeground(java.awt.Color var0) { }
	public void setSelectionInterval(int var0, int var1) { }
	public void setSelectionMode(int var0) { }
	public void setSelectionModel(javax.swing.ListSelectionModel var0) { }
	public void setUI(javax.swing.plaf.ListUI var0) { }
	public void setValueIsAdjusting(boolean var0) { }
	public void setVisibleRowCount(int var0) { }
}

