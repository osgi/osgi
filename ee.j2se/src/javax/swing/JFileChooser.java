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

package javax.swing;
public class JFileChooser extends javax.swing.JComponent implements javax.accessibility.Accessible {
	public JFileChooser() { }
	public JFileChooser(java.io.File var0) { }
	public JFileChooser(java.io.File var0, javax.swing.filechooser.FileSystemView var1) { }
	public JFileChooser(java.lang.String var0) { }
	public JFileChooser(java.lang.String var0, javax.swing.filechooser.FileSystemView var1) { }
	public JFileChooser(javax.swing.filechooser.FileSystemView var0) { }
	public boolean accept(java.io.File var0) { return false; }
	public void addActionListener(java.awt.event.ActionListener var0) { }
	public void addChoosableFileFilter(javax.swing.filechooser.FileFilter var0) { }
	public void approveSelection() { }
	public void cancelSelection() { }
	public void changeToParentDirectory() { }
	protected javax.swing.JDialog createDialog(java.awt.Component var0) { return null; }
	public void ensureFileIsVisible(java.io.File var0) { }
	protected void fireActionPerformed(java.lang.String var0) { }
	public javax.swing.filechooser.FileFilter getAcceptAllFileFilter() { return null; }
	public javax.swing.JComponent getAccessory() { return null; }
	public java.awt.event.ActionListener[] getActionListeners() { return null; }
	public int getApproveButtonMnemonic() { return 0; }
	public java.lang.String getApproveButtonText() { return null; }
	public java.lang.String getApproveButtonToolTipText() { return null; }
	public javax.swing.filechooser.FileFilter[] getChoosableFileFilters() { return null; }
	public boolean getControlButtonsAreShown() { return false; }
	public java.io.File getCurrentDirectory() { return null; }
	public java.lang.String getDescription(java.io.File var0) { return null; }
	public java.lang.String getDialogTitle() { return null; }
	public int getDialogType() { return 0; }
	public boolean getDragEnabled() { return false; }
	public javax.swing.filechooser.FileFilter getFileFilter() { return null; }
	public int getFileSelectionMode() { return 0; }
	public javax.swing.filechooser.FileSystemView getFileSystemView() { return null; }
	public javax.swing.filechooser.FileView getFileView() { return null; }
	public javax.swing.Icon getIcon(java.io.File var0) { return null; }
	public java.lang.String getName(java.io.File var0) { return null; }
	public java.io.File getSelectedFile() { return null; }
	public java.io.File[] getSelectedFiles() { return null; }
	public java.lang.String getTypeDescription(java.io.File var0) { return null; }
	public javax.swing.plaf.FileChooserUI getUI() { return null; }
	public boolean isAcceptAllFileFilterUsed() { return false; }
	public boolean isDirectorySelectionEnabled() { return false; }
	public boolean isFileHidingEnabled() { return false; }
	public boolean isFileSelectionEnabled() { return false; }
	public boolean isMultiSelectionEnabled() { return false; }
	public boolean isTraversable(java.io.File var0) { return false; }
	public void removeActionListener(java.awt.event.ActionListener var0) { }
	public boolean removeChoosableFileFilter(javax.swing.filechooser.FileFilter var0) { return false; }
	public void rescanCurrentDirectory() { }
	public void resetChoosableFileFilters() { }
	public void setAcceptAllFileFilterUsed(boolean var0) { }
	public void setAccessory(javax.swing.JComponent var0) { }
	public void setApproveButtonMnemonic(char var0) { }
	public void setApproveButtonMnemonic(int var0) { }
	public void setApproveButtonText(java.lang.String var0) { }
	public void setApproveButtonToolTipText(java.lang.String var0) { }
	public void setControlButtonsAreShown(boolean var0) { }
	public void setCurrentDirectory(java.io.File var0) { }
	public void setDialogTitle(java.lang.String var0) { }
	public void setDialogType(int var0) { }
	public void setDragEnabled(boolean var0) { }
	public void setFileFilter(javax.swing.filechooser.FileFilter var0) { }
	public void setFileHidingEnabled(boolean var0) { }
	public void setFileSelectionMode(int var0) { }
	public void setFileSystemView(javax.swing.filechooser.FileSystemView var0) { }
	public void setFileView(javax.swing.filechooser.FileView var0) { }
	public void setMultiSelectionEnabled(boolean var0) { }
	public void setSelectedFile(java.io.File var0) { }
	public void setSelectedFiles(java.io.File[] var0) { }
	protected void setup(javax.swing.filechooser.FileSystemView var0) { }
	public int showDialog(java.awt.Component var0, java.lang.String var1) { return 0; }
	public int showOpenDialog(java.awt.Component var0) { return 0; }
	public int showSaveDialog(java.awt.Component var0) { return 0; }
	public final static java.lang.String ACCEPT_ALL_FILE_FILTER_USED_CHANGED_PROPERTY = "acceptAllFileFilterUsedChanged";
	public final static java.lang.String ACCESSORY_CHANGED_PROPERTY = "AccessoryChangedProperty";
	public final static java.lang.String APPROVE_BUTTON_MNEMONIC_CHANGED_PROPERTY = "ApproveButtonMnemonicChangedProperty";
	public final static java.lang.String APPROVE_BUTTON_TEXT_CHANGED_PROPERTY = "ApproveButtonTextChangedProperty";
	public final static java.lang.String APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY = "ApproveButtonToolTipTextChangedProperty";
	public final static int APPROVE_OPTION = 0;
	public final static java.lang.String APPROVE_SELECTION = "ApproveSelection";
	public final static int CANCEL_OPTION = 1;
	public final static java.lang.String CANCEL_SELECTION = "CancelSelection";
	public final static java.lang.String CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY = "ChoosableFileFilterChangedProperty";
	public final static java.lang.String CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY = "ControlButtonsAreShownChangedProperty";
	public final static int CUSTOM_DIALOG = 2;
	public final static java.lang.String DIALOG_TITLE_CHANGED_PROPERTY = "DialogTitleChangedProperty";
	public final static java.lang.String DIALOG_TYPE_CHANGED_PROPERTY = "DialogTypeChangedProperty";
	public final static int DIRECTORIES_ONLY = 1;
	public final static java.lang.String DIRECTORY_CHANGED_PROPERTY = "directoryChanged";
	public final static int ERROR_OPTION = -1;
	public final static int FILES_AND_DIRECTORIES = 2;
	public final static int FILES_ONLY = 0;
	public final static java.lang.String FILE_FILTER_CHANGED_PROPERTY = "fileFilterChanged";
	public final static java.lang.String FILE_HIDING_CHANGED_PROPERTY = "FileHidingChanged";
	public final static java.lang.String FILE_SELECTION_MODE_CHANGED_PROPERTY = "fileSelectionChanged";
	public final static java.lang.String FILE_SYSTEM_VIEW_CHANGED_PROPERTY = "FileSystemViewChanged";
	public final static java.lang.String FILE_VIEW_CHANGED_PROPERTY = "fileViewChanged";
	public final static java.lang.String MULTI_SELECTION_ENABLED_CHANGED_PROPERTY = "MultiSelectionEnabledChangedProperty";
	public final static int OPEN_DIALOG = 0;
	public final static int SAVE_DIALOG = 1;
	public final static java.lang.String SELECTED_FILES_CHANGED_PROPERTY = "SelectedFilesChangedProperty";
	public final static java.lang.String SELECTED_FILE_CHANGED_PROPERTY = "SelectedFileChangedProperty";
	protected javax.accessibility.AccessibleContext accessibleContext;
	protected class AccessibleJFileChooser extends javax.swing.JComponent.AccessibleJComponent {
		protected AccessibleJFileChooser() { }
	}
}

