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
public class BasicSplitPaneUI extends javax.swing.plaf.SplitPaneUI {
	public class BasicHorizontalLayoutManager implements java.awt.LayoutManager2 {
		protected java.awt.Component[] components;
		protected int[] sizes;
		public void addLayoutComponent(java.awt.Component var0, java.lang.Object var1) { }
		public void addLayoutComponent(java.lang.String var0, java.awt.Component var1) { }
		protected int getAvailableSize(java.awt.Dimension var0, java.awt.Insets var1) { return 0; }
		protected int getInitialLocation(java.awt.Insets var0) { return 0; }
		public float getLayoutAlignmentX(java.awt.Container var0) { return 0.0f; }
		public float getLayoutAlignmentY(java.awt.Container var0) { return 0.0f; }
		protected int getPreferredSizeOfComponent(java.awt.Component var0) { return 0; }
		protected int getSizeOfComponent(java.awt.Component var0) { return 0; }
		protected int[] getSizes() { return null; }
		public void invalidateLayout(java.awt.Container var0) { }
		public void layoutContainer(java.awt.Container var0) { }
		public java.awt.Dimension maximumLayoutSize(java.awt.Container var0) { return null; }
		public java.awt.Dimension minimumLayoutSize(java.awt.Container var0) { return null; }
		public java.awt.Dimension preferredLayoutSize(java.awt.Container var0) { return null; }
		public void removeLayoutComponent(java.awt.Component var0) { }
		protected void resetSizeAt(int var0) { }
		public void resetToPreferredSizes() { }
		protected void setComponentToSize(java.awt.Component var0, int var1, int var2, java.awt.Insets var3, java.awt.Dimension var4) { }
		protected void setSizes(int[] var0) { }
		protected void updateComponents() { }
		BasicHorizontalLayoutManager() { } /* generated constructor to prevent compiler adding default public constructor */
	}
	public class BasicVerticalLayoutManager extends javax.swing.plaf.basic.BasicSplitPaneUI.BasicHorizontalLayoutManager {
		public BasicVerticalLayoutManager() { } 
	}
	public class FocusHandler extends java.awt.event.FocusAdapter {
		public FocusHandler() { } 
	}
	public class KeyboardDownRightHandler implements java.awt.event.ActionListener {
		public KeyboardDownRightHandler() { } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public class KeyboardEndHandler implements java.awt.event.ActionListener {
		public KeyboardEndHandler() { } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public class KeyboardHomeHandler implements java.awt.event.ActionListener {
		public KeyboardHomeHandler() { } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public class KeyboardResizeToggleHandler implements java.awt.event.ActionListener {
		public KeyboardResizeToggleHandler() { } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public class KeyboardUpLeftHandler implements java.awt.event.ActionListener {
		public KeyboardUpLeftHandler() { } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public class PropertyHandler implements java.beans.PropertyChangeListener {
		public PropertyHandler() { } 
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	}
	protected static int KEYBOARD_DIVIDER_MOVE_OFFSET;
	protected final static java.lang.String NON_CONTINUOUS_DIVIDER = "nonContinuousDivider";
	protected int beginDragDividerLocation;
	protected javax.swing.plaf.basic.BasicSplitPaneDivider divider;
	/** @deprecated */
	@java.lang.Deprecated
	protected javax.swing.KeyStroke dividerResizeToggleKey;
	protected int dividerSize;
	/** @deprecated */
	@java.lang.Deprecated
	protected javax.swing.KeyStroke downKey;
	protected boolean draggingHW;
	/** @deprecated */
	@java.lang.Deprecated
	protected javax.swing.KeyStroke endKey;
	protected java.awt.event.FocusListener focusListener;
	/** @deprecated */
	@java.lang.Deprecated
	protected javax.swing.KeyStroke homeKey;
	/** @deprecated */
	@java.lang.Deprecated
	protected java.awt.event.ActionListener keyboardDownRightListener;
	/** @deprecated */
	@java.lang.Deprecated
	protected java.awt.event.ActionListener keyboardEndListener;
	/** @deprecated */
	@java.lang.Deprecated
	protected java.awt.event.ActionListener keyboardHomeListener;
	/** @deprecated */
	@java.lang.Deprecated
	protected java.awt.event.ActionListener keyboardResizeToggleListener;
	/** @deprecated */
	@java.lang.Deprecated
	protected java.awt.event.ActionListener keyboardUpLeftListener;
	protected javax.swing.plaf.basic.BasicSplitPaneUI.BasicHorizontalLayoutManager layoutManager;
	/** @deprecated */
	@java.lang.Deprecated
	protected javax.swing.KeyStroke leftKey;
	protected java.awt.Component nonContinuousLayoutDivider;
	protected java.beans.PropertyChangeListener propertyChangeListener;
	/** @deprecated */
	@java.lang.Deprecated
	protected javax.swing.KeyStroke rightKey;
	protected javax.swing.JSplitPane splitPane;
	/** @deprecated */
	@java.lang.Deprecated
	protected javax.swing.KeyStroke upKey;
	public BasicSplitPaneUI() { } 
	public javax.swing.plaf.basic.BasicSplitPaneDivider createDefaultDivider() { return null; }
	protected java.awt.Component createDefaultNonContinuousLayoutDivider() { return null; }
	protected java.awt.event.FocusListener createFocusListener() { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	protected java.awt.event.ActionListener createKeyboardDownRightListener() { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	protected java.awt.event.ActionListener createKeyboardEndListener() { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	protected java.awt.event.ActionListener createKeyboardHomeListener() { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	protected java.awt.event.ActionListener createKeyboardResizeToggleListener() { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	protected java.awt.event.ActionListener createKeyboardUpLeftListener() { return null; }
	protected java.beans.PropertyChangeListener createPropertyChangeListener() { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected void dragDividerTo(int var0) { }
	protected void finishDraggingTo(int var0) { }
	public void finishedPaintingChildren(javax.swing.JSplitPane var0, java.awt.Graphics var1) { }
	public javax.swing.plaf.basic.BasicSplitPaneDivider getDivider() { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	protected int getDividerBorderSize() { return 0; }
	public int getDividerLocation(javax.swing.JSplitPane var0) { return 0; }
	public java.awt.Insets getInsets(javax.swing.JComponent var0) { return null; }
	public int getLastDragLocation() { return 0; }
	public int getMaximumDividerLocation(javax.swing.JSplitPane var0) { return 0; }
	public int getMinimumDividerLocation(javax.swing.JSplitPane var0) { return 0; }
	public java.awt.Component getNonContinuousLayoutDivider() { return null; }
	public int getOrientation() { return 0; }
	public javax.swing.JSplitPane getSplitPane() { return null; }
	protected void installDefaults() { }
	protected void installKeyboardActions() { }
	protected void installListeners() { }
	public boolean isContinuousLayout() { return false; }
	protected void resetLayoutManager() { }
	public void resetToPreferredSizes(javax.swing.JSplitPane var0) { }
	public void setContinuousLayout(boolean var0) { }
	public void setDividerLocation(javax.swing.JSplitPane var0, int var1) { }
	public void setLastDragLocation(int var0) { }
	protected void setNonContinuousLayoutDivider(java.awt.Component var0) { }
	protected void setNonContinuousLayoutDivider(java.awt.Component var0, boolean var1) { }
	public void setOrientation(int var0) { }
	protected void startDragging() { }
	protected void uninstallDefaults() { }
	protected void uninstallKeyboardActions() { }
	protected void uninstallListeners() { }
}

