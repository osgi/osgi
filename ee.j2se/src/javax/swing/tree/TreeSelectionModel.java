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
public interface TreeSelectionModel {
	public final static int CONTIGUOUS_TREE_SELECTION = 2;
	public final static int DISCONTIGUOUS_TREE_SELECTION = 4;
	public final static int SINGLE_TREE_SELECTION = 1;
	void addPropertyChangeListener(java.beans.PropertyChangeListener var0);
	void addSelectionPath(javax.swing.tree.TreePath var0);
	void addSelectionPaths(javax.swing.tree.TreePath[] var0);
	void addTreeSelectionListener(javax.swing.event.TreeSelectionListener var0);
	void clearSelection();
	javax.swing.tree.TreePath getLeadSelectionPath();
	int getLeadSelectionRow();
	int getMaxSelectionRow();
	int getMinSelectionRow();
	javax.swing.tree.RowMapper getRowMapper();
	int getSelectionCount();
	int getSelectionMode();
	javax.swing.tree.TreePath getSelectionPath();
	javax.swing.tree.TreePath[] getSelectionPaths();
	int[] getSelectionRows();
	boolean isPathSelected(javax.swing.tree.TreePath var0);
	boolean isRowSelected(int var0);
	boolean isSelectionEmpty();
	void removePropertyChangeListener(java.beans.PropertyChangeListener var0);
	void removeSelectionPath(javax.swing.tree.TreePath var0);
	void removeSelectionPaths(javax.swing.tree.TreePath[] var0);
	void removeTreeSelectionListener(javax.swing.event.TreeSelectionListener var0);
	void resetRowSelection();
	void setRowMapper(javax.swing.tree.RowMapper var0);
	void setSelectionMode(int var0);
	void setSelectionPath(javax.swing.tree.TreePath var0);
	void setSelectionPaths(javax.swing.tree.TreePath[] var0);
}

