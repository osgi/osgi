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
public interface TableColumnModel {
	void addColumn(javax.swing.table.TableColumn var0);
	void addColumnModelListener(javax.swing.event.TableColumnModelListener var0);
	javax.swing.table.TableColumn getColumn(int var0);
	int getColumnCount();
	int getColumnIndex(java.lang.Object var0);
	int getColumnIndexAtX(int var0);
	int getColumnMargin();
	boolean getColumnSelectionAllowed();
	java.util.Enumeration<javax.swing.table.TableColumn> getColumns();
	int getSelectedColumnCount();
	int[] getSelectedColumns();
	javax.swing.ListSelectionModel getSelectionModel();
	int getTotalColumnWidth();
	void moveColumn(int var0, int var1);
	void removeColumn(javax.swing.table.TableColumn var0);
	void removeColumnModelListener(javax.swing.event.TableColumnModelListener var0);
	void setColumnMargin(int var0);
	void setColumnSelectionAllowed(boolean var0);
	void setSelectionModel(javax.swing.ListSelectionModel var0);
}

