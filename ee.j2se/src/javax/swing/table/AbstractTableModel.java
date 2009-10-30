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

package javax.swing.table;
public abstract class AbstractTableModel implements java.io.Serializable, javax.swing.table.TableModel {
	protected javax.swing.event.EventListenerList listenerList;
	public AbstractTableModel() { } 
	public void addTableModelListener(javax.swing.event.TableModelListener var0) { }
	public int findColumn(java.lang.String var0) { return 0; }
	public void fireTableCellUpdated(int var0, int var1) { }
	public void fireTableChanged(javax.swing.event.TableModelEvent var0) { }
	public void fireTableDataChanged() { }
	public void fireTableRowsDeleted(int var0, int var1) { }
	public void fireTableRowsInserted(int var0, int var1) { }
	public void fireTableRowsUpdated(int var0, int var1) { }
	public void fireTableStructureChanged() { }
	public java.lang.Class<?> getColumnClass(int var0) { return null; }
	public java.lang.String getColumnName(int var0) { return null; }
	public <T extends java.util.EventListener> T[] getListeners(java.lang.Class<T> var0) { return null; }
	public javax.swing.event.TableModelListener[] getTableModelListeners() { return null; }
	public boolean isCellEditable(int var0, int var1) { return false; }
	public void removeTableModelListener(javax.swing.event.TableModelListener var0) { }
	public void setValueAt(java.lang.Object var0, int var1, int var2) { }
}

