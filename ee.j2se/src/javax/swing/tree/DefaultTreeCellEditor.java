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

package javax.swing.tree;
public class DefaultTreeCellEditor implements java.awt.event.ActionListener, javax.swing.event.TreeSelectionListener, javax.swing.tree.TreeCellEditor {
	public DefaultTreeCellEditor(javax.swing.JTree var0, javax.swing.tree.DefaultTreeCellRenderer var1) { }
	public DefaultTreeCellEditor(javax.swing.JTree var0, javax.swing.tree.DefaultTreeCellRenderer var1, javax.swing.tree.TreeCellEditor var2) { }
	public void actionPerformed(java.awt.event.ActionEvent var0) { }
	public void addCellEditorListener(javax.swing.event.CellEditorListener var0) { }
	protected boolean canEditImmediately(java.util.EventObject var0) { return false; }
	public void cancelCellEditing() { }
	protected java.awt.Container createContainer() { return null; }
	protected javax.swing.tree.TreeCellEditor createTreeCellEditor() { return null; }
	protected void determineOffset(javax.swing.JTree var0, java.lang.Object var1, boolean var2, boolean var3, boolean var4, int var5) { }
	public java.awt.Color getBorderSelectionColor() { return null; }
	public javax.swing.event.CellEditorListener[] getCellEditorListeners() { return null; }
	public java.lang.Object getCellEditorValue() { return null; }
	public java.awt.Font getFont() { return null; }
	public java.awt.Component getTreeCellEditorComponent(javax.swing.JTree var0, java.lang.Object var1, boolean var2, boolean var3, boolean var4, int var5) { return null; }
	protected boolean inHitRegion(int var0, int var1) { return false; }
	public boolean isCellEditable(java.util.EventObject var0) { return false; }
	protected void prepareForEditing() { }
	public void removeCellEditorListener(javax.swing.event.CellEditorListener var0) { }
	public void setBorderSelectionColor(java.awt.Color var0) { }
	public void setFont(java.awt.Font var0) { }
	protected void setTree(javax.swing.JTree var0) { }
	public boolean shouldSelectCell(java.util.EventObject var0) { return false; }
	protected boolean shouldStartEditingTimer(java.util.EventObject var0) { return false; }
	protected void startEditingTimer() { }
	public boolean stopCellEditing() { return false; }
	public void valueChanged(javax.swing.event.TreeSelectionEvent var0) { }
	protected java.awt.Color borderSelectionColor;
	protected boolean canEdit;
	protected java.awt.Component editingComponent;
	protected java.awt.Container editingContainer;
	protected javax.swing.Icon editingIcon;
	protected java.awt.Font font;
	protected javax.swing.tree.TreePath lastPath;
	protected int lastRow;
	protected int offset;
	protected javax.swing.tree.TreeCellEditor realEditor;
	protected javax.swing.tree.DefaultTreeCellRenderer renderer;
	protected javax.swing.Timer timer;
	protected javax.swing.JTree tree;
	public class DefaultTextField extends javax.swing.JTextField {
		public DefaultTextField(javax.swing.border.Border var0) { }
		protected javax.swing.border.Border border;
	}
	public class EditorContainer extends java.awt.Container {
		public EditorContainer() { }
		public void EditorContainer() { }
	}
}

