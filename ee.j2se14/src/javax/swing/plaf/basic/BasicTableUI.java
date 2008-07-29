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
public class BasicTableUI extends javax.swing.plaf.TableUI {
	public BasicTableUI() { }
	protected java.awt.event.FocusListener createFocusListener() { return null; }
	protected java.awt.event.KeyListener createKeyListener() { return null; }
	protected javax.swing.event.MouseInputListener createMouseInputListener() { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected void installDefaults() { }
	protected void installKeyboardActions() { }
	protected void installListeners() { }
	protected void uninstallDefaults() { }
	protected void uninstallKeyboardActions() { }
	protected void uninstallListeners() { }
	protected java.awt.event.FocusListener focusListener;
	protected java.awt.event.KeyListener keyListener;
	protected javax.swing.event.MouseInputListener mouseInputListener;
	protected javax.swing.CellRendererPane rendererPane;
	protected javax.swing.JTable table;
	public class FocusHandler implements java.awt.event.FocusListener {
		public FocusHandler() { }
		public void focusGained(java.awt.event.FocusEvent var0) { }
		public void focusLost(java.awt.event.FocusEvent var0) { }
	}
	public class KeyHandler implements java.awt.event.KeyListener {
		public KeyHandler() { }
		public void keyPressed(java.awt.event.KeyEvent var0) { }
		public void keyReleased(java.awt.event.KeyEvent var0) { }
		public void keyTyped(java.awt.event.KeyEvent var0) { }
	}
	public class MouseInputHandler implements javax.swing.event.MouseInputListener {
		public MouseInputHandler() { }
		public void mouseClicked(java.awt.event.MouseEvent var0) { }
		public void mouseDragged(java.awt.event.MouseEvent var0) { }
		public void mouseEntered(java.awt.event.MouseEvent var0) { }
		public void mouseExited(java.awt.event.MouseEvent var0) { }
		public void mouseMoved(java.awt.event.MouseEvent var0) { }
		public void mousePressed(java.awt.event.MouseEvent var0) { }
		public void mouseReleased(java.awt.event.MouseEvent var0) { }
	}
}

