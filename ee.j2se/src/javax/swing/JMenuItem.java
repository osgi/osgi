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
public class JMenuItem extends javax.swing.AbstractButton implements javax.accessibility.Accessible, javax.swing.MenuElement {
	public JMenuItem() { }
	public JMenuItem(java.lang.String var0) { }
	public JMenuItem(java.lang.String var0, int var1) { }
	public JMenuItem(java.lang.String var0, javax.swing.Icon var1) { }
	public JMenuItem(javax.swing.Action var0) { }
	public JMenuItem(javax.swing.Icon var0) { }
	public void addMenuDragMouseListener(javax.swing.event.MenuDragMouseListener var0) { }
	public void addMenuKeyListener(javax.swing.event.MenuKeyListener var0) { }
	protected void fireMenuDragMouseDragged(javax.swing.event.MenuDragMouseEvent var0) { }
	protected void fireMenuDragMouseEntered(javax.swing.event.MenuDragMouseEvent var0) { }
	protected void fireMenuDragMouseExited(javax.swing.event.MenuDragMouseEvent var0) { }
	protected void fireMenuDragMouseReleased(javax.swing.event.MenuDragMouseEvent var0) { }
	protected void fireMenuKeyPressed(javax.swing.event.MenuKeyEvent var0) { }
	protected void fireMenuKeyReleased(javax.swing.event.MenuKeyEvent var0) { }
	protected void fireMenuKeyTyped(javax.swing.event.MenuKeyEvent var0) { }
	public javax.swing.KeyStroke getAccelerator() { return null; }
	public java.awt.Component getComponent() { return null; }
	public javax.swing.event.MenuDragMouseListener[] getMenuDragMouseListeners() { return null; }
	public javax.swing.event.MenuKeyListener[] getMenuKeyListeners() { return null; }
	public javax.swing.MenuElement[] getSubElements() { return null; }
	public boolean isArmed() { return false; }
	public void menuSelectionChanged(boolean var0) { }
	public void processKeyEvent(java.awt.event.KeyEvent var0, javax.swing.MenuElement[] var1, javax.swing.MenuSelectionManager var2) { }
	public void processMenuDragMouseEvent(javax.swing.event.MenuDragMouseEvent var0) { }
	public void processMenuKeyEvent(javax.swing.event.MenuKeyEvent var0) { }
	public void processMouseEvent(java.awt.event.MouseEvent var0, javax.swing.MenuElement[] var1, javax.swing.MenuSelectionManager var2) { }
	public void removeMenuDragMouseListener(javax.swing.event.MenuDragMouseListener var0) { }
	public void removeMenuKeyListener(javax.swing.event.MenuKeyListener var0) { }
	public void setAccelerator(javax.swing.KeyStroke var0) { }
	public void setArmed(boolean var0) { }
	public void setUI(javax.swing.plaf.MenuItemUI var0) { }
	protected class AccessibleJMenuItem extends javax.swing.AbstractButton.AccessibleAbstractButton implements javax.swing.event.ChangeListener {
		public void stateChanged(javax.swing.event.ChangeEvent var0) { }
		AccessibleJMenuItem() { } /* generated constructor to prevent compiler adding default public constructor */
	}
}

