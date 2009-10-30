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
public class BasicInternalFrameUI extends javax.swing.plaf.InternalFrameUI {
	protected class BasicInternalFrameListener implements javax.swing.event.InternalFrameListener {
		protected BasicInternalFrameListener() { } 
		public void internalFrameActivated(javax.swing.event.InternalFrameEvent var0) { }
		public void internalFrameClosed(javax.swing.event.InternalFrameEvent var0) { }
		public void internalFrameClosing(javax.swing.event.InternalFrameEvent var0) { }
		public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent var0) { }
		public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent var0) { }
		public void internalFrameIconified(javax.swing.event.InternalFrameEvent var0) { }
		public void internalFrameOpened(javax.swing.event.InternalFrameEvent var0) { }
	}
	protected class BorderListener extends javax.swing.event.MouseInputAdapter implements javax.swing.SwingConstants {
		protected final int RESIZE_NONE = 0;
		protected BorderListener() { } 
	}
	protected class ComponentHandler implements java.awt.event.ComponentListener {
		protected ComponentHandler() { } 
		public void componentHidden(java.awt.event.ComponentEvent var0) { }
		public void componentMoved(java.awt.event.ComponentEvent var0) { }
		public void componentResized(java.awt.event.ComponentEvent var0) { }
		public void componentShown(java.awt.event.ComponentEvent var0) { }
	}
	protected class GlassPaneDispatcher implements javax.swing.event.MouseInputListener {
		protected GlassPaneDispatcher() { } 
		public void mouseClicked(java.awt.event.MouseEvent var0) { }
		public void mouseDragged(java.awt.event.MouseEvent var0) { }
		public void mouseEntered(java.awt.event.MouseEvent var0) { }
		public void mouseExited(java.awt.event.MouseEvent var0) { }
		public void mouseMoved(java.awt.event.MouseEvent var0) { }
		public void mousePressed(java.awt.event.MouseEvent var0) { }
		public void mouseReleased(java.awt.event.MouseEvent var0) { }
	}
	public class InternalFrameLayout implements java.awt.LayoutManager {
		public InternalFrameLayout() { } 
		public void addLayoutComponent(java.lang.String var0, java.awt.Component var1) { }
		public void layoutContainer(java.awt.Container var0) { }
		public java.awt.Dimension minimumLayoutSize(java.awt.Container var0) { return null; }
		public java.awt.Dimension preferredLayoutSize(java.awt.Container var0) { return null; }
		public void removeLayoutComponent(java.awt.Component var0) { }
	}
	public class InternalFramePropertyChangeListener implements java.beans.PropertyChangeListener {
		public InternalFramePropertyChangeListener() { } 
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	}
	protected javax.swing.event.MouseInputAdapter borderListener;
	protected java.awt.event.ComponentListener componentListener;
	protected javax.swing.JComponent eastPane;
	protected javax.swing.JInternalFrame frame;
	protected javax.swing.event.MouseInputListener glassPaneDispatcher;
	protected java.awt.LayoutManager internalFrameLayout;
	protected javax.swing.JComponent northPane;
	/** @deprecated */ protected javax.swing.KeyStroke openMenuKey;
	protected java.beans.PropertyChangeListener propertyChangeListener;
	protected javax.swing.JComponent southPane;
	protected javax.swing.plaf.basic.BasicInternalFrameTitlePane titlePane;
	protected javax.swing.JComponent westPane;
	public BasicInternalFrameUI(javax.swing.JInternalFrame var0) { } 
	protected void activateFrame(javax.swing.JInternalFrame var0) { }
	protected void closeFrame(javax.swing.JInternalFrame var0) { }
	protected javax.swing.event.MouseInputAdapter createBorderListener(javax.swing.JInternalFrame var0) { return null; }
	protected java.awt.event.ComponentListener createComponentListener() { return null; }
	protected javax.swing.DesktopManager createDesktopManager() { return null; }
	protected javax.swing.JComponent createEastPane(javax.swing.JInternalFrame var0) { return null; }
	protected javax.swing.event.MouseInputListener createGlassPaneDispatcher() { return null; }
	protected void createInternalFrameListener() { }
	protected java.awt.LayoutManager createLayoutManager() { return null; }
	protected javax.swing.JComponent createNorthPane(javax.swing.JInternalFrame var0) { return null; }
	protected java.beans.PropertyChangeListener createPropertyChangeListener() { return null; }
	protected javax.swing.JComponent createSouthPane(javax.swing.JInternalFrame var0) { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected javax.swing.JComponent createWestPane(javax.swing.JInternalFrame var0) { return null; }
	protected void deactivateFrame(javax.swing.JInternalFrame var0) { }
	protected void deiconifyFrame(javax.swing.JInternalFrame var0) { }
	protected void deinstallMouseHandlers(javax.swing.JComponent var0) { }
	protected javax.swing.DesktopManager getDesktopManager() { return null; }
	public javax.swing.JComponent getEastPane() { return null; }
	public javax.swing.JComponent getNorthPane() { return null; }
	public javax.swing.JComponent getSouthPane() { return null; }
	public javax.swing.JComponent getWestPane() { return null; }
	protected void iconifyFrame(javax.swing.JInternalFrame var0) { }
	protected void installComponents() { }
	protected void installDefaults() { }
	protected void installKeyboardActions() { }
	protected void installListeners() { }
	protected void installMouseHandlers(javax.swing.JComponent var0) { }
	public final boolean isKeyBindingActive() { return false; }
	protected final boolean isKeyBindingRegistered() { return false; }
	protected void maximizeFrame(javax.swing.JInternalFrame var0) { }
	protected void minimizeFrame(javax.swing.JInternalFrame var0) { }
	protected void replacePane(javax.swing.JComponent var0, javax.swing.JComponent var1) { }
	public void setEastPane(javax.swing.JComponent var0) { }
	protected final void setKeyBindingActive(boolean var0) { }
	protected final void setKeyBindingRegistered(boolean var0) { }
	public void setNorthPane(javax.swing.JComponent var0) { }
	public void setSouthPane(javax.swing.JComponent var0) { }
	public void setWestPane(javax.swing.JComponent var0) { }
	protected void setupMenuCloseKey() { }
	protected void setupMenuOpenKey() { }
	protected void uninstallComponents() { }
	protected void uninstallDefaults() { }
	protected void uninstallKeyboardActions() { }
	protected void uninstallListeners() { }
}

