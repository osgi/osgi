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

package javax.swing.plaf.basic;
public class BasicMenuItemUI extends javax.swing.plaf.MenuItemUI {
	protected class MouseInputHandler implements javax.swing.event.MouseInputListener {
		protected MouseInputHandler() { } 
		public void mouseClicked(java.awt.event.MouseEvent var0) { }
		public void mouseDragged(java.awt.event.MouseEvent var0) { }
		public void mouseEntered(java.awt.event.MouseEvent var0) { }
		public void mouseExited(java.awt.event.MouseEvent var0) { }
		public void mouseMoved(java.awt.event.MouseEvent var0) { }
		public void mousePressed(java.awt.event.MouseEvent var0) { }
		public void mouseReleased(java.awt.event.MouseEvent var0) { }
	}
	protected java.awt.Font acceleratorFont;
	protected java.awt.Color acceleratorForeground;
	protected java.awt.Color acceleratorSelectionForeground;
	protected javax.swing.Icon arrowIcon;
	protected javax.swing.Icon checkIcon;
	protected int defaultTextIconGap;
	protected java.awt.Color disabledForeground;
	protected javax.swing.event.MenuDragMouseListener menuDragMouseListener;
	protected javax.swing.JMenuItem menuItem;
	protected javax.swing.event.MenuKeyListener menuKeyListener;
	protected javax.swing.event.MouseInputListener mouseInputListener;
	protected boolean oldBorderPainted;
	protected java.beans.PropertyChangeListener propertyChangeListener;
	protected java.awt.Color selectionBackground;
	protected java.awt.Color selectionForeground;
	public BasicMenuItemUI() { } 
	protected javax.swing.event.MenuDragMouseListener createMenuDragMouseListener(javax.swing.JComponent var0) { return null; }
	protected javax.swing.event.MenuKeyListener createMenuKeyListener(javax.swing.JComponent var0) { return null; }
	protected javax.swing.event.MouseInputListener createMouseInputListener(javax.swing.JComponent var0) { return null; }
	protected java.beans.PropertyChangeListener createPropertyChangeListener(javax.swing.JComponent var0) { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected void doClick(javax.swing.MenuSelectionManager var0) { }
	public javax.swing.MenuElement[] getPath() { return null; }
	protected java.awt.Dimension getPreferredMenuItemSize(javax.swing.JComponent var0, javax.swing.Icon var1, javax.swing.Icon var2, int var3) { return null; }
	protected java.lang.String getPropertyPrefix() { return null; }
	protected void installComponents(javax.swing.JMenuItem var0) { }
	protected void installDefaults() { }
	protected void installKeyboardActions() { }
	protected void installListeners() { }
	protected void paintBackground(java.awt.Graphics var0, javax.swing.JMenuItem var1, java.awt.Color var2) { }
	protected void paintMenuItem(java.awt.Graphics var0, javax.swing.JComponent var1, javax.swing.Icon var2, javax.swing.Icon var3, java.awt.Color var4, java.awt.Color var5, int var6) { }
	protected void paintText(java.awt.Graphics var0, javax.swing.JMenuItem var1, java.awt.Rectangle var2, java.lang.String var3) { }
	protected void uninstallComponents(javax.swing.JMenuItem var0) { }
	protected void uninstallDefaults() { }
	protected void uninstallKeyboardActions() { }
	protected void uninstallListeners() { }
}

