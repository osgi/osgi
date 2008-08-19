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

package javax.swing.tree;
public abstract class AbstractLayoutCache implements javax.swing.tree.RowMapper {
	public AbstractLayoutCache() { }
	public abstract java.awt.Rectangle getBounds(javax.swing.tree.TreePath var0, java.awt.Rectangle var1);
	public abstract boolean getExpandedState(javax.swing.tree.TreePath var0);
	public javax.swing.tree.TreeModel getModel() { return null; }
	public javax.swing.tree.AbstractLayoutCache.NodeDimensions getNodeDimensions() { return null; }
	protected java.awt.Rectangle getNodeDimensions(java.lang.Object var0, int var1, int var2, boolean var3, java.awt.Rectangle var4) { return null; }
	public abstract javax.swing.tree.TreePath getPathClosestTo(int var0, int var1);
	public abstract javax.swing.tree.TreePath getPathForRow(int var0);
	public int getPreferredHeight() { return 0; }
	public int getPreferredWidth(java.awt.Rectangle var0) { return 0; }
	public abstract int getRowCount();
	public abstract int getRowForPath(javax.swing.tree.TreePath var0);
	public int getRowHeight() { return 0; }
	public int[] getRowsForPaths(javax.swing.tree.TreePath[] var0) { return null; }
	public javax.swing.tree.TreeSelectionModel getSelectionModel() { return null; }
	public abstract int getVisibleChildCount(javax.swing.tree.TreePath var0);
	public abstract java.util.Enumeration getVisiblePathsFrom(javax.swing.tree.TreePath var0);
	public abstract void invalidatePathBounds(javax.swing.tree.TreePath var0);
	public abstract void invalidateSizes();
	public abstract boolean isExpanded(javax.swing.tree.TreePath var0);
	protected boolean isFixedRowHeight() { return false; }
	public boolean isRootVisible() { return false; }
	public abstract void setExpandedState(javax.swing.tree.TreePath var0, boolean var1);
	public void setModel(javax.swing.tree.TreeModel var0) { }
	public void setNodeDimensions(javax.swing.tree.AbstractLayoutCache.NodeDimensions var0) { }
	public void setRootVisible(boolean var0) { }
	public void setRowHeight(int var0) { }
	public void setSelectionModel(javax.swing.tree.TreeSelectionModel var0) { }
	public abstract void treeNodesChanged(javax.swing.event.TreeModelEvent var0);
	public abstract void treeNodesInserted(javax.swing.event.TreeModelEvent var0);
	public abstract void treeNodesRemoved(javax.swing.event.TreeModelEvent var0);
	public abstract void treeStructureChanged(javax.swing.event.TreeModelEvent var0);
	protected javax.swing.tree.AbstractLayoutCache.NodeDimensions nodeDimensions;
	protected boolean rootVisible;
	protected int rowHeight;
	protected javax.swing.tree.TreeModel treeModel;
	protected javax.swing.tree.TreeSelectionModel treeSelectionModel;
	public static abstract class NodeDimensions {
		public NodeDimensions() { }
		public abstract java.awt.Rectangle getNodeDimensions(java.lang.Object var0, int var1, int var2, boolean var3, java.awt.Rectangle var4);
	}
}

