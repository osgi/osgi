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

package javax.swing.tree;
public class DefaultTreeModel implements java.io.Serializable, javax.swing.tree.TreeModel {
	protected boolean asksAllowsChildren;
	protected javax.swing.event.EventListenerList listenerList;
	protected javax.swing.tree.TreeNode root;
	public DefaultTreeModel(javax.swing.tree.TreeNode var0) { } 
	public DefaultTreeModel(javax.swing.tree.TreeNode var0, boolean var1) { } 
	public void addTreeModelListener(javax.swing.event.TreeModelListener var0) { }
	public boolean asksAllowsChildren() { return false; }
	protected void fireTreeNodesChanged(java.lang.Object var0, java.lang.Object[] var1, int[] var2, java.lang.Object[] var3) { }
	protected void fireTreeNodesInserted(java.lang.Object var0, java.lang.Object[] var1, int[] var2, java.lang.Object[] var3) { }
	protected void fireTreeNodesRemoved(java.lang.Object var0, java.lang.Object[] var1, int[] var2, java.lang.Object[] var3) { }
	protected void fireTreeStructureChanged(java.lang.Object var0, java.lang.Object[] var1, int[] var2, java.lang.Object[] var3) { }
	public java.lang.Object getChild(java.lang.Object var0, int var1) { return null; }
	public int getChildCount(java.lang.Object var0) { return 0; }
	public int getIndexOfChild(java.lang.Object var0, java.lang.Object var1) { return 0; }
	public <T extends java.util.EventListener> T[] getListeners(java.lang.Class<T> var0) { return null; }
	public javax.swing.tree.TreeNode[] getPathToRoot(javax.swing.tree.TreeNode var0) { return null; }
	protected javax.swing.tree.TreeNode[] getPathToRoot(javax.swing.tree.TreeNode var0, int var1) { return null; }
	public java.lang.Object getRoot() { return null; }
	public javax.swing.event.TreeModelListener[] getTreeModelListeners() { return null; }
	public void insertNodeInto(javax.swing.tree.MutableTreeNode var0, javax.swing.tree.MutableTreeNode var1, int var2) { }
	public boolean isLeaf(java.lang.Object var0) { return false; }
	public void nodeChanged(javax.swing.tree.TreeNode var0) { }
	public void nodeStructureChanged(javax.swing.tree.TreeNode var0) { }
	public void nodesChanged(javax.swing.tree.TreeNode var0, int[] var1) { }
	public void nodesWereInserted(javax.swing.tree.TreeNode var0, int[] var1) { }
	public void nodesWereRemoved(javax.swing.tree.TreeNode var0, int[] var1, java.lang.Object[] var2) { }
	public void reload() { }
	public void reload(javax.swing.tree.TreeNode var0) { }
	public void removeNodeFromParent(javax.swing.tree.MutableTreeNode var0) { }
	public void removeTreeModelListener(javax.swing.event.TreeModelListener var0) { }
	public void setAsksAllowsChildren(boolean var0) { }
	public void setRoot(javax.swing.tree.TreeNode var0) { }
	public void valueForPathChanged(javax.swing.tree.TreePath var0, java.lang.Object var1) { }
}

