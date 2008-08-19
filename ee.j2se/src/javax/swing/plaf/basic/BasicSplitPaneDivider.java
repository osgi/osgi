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
public class BasicSplitPaneDivider extends java.awt.Container implements java.beans.PropertyChangeListener {
	public BasicSplitPaneDivider(javax.swing.plaf.basic.BasicSplitPaneUI var0) { }
	protected javax.swing.JButton createLeftOneTouchButton() { return null; }
	protected javax.swing.JButton createRightOneTouchButton() { return null; }
	protected void dragDividerTo(int var0) { }
	protected void finishDraggingTo(int var0) { }
	public javax.swing.plaf.basic.BasicSplitPaneUI getBasicSplitPaneUI() { return null; }
	public javax.swing.border.Border getBorder() { return null; }
	public int getDividerSize() { return 0; }
	protected void oneTouchExpandableChanged() { }
	protected void prepareForDragging() { }
	public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	public void setBasicSplitPaneUI(javax.swing.plaf.basic.BasicSplitPaneUI var0) { }
	public void setBorder(javax.swing.border.Border var0) { }
	public void setDividerSize(int var0) { }
	protected final static int ONE_TOUCH_OFFSET = 2;
	protected final static int ONE_TOUCH_SIZE = 6;
	protected int dividerSize;
	protected javax.swing.plaf.basic.BasicSplitPaneDivider.DragController dragger;
	protected java.awt.Component hiddenDivider;
	protected javax.swing.JButton leftButton;
	protected javax.swing.plaf.basic.BasicSplitPaneDivider.MouseHandler mouseHandler;
	protected int orientation;
	protected javax.swing.JButton rightButton;
	protected javax.swing.JSplitPane splitPane;
	protected javax.swing.plaf.basic.BasicSplitPaneUI splitPaneUI;
	protected class DividerLayout implements java.awt.LayoutManager {
		protected DividerLayout() { }
		public void addLayoutComponent(java.lang.String var0, java.awt.Component var1) { }
		public void layoutContainer(java.awt.Container var0) { }
		public java.awt.Dimension minimumLayoutSize(java.awt.Container var0) { return null; }
		public java.awt.Dimension preferredLayoutSize(java.awt.Container var0) { return null; }
		public void removeLayoutComponent(java.awt.Component var0) { }
	}
	protected class DragController {
		protected DragController(java.awt.event.MouseEvent var0) { }
		protected void completeDrag(int var0, int var1) { }
		protected void completeDrag(java.awt.event.MouseEvent var0) { }
		protected void continueDrag(int var0, int var1) { }
		protected void continueDrag(java.awt.event.MouseEvent var0) { }
		protected int getNeededLocation(int var0, int var1) { return 0; }
		protected boolean isValid() { return false; }
		protected int positionForMouseEvent(java.awt.event.MouseEvent var0) { return 0; }
	}
	protected class MouseHandler extends java.awt.event.MouseAdapter implements java.awt.event.MouseMotionListener {
		protected MouseHandler() { }
		public void mouseDragged(java.awt.event.MouseEvent var0) { }
		public void mouseMoved(java.awt.event.MouseEvent var0) { }
	}
	protected class VerticalDragController extends javax.swing.plaf.basic.BasicSplitPaneDivider.DragController {
		protected VerticalDragController(java.awt.event.MouseEvent var0) { super((java.awt.event.MouseEvent) null); }
	}
}

