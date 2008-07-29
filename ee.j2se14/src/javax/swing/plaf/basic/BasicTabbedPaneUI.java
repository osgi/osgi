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
public class BasicTabbedPaneUI extends javax.swing.plaf.TabbedPaneUI implements javax.swing.SwingConstants {
	public BasicTabbedPaneUI() { }
	protected void assureRectsCreated(int var0) { }
	protected int calculateMaxTabHeight(int var0) { return 0; }
	protected int calculateMaxTabWidth(int var0) { return 0; }
	protected int calculateTabAreaHeight(int var0, int var1, int var2) { return 0; }
	protected int calculateTabAreaWidth(int var0, int var1, int var2) { return 0; }
	protected int calculateTabHeight(int var0, int var1, int var2) { return 0; }
	protected int calculateTabWidth(int var0, int var1, java.awt.FontMetrics var2) { return 0; }
	protected javax.swing.event.ChangeListener createChangeListener() { return null; }
	protected java.awt.event.FocusListener createFocusListener() { return null; }
	protected java.awt.LayoutManager createLayoutManager() { return null; }
	protected java.awt.event.MouseListener createMouseListener() { return null; }
	protected java.beans.PropertyChangeListener createPropertyChangeListener() { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected void expandTabRunsArray() { }
	protected java.awt.Insets getContentBorderInsets(int var0) { return null; }
	protected java.awt.FontMetrics getFontMetrics() { return null; }
	protected javax.swing.Icon getIconForTab(int var0) { return null; }
	protected int getNextTabIndex(int var0) { return 0; }
	protected int getNextTabIndexInRun(int var0, int var1) { return 0; }
	protected int getNextTabRun(int var0) { return 0; }
	protected int getPreviousTabIndex(int var0) { return 0; }
	protected int getPreviousTabIndexInRun(int var0, int var1) { return 0; }
	protected int getPreviousTabRun(int var0) { return 0; }
	protected int getRunForTab(int var0, int var1) { return 0; }
	protected java.awt.Insets getSelectedTabPadInsets(int var0) { return null; }
	protected java.awt.Insets getTabAreaInsets(int var0) { return null; }
	protected java.awt.Rectangle getTabBounds(int var0, java.awt.Rectangle var1) { return null; }
	public java.awt.Rectangle getTabBounds(javax.swing.JTabbedPane var0, int var1) { return null; }
	protected java.awt.Insets getTabInsets(int var0, int var1) { return null; }
	protected int getTabLabelShiftX(int var0, int var1, boolean var2) { return 0; }
	protected int getTabLabelShiftY(int var0, int var1, boolean var2) { return 0; }
	public int getTabRunCount(javax.swing.JTabbedPane var0) { return 0; }
	protected int getTabRunIndent(int var0, int var1) { return 0; }
	protected int getTabRunOffset(int var0, int var1, int var2, boolean var3) { return 0; }
	protected int getTabRunOverlay(int var0) { return 0; }
	protected javax.swing.text.View getTextViewForTab(int var0) { return null; }
	protected java.awt.Component getVisibleComponent() { return null; }
	protected void installComponents() { }
	protected void installDefaults() { }
	protected void installKeyboardActions() { }
	protected void installListeners() { }
	protected int lastTabInRun(int var0, int var1) { return 0; }
	protected void layoutLabel(int var0, java.awt.FontMetrics var1, int var2, java.lang.String var3, javax.swing.Icon var4, java.awt.Rectangle var5, java.awt.Rectangle var6, java.awt.Rectangle var7, boolean var8) { }
	protected void navigateSelectedTab(int var0) { }
	protected void paintContentBorder(java.awt.Graphics var0, int var1, int var2) { }
	protected void paintContentBorderBottomEdge(java.awt.Graphics var0, int var1, int var2, int var3, int var4, int var5, int var6) { }
	protected void paintContentBorderLeftEdge(java.awt.Graphics var0, int var1, int var2, int var3, int var4, int var5, int var6) { }
	protected void paintContentBorderRightEdge(java.awt.Graphics var0, int var1, int var2, int var3, int var4, int var5, int var6) { }
	protected void paintContentBorderTopEdge(java.awt.Graphics var0, int var1, int var2, int var3, int var4, int var5, int var6) { }
	protected void paintFocusIndicator(java.awt.Graphics var0, int var1, java.awt.Rectangle[] var2, int var3, java.awt.Rectangle var4, java.awt.Rectangle var5, boolean var6) { }
	protected void paintIcon(java.awt.Graphics var0, int var1, int var2, javax.swing.Icon var3, java.awt.Rectangle var4, boolean var5) { }
	protected void paintTab(java.awt.Graphics var0, int var1, java.awt.Rectangle[] var2, int var3, java.awt.Rectangle var4, java.awt.Rectangle var5) { }
	protected void paintTabArea(java.awt.Graphics var0, int var1, int var2) { }
	protected void paintTabBackground(java.awt.Graphics var0, int var1, int var2, int var3, int var4, int var5, int var6, boolean var7) { }
	protected void paintTabBorder(java.awt.Graphics var0, int var1, int var2, int var3, int var4, int var5, int var6, boolean var7) { }
	protected void paintText(java.awt.Graphics var0, int var1, java.awt.Font var2, java.awt.FontMetrics var3, int var4, java.lang.String var5, java.awt.Rectangle var6, boolean var7) { }
	protected static void rotateInsets(java.awt.Insets var0, java.awt.Insets var1, int var2) { }
	protected void selectAdjacentRunTab(int var0, int var1, int var2) { }
	protected void selectNextTab(int var0) { }
	protected void selectNextTabInRun(int var0) { }
	protected void selectPreviousTab(int var0) { }
	protected void selectPreviousTabInRun(int var0) { }
	protected void setVisibleComponent(java.awt.Component var0) { }
	protected boolean shouldPadTabRun(int var0, int var1) { return false; }
	protected boolean shouldRotateTabRuns(int var0) { return false; }
	public int tabForCoordinate(javax.swing.JTabbedPane var0, int var1, int var2) { return 0; }
	protected void uninstallComponents() { }
	protected void uninstallDefaults() { }
	protected void uninstallKeyboardActions() { }
	protected void uninstallListeners() { }
	protected java.awt.Rectangle calcRect;
	protected java.awt.Insets contentBorderInsets;
	protected java.awt.Color darkShadow;
	/** @deprecated */ protected javax.swing.KeyStroke downKey;
	protected java.awt.Color focus;
	protected java.awt.event.FocusListener focusListener;
	protected java.awt.Color highlight;
	/** @deprecated */ protected javax.swing.KeyStroke leftKey;
	protected java.awt.Color lightHighlight;
	protected int maxTabHeight;
	protected int maxTabWidth;
	protected java.awt.event.MouseListener mouseListener;
	protected java.beans.PropertyChangeListener propertyChangeListener;
	protected java.awt.Rectangle[] rects;
	/** @deprecated */ protected javax.swing.KeyStroke rightKey;
	protected int runCount;
	protected int selectedRun;
	protected java.awt.Insets selectedTabPadInsets;
	protected java.awt.Color shadow;
	protected java.awt.Insets tabAreaInsets;
	protected javax.swing.event.ChangeListener tabChangeListener;
	protected java.awt.Insets tabInsets;
	protected javax.swing.JTabbedPane tabPane;
	protected int tabRunOverlay;
	protected int[] tabRuns;
	protected int textIconGap;
	/** @deprecated */ protected javax.swing.KeyStroke upKey;
	public class FocusHandler extends java.awt.event.FocusAdapter {
		public FocusHandler() { }
	}
	public class MouseHandler extends java.awt.event.MouseAdapter {
		public MouseHandler() { }
	}
	public class PropertyChangeHandler implements java.beans.PropertyChangeListener {
		public PropertyChangeHandler() { }
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	}
	public class TabSelectionHandler implements javax.swing.event.ChangeListener {
		public TabSelectionHandler() { }
		public void stateChanged(javax.swing.event.ChangeEvent var0) { }
	}
	public class TabbedPaneLayout implements java.awt.LayoutManager {
		public TabbedPaneLayout() { }
		public void addLayoutComponent(java.lang.String var0, java.awt.Component var1) { }
		public void calculateLayoutInfo() { }
		protected java.awt.Dimension calculateSize(boolean var0) { return null; }
		protected void calculateTabRects(int var0, int var1) { }
		public void layoutContainer(java.awt.Container var0) { }
		public java.awt.Dimension minimumLayoutSize(java.awt.Container var0) { return null; }
		protected void normalizeTabRuns(int var0, int var1, int var2, int var3) { }
		protected void padSelectedTab(int var0, int var1) { }
		protected void padTabRun(int var0, int var1, int var2, int var3) { }
		public java.awt.Dimension preferredLayoutSize(java.awt.Container var0) { return null; }
		protected int preferredTabAreaHeight(int var0, int var1) { return 0; }
		protected int preferredTabAreaWidth(int var0, int var1) { return 0; }
		public void removeLayoutComponent(java.awt.Component var0) { }
		protected void rotateTabRuns(int var0, int var1) { }
	}
}

