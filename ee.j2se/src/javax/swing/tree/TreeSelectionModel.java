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
public abstract interface TreeSelectionModel {
	public abstract void addPropertyChangeListener(java.beans.PropertyChangeListener var0);
	public abstract void addSelectionPath(javax.swing.tree.TreePath var0);
	public abstract void addSelectionPaths(javax.swing.tree.TreePath[] var0);
	public abstract void addTreeSelectionListener(javax.swing.event.TreeSelectionListener var0);
	public abstract void clearSelection();
	public abstract javax.swing.tree.TreePath getLeadSelectionPath();
	public abstract int getLeadSelectionRow();
	public abstract int getMaxSelectionRow();
	public abstract int getMinSelectionRow();
	public abstract javax.swing.tree.RowMapper getRowMapper();
	public abstract int getSelectionCount();
	public abstract int getSelectionMode();
	public abstract javax.swing.tree.TreePath getSelectionPath();
	public abstract javax.swing.tree.TreePath[] getSelectionPaths();
	public abstract int[] getSelectionRows();
	public abstract boolean isPathSelected(javax.swing.tree.TreePath var0);
	public abstract boolean isRowSelected(int var0);
	public abstract boolean isSelectionEmpty();
	public abstract void removePropertyChangeListener(java.beans.PropertyChangeListener var0);
	public abstract void removeSelectionPath(javax.swing.tree.TreePath var0);
	public abstract void removeSelectionPaths(javax.swing.tree.TreePath[] var0);
	public abstract void removeTreeSelectionListener(javax.swing.event.TreeSelectionListener var0);
	public abstract void resetRowSelection();
	public abstract void setRowMapper(javax.swing.tree.RowMapper var0);
	public abstract void setSelectionMode(int var0);
	public abstract void setSelectionPath(javax.swing.tree.TreePath var0);
	public abstract void setSelectionPaths(javax.swing.tree.TreePath[] var0);
	public final static int CONTIGUOUS_TREE_SELECTION = 2;
	public final static int DISCONTIGUOUS_TREE_SELECTION = 4;
	public final static int SINGLE_TREE_SELECTION = 1;
}

