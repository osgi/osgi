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
public class BasicToolBarUI extends javax.swing.plaf.ToolBarUI implements javax.swing.SwingConstants {
	public BasicToolBarUI() { }
	public boolean canDock(java.awt.Component var0, java.awt.Point var1) { return false; }
	protected javax.swing.event.MouseInputListener createDockingListener() { return null; }
	protected javax.swing.plaf.basic.BasicToolBarUI.DragWindow createDragWindow(javax.swing.JToolBar var0) { return null; }
	protected javax.swing.JFrame createFloatingFrame(javax.swing.JToolBar var0) { return null; }
	protected javax.swing.RootPaneContainer createFloatingWindow(javax.swing.JToolBar var0) { return null; }
	protected java.awt.event.WindowListener createFrameListener() { return null; }
	protected javax.swing.border.Border createNonRolloverBorder() { return null; }
	protected java.beans.PropertyChangeListener createPropertyListener() { return null; }
	protected javax.swing.border.Border createRolloverBorder() { return null; }
	protected java.awt.event.ContainerListener createToolBarContListener() { return null; }
	protected java.awt.event.FocusListener createToolBarFocusListener() { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected void dragTo(java.awt.Point var0, java.awt.Point var1) { }
	protected void floatAt(java.awt.Point var0, java.awt.Point var1) { }
	public java.awt.Color getDockingColor() { return null; }
	public java.awt.Color getFloatingColor() { return null; }
	protected void installComponents() { }
	protected void installDefaults() { }
	protected void installKeyboardActions() { }
	protected void installListeners() { }
	protected void installNonRolloverBorders(javax.swing.JComponent var0) { }
	protected void installNormalBorders(javax.swing.JComponent var0) { }
	protected void installRolloverBorders(javax.swing.JComponent var0) { }
	public boolean isFloating() { return false; }
	public boolean isRolloverBorders() { return false; }
	protected void navigateFocusedComp(int var0) { }
	protected void setBorderToNonRollover(java.awt.Component var0) { }
	protected void setBorderToNormal(java.awt.Component var0) { }
	protected void setBorderToRollover(java.awt.Component var0) { }
	public void setDockingColor(java.awt.Color var0) { }
	public void setFloating(boolean var0, java.awt.Point var1) { }
	public void setFloatingColor(java.awt.Color var0) { }
	public void setFloatingLocation(int var0, int var1) { }
	public void setOrientation(int var0) { }
	public void setRolloverBorders(boolean var0) { }
	protected void uninstallComponents() { }
	protected void uninstallDefaults() { }
	protected void uninstallKeyboardActions() { }
	protected void uninstallListeners() { }
	protected java.lang.String constraintBeforeFloating;
	protected java.awt.Color dockingBorderColor;
	protected java.awt.Color dockingColor;
	protected javax.swing.event.MouseInputListener dockingListener;
	/** @deprecated */ protected javax.swing.KeyStroke downKey;
	protected javax.swing.plaf.basic.BasicToolBarUI.DragWindow dragWindow;
	protected java.awt.Color floatingBorderColor;
	protected java.awt.Color floatingColor;
	protected int focusedCompIndex;
	/** @deprecated */ protected javax.swing.KeyStroke leftKey;
	protected java.beans.PropertyChangeListener propertyListener;
	/** @deprecated */ protected javax.swing.KeyStroke rightKey;
	protected javax.swing.JToolBar toolBar;
	protected java.awt.event.ContainerListener toolBarContListener;
	protected java.awt.event.FocusListener toolBarFocusListener;
	/** @deprecated */ protected javax.swing.KeyStroke upKey;
	public class DockingListener implements javax.swing.event.MouseInputListener {
		public DockingListener(javax.swing.JToolBar var0) { }
		public void mouseClicked(java.awt.event.MouseEvent var0) { }
		public void mouseDragged(java.awt.event.MouseEvent var0) { }
		public void mouseEntered(java.awt.event.MouseEvent var0) { }
		public void mouseExited(java.awt.event.MouseEvent var0) { }
		public void mouseMoved(java.awt.event.MouseEvent var0) { }
		public void mousePressed(java.awt.event.MouseEvent var0) { }
		public void mouseReleased(java.awt.event.MouseEvent var0) { }
		protected boolean isDragging;
		protected java.awt.Point origin;
		protected javax.swing.JToolBar toolBar;
	}
	protected class DragWindow extends java.awt.Window {
		public java.awt.Color getBorderColor() { return null; }
		public java.awt.Point getOffset() { return null; }
		public void setBorderColor(java.awt.Color var0) { }
		public void setOffset(java.awt.Point var0) { }
		public void setOrientation(int var0) { }
		private DragWindow() { super((java.awt.Window) null, (java.awt.GraphicsConfiguration) null); } /* generated constructor to prevent compiler adding default public constructor */
	}
	protected class FrameListener extends java.awt.event.WindowAdapter {
		protected FrameListener() { }
	}
	protected class PropertyListener implements java.beans.PropertyChangeListener {
		protected PropertyListener() { }
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	}
	protected class ToolBarContListener implements java.awt.event.ContainerListener {
		protected ToolBarContListener() { }
		public void componentAdded(java.awt.event.ContainerEvent var0) { }
		public void componentRemoved(java.awt.event.ContainerEvent var0) { }
	}
	protected class ToolBarFocusListener implements java.awt.event.FocusListener {
		protected ToolBarFocusListener() { }
		public void focusGained(java.awt.event.FocusEvent var0) { }
		public void focusLost(java.awt.event.FocusEvent var0) { }
	}
}

