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
public class DefaultTreeSelectionModel implements java.io.Serializable, java.lang.Cloneable, javax.swing.tree.TreeSelectionModel {
	public final static java.lang.String SELECTION_MODE_PROPERTY = "selectionMode";
	protected javax.swing.event.SwingPropertyChangeSupport changeSupport;
	protected int leadIndex;
	protected javax.swing.tree.TreePath leadPath;
	protected int leadRow;
	protected javax.swing.DefaultListSelectionModel listSelectionModel;
	protected javax.swing.event.EventListenerList listenerList;
	protected javax.swing.tree.RowMapper rowMapper;
	protected javax.swing.tree.TreePath[] selection;
	protected int selectionMode;
	public DefaultTreeSelectionModel() { } 
	public void addPropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public void addSelectionPath(javax.swing.tree.TreePath var0) { }
	public void addSelectionPaths(javax.swing.tree.TreePath[] var0) { }
	public void addTreeSelectionListener(javax.swing.event.TreeSelectionListener var0) { }
	protected boolean arePathsContiguous(javax.swing.tree.TreePath[] var0) { return false; }
	protected boolean canPathsBeAdded(javax.swing.tree.TreePath[] var0) { return false; }
	protected boolean canPathsBeRemoved(javax.swing.tree.TreePath[] var0) { return false; }
	public void clearSelection() { }
	public java.lang.Object clone() throws java.lang.CloneNotSupportedException { return null; }
	protected void fireValueChanged(javax.swing.event.TreeSelectionEvent var0) { }
	public javax.swing.tree.TreePath getLeadSelectionPath() { return null; }
	public int getLeadSelectionRow() { return 0; }
	public <T extends java.util.EventListener> T[] getListeners(java.lang.Class<T> var0) { return null; }
	public int getMaxSelectionRow() { return 0; }
	public int getMinSelectionRow() { return 0; }
	public java.beans.PropertyChangeListener[] getPropertyChangeListeners() { return null; }
	public javax.swing.tree.RowMapper getRowMapper() { return null; }
	public int getSelectionCount() { return 0; }
	public int getSelectionMode() { return 0; }
	public javax.swing.tree.TreePath getSelectionPath() { return null; }
	public javax.swing.tree.TreePath[] getSelectionPaths() { return null; }
	public int[] getSelectionRows() { return null; }
	public javax.swing.event.TreeSelectionListener[] getTreeSelectionListeners() { return null; }
	protected void insureRowContinuity() { }
	protected void insureUniqueness() { }
	public boolean isPathSelected(javax.swing.tree.TreePath var0) { return false; }
	public boolean isRowSelected(int var0) { return false; }
	public boolean isSelectionEmpty() { return false; }
	protected void notifyPathChange(java.util.Vector<javax.swing.tree.PathPlaceHolder> var0, javax.swing.tree.TreePath var1) { }
	public void removePropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public void removeSelectionPath(javax.swing.tree.TreePath var0) { }
	public void removeSelectionPaths(javax.swing.tree.TreePath[] var0) { }
	public void removeTreeSelectionListener(javax.swing.event.TreeSelectionListener var0) { }
	public void resetRowSelection() { }
	public void setRowMapper(javax.swing.tree.RowMapper var0) { }
	public void setSelectionMode(int var0) { }
	public void setSelectionPath(javax.swing.tree.TreePath var0) { }
	public void setSelectionPaths(javax.swing.tree.TreePath[] var0) { }
	protected void updateLeadIndex() { }
}

