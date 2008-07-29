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

package javax.swing.table;
public abstract interface TableModel {
	public abstract void addTableModelListener(javax.swing.event.TableModelListener var0);
	public abstract java.lang.Class getColumnClass(int var0);
	public abstract int getColumnCount();
	public abstract java.lang.String getColumnName(int var0);
	public abstract int getRowCount();
	public abstract java.lang.Object getValueAt(int var0, int var1);
	public abstract boolean isCellEditable(int var0, int var1);
	public abstract void removeTableModelListener(javax.swing.event.TableModelListener var0);
	public abstract void setValueAt(java.lang.Object var0, int var1, int var2);
}

