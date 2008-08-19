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
public class JMenuBar extends javax.swing.JComponent implements javax.accessibility.Accessible, javax.swing.MenuElement {
	public JMenuBar() { }
	public javax.swing.JMenu add(javax.swing.JMenu var0) { return null; }
	public java.awt.Component getComponent() { return null; }
	/** @deprecated */ public java.awt.Component getComponentAtIndex(int var0) { return null; }
	public int getComponentIndex(java.awt.Component var0) { return 0; }
	public javax.swing.JMenu getHelpMenu() { return null; }
	public java.awt.Insets getMargin() { return null; }
	public javax.swing.JMenu getMenu(int var0) { return null; }
	public int getMenuCount() { return 0; }
	public javax.swing.SingleSelectionModel getSelectionModel() { return null; }
	public javax.swing.MenuElement[] getSubElements() { return null; }
	public javax.swing.plaf.MenuBarUI getUI() { return null; }
	public boolean isBorderPainted() { return false; }
	public boolean isSelected() { return false; }
	public void menuSelectionChanged(boolean var0) { }
	public void processKeyEvent(java.awt.event.KeyEvent var0, javax.swing.MenuElement[] var1, javax.swing.MenuSelectionManager var2) { }
	public void processMouseEvent(java.awt.event.MouseEvent var0, javax.swing.MenuElement[] var1, javax.swing.MenuSelectionManager var2) { }
	public void setBorderPainted(boolean var0) { }
	public void setHelpMenu(javax.swing.JMenu var0) { }
	public void setMargin(java.awt.Insets var0) { }
	public void setSelected(java.awt.Component var0) { }
	public void setSelectionModel(javax.swing.SingleSelectionModel var0) { }
	public void setUI(javax.swing.plaf.MenuBarUI var0) { }
	protected class AccessibleJMenuBar extends javax.swing.JComponent.AccessibleJComponent implements javax.accessibility.AccessibleSelection {
		protected AccessibleJMenuBar() { }
		public void addAccessibleSelection(int var0) { }
		public void clearAccessibleSelection() { }
		public javax.accessibility.Accessible getAccessibleSelection(int var0) { return null; }
		public int getAccessibleSelectionCount() { return 0; }
		public boolean isAccessibleChildSelected(int var0) { return false; }
		public void removeAccessibleSelection(int var0) { }
		public void selectAllAccessibleSelection() { }
	}
}

