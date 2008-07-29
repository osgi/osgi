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
public class BasicComboBoxUI extends javax.swing.plaf.ComboBoxUI {
	public BasicComboBoxUI() { }
	public void addEditor() { }
	public void configureArrowButton() { }
	protected void configureEditor() { }
	protected javax.swing.JButton createArrowButton() { return null; }
	protected javax.swing.ComboBoxEditor createEditor() { return null; }
	protected java.awt.event.FocusListener createFocusListener() { return null; }
	protected java.awt.event.ItemListener createItemListener() { return null; }
	protected java.awt.event.KeyListener createKeyListener() { return null; }
	protected java.awt.LayoutManager createLayoutManager() { return null; }
	protected javax.swing.event.ListDataListener createListDataListener() { return null; }
	protected javax.swing.plaf.basic.ComboPopup createPopup() { return null; }
	protected java.beans.PropertyChangeListener createPropertyChangeListener() { return null; }
	protected javax.swing.ListCellRenderer createRenderer() { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected java.awt.Dimension getDefaultSize() { return null; }
	protected java.awt.Dimension getDisplaySize() { return null; }
	protected java.awt.Insets getInsets() { return null; }
	protected void installComponents() { }
	protected void installDefaults() { }
	protected void installKeyboardActions() { }
	protected void installListeners() { }
	public boolean isFocusTraversable(javax.swing.JComboBox var0) { return false; }
	protected boolean isNavigationKey(int var0) { return false; }
	public boolean isPopupVisible(javax.swing.JComboBox var0) { return false; }
	public void paintCurrentValue(java.awt.Graphics var0, java.awt.Rectangle var1, boolean var2) { }
	public void paintCurrentValueBackground(java.awt.Graphics var0, java.awt.Rectangle var1, boolean var2) { }
	protected java.awt.Rectangle rectangleForCurrentValue() { return null; }
	public void removeEditor() { }
	protected void selectNextPossibleValue() { }
	protected void selectPreviousPossibleValue() { }
	public void setPopupVisible(javax.swing.JComboBox var0, boolean var1) { }
	protected void toggleOpenClose() { }
	public void unconfigureArrowButton() { }
	protected void unconfigureEditor() { }
	protected void uninstallComponents() { }
	protected void uninstallDefaults() { }
	protected void uninstallKeyboardActions() { }
	protected void uninstallListeners() { }
	protected javax.swing.JButton arrowButton;
	protected java.awt.Dimension cachedMinimumSize;
	protected javax.swing.JComboBox comboBox;
	protected javax.swing.CellRendererPane currentValuePane;
	protected java.awt.Component editor;
	protected java.awt.event.FocusListener focusListener;
	protected boolean hasFocus;
	protected boolean isMinimumSizeDirty;
	protected java.awt.event.ItemListener itemListener;
	protected java.awt.event.KeyListener keyListener;
	protected javax.swing.JList listBox;
	protected javax.swing.event.ListDataListener listDataListener;
	protected javax.swing.plaf.basic.ComboPopup popup;
	protected java.awt.event.KeyListener popupKeyListener;
	protected java.awt.event.MouseListener popupMouseListener;
	protected java.awt.event.MouseMotionListener popupMouseMotionListener;
	protected java.beans.PropertyChangeListener propertyChangeListener;
	public class ComboBoxLayoutManager implements java.awt.LayoutManager {
		public ComboBoxLayoutManager() { }
		public void addLayoutComponent(java.lang.String var0, java.awt.Component var1) { }
		public void layoutContainer(java.awt.Container var0) { }
		public java.awt.Dimension minimumLayoutSize(java.awt.Container var0) { return null; }
		public java.awt.Dimension preferredLayoutSize(java.awt.Container var0) { return null; }
		public void removeLayoutComponent(java.awt.Component var0) { }
	}
	public class FocusHandler implements java.awt.event.FocusListener {
		public FocusHandler() { }
		public void focusGained(java.awt.event.FocusEvent var0) { }
		public void focusLost(java.awt.event.FocusEvent var0) { }
	}
	public class ItemHandler implements java.awt.event.ItemListener {
		public ItemHandler() { }
		public void itemStateChanged(java.awt.event.ItemEvent var0) { }
	}
	public class KeyHandler extends java.awt.event.KeyAdapter {
		public KeyHandler() { }
	}
	public class ListDataHandler implements javax.swing.event.ListDataListener {
		public ListDataHandler() { }
		public void contentsChanged(javax.swing.event.ListDataEvent var0) { }
		public void intervalAdded(javax.swing.event.ListDataEvent var0) { }
		public void intervalRemoved(javax.swing.event.ListDataEvent var0) { }
	}
	public class PropertyChangeHandler implements java.beans.PropertyChangeListener {
		public PropertyChangeHandler() { }
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	}
}

