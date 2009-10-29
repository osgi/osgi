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

package javax.swing;
public abstract interface CellEditor {
	public abstract void addCellEditorListener(javax.swing.event.CellEditorListener var0);
	public abstract void cancelCellEditing();
	public abstract java.lang.Object getCellEditorValue();
	public abstract boolean isCellEditable(java.util.EventObject var0);
	public abstract void removeCellEditorListener(javax.swing.event.CellEditorListener var0);
	public abstract boolean shouldSelectCell(java.util.EventObject var0);
	public abstract boolean stopCellEditing();
}

