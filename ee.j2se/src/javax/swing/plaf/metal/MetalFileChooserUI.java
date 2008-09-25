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

package javax.swing.plaf.metal;
public class MetalFileChooserUI extends javax.swing.plaf.basic.BasicFileChooserUI {
	public MetalFileChooserUI(javax.swing.JFileChooser var0) { super((javax.swing.JFileChooser) null); }
	protected void addControlButtons() { }
	protected javax.swing.ActionMap createActionMap() { return null; }
	protected javax.swing.JPanel createDetailsView(javax.swing.JFileChooser var0) { return null; }
	protected javax.swing.plaf.metal.MetalFileChooserUI.DirectoryComboBoxModel createDirectoryComboBoxModel(javax.swing.JFileChooser var0) { return null; }
	protected javax.swing.plaf.metal.MetalFileChooserUI.DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(javax.swing.JFileChooser var0) { return null; }
	protected javax.swing.plaf.metal.MetalFileChooserUI.FilterComboBoxModel createFilterComboBoxModel() { return null; }
	protected javax.swing.plaf.metal.MetalFileChooserUI.FilterComboBoxRenderer createFilterComboBoxRenderer() { return null; }
	protected javax.swing.JPanel createList(javax.swing.JFileChooser var0) { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected javax.swing.ActionMap getActionMap() { return null; }
	protected javax.swing.JPanel getBottomPanel() { return null; }
	protected javax.swing.JPanel getButtonPanel() { return null; }
	protected void removeControlButtons() { }
	public void valueChanged(javax.swing.event.ListSelectionEvent var0) { }
	protected class DirectoryComboBoxAction extends javax.swing.AbstractAction {
		protected DirectoryComboBoxAction() { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	protected class DirectoryComboBoxModel extends javax.swing.AbstractListModel implements javax.swing.ComboBoxModel {
		public DirectoryComboBoxModel() { }
		public int getDepth(int var0) { return 0; }
		public java.lang.Object getElementAt(int var0) { return null; }
		public java.lang.Object getSelectedItem() { return null; }
		public int getSize() { return 0; }
		public void setSelectedItem(java.lang.Object var0) { }
	}
	class DirectoryComboBoxRenderer extends javax.swing.DefaultListCellRenderer {
		private DirectoryComboBoxRenderer() { } /* generated constructor to prevent compiler adding default public constructor */
	}
	protected class FileRenderer extends javax.swing.DefaultListCellRenderer {
		protected FileRenderer() { }
	}
	protected class FilterComboBoxModel extends javax.swing.AbstractListModel implements java.beans.PropertyChangeListener, javax.swing.ComboBoxModel {
		protected FilterComboBoxModel() { }
		public java.lang.Object getElementAt(int var0) { return null; }
		public java.lang.Object getSelectedItem() { return null; }
		public int getSize() { return 0; }
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
		public void setSelectedItem(java.lang.Object var0) { }
		protected javax.swing.filechooser.FileFilter[] filters;
	}
	public class FilterComboBoxRenderer extends javax.swing.DefaultListCellRenderer {
		public FilterComboBoxRenderer() { }
	}
	protected class SingleClickListener extends java.awt.event.MouseAdapter {
		public SingleClickListener(javax.swing.JList var0) { }
	}
}

