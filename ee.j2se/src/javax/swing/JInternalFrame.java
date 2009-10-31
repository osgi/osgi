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
public class JInternalFrame extends javax.swing.JComponent implements javax.accessibility.Accessible, javax.swing.RootPaneContainer, javax.swing.WindowConstants {
	protected class AccessibleJInternalFrame extends javax.swing.JComponent.AccessibleJComponent implements javax.accessibility.AccessibleValue {
		protected AccessibleJInternalFrame() { } 
		public java.lang.Number getCurrentAccessibleValue() { return null; }
		public java.lang.Number getMaximumAccessibleValue() { return null; }
		public java.lang.Number getMinimumAccessibleValue() { return null; }
		public boolean setCurrentAccessibleValue(java.lang.Number var0) { return false; }
	}
	public static class JDesktopIcon extends javax.swing.JComponent implements javax.accessibility.Accessible {
		protected class AccessibleJDesktopIcon extends javax.swing.JComponent.AccessibleJComponent implements javax.accessibility.AccessibleValue {
			protected AccessibleJDesktopIcon() { } 
			public java.lang.Number getCurrentAccessibleValue() { return null; }
			public java.lang.Number getMaximumAccessibleValue() { return null; }
			public java.lang.Number getMinimumAccessibleValue() { return null; }
			public boolean setCurrentAccessibleValue(java.lang.Number var0) { return false; }
		}
		public JDesktopIcon(javax.swing.JInternalFrame var0) { } 
		public javax.swing.JDesktopPane getDesktopPane() { return null; }
		public javax.swing.JInternalFrame getInternalFrame() { return null; }
		public javax.swing.plaf.DesktopIconUI getUI() { return null; }
		public void setInternalFrame(javax.swing.JInternalFrame var0) { }
		public void setUI(javax.swing.plaf.DesktopIconUI var0) { }
	}
	public final static java.lang.String CONTENT_PANE_PROPERTY = "contentPane";
	public final static java.lang.String FRAME_ICON_PROPERTY = "frameIcon";
	public final static java.lang.String GLASS_PANE_PROPERTY = "glassPane";
	public final static java.lang.String IS_CLOSED_PROPERTY = "closed";
	public final static java.lang.String IS_ICON_PROPERTY = "icon";
	public final static java.lang.String IS_MAXIMUM_PROPERTY = "maximum";
	public final static java.lang.String IS_SELECTED_PROPERTY = "selected";
	public final static java.lang.String LAYERED_PANE_PROPERTY = "layeredPane";
	public final static java.lang.String MENU_BAR_PROPERTY = "JMenuBar";
	public final static java.lang.String ROOT_PANE_PROPERTY = "rootPane";
	public final static java.lang.String TITLE_PROPERTY = "title";
	protected boolean closable;
	protected javax.swing.JInternalFrame.JDesktopIcon desktopIcon;
	protected javax.swing.Icon frameIcon;
	protected boolean iconable;
	protected boolean isClosed;
	protected boolean isIcon;
	protected boolean isMaximum;
	protected boolean isSelected;
	protected boolean maximizable;
	protected boolean resizable;
	protected javax.swing.JRootPane rootPane;
	protected boolean rootPaneCheckingEnabled;
	protected java.lang.String title;
	public JInternalFrame() { } 
	public JInternalFrame(java.lang.String var0) { } 
	public JInternalFrame(java.lang.String var0, boolean var1) { } 
	public JInternalFrame(java.lang.String var0, boolean var1, boolean var2) { } 
	public JInternalFrame(java.lang.String var0, boolean var1, boolean var2, boolean var3) { } 
	public JInternalFrame(java.lang.String var0, boolean var1, boolean var2, boolean var3, boolean var4) { } 
	public void addInternalFrameListener(javax.swing.event.InternalFrameListener var0) { }
	protected javax.swing.JRootPane createRootPane() { return null; }
	public void dispose() { }
	public void doDefaultCloseAction() { }
	protected void fireInternalFrameEvent(int var0) { }
	public java.awt.Container getContentPane() { return null; }
	public int getDefaultCloseOperation() { return 0; }
	public javax.swing.JInternalFrame.JDesktopIcon getDesktopIcon() { return null; }
	public javax.swing.JDesktopPane getDesktopPane() { return null; }
	public final java.awt.Container getFocusCycleRootAncestor() { return null; }
	public java.awt.Component getFocusOwner() { return null; }
	public javax.swing.Icon getFrameIcon() { return null; }
	public java.awt.Component getGlassPane() { return null; }
	public javax.swing.event.InternalFrameListener[] getInternalFrameListeners() { return null; }
	public javax.swing.JMenuBar getJMenuBar() { return null; }
	public java.awt.Cursor getLastCursor() { return null; }
	public int getLayer() { return 0; }
	public javax.swing.JLayeredPane getLayeredPane() { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	public javax.swing.JMenuBar getMenuBar() { return null; }
	public java.awt.Component getMostRecentFocusOwner() { return null; }
	public java.awt.Rectangle getNormalBounds() { return null; }
	public java.lang.String getTitle() { return null; }
	public javax.swing.plaf.InternalFrameUI getUI() { return null; }
	public final java.lang.String getWarningString() { return null; }
	public boolean isClosable() { return false; }
	public boolean isClosed() { return false; }
	public final boolean isFocusCycleRoot() { return false; }
	public boolean isIcon() { return false; }
	public boolean isIconifiable() { return false; }
	public boolean isMaximizable() { return false; }
	public boolean isMaximum() { return false; }
	public boolean isResizable() { return false; }
	protected boolean isRootPaneCheckingEnabled() { return false; }
	public boolean isSelected() { return false; }
	public void moveToBack() { }
	public void moveToFront() { }
	public void pack() { }
	public void removeInternalFrameListener(javax.swing.event.InternalFrameListener var0) { }
	public void restoreSubcomponentFocus() { }
	public void setClosable(boolean var0) { }
	public void setClosed(boolean var0) throws java.beans.PropertyVetoException { }
	public void setContentPane(java.awt.Container var0) { }
	public void setDefaultCloseOperation(int var0) { }
	public void setDesktopIcon(javax.swing.JInternalFrame.JDesktopIcon var0) { }
	public final void setFocusCycleRoot(boolean var0) { }
	public void setFrameIcon(javax.swing.Icon var0) { }
	public void setGlassPane(java.awt.Component var0) { }
	public void setIcon(boolean var0) throws java.beans.PropertyVetoException { }
	public void setIconifiable(boolean var0) { }
	public void setJMenuBar(javax.swing.JMenuBar var0) { }
	public void setLayer(int var0) { }
	public void setLayer(java.lang.Integer var0) { }
	public void setLayeredPane(javax.swing.JLayeredPane var0) { }
	public void setMaximizable(boolean var0) { }
	public void setMaximum(boolean var0) throws java.beans.PropertyVetoException { }
	/** @deprecated */
	@java.lang.Deprecated
	public void setMenuBar(javax.swing.JMenuBar var0) { }
	public void setNormalBounds(java.awt.Rectangle var0) { }
	public void setResizable(boolean var0) { }
	protected void setRootPane(javax.swing.JRootPane var0) { }
	protected void setRootPaneCheckingEnabled(boolean var0) { }
	public void setSelected(boolean var0) throws java.beans.PropertyVetoException { }
	public void setTitle(java.lang.String var0) { }
	public void setUI(javax.swing.plaf.InternalFrameUI var0) { }
	public void toBack() { }
	public void toFront() { }
}

