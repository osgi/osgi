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

package javax.swing;
public class JComboBox extends javax.swing.JComponent implements java.awt.ItemSelectable, java.awt.event.ActionListener, javax.accessibility.Accessible, javax.swing.event.ListDataListener {
	protected class AccessibleJComboBox extends javax.swing.JComponent.AccessibleJComponent implements javax.accessibility.AccessibleAction, javax.accessibility.AccessibleSelection {
		public AccessibleJComboBox() { } 
		public void addAccessibleSelection(int var0) { }
		public void clearAccessibleSelection() { }
		public boolean doAccessibleAction(int var0) { return false; }
		public int getAccessibleActionCount() { return 0; }
		public java.lang.String getAccessibleActionDescription(int var0) { return null; }
		public javax.accessibility.Accessible getAccessibleSelection(int var0) { return null; }
		public int getAccessibleSelectionCount() { return 0; }
		public boolean isAccessibleChildSelected(int var0) { return false; }
		public void removeAccessibleSelection(int var0) { }
		public void selectAllAccessibleSelection() { }
	}
	public interface KeySelectionManager {
		int selectionForKey(char var0, javax.swing.ComboBoxModel var1);
	}
	protected java.lang.String actionCommand;
	protected javax.swing.ComboBoxModel dataModel;
	protected javax.swing.ComboBoxEditor editor;
	protected boolean isEditable;
	protected javax.swing.JComboBox.KeySelectionManager keySelectionManager;
	protected boolean lightWeightPopupEnabled;
	protected int maximumRowCount;
	protected javax.swing.ListCellRenderer renderer;
	protected java.lang.Object selectedItemReminder;
	public JComboBox() { } 
	public JComboBox(java.util.Vector<?> var0) { } 
	public JComboBox(javax.swing.ComboBoxModel var0) { } 
	public JComboBox(java.lang.Object[] var0) { } 
	public void actionPerformed(java.awt.event.ActionEvent var0) { }
	protected void actionPropertyChanged(javax.swing.Action var0, java.lang.String var1) { }
	public void addActionListener(java.awt.event.ActionListener var0) { }
	public void addItem(java.lang.Object var0) { }
	public void addItemListener(java.awt.event.ItemListener var0) { }
	public void addPopupMenuListener(javax.swing.event.PopupMenuListener var0) { }
	public void configureEditor(javax.swing.ComboBoxEditor var0, java.lang.Object var1) { }
	protected void configurePropertiesFromAction(javax.swing.Action var0) { }
	public void contentsChanged(javax.swing.event.ListDataEvent var0) { }
	protected java.beans.PropertyChangeListener createActionPropertyChangeListener(javax.swing.Action var0) { return null; }
	protected javax.swing.JComboBox.KeySelectionManager createDefaultKeySelectionManager() { return null; }
	protected void fireActionEvent() { }
	protected void fireItemStateChanged(java.awt.event.ItemEvent var0) { }
	public void firePopupMenuCanceled() { }
	public void firePopupMenuWillBecomeInvisible() { }
	public void firePopupMenuWillBecomeVisible() { }
	public javax.swing.Action getAction() { return null; }
	public java.lang.String getActionCommand() { return null; }
	public java.awt.event.ActionListener[] getActionListeners() { return null; }
	public javax.swing.ComboBoxEditor getEditor() { return null; }
	public java.lang.Object getItemAt(int var0) { return null; }
	public int getItemCount() { return 0; }
	public java.awt.event.ItemListener[] getItemListeners() { return null; }
	public javax.swing.JComboBox.KeySelectionManager getKeySelectionManager() { return null; }
	public int getMaximumRowCount() { return 0; }
	public javax.swing.ComboBoxModel getModel() { return null; }
	public javax.swing.event.PopupMenuListener[] getPopupMenuListeners() { return null; }
	public java.lang.Object getPrototypeDisplayValue() { return null; }
	public javax.swing.ListCellRenderer getRenderer() { return null; }
	public int getSelectedIndex() { return 0; }
	public java.lang.Object getSelectedItem() { return null; }
	public java.lang.Object[] getSelectedObjects() { return null; }
	public javax.swing.plaf.ComboBoxUI getUI() { return null; }
	public void hidePopup() { }
	public void insertItemAt(java.lang.Object var0, int var1) { }
	protected void installAncestorListener() { }
	public void intervalAdded(javax.swing.event.ListDataEvent var0) { }
	public void intervalRemoved(javax.swing.event.ListDataEvent var0) { }
	public boolean isEditable() { return false; }
	public boolean isLightWeightPopupEnabled() { return false; }
	public boolean isPopupVisible() { return false; }
	public void processKeyEvent(java.awt.event.KeyEvent var0) { }
	public void removeActionListener(java.awt.event.ActionListener var0) { }
	public void removeAllItems() { }
	public void removeItem(java.lang.Object var0) { }
	public void removeItemAt(int var0) { }
	public void removeItemListener(java.awt.event.ItemListener var0) { }
	public void removePopupMenuListener(javax.swing.event.PopupMenuListener var0) { }
	public boolean selectWithKeyChar(char var0) { return false; }
	protected void selectedItemChanged() { }
	public void setAction(javax.swing.Action var0) { }
	public void setActionCommand(java.lang.String var0) { }
	public void setEditable(boolean var0) { }
	public void setEditor(javax.swing.ComboBoxEditor var0) { }
	public void setKeySelectionManager(javax.swing.JComboBox.KeySelectionManager var0) { }
	public void setLightWeightPopupEnabled(boolean var0) { }
	public void setMaximumRowCount(int var0) { }
	public void setModel(javax.swing.ComboBoxModel var0) { }
	public void setPopupVisible(boolean var0) { }
	public void setPrototypeDisplayValue(java.lang.Object var0) { }
	public void setRenderer(javax.swing.ListCellRenderer var0) { }
	public void setSelectedIndex(int var0) { }
	public void setSelectedItem(java.lang.Object var0) { }
	public void setUI(javax.swing.plaf.ComboBoxUI var0) { }
	public void showPopup() { }
}

