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

package javax.swing.plaf.basic;
public class BasicTreeUI extends javax.swing.plaf.TreeUI {
	public BasicTreeUI() { }
	public void cancelEditing(javax.swing.JTree var0) { }
	protected void checkForClickInExpandControl(javax.swing.tree.TreePath var0, int var1, int var2) { }
	protected void completeEditing() { }
	protected void completeEditing(boolean var0, boolean var1, boolean var2) { }
	protected void completeUIInstall() { }
	protected void completeUIUninstall() { }
	protected void configureLayoutCache() { }
	protected javax.swing.event.CellEditorListener createCellEditorListener() { return null; }
	protected javax.swing.CellRendererPane createCellRendererPane() { return null; }
	protected java.awt.event.ComponentListener createComponentListener() { return null; }
	protected javax.swing.tree.TreeCellEditor createDefaultCellEditor() { return null; }
	protected javax.swing.tree.TreeCellRenderer createDefaultCellRenderer() { return null; }
	protected java.awt.event.FocusListener createFocusListener() { return null; }
	protected java.awt.event.KeyListener createKeyListener() { return null; }
	protected javax.swing.tree.AbstractLayoutCache createLayoutCache() { return null; }
	protected java.awt.event.MouseListener createMouseListener() { return null; }
	protected javax.swing.tree.AbstractLayoutCache.NodeDimensions createNodeDimensions() { return null; }
	protected java.beans.PropertyChangeListener createPropertyChangeListener() { return null; }
	protected java.beans.PropertyChangeListener createSelectionModelPropertyChangeListener() { return null; }
	protected javax.swing.event.TreeExpansionListener createTreeExpansionListener() { return null; }
	protected javax.swing.event.TreeModelListener createTreeModelListener() { return null; }
	protected javax.swing.event.TreeSelectionListener createTreeSelectionListener() { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected void drawCentered(java.awt.Component var0, java.awt.Graphics var1, javax.swing.Icon var2, int var3, int var4) { }
	protected void drawDashedHorizontalLine(java.awt.Graphics var0, int var1, int var2, int var3) { }
	protected void drawDashedVerticalLine(java.awt.Graphics var0, int var1, int var2, int var3) { }
	protected void ensureRowsAreVisible(int var0, int var1) { }
	protected javax.swing.tree.TreeCellEditor getCellEditor() { return null; }
	protected javax.swing.tree.TreeCellRenderer getCellRenderer() { return null; }
	public javax.swing.tree.TreePath getClosestPathForLocation(javax.swing.JTree var0, int var1, int var2) { return null; }
	public javax.swing.Icon getCollapsedIcon() { return null; }
	public javax.swing.tree.TreePath getEditingPath(javax.swing.JTree var0) { return null; }
	public javax.swing.Icon getExpandedIcon() { return null; }
	protected java.awt.Color getHashColor() { return null; }
	protected int getHorizontalLegBuffer() { return 0; }
	protected javax.swing.tree.TreePath getLastChildPath(javax.swing.tree.TreePath var0) { return null; }
	public int getLeftChildIndent() { return 0; }
	protected javax.swing.tree.TreeModel getModel() { return null; }
	public java.awt.Rectangle getPathBounds(javax.swing.JTree var0, javax.swing.tree.TreePath var1) { return null; }
	public javax.swing.tree.TreePath getPathForRow(javax.swing.JTree var0, int var1) { return null; }
	public java.awt.Dimension getPreferredMinSize() { return null; }
	public java.awt.Dimension getPreferredSize(javax.swing.JComponent var0, boolean var1) { return null; }
	public int getRightChildIndent() { return 0; }
	public int getRowCount(javax.swing.JTree var0) { return 0; }
	public int getRowForPath(javax.swing.JTree var0, javax.swing.tree.TreePath var1) { return 0; }
	protected int getRowHeight() { return 0; }
	protected javax.swing.tree.TreeSelectionModel getSelectionModel() { return null; }
	protected boolean getShowsRootHandles() { return false; }
	protected int getVerticalLegBuffer() { return 0; }
	protected void handleExpandControlClick(javax.swing.tree.TreePath var0, int var1, int var2) { }
	protected void installComponents() { }
	protected void installDefaults() { }
	protected void installKeyboardActions() { }
	protected void installListeners() { }
	protected boolean isEditable() { return false; }
	public boolean isEditing(javax.swing.JTree var0) { return false; }
	protected boolean isLargeModel() { return false; }
	protected boolean isLeaf(int var0) { return false; }
	protected boolean isLocationInExpandControl(javax.swing.tree.TreePath var0, int var1, int var2) { return false; }
	protected boolean isMultiSelectEvent(java.awt.event.MouseEvent var0) { return false; }
	protected boolean isRootVisible() { return false; }
	protected boolean isToggleEvent(java.awt.event.MouseEvent var0) { return false; }
	protected boolean isToggleSelectionEvent(java.awt.event.MouseEvent var0) { return false; }
	protected void paintExpandControl(java.awt.Graphics var0, java.awt.Rectangle var1, java.awt.Insets var2, java.awt.Rectangle var3, javax.swing.tree.TreePath var4, int var5, boolean var6, boolean var7, boolean var8) { }
	protected void paintHorizontalLine(java.awt.Graphics var0, javax.swing.JComponent var1, int var2, int var3, int var4) { }
	protected void paintHorizontalPartOfLeg(java.awt.Graphics var0, java.awt.Rectangle var1, java.awt.Insets var2, java.awt.Rectangle var3, javax.swing.tree.TreePath var4, int var5, boolean var6, boolean var7, boolean var8) { }
	protected void paintRow(java.awt.Graphics var0, java.awt.Rectangle var1, java.awt.Insets var2, java.awt.Rectangle var3, javax.swing.tree.TreePath var4, int var5, boolean var6, boolean var7, boolean var8) { }
	protected void paintVerticalLine(java.awt.Graphics var0, javax.swing.JComponent var1, int var2, int var3, int var4) { }
	protected void paintVerticalPartOfLeg(java.awt.Graphics var0, java.awt.Rectangle var1, java.awt.Insets var2, javax.swing.tree.TreePath var3) { }
	protected void pathWasCollapsed(javax.swing.tree.TreePath var0) { }
	protected void pathWasExpanded(javax.swing.tree.TreePath var0) { }
	protected void prepareForUIInstall() { }
	protected void prepareForUIUninstall() { }
	protected void selectPathForEvent(javax.swing.tree.TreePath var0, java.awt.event.MouseEvent var1) { }
	protected void setCellEditor(javax.swing.tree.TreeCellEditor var0) { }
	protected void setCellRenderer(javax.swing.tree.TreeCellRenderer var0) { }
	public void setCollapsedIcon(javax.swing.Icon var0) { }
	protected void setEditable(boolean var0) { }
	public void setExpandedIcon(javax.swing.Icon var0) { }
	protected void setHashColor(java.awt.Color var0) { }
	protected void setLargeModel(boolean var0) { }
	public void setLeftChildIndent(int var0) { }
	protected void setModel(javax.swing.tree.TreeModel var0) { }
	public void setPreferredMinSize(java.awt.Dimension var0) { }
	public void setRightChildIndent(int var0) { }
	protected void setRootVisible(boolean var0) { }
	protected void setRowHeight(int var0) { }
	protected void setSelectionModel(javax.swing.tree.TreeSelectionModel var0) { }
	protected void setShowsRootHandles(boolean var0) { }
	protected boolean shouldPaintExpandControl(javax.swing.tree.TreePath var0, int var1, boolean var2, boolean var3, boolean var4) { return false; }
	protected boolean startEditing(javax.swing.tree.TreePath var0, java.awt.event.MouseEvent var1) { return false; }
	public void startEditingAtPath(javax.swing.JTree var0, javax.swing.tree.TreePath var1) { }
	public boolean stopEditing(javax.swing.JTree var0) { return false; }
	protected void toggleExpandState(javax.swing.tree.TreePath var0) { }
	protected void uninstallComponents() { }
	protected void uninstallDefaults() { }
	protected void uninstallKeyboardActions() { }
	protected void uninstallListeners() { }
	protected void updateCachedPreferredSize() { }
	protected void updateCellEditor() { }
	protected void updateDepthOffset() { }
	protected void updateExpandedDescendants(javax.swing.tree.TreePath var0) { }
	protected void updateLayoutCacheExpandedNodes() { }
	protected void updateRenderer() { }
	protected void updateSize() { }
	protected javax.swing.tree.TreeCellEditor cellEditor;
	protected javax.swing.Icon collapsedIcon;
	protected boolean createdCellEditor;
	protected boolean createdRenderer;
	protected javax.swing.tree.TreeCellRenderer currentCellRenderer;
	protected int depthOffset;
	protected java.util.Hashtable drawingCache;
	protected java.awt.Component editingComponent;
	protected javax.swing.tree.TreePath editingPath;
	protected int editingRow;
	protected boolean editorHasDifferentSize;
	protected javax.swing.Icon expandedIcon;
	protected boolean largeModel;
	protected int lastSelectedRow;
	protected int leftChildIndent;
	protected javax.swing.tree.AbstractLayoutCache.NodeDimensions nodeDimensions;
	protected java.awt.Dimension preferredMinSize;
	protected java.awt.Dimension preferredSize;
	protected javax.swing.CellRendererPane rendererPane;
	protected int rightChildIndent;
	protected boolean stopEditingInCompleteEditing;
	protected int totalChildIndent;
	protected javax.swing.JTree tree;
	protected javax.swing.tree.TreeModel treeModel;
	protected javax.swing.tree.TreeSelectionModel treeSelectionModel;
	protected javax.swing.tree.AbstractLayoutCache treeState;
	protected boolean validCachedPreferredSize;
	public class CellEditorHandler implements javax.swing.event.CellEditorListener {
		public CellEditorHandler() { }
		public void editingCanceled(javax.swing.event.ChangeEvent var0) { }
		public void editingStopped(javax.swing.event.ChangeEvent var0) { }
	}
	public class ComponentHandler extends java.awt.event.ComponentAdapter implements java.awt.event.ActionListener {
		public ComponentHandler() { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
		protected javax.swing.JScrollPane getScrollPane() { return null; }
		protected void startTimer() { }
		protected javax.swing.JScrollBar scrollBar;
		protected javax.swing.Timer timer;
	}
	public class FocusHandler implements java.awt.event.FocusListener {
		public FocusHandler() { }
		public void focusGained(java.awt.event.FocusEvent var0) { }
		public void focusLost(java.awt.event.FocusEvent var0) { }
	}
	public class KeyHandler extends java.awt.event.KeyAdapter {
		public KeyHandler() { }
		protected boolean isKeyDown;
		protected javax.swing.Action repeatKeyAction;
	}
	public class MouseHandler extends java.awt.event.MouseAdapter implements java.awt.event.MouseMotionListener {
		public MouseHandler() { }
		public void mouseDragged(java.awt.event.MouseEvent var0) { }
		public void mouseMoved(java.awt.event.MouseEvent var0) { }
	}
	public class MouseInputHandler implements javax.swing.event.MouseInputListener {
		public MouseInputHandler(java.awt.Component var0, java.awt.Component var1, java.awt.event.MouseEvent var2) { }
		public void mouseClicked(java.awt.event.MouseEvent var0) { }
		public void mouseDragged(java.awt.event.MouseEvent var0) { }
		public void mouseEntered(java.awt.event.MouseEvent var0) { }
		public void mouseExited(java.awt.event.MouseEvent var0) { }
		public void mouseMoved(java.awt.event.MouseEvent var0) { }
		public void mousePressed(java.awt.event.MouseEvent var0) { }
		public void mouseReleased(java.awt.event.MouseEvent var0) { }
		protected void removeFromSource() { }
		protected java.awt.Component destination;
		protected java.awt.Component source;
	}
	public class NodeDimensionsHandler extends javax.swing.tree.AbstractLayoutCache.NodeDimensions {
		public NodeDimensionsHandler() { }
		public java.awt.Rectangle getNodeDimensions(java.lang.Object var0, int var1, int var2, boolean var3, java.awt.Rectangle var4) { return null; }
		protected int getRowX(int var0, int var1) { return 0; }
	}
	public class PropertyChangeHandler implements java.beans.PropertyChangeListener {
		public PropertyChangeHandler() { }
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	}
	public class SelectionModelPropertyChangeHandler implements java.beans.PropertyChangeListener {
		public SelectionModelPropertyChangeHandler() { }
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	}
	public class TreeCancelEditingAction extends javax.swing.AbstractAction {
		public TreeCancelEditingAction(java.lang.String var0) { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public class TreeExpansionHandler implements javax.swing.event.TreeExpansionListener {
		public TreeExpansionHandler() { }
		public void treeCollapsed(javax.swing.event.TreeExpansionEvent var0) { }
		public void treeExpanded(javax.swing.event.TreeExpansionEvent var0) { }
	}
	public class TreeHomeAction extends javax.swing.AbstractAction {
		public TreeHomeAction(int var0, java.lang.String var1) { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
		protected int direction;
	}
	public class TreeIncrementAction extends javax.swing.AbstractAction {
		public TreeIncrementAction(int var0, java.lang.String var1) { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
		protected int direction;
	}
	public class TreeModelHandler implements javax.swing.event.TreeModelListener {
		public TreeModelHandler() { }
		public void treeNodesChanged(javax.swing.event.TreeModelEvent var0) { }
		public void treeNodesInserted(javax.swing.event.TreeModelEvent var0) { }
		public void treeNodesRemoved(javax.swing.event.TreeModelEvent var0) { }
		public void treeStructureChanged(javax.swing.event.TreeModelEvent var0) { }
	}
	public class TreePageAction extends javax.swing.AbstractAction {
		public TreePageAction(int var0, java.lang.String var1) { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
		protected int direction;
	}
	public class TreeSelectionHandler implements javax.swing.event.TreeSelectionListener {
		public TreeSelectionHandler() { }
		public void valueChanged(javax.swing.event.TreeSelectionEvent var0) { }
	}
	public class TreeToggleAction extends javax.swing.AbstractAction {
		public TreeToggleAction(java.lang.String var0) { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public class TreeTraverseAction extends javax.swing.AbstractAction {
		public TreeTraverseAction(int var0, java.lang.String var1) { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
		protected int direction;
	}
}

