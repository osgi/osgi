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

package java.awt;
public abstract class Component implements java.awt.MenuContainer, java.awt.image.ImageObserver, java.io.Serializable {
	protected Component() { }
	/** @deprecated */ public boolean action(java.awt.Event var0, java.lang.Object var1) { return false; }
	public void add(java.awt.PopupMenu var0) { }
	public void addComponentListener(java.awt.event.ComponentListener var0) { }
	public void addFocusListener(java.awt.event.FocusListener var0) { }
	public void addHierarchyBoundsListener(java.awt.event.HierarchyBoundsListener var0) { }
	public void addHierarchyListener(java.awt.event.HierarchyListener var0) { }
	public void addInputMethodListener(java.awt.event.InputMethodListener var0) { }
	public void addKeyListener(java.awt.event.KeyListener var0) { }
	public void addMouseListener(java.awt.event.MouseListener var0) { }
	public void addMouseMotionListener(java.awt.event.MouseMotionListener var0) { }
	public void addMouseWheelListener(java.awt.event.MouseWheelListener var0) { }
	public void addNotify() { }
	public void addPropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public void addPropertyChangeListener(java.lang.String var0, java.beans.PropertyChangeListener var1) { }
	public void applyComponentOrientation(java.awt.ComponentOrientation var0) { }
	public boolean areFocusTraversalKeysSet(int var0) { return false; }
	/** @deprecated */ public java.awt.Rectangle bounds() { return null; }
	public int checkImage(java.awt.Image var0, int var1, int var2, java.awt.image.ImageObserver var3) { return 0; }
	public int checkImage(java.awt.Image var0, java.awt.image.ImageObserver var1) { return 0; }
	protected java.awt.AWTEvent coalesceEvents(java.awt.AWTEvent var0, java.awt.AWTEvent var1) { return null; }
	public boolean contains(int var0, int var1) { return false; }
	public boolean contains(java.awt.Point var0) { return false; }
	public java.awt.Image createImage(int var0, int var1) { return null; }
	public java.awt.Image createImage(java.awt.image.ImageProducer var0) { return null; }
	public java.awt.image.VolatileImage createVolatileImage(int var0, int var1) { return null; }
	public java.awt.image.VolatileImage createVolatileImage(int var0, int var1, java.awt.ImageCapabilities var2) throws java.awt.AWTException { return null; }
	/** @deprecated */ public void deliverEvent(java.awt.Event var0) { }
	/** @deprecated */ public void disable() { }
	protected final void disableEvents(long var0) { }
	public final void dispatchEvent(java.awt.AWTEvent var0) { }
	public void doLayout() { }
	/** @deprecated */ public void enable() { }
	/** @deprecated */ public void enable(boolean var0) { }
	protected final void enableEvents(long var0) { }
	public void enableInputMethods(boolean var0) { }
	protected void firePropertyChange(java.lang.String var0, int var1, int var2) { }
	protected void firePropertyChange(java.lang.String var0, java.lang.Object var1, java.lang.Object var2) { }
	protected void firePropertyChange(java.lang.String var0, boolean var1, boolean var2) { }
	public javax.accessibility.AccessibleContext getAccessibleContext() { return null; }
	public float getAlignmentX() { return 0.0f; }
	public float getAlignmentY() { return 0.0f; }
	public java.awt.Color getBackground() { return null; }
	public java.awt.Rectangle getBounds() { return null; }
	public java.awt.Rectangle getBounds(java.awt.Rectangle var0) { return null; }
	public java.awt.image.ColorModel getColorModel() { return null; }
	public java.awt.Component getComponentAt(int var0, int var1) { return null; }
	public java.awt.Component getComponentAt(java.awt.Point var0) { return null; }
	public java.awt.event.ComponentListener[] getComponentListeners() { return null; }
	public java.awt.ComponentOrientation getComponentOrientation() { return null; }
	public java.awt.Cursor getCursor() { return null; }
	public java.awt.dnd.DropTarget getDropTarget() { return null; }
	public java.awt.Container getFocusCycleRootAncestor() { return null; }
	public java.awt.event.FocusListener[] getFocusListeners() { return null; }
	public java.util.Set getFocusTraversalKeys(int var0) { return null; }
	public boolean getFocusTraversalKeysEnabled() { return false; }
	public java.awt.Font getFont() { return null; }
	public java.awt.FontMetrics getFontMetrics(java.awt.Font var0) { return null; }
	public java.awt.Color getForeground() { return null; }
	public java.awt.Graphics getGraphics() { return null; }
	public java.awt.GraphicsConfiguration getGraphicsConfiguration() { return null; }
	public int getHeight() { return 0; }
	public java.awt.event.HierarchyBoundsListener[] getHierarchyBoundsListeners() { return null; }
	public java.awt.event.HierarchyListener[] getHierarchyListeners() { return null; }
	public boolean getIgnoreRepaint() { return false; }
	public java.awt.im.InputContext getInputContext() { return null; }
	public java.awt.event.InputMethodListener[] getInputMethodListeners() { return null; }
	public java.awt.im.InputMethodRequests getInputMethodRequests() { return null; }
	public java.awt.event.KeyListener[] getKeyListeners() { return null; }
	public java.util.EventListener[] getListeners(java.lang.Class var0) { return null; }
	public java.util.Locale getLocale() { return null; }
	public java.awt.Point getLocation() { return null; }
	public java.awt.Point getLocation(java.awt.Point var0) { return null; }
	public java.awt.Point getLocationOnScreen() { return null; }
	public java.awt.Dimension getMaximumSize() { return null; }
	public java.awt.Dimension getMinimumSize() { return null; }
	public java.awt.event.MouseListener[] getMouseListeners() { return null; }
	public java.awt.event.MouseMotionListener[] getMouseMotionListeners() { return null; }
	public java.awt.event.MouseWheelListener[] getMouseWheelListeners() { return null; }
	public java.lang.String getName() { return null; }
	public java.awt.Container getParent() { return null; }
	/** @deprecated */ public java.awt.peer.ComponentPeer getPeer() { return null; }
	public java.awt.Dimension getPreferredSize() { return null; }
	public java.beans.PropertyChangeListener[] getPropertyChangeListeners() { return null; }
	public java.beans.PropertyChangeListener[] getPropertyChangeListeners(java.lang.String var0) { return null; }
	public java.awt.Dimension getSize() { return null; }
	public java.awt.Dimension getSize(java.awt.Dimension var0) { return null; }
	public java.awt.Toolkit getToolkit() { return null; }
	public final java.lang.Object getTreeLock() { return null; }
	public int getWidth() { return 0; }
	public int getX() { return 0; }
	public int getY() { return 0; }
	/** @deprecated */ public boolean gotFocus(java.awt.Event var0, java.lang.Object var1) { return false; }
	/** @deprecated */ public boolean handleEvent(java.awt.Event var0) { return false; }
	public boolean hasFocus() { return false; }
	/** @deprecated */ public void hide() { }
	public boolean imageUpdate(java.awt.Image var0, int var1, int var2, int var3, int var4, int var5) { return false; }
	/** @deprecated */ public boolean inside(int var0, int var1) { return false; }
	public void invalidate() { }
	public boolean isBackgroundSet() { return false; }
	public boolean isCursorSet() { return false; }
	public boolean isDisplayable() { return false; }
	public boolean isDoubleBuffered() { return false; }
	public boolean isEnabled() { return false; }
	public boolean isFocusCycleRoot(java.awt.Container var0) { return false; }
	public boolean isFocusOwner() { return false; }
	/** @deprecated */ public boolean isFocusTraversable() { return false; }
	public boolean isFocusable() { return false; }
	public boolean isFontSet() { return false; }
	public boolean isForegroundSet() { return false; }
	public boolean isLightweight() { return false; }
	public boolean isOpaque() { return false; }
	public boolean isShowing() { return false; }
	public boolean isValid() { return false; }
	public boolean isVisible() { return false; }
	/** @deprecated */ public boolean keyDown(java.awt.Event var0, int var1) { return false; }
	/** @deprecated */ public boolean keyUp(java.awt.Event var0, int var1) { return false; }
	/** @deprecated */ public void layout() { }
	public void list() { }
	public void list(java.io.PrintStream var0) { }
	public void list(java.io.PrintStream var0, int var1) { }
	public void list(java.io.PrintWriter var0) { }
	public void list(java.io.PrintWriter var0, int var1) { }
	/** @deprecated */ public java.awt.Component locate(int var0, int var1) { return null; }
	/** @deprecated */ public java.awt.Point location() { return null; }
	/** @deprecated */ public boolean lostFocus(java.awt.Event var0, java.lang.Object var1) { return false; }
	/** @deprecated */ public java.awt.Dimension minimumSize() { return null; }
	/** @deprecated */ public boolean mouseDown(java.awt.Event var0, int var1, int var2) { return false; }
	/** @deprecated */ public boolean mouseDrag(java.awt.Event var0, int var1, int var2) { return false; }
	/** @deprecated */ public boolean mouseEnter(java.awt.Event var0, int var1, int var2) { return false; }
	/** @deprecated */ public boolean mouseExit(java.awt.Event var0, int var1, int var2) { return false; }
	/** @deprecated */ public boolean mouseMove(java.awt.Event var0, int var1, int var2) { return false; }
	/** @deprecated */ public boolean mouseUp(java.awt.Event var0, int var1, int var2) { return false; }
	/** @deprecated */ public void move(int var0, int var1) { }
	/** @deprecated */ public void nextFocus() { }
	public void paint(java.awt.Graphics var0) { }
	public void paintAll(java.awt.Graphics var0) { }
	protected java.lang.String paramString() { return null; }
	/** @deprecated */ public boolean postEvent(java.awt.Event var0) { return false; }
	/** @deprecated */ public java.awt.Dimension preferredSize() { return null; }
	public boolean prepareImage(java.awt.Image var0, int var1, int var2, java.awt.image.ImageObserver var3) { return false; }
	public boolean prepareImage(java.awt.Image var0, java.awt.image.ImageObserver var1) { return false; }
	public void print(java.awt.Graphics var0) { }
	public void printAll(java.awt.Graphics var0) { }
	protected void processComponentEvent(java.awt.event.ComponentEvent var0) { }
	protected void processEvent(java.awt.AWTEvent var0) { }
	protected void processFocusEvent(java.awt.event.FocusEvent var0) { }
	protected void processHierarchyBoundsEvent(java.awt.event.HierarchyEvent var0) { }
	protected void processHierarchyEvent(java.awt.event.HierarchyEvent var0) { }
	protected void processInputMethodEvent(java.awt.event.InputMethodEvent var0) { }
	protected void processKeyEvent(java.awt.event.KeyEvent var0) { }
	protected void processMouseEvent(java.awt.event.MouseEvent var0) { }
	protected void processMouseMotionEvent(java.awt.event.MouseEvent var0) { }
	protected void processMouseWheelEvent(java.awt.event.MouseWheelEvent var0) { }
	public void remove(java.awt.MenuComponent var0) { }
	public void removeComponentListener(java.awt.event.ComponentListener var0) { }
	public void removeFocusListener(java.awt.event.FocusListener var0) { }
	public void removeHierarchyBoundsListener(java.awt.event.HierarchyBoundsListener var0) { }
	public void removeHierarchyListener(java.awt.event.HierarchyListener var0) { }
	public void removeInputMethodListener(java.awt.event.InputMethodListener var0) { }
	public void removeKeyListener(java.awt.event.KeyListener var0) { }
	public void removeMouseListener(java.awt.event.MouseListener var0) { }
	public void removeMouseMotionListener(java.awt.event.MouseMotionListener var0) { }
	public void removeMouseWheelListener(java.awt.event.MouseWheelListener var0) { }
	public void removeNotify() { }
	public void removePropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public void removePropertyChangeListener(java.lang.String var0, java.beans.PropertyChangeListener var1) { }
	public void repaint() { }
	public void repaint(int var0, int var1, int var2, int var3) { }
	public void repaint(long var0) { }
	public void repaint(long var0, int var1, int var2, int var3, int var4) { }
	public void requestFocus() { }
	protected boolean requestFocus(boolean var0) { return false; }
	public boolean requestFocusInWindow() { return false; }
	protected boolean requestFocusInWindow(boolean var0) { return false; }
	/** @deprecated */ public void reshape(int var0, int var1, int var2, int var3) { }
	/** @deprecated */ public void resize(int var0, int var1) { }
	/** @deprecated */ public void resize(java.awt.Dimension var0) { }
	public void setBackground(java.awt.Color var0) { }
	public void setBounds(int var0, int var1, int var2, int var3) { }
	public void setBounds(java.awt.Rectangle var0) { }
	public void setComponentOrientation(java.awt.ComponentOrientation var0) { }
	public void setCursor(java.awt.Cursor var0) { }
	public void setDropTarget(java.awt.dnd.DropTarget var0) { }
	public void setEnabled(boolean var0) { }
	public void setFocusTraversalKeys(int var0, java.util.Set var1) { }
	public void setFocusTraversalKeysEnabled(boolean var0) { }
	public void setFocusable(boolean var0) { }
	public void setFont(java.awt.Font var0) { }
	public void setForeground(java.awt.Color var0) { }
	public void setIgnoreRepaint(boolean var0) { }
	public void setLocale(java.util.Locale var0) { }
	public void setLocation(int var0, int var1) { }
	public void setLocation(java.awt.Point var0) { }
	public void setName(java.lang.String var0) { }
	public void setSize(int var0, int var1) { }
	public void setSize(java.awt.Dimension var0) { }
	public void setVisible(boolean var0) { }
	/** @deprecated */ public void show() { }
	/** @deprecated */ public void show(boolean var0) { }
	/** @deprecated */ public java.awt.Dimension size() { return null; }
	public void transferFocus() { }
	public void transferFocusBackward() { }
	public void transferFocusUpCycle() { }
	public void update(java.awt.Graphics var0) { }
	public void validate() { }
	public final static float BOTTOM_ALIGNMENT = 1.0f;
	public final static float CENTER_ALIGNMENT = 0.5f;
	public final static float LEFT_ALIGNMENT = 0.0f;
	public final static float RIGHT_ALIGNMENT = 1.0f;
	public final static float TOP_ALIGNMENT = 0.0f;
	protected abstract class AccessibleAWTComponent extends javax.accessibility.AccessibleContext implements java.io.Serializable, javax.accessibility.AccessibleComponent {
		protected AccessibleAWTComponent() { }
		public void addFocusListener(java.awt.event.FocusListener var0) { }
		public boolean contains(java.awt.Point var0) { return false; }
		public javax.accessibility.Accessible getAccessibleAt(java.awt.Point var0) { return null; }
		public javax.accessibility.Accessible getAccessibleChild(int var0) { return null; }
		public int getAccessibleChildrenCount() { return 0; }
		public int getAccessibleIndexInParent() { return 0; }
		public javax.accessibility.AccessibleRole getAccessibleRole() { return null; }
		public javax.accessibility.AccessibleStateSet getAccessibleStateSet() { return null; }
		public java.awt.Color getBackground() { return null; }
		public java.awt.Rectangle getBounds() { return null; }
		public java.awt.Cursor getCursor() { return null; }
		public java.awt.Font getFont() { return null; }
		public java.awt.FontMetrics getFontMetrics(java.awt.Font var0) { return null; }
		public java.awt.Color getForeground() { return null; }
		public java.util.Locale getLocale() { return null; }
		public java.awt.Point getLocation() { return null; }
		public java.awt.Point getLocationOnScreen() { return null; }
		public java.awt.Dimension getSize() { return null; }
		public boolean isEnabled() { return false; }
		public boolean isFocusTraversable() { return false; }
		public boolean isShowing() { return false; }
		public boolean isVisible() { return false; }
		public void removeFocusListener(java.awt.event.FocusListener var0) { }
		public void requestFocus() { }
		public void setBackground(java.awt.Color var0) { }
		public void setBounds(java.awt.Rectangle var0) { }
		public void setCursor(java.awt.Cursor var0) { }
		public void setEnabled(boolean var0) { }
		public void setFont(java.awt.Font var0) { }
		public void setForeground(java.awt.Color var0) { }
		public void setLocation(java.awt.Point var0) { }
		public void setSize(java.awt.Dimension var0) { }
		public void setVisible(boolean var0) { }
		protected java.awt.event.ComponentListener accessibleAWTComponentHandler;
		protected java.awt.event.FocusListener accessibleAWTFocusHandler;
		protected class AccessibleAWTComponentHandler implements java.awt.event.ComponentListener {
			protected AccessibleAWTComponentHandler() { }
			public void componentHidden(java.awt.event.ComponentEvent var0) { }
			public void componentMoved(java.awt.event.ComponentEvent var0) { }
			public void componentResized(java.awt.event.ComponentEvent var0) { }
			public void componentShown(java.awt.event.ComponentEvent var0) { }
		}
		protected class AccessibleAWTFocusHandler implements java.awt.event.FocusListener {
			protected AccessibleAWTFocusHandler() { }
			public void focusGained(java.awt.event.FocusEvent var0) { }
			public void focusLost(java.awt.event.FocusEvent var0) { }
		}
	}
	protected class BltBufferStrategy extends java.awt.image.BufferStrategy {
		protected BltBufferStrategy(int var0, java.awt.BufferCapabilities var1) { }
		public boolean contentsLost() { return false; }
		public boolean contentsRestored() { return false; }
		protected void createBackBuffers(int var0) { }
		public java.awt.BufferCapabilities getCapabilities() { return null; }
		public java.awt.Graphics getDrawGraphics() { return null; }
		protected void revalidate() { }
		public void show() { }
		protected java.awt.image.VolatileImage[] backBuffers;
		protected java.awt.BufferCapabilities caps;
		protected int height;
		protected boolean validatedContents;
		protected int width;
	}
	protected class FlipBufferStrategy extends java.awt.image.BufferStrategy {
		protected FlipBufferStrategy(int var0, java.awt.BufferCapabilities var1) throws java.awt.AWTException { }
		public boolean contentsLost() { return false; }
		public boolean contentsRestored() { return false; }
		protected void createBuffers(int var0, java.awt.BufferCapabilities var1) throws java.awt.AWTException { }
		protected void destroyBuffers() { }
		protected void flip(java.awt.BufferCapabilities.FlipContents var0) { }
		protected java.awt.Image getBackBuffer() { return null; }
		public java.awt.BufferCapabilities getCapabilities() { return null; }
		public java.awt.Graphics getDrawGraphics() { return null; }
		protected void revalidate() { }
		public void show() { }
		protected java.awt.BufferCapabilities caps;
		protected java.awt.Image drawBuffer;
		protected java.awt.image.VolatileImage drawVBuffer;
		protected int numBuffers;
		protected boolean validatedContents;
	}
}

