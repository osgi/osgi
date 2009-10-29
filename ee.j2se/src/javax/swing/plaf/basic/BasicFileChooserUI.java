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

package javax.swing.plaf.basic;
public class BasicFileChooserUI extends javax.swing.plaf.FileChooserUI {
	public BasicFileChooserUI(javax.swing.JFileChooser var0) { }
	public void clearIconCache() { }
	protected java.awt.event.MouseListener createDoubleClickListener(javax.swing.JFileChooser var0, javax.swing.JList var1) { return null; }
	public javax.swing.event.ListSelectionListener createListSelectionListener(javax.swing.JFileChooser var0) { return null; }
	protected void createModel() { }
	public java.beans.PropertyChangeListener createPropertyChangeListener(javax.swing.JFileChooser var0) { return null; }
	public void ensureFileIsVisible(javax.swing.JFileChooser var0, java.io.File var1) { }
	public javax.swing.filechooser.FileFilter getAcceptAllFileFilter(javax.swing.JFileChooser var0) { return null; }
	public javax.swing.JPanel getAccessoryPanel() { return null; }
	protected javax.swing.JButton getApproveButton(javax.swing.JFileChooser var0) { return null; }
	public int getApproveButtonMnemonic(javax.swing.JFileChooser var0) { return 0; }
	public java.lang.String getApproveButtonText(javax.swing.JFileChooser var0) { return null; }
	public java.lang.String getApproveButtonToolTipText(javax.swing.JFileChooser var0) { return null; }
	public javax.swing.Action getApproveSelectionAction() { return null; }
	public javax.swing.Action getCancelSelectionAction() { return null; }
	public javax.swing.Action getChangeToParentDirectoryAction() { return null; }
	public java.lang.String getDialogTitle(javax.swing.JFileChooser var0) { return null; }
	protected java.io.File getDirectory() { return null; }
	public java.lang.String getDirectoryName() { return null; }
	public javax.swing.JFileChooser getFileChooser() { return null; }
	public java.lang.String getFileName() { return null; }
	public javax.swing.filechooser.FileView getFileView(javax.swing.JFileChooser var0) { return null; }
	public javax.swing.Action getGoHomeAction() { return null; }
	public javax.swing.plaf.basic.BasicDirectoryModel getModel() { return null; }
	public javax.swing.Action getNewFolderAction() { return null; }
	public javax.swing.Action getUpdateAction() { return null; }
	public void installComponents(javax.swing.JFileChooser var0) { }
	protected void installDefaults(javax.swing.JFileChooser var0) { }
	protected void installIcons(javax.swing.JFileChooser var0) { }
	protected void installListeners(javax.swing.JFileChooser var0) { }
	protected void installStrings(javax.swing.JFileChooser var0) { }
	protected boolean isDirectorySelected() { return false; }
	public void rescanCurrentDirectory(javax.swing.JFileChooser var0) { }
	protected void setDirectory(java.io.File var0) { }
	public void setDirectoryName(java.lang.String var0) { }
	protected void setDirectorySelected(boolean var0) { }
	public void setFileName(java.lang.String var0) { }
	public void uninstallComponents(javax.swing.JFileChooser var0) { }
	protected void uninstallDefaults(javax.swing.JFileChooser var0) { }
	protected void uninstallIcons(javax.swing.JFileChooser var0) { }
	protected void uninstallListeners(javax.swing.JFileChooser var0) { }
	protected void uninstallStrings(javax.swing.JFileChooser var0) { }
	protected int cancelButtonMnemonic;
	protected java.lang.String cancelButtonText;
	protected java.lang.String cancelButtonToolTipText;
	protected javax.swing.Icon computerIcon;
	protected javax.swing.Icon detailsViewIcon;
	protected javax.swing.Icon directoryIcon;
	protected int directoryOpenButtonMnemonic;
	protected java.lang.String directoryOpenButtonText;
	protected java.lang.String directoryOpenButtonToolTipText;
	protected javax.swing.Icon fileIcon;
	protected javax.swing.Icon floppyDriveIcon;
	protected javax.swing.Icon hardDriveIcon;
	protected int helpButtonMnemonic;
	protected java.lang.String helpButtonText;
	protected java.lang.String helpButtonToolTipText;
	protected javax.swing.Icon homeFolderIcon;
	protected javax.swing.Icon listViewIcon;
	protected javax.swing.Icon newFolderIcon;
	protected int openButtonMnemonic;
	protected java.lang.String openButtonText;
	protected java.lang.String openButtonToolTipText;
	protected int saveButtonMnemonic;
	protected java.lang.String saveButtonText;
	protected java.lang.String saveButtonToolTipText;
	protected javax.swing.Icon upFolderIcon;
	protected int updateButtonMnemonic;
	protected java.lang.String updateButtonText;
	protected java.lang.String updateButtonToolTipText;
	protected class AcceptAllFileFilter extends javax.swing.filechooser.FileFilter {
		public AcceptAllFileFilter() { }
		public boolean accept(java.io.File var0) { return false; }
		public java.lang.String getDescription() { return null; }
	}
	protected class ApproveSelectionAction extends javax.swing.AbstractAction {
		protected ApproveSelectionAction() { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	protected class BasicFileView extends javax.swing.filechooser.FileView {
		public BasicFileView() { }
		public void cacheIcon(java.io.File var0, javax.swing.Icon var1) { }
		public void clearIconCache() { }
		public javax.swing.Icon getCachedIcon(java.io.File var0) { return null; }
		public java.lang.Boolean isHidden(java.io.File var0) { return null; }
		protected java.util.Hashtable iconCache;
	}
	protected class CancelSelectionAction extends javax.swing.AbstractAction {
		protected CancelSelectionAction() { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	protected class ChangeToParentDirectoryAction extends javax.swing.AbstractAction {
		protected ChangeToParentDirectoryAction() { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	protected class DoubleClickListener extends java.awt.event.MouseAdapter {
		public DoubleClickListener(javax.swing.JList var0) { }
	}
	protected class GoHomeAction extends javax.swing.AbstractAction {
		protected GoHomeAction() { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	protected class NewFolderAction extends javax.swing.AbstractAction {
		protected NewFolderAction() { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	protected class SelectionListener implements javax.swing.event.ListSelectionListener {
		protected SelectionListener() { }
		public void valueChanged(javax.swing.event.ListSelectionEvent var0) { }
	}
	protected class UpdateAction extends javax.swing.AbstractAction {
		protected UpdateAction() { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
}

