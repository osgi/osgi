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
public class BasicListUI extends javax.swing.plaf.ListUI {
	public BasicListUI() { }
	protected int convertRowToY(int var0) { return 0; }
	protected int convertYToRow(int var0) { return 0; }
	protected java.awt.event.FocusListener createFocusListener() { return null; }
	protected javax.swing.event.ListDataListener createListDataListener() { return null; }
	protected javax.swing.event.ListSelectionListener createListSelectionListener() { return null; }
	protected javax.swing.event.MouseInputListener createMouseInputListener() { return null; }
	protected java.beans.PropertyChangeListener createPropertyChangeListener() { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	public java.awt.Rectangle getCellBounds(javax.swing.JList var0, int var1, int var2) { return null; }
	protected int getRowHeight(int var0) { return 0; }
	public java.awt.Point indexToLocation(javax.swing.JList var0, int var1) { return null; }
	protected void installDefaults() { }
	protected void installKeyboardActions() { }
	protected void installListeners() { }
	public int locationToIndex(javax.swing.JList var0, java.awt.Point var1) { return 0; }
	protected void maybeUpdateLayoutState() { }
	protected void paintCell(java.awt.Graphics var0, int var1, java.awt.Rectangle var2, javax.swing.ListCellRenderer var3, javax.swing.ListModel var4, javax.swing.ListSelectionModel var5, int var6) { }
	protected void selectNextIndex() { }
	protected void selectPreviousIndex() { }
	protected void uninstallDefaults() { }
	protected void uninstallKeyboardActions() { }
	protected void uninstallListeners() { }
	protected void updateLayoutState() { }
	protected int cellHeight;
	protected int[] cellHeights;
	protected final static int cellRendererChanged = 64;
	protected int cellWidth;
	protected final static int fixedCellHeightChanged = 16;
	protected final static int fixedCellWidthChanged = 8;
	protected java.awt.event.FocusListener focusListener;
	protected final static int fontChanged = 4;
	protected javax.swing.JList list;
	protected javax.swing.event.ListDataListener listDataListener;
	protected javax.swing.event.ListSelectionListener listSelectionListener;
	protected final static int modelChanged = 1;
	protected javax.swing.event.MouseInputListener mouseInputListener;
	protected java.beans.PropertyChangeListener propertyChangeListener;
	protected final static int prototypeCellValueChanged = 32;
	protected javax.swing.CellRendererPane rendererPane;
	protected final static int selectionModelChanged = 2;
	protected int updateLayoutStateNeeded;
	public class FocusHandler implements java.awt.event.FocusListener {
		public FocusHandler() { }
		public void focusGained(java.awt.event.FocusEvent var0) { }
		public void focusLost(java.awt.event.FocusEvent var0) { }
		protected void repaintCellFocus() { }
	}
	public class ListDataHandler implements javax.swing.event.ListDataListener {
		public ListDataHandler() { }
		public void contentsChanged(javax.swing.event.ListDataEvent var0) { }
		public void intervalAdded(javax.swing.event.ListDataEvent var0) { }
		public void intervalRemoved(javax.swing.event.ListDataEvent var0) { }
	}
	public class ListSelectionHandler implements javax.swing.event.ListSelectionListener {
		public ListSelectionHandler() { }
		public void valueChanged(javax.swing.event.ListSelectionEvent var0) { }
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
	public class PropertyChangeHandler implements java.beans.PropertyChangeListener {
		public PropertyChangeHandler() { }
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	}
}

