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

package java.awt;
public class AWTEventMulticaster implements java.awt.event.ActionListener, java.awt.event.AdjustmentListener, java.awt.event.ComponentListener, java.awt.event.ContainerListener, java.awt.event.FocusListener, java.awt.event.HierarchyBoundsListener, java.awt.event.HierarchyListener, java.awt.event.InputMethodListener, java.awt.event.ItemListener, java.awt.event.KeyListener, java.awt.event.MouseListener, java.awt.event.MouseMotionListener, java.awt.event.MouseWheelListener, java.awt.event.TextListener, java.awt.event.WindowFocusListener, java.awt.event.WindowListener, java.awt.event.WindowStateListener {
	protected final java.util.EventListener a; { a = null; }
	protected final java.util.EventListener b; { b = null; }
	protected AWTEventMulticaster(java.util.EventListener var0, java.util.EventListener var1) { } 
	public void actionPerformed(java.awt.event.ActionEvent var0) { }
	public static java.awt.event.ActionListener add(java.awt.event.ActionListener var0, java.awt.event.ActionListener var1) { return null; }
	public static java.awt.event.AdjustmentListener add(java.awt.event.AdjustmentListener var0, java.awt.event.AdjustmentListener var1) { return null; }
	public static java.awt.event.ComponentListener add(java.awt.event.ComponentListener var0, java.awt.event.ComponentListener var1) { return null; }
	public static java.awt.event.ContainerListener add(java.awt.event.ContainerListener var0, java.awt.event.ContainerListener var1) { return null; }
	public static java.awt.event.FocusListener add(java.awt.event.FocusListener var0, java.awt.event.FocusListener var1) { return null; }
	public static java.awt.event.HierarchyBoundsListener add(java.awt.event.HierarchyBoundsListener var0, java.awt.event.HierarchyBoundsListener var1) { return null; }
	public static java.awt.event.HierarchyListener add(java.awt.event.HierarchyListener var0, java.awt.event.HierarchyListener var1) { return null; }
	public static java.awt.event.InputMethodListener add(java.awt.event.InputMethodListener var0, java.awt.event.InputMethodListener var1) { return null; }
	public static java.awt.event.ItemListener add(java.awt.event.ItemListener var0, java.awt.event.ItemListener var1) { return null; }
	public static java.awt.event.KeyListener add(java.awt.event.KeyListener var0, java.awt.event.KeyListener var1) { return null; }
	public static java.awt.event.MouseListener add(java.awt.event.MouseListener var0, java.awt.event.MouseListener var1) { return null; }
	public static java.awt.event.MouseMotionListener add(java.awt.event.MouseMotionListener var0, java.awt.event.MouseMotionListener var1) { return null; }
	public static java.awt.event.MouseWheelListener add(java.awt.event.MouseWheelListener var0, java.awt.event.MouseWheelListener var1) { return null; }
	public static java.awt.event.TextListener add(java.awt.event.TextListener var0, java.awt.event.TextListener var1) { return null; }
	public static java.awt.event.WindowFocusListener add(java.awt.event.WindowFocusListener var0, java.awt.event.WindowFocusListener var1) { return null; }
	public static java.awt.event.WindowListener add(java.awt.event.WindowListener var0, java.awt.event.WindowListener var1) { return null; }
	public static java.awt.event.WindowStateListener add(java.awt.event.WindowStateListener var0, java.awt.event.WindowStateListener var1) { return null; }
	protected static java.util.EventListener addInternal(java.util.EventListener var0, java.util.EventListener var1) { return null; }
	public void adjustmentValueChanged(java.awt.event.AdjustmentEvent var0) { }
	public void ancestorMoved(java.awt.event.HierarchyEvent var0) { }
	public void ancestorResized(java.awt.event.HierarchyEvent var0) { }
	public void caretPositionChanged(java.awt.event.InputMethodEvent var0) { }
	public void componentAdded(java.awt.event.ContainerEvent var0) { }
	public void componentHidden(java.awt.event.ComponentEvent var0) { }
	public void componentMoved(java.awt.event.ComponentEvent var0) { }
	public void componentRemoved(java.awt.event.ContainerEvent var0) { }
	public void componentResized(java.awt.event.ComponentEvent var0) { }
	public void componentShown(java.awt.event.ComponentEvent var0) { }
	public void focusGained(java.awt.event.FocusEvent var0) { }
	public void focusLost(java.awt.event.FocusEvent var0) { }
	public static <T extends java.util.EventListener> T[] getListeners(java.util.EventListener var0, java.lang.Class<T> var1) { return null; }
	public void hierarchyChanged(java.awt.event.HierarchyEvent var0) { }
	public void inputMethodTextChanged(java.awt.event.InputMethodEvent var0) { }
	public void itemStateChanged(java.awt.event.ItemEvent var0) { }
	public void keyPressed(java.awt.event.KeyEvent var0) { }
	public void keyReleased(java.awt.event.KeyEvent var0) { }
	public void keyTyped(java.awt.event.KeyEvent var0) { }
	public void mouseClicked(java.awt.event.MouseEvent var0) { }
	public void mouseDragged(java.awt.event.MouseEvent var0) { }
	public void mouseEntered(java.awt.event.MouseEvent var0) { }
	public void mouseExited(java.awt.event.MouseEvent var0) { }
	public void mouseMoved(java.awt.event.MouseEvent var0) { }
	public void mousePressed(java.awt.event.MouseEvent var0) { }
	public void mouseReleased(java.awt.event.MouseEvent var0) { }
	public void mouseWheelMoved(java.awt.event.MouseWheelEvent var0) { }
	public static java.awt.event.ActionListener remove(java.awt.event.ActionListener var0, java.awt.event.ActionListener var1) { return null; }
	public static java.awt.event.AdjustmentListener remove(java.awt.event.AdjustmentListener var0, java.awt.event.AdjustmentListener var1) { return null; }
	public static java.awt.event.ComponentListener remove(java.awt.event.ComponentListener var0, java.awt.event.ComponentListener var1) { return null; }
	public static java.awt.event.ContainerListener remove(java.awt.event.ContainerListener var0, java.awt.event.ContainerListener var1) { return null; }
	public static java.awt.event.FocusListener remove(java.awt.event.FocusListener var0, java.awt.event.FocusListener var1) { return null; }
	public static java.awt.event.HierarchyBoundsListener remove(java.awt.event.HierarchyBoundsListener var0, java.awt.event.HierarchyBoundsListener var1) { return null; }
	public static java.awt.event.HierarchyListener remove(java.awt.event.HierarchyListener var0, java.awt.event.HierarchyListener var1) { return null; }
	public static java.awt.event.InputMethodListener remove(java.awt.event.InputMethodListener var0, java.awt.event.InputMethodListener var1) { return null; }
	public static java.awt.event.ItemListener remove(java.awt.event.ItemListener var0, java.awt.event.ItemListener var1) { return null; }
	public static java.awt.event.KeyListener remove(java.awt.event.KeyListener var0, java.awt.event.KeyListener var1) { return null; }
	public static java.awt.event.MouseListener remove(java.awt.event.MouseListener var0, java.awt.event.MouseListener var1) { return null; }
	public static java.awt.event.MouseMotionListener remove(java.awt.event.MouseMotionListener var0, java.awt.event.MouseMotionListener var1) { return null; }
	public static java.awt.event.MouseWheelListener remove(java.awt.event.MouseWheelListener var0, java.awt.event.MouseWheelListener var1) { return null; }
	public static java.awt.event.TextListener remove(java.awt.event.TextListener var0, java.awt.event.TextListener var1) { return null; }
	public static java.awt.event.WindowFocusListener remove(java.awt.event.WindowFocusListener var0, java.awt.event.WindowFocusListener var1) { return null; }
	public static java.awt.event.WindowListener remove(java.awt.event.WindowListener var0, java.awt.event.WindowListener var1) { return null; }
	public static java.awt.event.WindowStateListener remove(java.awt.event.WindowStateListener var0, java.awt.event.WindowStateListener var1) { return null; }
	protected java.util.EventListener remove(java.util.EventListener var0) { return null; }
	protected static java.util.EventListener removeInternal(java.util.EventListener var0, java.util.EventListener var1) { return null; }
	protected static void save(java.io.ObjectOutputStream var0, java.lang.String var1, java.util.EventListener var2) throws java.io.IOException { }
	protected void saveInternal(java.io.ObjectOutputStream var0, java.lang.String var1) throws java.io.IOException { }
	public void textValueChanged(java.awt.event.TextEvent var0) { }
	public void windowActivated(java.awt.event.WindowEvent var0) { }
	public void windowClosed(java.awt.event.WindowEvent var0) { }
	public void windowClosing(java.awt.event.WindowEvent var0) { }
	public void windowDeactivated(java.awt.event.WindowEvent var0) { }
	public void windowDeiconified(java.awt.event.WindowEvent var0) { }
	public void windowGainedFocus(java.awt.event.WindowEvent var0) { }
	public void windowIconified(java.awt.event.WindowEvent var0) { }
	public void windowLostFocus(java.awt.event.WindowEvent var0) { }
	public void windowOpened(java.awt.event.WindowEvent var0) { }
	public void windowStateChanged(java.awt.event.WindowEvent var0) { }
}

