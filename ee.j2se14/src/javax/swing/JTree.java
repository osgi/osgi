/*
 * $Date$
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

package javax.swing;
public class JTree extends javax.swing.JComponent implements javax.accessibility.Accessible, javax.swing.Scrollable {
	public JTree() { }
	public JTree(java.util.Hashtable var0) { }
	public JTree(java.util.Vector var0) { }
	public JTree(javax.swing.tree.TreeModel var0) { }
	public JTree(javax.swing.tree.TreeNode var0) { }
	public JTree(javax.swing.tree.TreeNode var0, boolean var1) { }
	public JTree(java.lang.Object[] var0) { }
	public void addSelectionInterval(int var0, int var1) { }
	public void addSelectionPath(javax.swing.tree.TreePath var0) { }
	public void addSelectionPaths(javax.swing.tree.TreePath[] var0) { }
	public void addSelectionRow(int var0) { }
	public void addSelectionRows(int[] var0) { }
	public void addTreeExpansionListener(javax.swing.event.TreeExpansionListener var0) { }
	public void addTreeSelectionListener(javax.swing.event.TreeSelectionListener var0) { }
	public void addTreeWillExpandListener(javax.swing.event.TreeWillExpandListener var0) { }
	public void cancelEditing() { }
	public void clearSelection() { }
	protected void clearToggledPaths() { }
	public void collapsePath(javax.swing.tree.TreePath var0) { }
	public void collapseRow(int var0) { }
	public java.lang.String convertValueToText(java.lang.Object var0, boolean var1, boolean var2, boolean var3, int var4, boolean var5) { return null; }
	protected static javax.swing.tree.TreeModel createTreeModel(java.lang.Object var0) { return null; }
	protected javax.swing.event.TreeModelListener createTreeModelListener() { return null; }
	public void expandPath(javax.swing.tree.TreePath var0) { }
	public void expandRow(int var0) { }
	public void fireTreeCollapsed(javax.swing.tree.TreePath var0) { }
	public void fireTreeExpanded(javax.swing.tree.TreePath var0) { }
	public void fireTreeWillCollapse(javax.swing.tree.TreePath var0) throws javax.swing.tree.ExpandVetoException { }
	public void fireTreeWillExpand(javax.swing.tree.TreePath var0) throws javax.swing.tree.ExpandVetoException { }
	protected void fireValueChanged(javax.swing.event.TreeSelectionEvent var0) { }
	public javax.swing.tree.TreePath getAnchorSelectionPath() { return null; }
	public javax.swing.tree.TreeCellEditor getCellEditor() { return null; }
	public javax.swing.tree.TreeCellRenderer getCellRenderer() { return null; }
	public javax.swing.tree.TreePath getClosestPathForLocation(int var0, int var1) { return null; }
	public int getClosestRowForLocation(int var0, int var1) { return 0; }
	protected static javax.swing.tree.TreeModel getDefaultTreeModel() { return null; }
	protected java.util.Enumeration getDescendantToggledPaths(javax.swing.tree.TreePath var0) { return null; }
	public boolean getDragEnabled() { return false; }
	public javax.swing.tree.TreePath getEditingPath() { return null; }
	public java.util.Enumeration getExpandedDescendants(javax.swing.tree.TreePath var0) { return null; }
	public boolean getExpandsSelectedPaths() { return false; }
	public boolean getInvokesStopCellEditing() { return false; }
	public java.lang.Object getLastSelectedPathComponent() { return null; }
	public javax.swing.tree.TreePath getLeadSelectionPath() { return null; }
	public int getLeadSelectionRow() { return 0; }
	public int getMaxSelectionRow() { return 0; }
	public int getMinSelectionRow() { return 0; }
	public javax.swing.tree.TreeModel getModel() { return null; }
	public javax.swing.tree.TreePath getNextMatch(java.lang.String var0, int var1, javax.swing.text.Position.Bias var2) { return null; }
	protected javax.swing.tree.TreePath[] getPathBetweenRows(int var0, int var1) { return null; }
	public java.awt.Rectangle getPathBounds(javax.swing.tree.TreePath var0) { return null; }
	public javax.swing.tree.TreePath getPathForLocation(int var0, int var1) { return null; }
	public javax.swing.tree.TreePath getPathForRow(int var0) { return null; }
	public java.awt.Dimension getPreferredScrollableViewportSize() { return null; }
	public java.awt.Rectangle getRowBounds(int var0) { return null; }
	public int getRowCount() { return 0; }
	public int getRowForLocation(int var0, int var1) { return 0; }
	public int getRowForPath(javax.swing.tree.TreePath var0) { return 0; }
	public int getRowHeight() { return 0; }
	public int getScrollableBlockIncrement(java.awt.Rectangle var0, int var1, int var2) { return 0; }
	public boolean getScrollableTracksViewportHeight() { return false; }
	public boolean getScrollableTracksViewportWidth() { return false; }
	public int getScrollableUnitIncrement(java.awt.Rectangle var0, int var1, int var2) { return 0; }
	public boolean getScrollsOnExpand() { return false; }
	public int getSelectionCount() { return 0; }
	public javax.swing.tree.TreeSelectionModel getSelectionModel() { return null; }
	public javax.swing.tree.TreePath getSelectionPath() { return null; }
	public javax.swing.tree.TreePath[] getSelectionPaths() { return null; }
	public int[] getSelectionRows() { return null; }
	public boolean getShowsRootHandles() { return false; }
	public int getToggleClickCount() { return 0; }
	public javax.swing.event.TreeExpansionListener[] getTreeExpansionListeners() { return null; }
	public javax.swing.event.TreeSelectionListener[] getTreeSelectionListeners() { return null; }
	public javax.swing.event.TreeWillExpandListener[] getTreeWillExpandListeners() { return null; }
	public javax.swing.plaf.TreeUI getUI() { return null; }
	public int getVisibleRowCount() { return 0; }
	public boolean hasBeenExpanded(javax.swing.tree.TreePath var0) { return false; }
	public boolean isCollapsed(int var0) { return false; }
	public boolean isCollapsed(javax.swing.tree.TreePath var0) { return false; }
	public boolean isEditable() { return false; }
	public boolean isEditing() { return false; }
	public boolean isExpanded(int var0) { return false; }
	public boolean isExpanded(javax.swing.tree.TreePath var0) { return false; }
	public boolean isFixedRowHeight() { return false; }
	public boolean isLargeModel() { return false; }
	public boolean isPathEditable(javax.swing.tree.TreePath var0) { return false; }
	public boolean isPathSelected(javax.swing.tree.TreePath var0) { return false; }
	public boolean isRootVisible() { return false; }
	public boolean isRowSelected(int var0) { return false; }
	public boolean isSelectionEmpty() { return false; }
	public boolean isVisible(javax.swing.tree.TreePath var0) { return false; }
	public void makeVisible(javax.swing.tree.TreePath var0) { }
	protected boolean removeDescendantSelectedPaths(javax.swing.tree.TreePath var0, boolean var1) { return false; }
	protected void removeDescendantToggledPaths(java.util.Enumeration var0) { }
	public void removeSelectionInterval(int var0, int var1) { }
	public void removeSelectionPath(javax.swing.tree.TreePath var0) { }
	public void removeSelectionPaths(javax.swing.tree.TreePath[] var0) { }
	public void removeSelectionRow(int var0) { }
	public void removeSelectionRows(int[] var0) { }
	public void removeTreeExpansionListener(javax.swing.event.TreeExpansionListener var0) { }
	public void removeTreeSelectionListener(javax.swing.event.TreeSelectionListener var0) { }
	public void removeTreeWillExpandListener(javax.swing.event.TreeWillExpandListener var0) { }
	public void scrollPathToVisible(javax.swing.tree.TreePath var0) { }
	public void scrollRowToVisible(int var0) { }
	public void setAnchorSelectionPath(javax.swing.tree.TreePath var0) { }
	public void setCellEditor(javax.swing.tree.TreeCellEditor var0) { }
	public void setCellRenderer(javax.swing.tree.TreeCellRenderer var0) { }
	public void setDragEnabled(boolean var0) { }
	public void setEditable(boolean var0) { }
	protected void setExpandedState(javax.swing.tree.TreePath var0, boolean var1) { }
	public void setExpandsSelectedPaths(boolean var0) { }
	public void setInvokesStopCellEditing(boolean var0) { }
	public void setLargeModel(boolean var0) { }
	public void setLeadSelectionPath(javax.swing.tree.TreePath var0) { }
	public void setModel(javax.swing.tree.TreeModel var0) { }
	public void setRootVisible(boolean var0) { }
	public void setRowHeight(int var0) { }
	public void setScrollsOnExpand(boolean var0) { }
	public void setSelectionInterval(int var0, int var1) { }
	public void setSelectionModel(javax.swing.tree.TreeSelectionModel var0) { }
	public void setSelectionPath(javax.swing.tree.TreePath var0) { }
	public void setSelectionPaths(javax.swing.tree.TreePath[] var0) { }
	public void setSelectionRow(int var0) { }
	public void setSelectionRows(int[] var0) { }
	public void setShowsRootHandles(boolean var0) { }
	public void setToggleClickCount(int var0) { }
	public void setUI(javax.swing.plaf.TreeUI var0) { }
	public void setVisibleRowCount(int var0) { }
	public void startEditingAtPath(javax.swing.tree.TreePath var0) { }
	public boolean stopEditing() { return false; }
	public void treeDidChange() { }
	public final static java.lang.String ANCHOR_SELECTION_PATH_PROPERTY = "anchorSelectionPath";
	public final static java.lang.String CELL_EDITOR_PROPERTY = "cellEditor";
	public final static java.lang.String CELL_RENDERER_PROPERTY = "cellRenderer";
	public final static java.lang.String EDITABLE_PROPERTY = "editable";
	public final static java.lang.String EXPANDS_SELECTED_PATHS_PROPERTY = "expandsSelectedPaths";
	public final static java.lang.String INVOKES_STOP_CELL_EDITING_PROPERTY = "invokesStopCellEditing";
	public final static java.lang.String LARGE_MODEL_PROPERTY = "largeModel";
	public final static java.lang.String LEAD_SELECTION_PATH_PROPERTY = "leadSelectionPath";
	public final static java.lang.String ROOT_VISIBLE_PROPERTY = "rootVisible";
	public final static java.lang.String ROW_HEIGHT_PROPERTY = "rowHeight";
	public final static java.lang.String SCROLLS_ON_EXPAND_PROPERTY = "scrollsOnExpand";
	public final static java.lang.String SELECTION_MODEL_PROPERTY = "selectionModel";
	public final static java.lang.String SHOWS_ROOT_HANDLES_PROPERTY = "showsRootHandles";
	public final static java.lang.String TOGGLE_CLICK_COUNT_PROPERTY = "toggleClickCount";
	public final static java.lang.String TREE_MODEL_PROPERTY = "model";
	public final static java.lang.String VISIBLE_ROW_COUNT_PROPERTY = "visibleRowCount";
	protected javax.swing.tree.TreeCellEditor cellEditor;
	protected javax.swing.tree.TreeCellRenderer cellRenderer;
	protected boolean editable;
	protected boolean invokesStopCellEditing;
	protected boolean largeModel;
	protected boolean rootVisible;
	protected int rowHeight;
	protected boolean scrollsOnExpand;
	protected javax.swing.tree.TreeSelectionModel selectionModel;
	protected javax.swing.JTree.TreeSelectionRedirector selectionRedirector;
	protected boolean showsRootHandles;
	protected int toggleClickCount;
	protected javax.swing.tree.TreeModel treeModel;
	protected javax.swing.event.TreeModelListener treeModelListener;
	protected int visibleRowCount;
	protected class AccessibleJTree extends javax.swing.JComponent.AccessibleJComponent implements javax.accessibility.AccessibleSelection, javax.swing.event.TreeExpansionListener, javax.swing.event.TreeModelListener, javax.swing.event.TreeSelectionListener {
		public AccessibleJTree() { }
		public void addAccessibleSelection(int var0) { }
		public void clearAccessibleSelection() { }
		public void fireVisibleDataPropertyChange() { }
		public javax.accessibility.Accessible getAccessibleSelection(int var0) { return null; }
		public int getAccessibleSelectionCount() { return 0; }
		public boolean isAccessibleChildSelected(int var0) { return false; }
		public void removeAccessibleSelection(int var0) { }
		public void selectAllAccessibleSelection() { }
		public void treeCollapsed(javax.swing.event.TreeExpansionEvent var0) { }
		public void treeExpanded(javax.swing.event.TreeExpansionEvent var0) { }
		public void treeNodesChanged(javax.swing.event.TreeModelEvent var0) { }
		public void treeNodesInserted(javax.swing.event.TreeModelEvent var0) { }
		public void treeNodesRemoved(javax.swing.event.TreeModelEvent var0) { }
		public void treeStructureChanged(javax.swing.event.TreeModelEvent var0) { }
		public void valueChanged(javax.swing.event.TreeSelectionEvent var0) { }
		protected class AccessibleJTreeNode extends javax.accessibility.AccessibleContext implements javax.accessibility.Accessible, javax.accessibility.AccessibleAction, javax.accessibility.AccessibleComponent, javax.accessibility.AccessibleSelection {
			public AccessibleJTreeNode(javax.swing.JTree var0, javax.swing.tree.TreePath var1, javax.accessibility.Accessible var2) { }
			public void addAccessibleSelection(int var0) { }
			public void addFocusListener(java.awt.event.FocusListener var0) { }
			public void clearAccessibleSelection() { }
			public boolean contains(java.awt.Point var0) { return false; }
			public boolean doAccessibleAction(int var0) { return false; }
			public int getAccessibleActionCount() { return 0; }
			public java.lang.String getAccessibleActionDescription(int var0) { return null; }
			public javax.accessibility.Accessible getAccessibleAt(java.awt.Point var0) { return null; }
			public javax.accessibility.Accessible getAccessibleChild(int var0) { return null; }
			public int getAccessibleChildrenCount() { return 0; }
			public javax.accessibility.AccessibleContext getAccessibleContext() { return null; }
			public int getAccessibleIndexInParent() { return 0; }
			public javax.accessibility.AccessibleRole getAccessibleRole() { return null; }
			public javax.accessibility.Accessible getAccessibleSelection(int var0) { return null; }
			public int getAccessibleSelectionCount() { return 0; }
			public javax.accessibility.AccessibleStateSet getAccessibleStateSet() { return null; }
			public java.awt.Color getBackground() { return null; }
			public java.awt.Rectangle getBounds() { return null; }
			public java.awt.Cursor getCursor() { return null; }
			public java.awt.Font getFont() { return null; }
			public java.awt.FontMetrics getFontMetrics(java.awt.Font var0) { return null; }
			public java.awt.Color getForeground() { return null; }
			public java.util.Locale getLocale() { return null; }
			public java.awt.Point getLocation() { return null; }
			protected java.awt.Point getLocationInJTree() { return null; }
			public java.awt.Point getLocationOnScreen() { return null; }
			public java.awt.Dimension getSize() { return null; }
			public boolean isAccessibleChildSelected(int var0) { return false; }
			public boolean isEnabled() { return false; }
			public boolean isFocusTraversable() { return false; }
			public boolean isShowing() { return false; }
			public boolean isVisible() { return false; }
			public void removeAccessibleSelection(int var0) { }
			public void removeFocusListener(java.awt.event.FocusListener var0) { }
			public void requestFocus() { }
			public void selectAllAccessibleSelection() { }
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
	public static class DynamicUtilTreeNode extends javax.swing.tree.DefaultMutableTreeNode {
		public DynamicUtilTreeNode(java.lang.Object var0, java.lang.Object var1) { }
		public static void createChildren(javax.swing.tree.DefaultMutableTreeNode var0, java.lang.Object var1) { }
		protected void loadChildren() { }
		protected java.lang.Object childValue;
		protected boolean hasChildren;
		protected boolean loadedChildren;
	}
	protected static class EmptySelectionModel extends javax.swing.tree.DefaultTreeSelectionModel {
		protected EmptySelectionModel() { }
		public static javax.swing.JTree.EmptySelectionModel sharedInstance() { return null; }
		protected final static javax.swing.JTree.EmptySelectionModel sharedInstance; static { sharedInstance = null; }
	}
	protected class TreeModelHandler implements javax.swing.event.TreeModelListener {
		protected TreeModelHandler() { }
		public void treeNodesChanged(javax.swing.event.TreeModelEvent var0) { }
		public void treeNodesInserted(javax.swing.event.TreeModelEvent var0) { }
		public void treeNodesRemoved(javax.swing.event.TreeModelEvent var0) { }
		public void treeStructureChanged(javax.swing.event.TreeModelEvent var0) { }
	}
	protected class TreeSelectionRedirector implements java.io.Serializable, javax.swing.event.TreeSelectionListener {
		protected TreeSelectionRedirector() { }
		public void valueChanged(javax.swing.event.TreeSelectionEvent var0) { }
	}
}

