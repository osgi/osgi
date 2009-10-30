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
public class DefaultTableModel extends javax.swing.table.AbstractTableModel implements java.io.Serializable {
	protected java.util.Vector columnIdentifiers;
	protected java.util.Vector dataVector;
	public DefaultTableModel() { } 
	public DefaultTableModel(int var0, int var1) { } 
	public DefaultTableModel(java.util.Vector var0, int var1) { } 
	public DefaultTableModel(java.util.Vector var0, java.util.Vector var1) { } 
	public DefaultTableModel(java.lang.Object[] var0, int var1) { } 
	public DefaultTableModel(java.lang.Object[][] var0, java.lang.Object[] var1) { } 
	public void addColumn(java.lang.Object var0) { }
	public void addColumn(java.lang.Object var0, java.util.Vector var1) { }
	public void addColumn(java.lang.Object var0, java.lang.Object[] var1) { }
	public void addRow(java.util.Vector var0) { }
	public void addRow(java.lang.Object[] var0) { }
	protected static java.util.Vector convertToVector(java.lang.Object[] var0) { return null; }
	protected static java.util.Vector convertToVector(java.lang.Object[][] var0) { return null; }
	public int getColumnCount() { return 0; }
	public java.util.Vector getDataVector() { return null; }
	public int getRowCount() { return 0; }
	public java.lang.Object getValueAt(int var0, int var1) { return null; }
	public void insertRow(int var0, java.util.Vector var1) { }
	public void insertRow(int var0, java.lang.Object[] var1) { }
	public void moveRow(int var0, int var1, int var2) { }
	public void newDataAvailable(javax.swing.event.TableModelEvent var0) { }
	public void newRowsAdded(javax.swing.event.TableModelEvent var0) { }
	public void removeRow(int var0) { }
	public void rowsRemoved(javax.swing.event.TableModelEvent var0) { }
	public void setColumnCount(int var0) { }
	public void setColumnIdentifiers(java.util.Vector var0) { }
	public void setColumnIdentifiers(java.lang.Object[] var0) { }
	public void setDataVector(java.util.Vector var0, java.util.Vector var1) { }
	public void setDataVector(java.lang.Object[][] var0, java.lang.Object[] var1) { }
	public void setNumRows(int var0) { }
	public void setRowCount(int var0) { }
}

