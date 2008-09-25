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
public class BasicComboPopup extends javax.swing.JPopupMenu implements javax.swing.plaf.basic.ComboPopup {
	public BasicComboPopup(javax.swing.JComboBox var0) { }
	protected void autoScrollDown() { }
	protected void autoScrollUp() { }
	protected java.awt.Rectangle computePopupBounds(int var0, int var1, int var2, int var3) { return null; }
	protected void configureList() { }
	protected void configurePopup() { }
	protected void configureScroller() { }
	protected java.awt.event.MouseEvent convertMouseEvent(java.awt.event.MouseEvent var0) { return null; }
	protected java.awt.event.ItemListener createItemListener() { return null; }
	protected java.awt.event.KeyListener createKeyListener() { return null; }
	protected javax.swing.JList createList() { return null; }
	protected javax.swing.event.ListDataListener createListDataListener() { return null; }
	protected java.awt.event.MouseListener createListMouseListener() { return null; }
	protected java.awt.event.MouseMotionListener createListMouseMotionListener() { return null; }
	protected javax.swing.event.ListSelectionListener createListSelectionListener() { return null; }
	protected java.awt.event.MouseListener createMouseListener() { return null; }
	protected java.awt.event.MouseMotionListener createMouseMotionListener() { return null; }
	protected java.beans.PropertyChangeListener createPropertyChangeListener() { return null; }
	protected javax.swing.JScrollPane createScroller() { return null; }
	protected void delegateFocus(java.awt.event.MouseEvent var0) { }
	public java.awt.event.KeyListener getKeyListener() { return null; }
	public javax.swing.JList getList() { return null; }
	public java.awt.event.MouseListener getMouseListener() { return null; }
	public java.awt.event.MouseMotionListener getMouseMotionListener() { return null; }
	protected int getPopupHeightForRowCount(int var0) { return 0; }
	protected void installComboBoxListeners() { }
	protected void installComboBoxModelListeners(javax.swing.ComboBoxModel var0) { }
	protected void installKeyboardActions() { }
	protected void installListListeners() { }
	protected void startAutoScrolling(int var0) { }
	protected void stopAutoScrolling() { }
	protected void togglePopup() { }
	protected void uninstallComboBoxModelListeners(javax.swing.ComboBoxModel var0) { }
	protected void uninstallKeyboardActions() { }
	public void uninstallingUI() { }
	protected void updateListBoxSelectionForEvent(java.awt.event.MouseEvent var0, boolean var1) { }
	protected final static int SCROLL_DOWN = 1;
	protected final static int SCROLL_UP = 0;
	protected javax.swing.Timer autoscrollTimer;
	protected javax.swing.JComboBox comboBox;
	protected boolean hasEntered;
	protected boolean isAutoScrolling;
	protected java.awt.event.ItemListener itemListener;
	protected java.awt.event.KeyListener keyListener;
	protected javax.swing.JList list;
	protected javax.swing.event.ListDataListener listDataListener;
	protected java.awt.event.MouseListener listMouseListener;
	protected java.awt.event.MouseMotionListener listMouseMotionListener;
	protected javax.swing.event.ListSelectionListener listSelectionListener;
	protected java.awt.event.MouseListener mouseListener;
	protected java.awt.event.MouseMotionListener mouseMotionListener;
	protected java.beans.PropertyChangeListener propertyChangeListener;
	protected int scrollDirection;
	protected javax.swing.JScrollPane scroller;
	protected boolean valueIsAdjusting;
	public class InvocationKeyHandler extends java.awt.event.KeyAdapter {
		public InvocationKeyHandler() { }
	}
	protected class InvocationMouseHandler extends java.awt.event.MouseAdapter {
		protected InvocationMouseHandler() { }
	}
	protected class InvocationMouseMotionHandler extends java.awt.event.MouseMotionAdapter {
		protected InvocationMouseMotionHandler() { }
	}
	protected class ItemHandler implements java.awt.event.ItemListener {
		protected ItemHandler() { }
		public void itemStateChanged(java.awt.event.ItemEvent var0) { }
	}
	public class ListDataHandler implements javax.swing.event.ListDataListener {
		public ListDataHandler() { }
		public void contentsChanged(javax.swing.event.ListDataEvent var0) { }
		public void intervalAdded(javax.swing.event.ListDataEvent var0) { }
		public void intervalRemoved(javax.swing.event.ListDataEvent var0) { }
	}
	protected class ListMouseHandler extends java.awt.event.MouseAdapter {
		protected ListMouseHandler() { }
	}
	protected class ListMouseMotionHandler extends java.awt.event.MouseMotionAdapter {
		protected ListMouseMotionHandler() { }
	}
	protected class ListSelectionHandler implements javax.swing.event.ListSelectionListener {
		protected ListSelectionHandler() { }
		public void valueChanged(javax.swing.event.ListSelectionEvent var0) { }
	}
	protected class PropertyChangeHandler implements java.beans.PropertyChangeListener {
		protected PropertyChangeHandler() { }
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	}
}

