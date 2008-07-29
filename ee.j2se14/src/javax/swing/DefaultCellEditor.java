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

package javax.swing;
public class DefaultCellEditor extends javax.swing.AbstractCellEditor implements javax.swing.table.TableCellEditor, javax.swing.tree.TreeCellEditor {
	public DefaultCellEditor(javax.swing.JCheckBox var0) { }
	public DefaultCellEditor(javax.swing.JComboBox var0) { }
	public DefaultCellEditor(javax.swing.JTextField var0) { }
	public java.lang.Object getCellEditorValue() { return null; }
	public int getClickCountToStart() { return 0; }
	public java.awt.Component getComponent() { return null; }
	public java.awt.Component getTableCellEditorComponent(javax.swing.JTable var0, java.lang.Object var1, boolean var2, int var3, int var4) { return null; }
	public java.awt.Component getTreeCellEditorComponent(javax.swing.JTree var0, java.lang.Object var1, boolean var2, boolean var3, boolean var4, int var5) { return null; }
	public void setClickCountToStart(int var0) { }
	protected int clickCountToStart;
	protected javax.swing.DefaultCellEditor.EditorDelegate delegate;
	protected javax.swing.JComponent editorComponent;
	protected class EditorDelegate implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.io.Serializable {
		protected EditorDelegate() { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
		public void cancelCellEditing() { }
		public java.lang.Object getCellEditorValue() { return null; }
		public boolean isCellEditable(java.util.EventObject var0) { return false; }
		public void itemStateChanged(java.awt.event.ItemEvent var0) { }
		public void setValue(java.lang.Object var0) { }
		public boolean shouldSelectCell(java.util.EventObject var0) { return false; }
		public boolean startCellEditing(java.util.EventObject var0) { return false; }
		public boolean stopCellEditing() { return false; }
		protected java.lang.Object value;
	}
}

