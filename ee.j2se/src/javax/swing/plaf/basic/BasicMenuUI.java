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

package javax.swing.plaf.basic;
public class BasicMenuUI extends javax.swing.plaf.basic.BasicMenuItemUI {
	public BasicMenuUI() { }
	protected javax.swing.event.ChangeListener createChangeListener(javax.swing.JComponent var0) { return null; }
	protected javax.swing.event.MenuListener createMenuListener(javax.swing.JComponent var0) { return null; }
	protected java.beans.PropertyChangeListener createPropertyChangeListener(javax.swing.JComponent var0) { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected void setupPostTimer(javax.swing.JMenu var0) { }
	protected javax.swing.event.ChangeListener changeListener;
	protected javax.swing.event.MenuListener menuListener;
	protected java.beans.PropertyChangeListener propertyChangeListener;
	public class ChangeHandler implements javax.swing.event.ChangeListener {
		public ChangeHandler(javax.swing.JMenu var0, javax.swing.plaf.basic.BasicMenuUI var1) { }
		public void stateChanged(javax.swing.event.ChangeEvent var0) { }
		public boolean isSelected;
		public javax.swing.JMenu menu;
		public javax.swing.plaf.basic.BasicMenuUI ui;
		public java.awt.Component wasFocused;
	}
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
}

