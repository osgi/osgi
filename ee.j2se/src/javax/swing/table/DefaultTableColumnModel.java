/*
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
public class DefaultTableColumnModel implements java.beans.PropertyChangeListener, java.io.Serializable, javax.swing.event.ListSelectionListener, javax.swing.table.TableColumnModel {
	public DefaultTableColumnModel() { }
	public void addColumn(javax.swing.table.TableColumn var0) { }
	public void addColumnModelListener(javax.swing.event.TableColumnModelListener var0) { }
	protected javax.swing.ListSelectionModel createSelectionModel() { return null; }
	protected void fireColumnAdded(javax.swing.event.TableColumnModelEvent var0) { }
	protected void fireColumnMarginChanged() { }
	protected void fireColumnMoved(javax.swing.event.TableColumnModelEvent var0) { }
	protected void fireColumnRemoved(javax.swing.event.TableColumnModelEvent var0) { }
	protected void fireColumnSelectionChanged(javax.swing.event.ListSelectionEvent var0) { }
	public javax.swing.table.TableColumn getColumn(int var0) { return null; }
	public int getColumnCount() { return 0; }
	public int getColumnIndex(java.lang.Object var0) { return 0; }
	public int getColumnIndexAtX(int var0) { return 0; }
	public int getColumnMargin() { return 0; }
	public javax.swing.event.TableColumnModelListener[] getColumnModelListeners() { return null; }
	public boolean getColumnSelectionAllowed() { return false; }
	public java.util.Enumeration getColumns() { return null; }
	public java.util.EventListener[] getListeners(java.lang.Class var0) { return null; }
	public int getSelectedColumnCount() { return 0; }
	public int[] getSelectedColumns() { return null; }
	public javax.swing.ListSelectionModel getSelectionModel() { return null; }
	public int getTotalColumnWidth() { return 0; }
	public void moveColumn(int var0, int var1) { }
	public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	protected void recalcWidthCache() { }
	public void removeColumn(javax.swing.table.TableColumn var0) { }
	public void removeColumnModelListener(javax.swing.event.TableColumnModelListener var0) { }
	public void setColumnMargin(int var0) { }
	public void setColumnSelectionAllowed(boolean var0) { }
	public void setSelectionModel(javax.swing.ListSelectionModel var0) { }
	public void valueChanged(javax.swing.event.ListSelectionEvent var0) { }
	protected javax.swing.event.ChangeEvent changeEvent;
	protected int columnMargin;
	protected boolean columnSelectionAllowed;
	protected javax.swing.event.EventListenerList listenerList;
	protected javax.swing.ListSelectionModel selectionModel;
	protected java.util.Vector tableColumns;
	protected int totalColumnWidth;
}

