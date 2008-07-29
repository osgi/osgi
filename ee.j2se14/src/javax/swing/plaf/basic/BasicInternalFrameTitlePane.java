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

package javax.swing.plaf.basic;
public class BasicInternalFrameTitlePane extends javax.swing.JComponent {
	public BasicInternalFrameTitlePane(javax.swing.JInternalFrame var0) { }
	protected void addSubComponents() { }
	protected void addSystemMenuItems(javax.swing.JMenu var0) { }
	protected void assembleSystemMenu() { }
	protected void createActions() { }
	protected void createButtons() { }
	protected java.awt.LayoutManager createLayout() { return null; }
	protected java.beans.PropertyChangeListener createPropertyChangeListener() { return null; }
	protected javax.swing.JMenu createSystemMenu() { return null; }
	protected javax.swing.JMenuBar createSystemMenuBar() { return null; }
	protected void enableActions() { }
	protected java.lang.String getTitle(java.lang.String var0, java.awt.FontMetrics var1, int var2) { return null; }
	protected void installDefaults() { }
	protected void installListeners() { }
	protected void installTitlePane() { }
	public void paintComponent(java.awt.Graphics var0) { }
	protected void paintTitleBackground(java.awt.Graphics var0) { }
	protected void postClosingEvent(javax.swing.JInternalFrame var0) { }
	protected void setButtonIcons() { }
	protected void showSystemMenu() { }
	protected void uninstallDefaults() { }
	protected void uninstallListeners() { }
	protected final static java.lang.String CLOSE_CMD; static { CLOSE_CMD = null; }
	protected final static java.lang.String ICONIFY_CMD; static { ICONIFY_CMD = null; }
	protected final static java.lang.String MAXIMIZE_CMD; static { MAXIMIZE_CMD = null; }
	protected final static java.lang.String MOVE_CMD; static { MOVE_CMD = null; }
	protected final static java.lang.String RESTORE_CMD; static { RESTORE_CMD = null; }
	protected final static java.lang.String SIZE_CMD; static { SIZE_CMD = null; }
	protected javax.swing.Action closeAction;
	protected javax.swing.JButton closeButton;
	protected javax.swing.Icon closeIcon;
	protected javax.swing.JInternalFrame frame;
	protected javax.swing.JButton iconButton;
	protected javax.swing.Icon iconIcon;
	protected javax.swing.Action iconifyAction;
	protected javax.swing.JButton maxButton;
	protected javax.swing.Icon maxIcon;
	protected javax.swing.Action maximizeAction;
	protected javax.swing.JMenuBar menuBar;
	protected javax.swing.Icon minIcon;
	protected javax.swing.Action moveAction;
	protected java.awt.Color notSelectedTextColor;
	protected java.awt.Color notSelectedTitleColor;
	protected java.beans.PropertyChangeListener propertyChangeListener;
	protected javax.swing.Action restoreAction;
	protected java.awt.Color selectedTextColor;
	protected java.awt.Color selectedTitleColor;
	protected javax.swing.Action sizeAction;
	protected javax.swing.JMenu windowMenu;
	public class CloseAction extends javax.swing.AbstractAction {
		public CloseAction() { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public class IconifyAction extends javax.swing.AbstractAction {
		public IconifyAction() { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public class MaximizeAction extends javax.swing.AbstractAction {
		public MaximizeAction() { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public class MoveAction extends javax.swing.AbstractAction {
		public MoveAction() { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public class PropertyChangeHandler implements java.beans.PropertyChangeListener {
		public PropertyChangeHandler() { }
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	}
	public class RestoreAction extends javax.swing.AbstractAction {
		public RestoreAction() { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public class SizeAction extends javax.swing.AbstractAction {
		public SizeAction() { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public class SystemMenuBar extends javax.swing.JMenuBar {
		public SystemMenuBar() { }
	}
	public class TitlePaneLayout implements java.awt.LayoutManager {
		public TitlePaneLayout() { }
		public void addLayoutComponent(java.lang.String var0, java.awt.Component var1) { }
		public void layoutContainer(java.awt.Container var0) { }
		public java.awt.Dimension minimumLayoutSize(java.awt.Container var0) { return null; }
		public java.awt.Dimension preferredLayoutSize(java.awt.Container var0) { return null; }
		public void removeLayoutComponent(java.awt.Component var0) { }
	}
}

