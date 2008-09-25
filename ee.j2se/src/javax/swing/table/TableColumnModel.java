/*
 * $Revision$
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

package javax.swing.table;
public abstract interface TableColumnModel {
	public abstract void addColumn(javax.swing.table.TableColumn var0);
	public abstract void addColumnModelListener(javax.swing.event.TableColumnModelListener var0);
	public abstract javax.swing.table.TableColumn getColumn(int var0);
	public abstract int getColumnCount();
	public abstract int getColumnIndex(java.lang.Object var0);
	public abstract int getColumnIndexAtX(int var0);
	public abstract int getColumnMargin();
	public abstract boolean getColumnSelectionAllowed();
	public abstract java.util.Enumeration getColumns();
	public abstract int getSelectedColumnCount();
	public abstract int[] getSelectedColumns();
	public abstract javax.swing.ListSelectionModel getSelectionModel();
	public abstract int getTotalColumnWidth();
	public abstract void moveColumn(int var0, int var1);
	public abstract void removeColumn(javax.swing.table.TableColumn var0);
	public abstract void removeColumnModelListener(javax.swing.event.TableColumnModelListener var0);
	public abstract void setColumnMargin(int var0);
	public abstract void setColumnSelectionAllowed(boolean var0);
	public abstract void setSelectionModel(javax.swing.ListSelectionModel var0);
}

