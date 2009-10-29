/*
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
public class BasicScrollBarUI extends javax.swing.plaf.ScrollBarUI implements java.awt.LayoutManager, javax.swing.SwingConstants {
	public BasicScrollBarUI() { }
	public void addLayoutComponent(java.lang.String var0, java.awt.Component var1) { }
	protected void configureScrollBarColors() { }
	protected javax.swing.plaf.basic.BasicScrollBarUI.ArrowButtonListener createArrowButtonListener() { return null; }
	protected javax.swing.JButton createDecreaseButton(int var0) { return null; }
	protected javax.swing.JButton createIncreaseButton(int var0) { return null; }
	protected javax.swing.plaf.basic.BasicScrollBarUI.ModelListener createModelListener() { return null; }
	protected java.beans.PropertyChangeListener createPropertyChangeListener() { return null; }
	protected javax.swing.plaf.basic.BasicScrollBarUI.ScrollListener createScrollListener() { return null; }
	protected javax.swing.plaf.basic.BasicScrollBarUI.TrackListener createTrackListener() { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected java.awt.Dimension getMaximumThumbSize() { return null; }
	protected java.awt.Dimension getMinimumThumbSize() { return null; }
	protected java.awt.Rectangle getThumbBounds() { return null; }
	protected java.awt.Rectangle getTrackBounds() { return null; }
	protected void installComponents() { }
	protected void installDefaults() { }
	protected void installKeyboardActions() { }
	protected void installListeners() { }
	public void layoutContainer(java.awt.Container var0) { }
	protected void layoutHScrollbar(javax.swing.JScrollBar var0) { }
	protected void layoutVScrollbar(javax.swing.JScrollBar var0) { }
	public java.awt.Dimension minimumLayoutSize(java.awt.Container var0) { return null; }
	protected void paintDecreaseHighlight(java.awt.Graphics var0) { }
	protected void paintIncreaseHighlight(java.awt.Graphics var0) { }
	protected void paintThumb(java.awt.Graphics var0, javax.swing.JComponent var1, java.awt.Rectangle var2) { }
	protected void paintTrack(java.awt.Graphics var0, javax.swing.JComponent var1, java.awt.Rectangle var2) { }
	public java.awt.Dimension preferredLayoutSize(java.awt.Container var0) { return null; }
	public void removeLayoutComponent(java.awt.Component var0) { }
	protected void scrollByBlock(int var0) { }
	protected void scrollByUnit(int var0) { }
	protected void setThumbBounds(int var0, int var1, int var2, int var3) { }
	protected void uninstallComponents() { }
	protected void uninstallDefaults() { }
	protected void uninstallKeyboardActions() { }
	protected void uninstallListeners() { }
	protected final static int DECREASE_HIGHLIGHT = 1;
	protected final static int INCREASE_HIGHLIGHT = 2;
	protected final static int NO_HIGHLIGHT = 0;
	protected javax.swing.plaf.basic.BasicScrollBarUI.ArrowButtonListener buttonListener;
	protected javax.swing.JButton decrButton;
	protected javax.swing.JButton incrButton;
	protected boolean isDragging;
	protected java.awt.Dimension maximumThumbSize;
	protected java.awt.Dimension minimumThumbSize;
	protected javax.swing.plaf.basic.BasicScrollBarUI.ModelListener modelListener;
	protected java.beans.PropertyChangeListener propertyChangeListener;
	protected javax.swing.plaf.basic.BasicScrollBarUI.ScrollListener scrollListener;
	protected javax.swing.Timer scrollTimer;
	protected javax.swing.JScrollBar scrollbar;
	protected java.awt.Color thumbColor;
	protected java.awt.Color thumbDarkShadowColor;
	protected java.awt.Color thumbHighlightColor;
	protected java.awt.Color thumbLightShadowColor;
	protected java.awt.Rectangle thumbRect;
	protected java.awt.Color trackColor;
	protected int trackHighlight;
	protected java.awt.Color trackHighlightColor;
	protected javax.swing.plaf.basic.BasicScrollBarUI.TrackListener trackListener;
	protected java.awt.Rectangle trackRect;
	protected class ArrowButtonListener extends java.awt.event.MouseAdapter {
		protected ArrowButtonListener() { }
	}
	protected class ModelListener implements javax.swing.event.ChangeListener {
		protected ModelListener() { }
		public void stateChanged(javax.swing.event.ChangeEvent var0) { }
	}
	public class PropertyChangeHandler implements java.beans.PropertyChangeListener {
		public PropertyChangeHandler() { }
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	}
	protected class ScrollListener implements java.awt.event.ActionListener {
		public ScrollListener() { }
		public ScrollListener(int var0, boolean var1) { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
		public void setDirection(int var0) { }
		public void setScrollByBlock(boolean var0) { }
	}
	protected class TrackListener extends java.awt.event.MouseAdapter implements java.awt.event.MouseMotionListener {
		protected TrackListener() { }
		public void mouseDragged(java.awt.event.MouseEvent var0) { }
		public void mouseMoved(java.awt.event.MouseEvent var0) { }
		protected int currentMouseX;
		protected int currentMouseY;
		protected int offset;
	}
}

