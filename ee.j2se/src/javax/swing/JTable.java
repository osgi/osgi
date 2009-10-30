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
public class JTable extends javax.swing.JComponent implements javax.accessibility.Accessible, javax.swing.Scrollable, javax.swing.event.CellEditorListener, javax.swing.event.ListSelectionListener, javax.swing.event.TableColumnModelListener, javax.swing.event.TableModelListener {
	protected class AccessibleJTable extends javax.swing.JComponent.AccessibleJComponent implements java.beans.PropertyChangeListener, javax.accessibility.AccessibleExtendedTable, javax.accessibility.AccessibleSelection, javax.swing.event.CellEditorListener, javax.swing.event.ListSelectionListener, javax.swing.event.TableColumnModelListener, javax.swing.event.TableModelListener {
		protected class AccessibleJTableCell extends javax.accessibility.AccessibleContext implements javax.accessibility.Accessible, javax.accessibility.AccessibleComponent {
			public AccessibleJTableCell(javax.swing.JTable var0, int var1, int var2, int var3) { } 
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
		protected class AccessibleJTableModelChange implements javax.accessibility.AccessibleTableModelChange {
			protected int firstColumn;
			protected int firstRow;
			protected int lastColumn;
			protected int lastRow;
			protected int type;
			protected AccessibleJTableModelChange(int var0, int var1, int var2, int var3, int var4) { } 
			public int getFirstColumn() { return 0; }
			public int getFirstRow() { return 0; }
			public int getLastColumn() { return 0; }
			public int getLastRow() { return 0; }
			public int getType() { return 0; }
		}
		protected AccessibleJTable() { } 
		public void addAccessibleSelection(int var0) { }
		public void clearAccessibleSelection() { }
		public void columnAdded(javax.swing.event.TableColumnModelEvent var0) { }
		public void columnMarginChanged(javax.swing.event.ChangeEvent var0) { }
		public void columnMoved(javax.swing.event.TableColumnModelEvent var0) { }
		public void columnRemoved(javax.swing.event.TableColumnModelEvent var0) { }
		public void columnSelectionChanged(javax.swing.event.ListSelectionEvent var0) { }
		public void editingCanceled(javax.swing.event.ChangeEvent var0) { }
		public void editingStopped(javax.swing.event.ChangeEvent var0) { }
		public javax.accessibility.Accessible getAccessibleAt(int var0, int var1) { return null; }
		public javax.accessibility.Accessible getAccessibleCaption() { return null; }
		public int getAccessibleColumn(int var0) { return 0; }
		public int getAccessibleColumnAtIndex(int var0) { return 0; }
		public int getAccessibleColumnCount() { return 0; }
		public javax.accessibility.Accessible getAccessibleColumnDescription(int var0) { return null; }
		public int getAccessibleColumnExtentAt(int var0, int var1) { return 0; }
		public javax.accessibility.AccessibleTable getAccessibleColumnHeader() { return null; }
		public int getAccessibleIndex(int var0, int var1) { return 0; }
		public int getAccessibleIndexAt(int var0, int var1) { return 0; }
		public int getAccessibleRow(int var0) { return 0; }
		public int getAccessibleRowAtIndex(int var0) { return 0; }
		public int getAccessibleRowCount() { return 0; }
		public javax.accessibility.Accessible getAccessibleRowDescription(int var0) { return null; }
		public int getAccessibleRowExtentAt(int var0, int var1) { return 0; }
		public javax.accessibility.AccessibleTable getAccessibleRowHeader() { return null; }
		public javax.accessibility.Accessible getAccessibleSelection(int var0) { return null; }
		public int getAccessibleSelectionCount() { return 0; }
		public javax.accessibility.Accessible getAccessibleSummary() { return null; }
		public int[] getSelectedAccessibleColumns() { return null; }
		public int[] getSelectedAccessibleRows() { return null; }
		public boolean isAccessibleChildSelected(int var0) { return false; }
		public boolean isAccessibleColumnSelected(int var0) { return false; }
		public boolean isAccessibleRowSelected(int var0) { return false; }
		public boolean isAccessibleSelected(int var0, int var1) { return false; }
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
		public void removeAccessibleSelection(int var0) { }
		public void selectAllAccessibleSelection() { }
		public void setAccessibleCaption(javax.accessibility.Accessible var0) { }
		public void setAccessibleColumnDescription(int var0, javax.accessibility.Accessible var1) { }
		public void setAccessibleColumnHeader(javax.accessibility.AccessibleTable var0) { }
		public void setAccessibleRowDescription(int var0, javax.accessibility.Accessible var1) { }
		public void setAccessibleRowHeader(javax.accessibility.AccessibleTable var0) { }
		public void setAccessibleSummary(javax.accessibility.Accessible var0) { }
		public void tableChanged(javax.swing.event.TableModelEvent var0) { }
		public void tableRowsDeleted(javax.swing.event.TableModelEvent var0) { }
		public void tableRowsInserted(javax.swing.event.TableModelEvent var0) { }
		public void valueChanged(javax.swing.event.ListSelectionEvent var0) { }
	}
	public enum PrintMode {
		FIT_WIDTH,
		NORMAL;
	}
	public final static int AUTO_RESIZE_ALL_COLUMNS = 4;
	public final static int AUTO_RESIZE_LAST_COLUMN = 3;
	public final static int AUTO_RESIZE_NEXT_COLUMN = 1;
	public final static int AUTO_RESIZE_OFF = 0;
	public final static int AUTO_RESIZE_SUBSEQUENT_COLUMNS = 2;
	protected boolean autoCreateColumnsFromModel;
	protected int autoResizeMode;
	protected javax.swing.table.TableCellEditor cellEditor;
	protected boolean cellSelectionEnabled;
	protected javax.swing.table.TableColumnModel columnModel;
	protected javax.swing.table.TableModel dataModel;
	protected java.util.Hashtable defaultEditorsByColumnClass;
	protected java.util.Hashtable defaultRenderersByColumnClass;
	protected int editingColumn;
	protected int editingRow;
	protected java.awt.Component editorComp;
	protected java.awt.Color gridColor;
	protected java.awt.Dimension preferredViewportSize;
	protected int rowHeight;
	protected int rowMargin;
	protected boolean rowSelectionAllowed;
	protected java.awt.Color selectionBackground;
	protected java.awt.Color selectionForeground;
	protected javax.swing.ListSelectionModel selectionModel;
	protected boolean showHorizontalLines;
	protected boolean showVerticalLines;
	protected javax.swing.table.JTableHeader tableHeader;
	public JTable() { } 
	public JTable(int var0, int var1) { } 
	public JTable(java.util.Vector var0, java.util.Vector var1) { } 
	public JTable(javax.swing.table.TableModel var0) { } 
	public JTable(javax.swing.table.TableModel var0, javax.swing.table.TableColumnModel var1) { } 
	public JTable(javax.swing.table.TableModel var0, javax.swing.table.TableColumnModel var1, javax.swing.ListSelectionModel var2) { } 
	public JTable(java.lang.Object[][] var0, java.lang.Object[] var1) { } 
	public void addColumn(javax.swing.table.TableColumn var0) { }
	public void addColumnSelectionInterval(int var0, int var1) { }
	public void addRowSelectionInterval(int var0, int var1) { }
	public void changeSelection(int var0, int var1, boolean var2, boolean var3) { }
	public void clearSelection() { }
	public void columnAdded(javax.swing.event.TableColumnModelEvent var0) { }
	public int columnAtPoint(java.awt.Point var0) { return 0; }
	public void columnMarginChanged(javax.swing.event.ChangeEvent var0) { }
	public void columnMoved(javax.swing.event.TableColumnModelEvent var0) { }
	public void columnRemoved(javax.swing.event.TableColumnModelEvent var0) { }
	public void columnSelectionChanged(javax.swing.event.ListSelectionEvent var0) { }
	protected void configureEnclosingScrollPane() { }
	public int convertColumnIndexToModel(int var0) { return 0; }
	public int convertColumnIndexToView(int var0) { return 0; }
	protected javax.swing.table.TableColumnModel createDefaultColumnModel() { return null; }
	public void createDefaultColumnsFromModel() { }
	protected javax.swing.table.TableModel createDefaultDataModel() { return null; }
	protected void createDefaultEditors() { }
	protected void createDefaultRenderers() { }
	protected javax.swing.ListSelectionModel createDefaultSelectionModel() { return null; }
	protected javax.swing.table.JTableHeader createDefaultTableHeader() { return null; }
	/** @deprecated */ public static javax.swing.JScrollPane createScrollPaneForTable(javax.swing.JTable var0) { return null; }
	public boolean editCellAt(int var0, int var1) { return false; }
	public boolean editCellAt(int var0, int var1, java.util.EventObject var2) { return false; }
	public void editingCanceled(javax.swing.event.ChangeEvent var0) { }
	public void editingStopped(javax.swing.event.ChangeEvent var0) { }
	public boolean getAutoCreateColumnsFromModel() { return false; }
	public int getAutoResizeMode() { return 0; }
	public javax.swing.table.TableCellEditor getCellEditor() { return null; }
	public javax.swing.table.TableCellEditor getCellEditor(int var0, int var1) { return null; }
	public java.awt.Rectangle getCellRect(int var0, int var1, boolean var2) { return null; }
	public javax.swing.table.TableCellRenderer getCellRenderer(int var0, int var1) { return null; }
	public boolean getCellSelectionEnabled() { return false; }
	public javax.swing.table.TableColumn getColumn(java.lang.Object var0) { return null; }
	public java.lang.Class<?> getColumnClass(int var0) { return null; }
	public int getColumnCount() { return 0; }
	public javax.swing.table.TableColumnModel getColumnModel() { return null; }
	public java.lang.String getColumnName(int var0) { return null; }
	public boolean getColumnSelectionAllowed() { return false; }
	public javax.swing.table.TableCellEditor getDefaultEditor(java.lang.Class<?> var0) { return null; }
	public javax.swing.table.TableCellRenderer getDefaultRenderer(java.lang.Class<?> var0) { return null; }
	public boolean getDragEnabled() { return false; }
	public int getEditingColumn() { return 0; }
	public int getEditingRow() { return 0; }
	public java.awt.Component getEditorComponent() { return null; }
	public java.awt.Color getGridColor() { return null; }
	public java.awt.Dimension getIntercellSpacing() { return null; }
	public javax.swing.table.TableModel getModel() { return null; }
	public java.awt.Dimension getPreferredScrollableViewportSize() { return null; }
	public java.awt.print.Printable getPrintable(javax.swing.JTable.PrintMode var0, java.text.MessageFormat var1, java.text.MessageFormat var2) { return null; }
	public int getRowCount() { return 0; }
	public int getRowHeight() { return 0; }
	public int getRowHeight(int var0) { return 0; }
	public int getRowMargin() { return 0; }
	public boolean getRowSelectionAllowed() { return false; }
	public int getScrollableBlockIncrement(java.awt.Rectangle var0, int var1, int var2) { return 0; }
	public boolean getScrollableTracksViewportHeight() { return false; }
	public boolean getScrollableTracksViewportWidth() { return false; }
	public int getScrollableUnitIncrement(java.awt.Rectangle var0, int var1, int var2) { return 0; }
	public int getSelectedColumn() { return 0; }
	public int getSelectedColumnCount() { return 0; }
	public int[] getSelectedColumns() { return null; }
	public int getSelectedRow() { return 0; }
	public int getSelectedRowCount() { return 0; }
	public int[] getSelectedRows() { return null; }
	public java.awt.Color getSelectionBackground() { return null; }
	public java.awt.Color getSelectionForeground() { return null; }
	public javax.swing.ListSelectionModel getSelectionModel() { return null; }
	public boolean getShowHorizontalLines() { return false; }
	public boolean getShowVerticalLines() { return false; }
	public boolean getSurrendersFocusOnKeystroke() { return false; }
	public javax.swing.table.JTableHeader getTableHeader() { return null; }
	public javax.swing.plaf.TableUI getUI() { return null; }
	public java.lang.Object getValueAt(int var0, int var1) { return null; }
	protected void initializeLocalVars() { }
	public boolean isCellEditable(int var0, int var1) { return false; }
	public boolean isCellSelected(int var0, int var1) { return false; }
	public boolean isColumnSelected(int var0) { return false; }
	public boolean isEditing() { return false; }
	public boolean isRowSelected(int var0) { return false; }
	public void moveColumn(int var0, int var1) { }
	public java.awt.Component prepareEditor(javax.swing.table.TableCellEditor var0, int var1, int var2) { return null; }
	public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer var0, int var1, int var2) { return null; }
	public boolean print() throws java.awt.print.PrinterException { return false; }
	public boolean print(javax.swing.JTable.PrintMode var0) throws java.awt.print.PrinterException { return false; }
	public boolean print(javax.swing.JTable.PrintMode var0, java.text.MessageFormat var1, java.text.MessageFormat var2) throws java.awt.print.PrinterException { return false; }
	public boolean print(javax.swing.JTable.PrintMode var0, java.text.MessageFormat var1, java.text.MessageFormat var2, boolean var3, javax.print.attribute.PrintRequestAttributeSet var4, boolean var5) throws java.awt.print.PrinterException { return false; }
	public void removeColumn(javax.swing.table.TableColumn var0) { }
	public void removeColumnSelectionInterval(int var0, int var1) { }
	public void removeEditor() { }
	public void removeRowSelectionInterval(int var0, int var1) { }
	protected void resizeAndRepaint() { }
	public int rowAtPoint(java.awt.Point var0) { return 0; }
	public void selectAll() { }
	public void setAutoCreateColumnsFromModel(boolean var0) { }
	public void setAutoResizeMode(int var0) { }
	public void setCellEditor(javax.swing.table.TableCellEditor var0) { }
	public void setCellSelectionEnabled(boolean var0) { }
	public void setColumnModel(javax.swing.table.TableColumnModel var0) { }
	public void setColumnSelectionAllowed(boolean var0) { }
	public void setColumnSelectionInterval(int var0, int var1) { }
	public void setDefaultEditor(java.lang.Class<?> var0, javax.swing.table.TableCellEditor var1) { }
	public void setDefaultRenderer(java.lang.Class<?> var0, javax.swing.table.TableCellRenderer var1) { }
	public void setDragEnabled(boolean var0) { }
	public void setEditingColumn(int var0) { }
	public void setEditingRow(int var0) { }
	public void setGridColor(java.awt.Color var0) { }
	public void setIntercellSpacing(java.awt.Dimension var0) { }
	public void setModel(javax.swing.table.TableModel var0) { }
	public void setPreferredScrollableViewportSize(java.awt.Dimension var0) { }
	public void setRowHeight(int var0) { }
	public void setRowHeight(int var0, int var1) { }
	public void setRowMargin(int var0) { }
	public void setRowSelectionAllowed(boolean var0) { }
	public void setRowSelectionInterval(int var0, int var1) { }
	public void setSelectionBackground(java.awt.Color var0) { }
	public void setSelectionForeground(java.awt.Color var0) { }
	public void setSelectionMode(int var0) { }
	public void setSelectionModel(javax.swing.ListSelectionModel var0) { }
	public void setShowGrid(boolean var0) { }
	public void setShowHorizontalLines(boolean var0) { }
	public void setShowVerticalLines(boolean var0) { }
	public void setSurrendersFocusOnKeystroke(boolean var0) { }
	public void setTableHeader(javax.swing.table.JTableHeader var0) { }
	public void setUI(javax.swing.plaf.TableUI var0) { }
	public void setValueAt(java.lang.Object var0, int var1, int var2) { }
	public void sizeColumnsToFit(int var0) { }
	/** @deprecated */ public void sizeColumnsToFit(boolean var0) { }
	public void tableChanged(javax.swing.event.TableModelEvent var0) { }
	protected void unconfigureEnclosingScrollPane() { }
	public void valueChanged(javax.swing.event.ListSelectionEvent var0) { }
}

