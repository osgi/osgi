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
public class JMenu extends javax.swing.JMenuItem implements javax.accessibility.Accessible, javax.swing.MenuElement {
	public JMenu() { }
	public JMenu(java.lang.String var0) { }
	public JMenu(java.lang.String var0, boolean var1) { }
	public JMenu(javax.swing.Action var0) { }
	public javax.swing.JMenuItem add(java.lang.String var0) { return null; }
	public javax.swing.JMenuItem add(javax.swing.Action var0) { return null; }
	public javax.swing.JMenuItem add(javax.swing.JMenuItem var0) { return null; }
	public void addMenuListener(javax.swing.event.MenuListener var0) { }
	public void addSeparator() { }
	protected java.beans.PropertyChangeListener createActionChangeListener(javax.swing.JMenuItem var0) { return null; }
	protected javax.swing.JMenuItem createActionComponent(javax.swing.Action var0) { return null; }
	protected javax.swing.JMenu.WinListener createWinListener(javax.swing.JPopupMenu var0) { return null; }
	protected void fireMenuCanceled() { }
	protected void fireMenuDeselected() { }
	protected void fireMenuSelected() { }
	public int getDelay() { return 0; }
	public javax.swing.JMenuItem getItem(int var0) { return null; }
	public int getItemCount() { return 0; }
	public java.awt.Component getMenuComponent(int var0) { return null; }
	public int getMenuComponentCount() { return 0; }
	public java.awt.Component[] getMenuComponents() { return null; }
	public javax.swing.event.MenuListener[] getMenuListeners() { return null; }
	public javax.swing.JPopupMenu getPopupMenu() { return null; }
	protected java.awt.Point getPopupMenuOrigin() { return null; }
	public void insert(java.lang.String var0, int var1) { }
	public javax.swing.JMenuItem insert(javax.swing.Action var0, int var1) { return null; }
	public javax.swing.JMenuItem insert(javax.swing.JMenuItem var0, int var1) { return null; }
	public void insertSeparator(int var0) { }
	public boolean isMenuComponent(java.awt.Component var0) { return false; }
	public boolean isPopupMenuVisible() { return false; }
	public boolean isTearOff() { return false; }
	public boolean isTopLevelMenu() { return false; }
	public void remove(javax.swing.JMenuItem var0) { }
	public void removeMenuListener(javax.swing.event.MenuListener var0) { }
	public void setDelay(int var0) { }
	public void setMenuLocation(int var0, int var1) { }
	public void setPopupMenuVisible(boolean var0) { }
	protected javax.swing.JMenu.WinListener popupListener;
	protected class AccessibleJMenu extends javax.swing.JMenuItem.AccessibleJMenuItem implements javax.accessibility.AccessibleSelection {
		protected AccessibleJMenu() { }
		public void addAccessibleSelection(int var0) { }
		public void clearAccessibleSelection() { }
		public javax.accessibility.Accessible getAccessibleSelection(int var0) { return null; }
		public int getAccessibleSelectionCount() { return 0; }
		public boolean isAccessibleChildSelected(int var0) { return false; }
		public void removeAccessibleSelection(int var0) { }
		public void selectAllAccessibleSelection() { }
	}
	protected class WinListener extends java.awt.event.WindowAdapter implements java.io.Serializable {
		public WinListener(javax.swing.JPopupMenu var0) { }
	}
}

