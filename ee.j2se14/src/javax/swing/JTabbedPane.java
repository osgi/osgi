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
public class JTabbedPane extends javax.swing.JComponent implements java.io.Serializable, javax.accessibility.Accessible, javax.swing.SwingConstants {
	public JTabbedPane() { }
	public JTabbedPane(int var0) { }
	public JTabbedPane(int var0, int var1) { }
	public void addChangeListener(javax.swing.event.ChangeListener var0) { }
	public void addTab(java.lang.String var0, java.awt.Component var1) { }
	public void addTab(java.lang.String var0, javax.swing.Icon var1, java.awt.Component var2) { }
	public void addTab(java.lang.String var0, javax.swing.Icon var1, java.awt.Component var2, java.lang.String var3) { }
	protected javax.swing.event.ChangeListener createChangeListener() { return null; }
	protected void fireStateChanged() { }
	public java.awt.Color getBackgroundAt(int var0) { return null; }
	public java.awt.Rectangle getBoundsAt(int var0) { return null; }
	public javax.swing.event.ChangeListener[] getChangeListeners() { return null; }
	public java.awt.Component getComponentAt(int var0) { return null; }
	public javax.swing.Icon getDisabledIconAt(int var0) { return null; }
	public int getDisplayedMnemonicIndexAt(int var0) { return 0; }
	public java.awt.Color getForegroundAt(int var0) { return null; }
	public javax.swing.Icon getIconAt(int var0) { return null; }
	public int getMnemonicAt(int var0) { return 0; }
	public javax.swing.SingleSelectionModel getModel() { return null; }
	public java.awt.Component getSelectedComponent() { return null; }
	public int getSelectedIndex() { return 0; }
	public int getTabCount() { return 0; }
	public int getTabLayoutPolicy() { return 0; }
	public int getTabPlacement() { return 0; }
	public int getTabRunCount() { return 0; }
	public java.lang.String getTitleAt(int var0) { return null; }
	public java.lang.String getToolTipTextAt(int var0) { return null; }
	public javax.swing.plaf.TabbedPaneUI getUI() { return null; }
	public int indexAtLocation(int var0, int var1) { return 0; }
	public int indexOfComponent(java.awt.Component var0) { return 0; }
	public int indexOfTab(java.lang.String var0) { return 0; }
	public int indexOfTab(javax.swing.Icon var0) { return 0; }
	public void insertTab(java.lang.String var0, javax.swing.Icon var1, java.awt.Component var2, java.lang.String var3, int var4) { }
	public boolean isEnabledAt(int var0) { return false; }
	public void removeChangeListener(javax.swing.event.ChangeListener var0) { }
	public void removeTabAt(int var0) { }
	public void setBackgroundAt(int var0, java.awt.Color var1) { }
	public void setComponentAt(int var0, java.awt.Component var1) { }
	public void setDisabledIconAt(int var0, javax.swing.Icon var1) { }
	public void setDisplayedMnemonicIndexAt(int var0, int var1) { }
	public void setEnabledAt(int var0, boolean var1) { }
	public void setForegroundAt(int var0, java.awt.Color var1) { }
	public void setIconAt(int var0, javax.swing.Icon var1) { }
	public void setMnemonicAt(int var0, int var1) { }
	public void setModel(javax.swing.SingleSelectionModel var0) { }
	public void setSelectedComponent(java.awt.Component var0) { }
	public void setSelectedIndex(int var0) { }
	public void setTabLayoutPolicy(int var0) { }
	public void setTabPlacement(int var0) { }
	public void setTitleAt(int var0, java.lang.String var1) { }
	public void setToolTipTextAt(int var0, java.lang.String var1) { }
	public void setUI(javax.swing.plaf.TabbedPaneUI var0) { }
	public final static int SCROLL_TAB_LAYOUT = 1;
	public final static int WRAP_TAB_LAYOUT = 0;
	protected javax.swing.event.ChangeEvent changeEvent;
	protected javax.swing.event.ChangeListener changeListener;
	protected javax.swing.SingleSelectionModel model;
	protected int tabPlacement;
	protected class AccessibleJTabbedPane extends javax.swing.JComponent.AccessibleJComponent implements javax.accessibility.AccessibleSelection, javax.swing.event.ChangeListener {
		public AccessibleJTabbedPane() { }
		public void addAccessibleSelection(int var0) { }
		public void clearAccessibleSelection() { }
		public javax.accessibility.Accessible getAccessibleSelection(int var0) { return null; }
		public int getAccessibleSelectionCount() { return 0; }
		public boolean isAccessibleChildSelected(int var0) { return false; }
		public void removeAccessibleSelection(int var0) { }
		public void selectAllAccessibleSelection() { }
		public void stateChanged(javax.swing.event.ChangeEvent var0) { }
	}
	protected class ModelListener implements java.io.Serializable, javax.swing.event.ChangeListener {
		protected ModelListener() { }
		public void stateChanged(javax.swing.event.ChangeEvent var0) { }
	}
}

