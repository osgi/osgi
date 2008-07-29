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
public class BasicOptionPaneUI extends javax.swing.plaf.OptionPaneUI {
	public BasicOptionPaneUI() { }
	protected void addButtonComponents(java.awt.Container var0, java.lang.Object[] var1, int var2) { }
	protected void addIcon(java.awt.Container var0) { }
	protected void addMessageComponents(java.awt.Container var0, java.awt.GridBagConstraints var1, java.lang.Object var2, int var3, boolean var4) { }
	protected void burstStringInto(java.awt.Container var0, java.lang.String var1, int var2) { }
	public boolean containsCustomComponents(javax.swing.JOptionPane var0) { return false; }
	protected java.awt.event.ActionListener createButtonActionListener(int var0) { return null; }
	protected java.awt.Container createButtonArea() { return null; }
	protected java.awt.LayoutManager createLayoutManager() { return null; }
	protected java.awt.Container createMessageArea() { return null; }
	protected java.beans.PropertyChangeListener createPropertyChangeListener() { return null; }
	protected java.awt.Container createSeparator() { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected java.lang.Object[] getButtons() { return null; }
	protected javax.swing.Icon getIcon() { return null; }
	protected javax.swing.Icon getIconForType(int var0) { return null; }
	protected int getInitialValueIndex() { return 0; }
	protected int getMaxCharactersPerLineCount() { return 0; }
	protected java.lang.Object getMessage() { return null; }
	public java.awt.Dimension getMinimumOptionPaneSize() { return null; }
	protected boolean getSizeButtonsToSameWidth() { return false; }
	protected void installComponents() { }
	protected void installDefaults() { }
	protected void installKeyboardActions() { }
	protected void installListeners() { }
	protected void resetInputValue() { }
	public void selectInitialValue(javax.swing.JOptionPane var0) { }
	protected void uninstallComponents() { }
	protected void uninstallDefaults() { }
	protected void uninstallKeyboardActions() { }
	protected void uninstallListeners() { }
	public final static int MinimumHeight = 90;
	public final static int MinimumWidth = 262;
	protected boolean hasCustomComponents;
	protected java.awt.Component initialFocusComponent;
	protected javax.swing.JComponent inputComponent;
	protected java.awt.Dimension minimumSize;
	protected javax.swing.JOptionPane optionPane;
	protected java.beans.PropertyChangeListener propertyChangeListener;
	public class ButtonActionListener implements java.awt.event.ActionListener {
		public ButtonActionListener(int var0) { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
		protected int buttonIndex;
	}
	public static class ButtonAreaLayout implements java.awt.LayoutManager {
		public ButtonAreaLayout(boolean var0, int var1) { }
		public void addLayoutComponent(java.lang.String var0, java.awt.Component var1) { }
		public boolean getCentersChildren() { return false; }
		public int getPadding() { return 0; }
		public boolean getSyncAllWidths() { return false; }
		public void layoutContainer(java.awt.Container var0) { }
		public java.awt.Dimension minimumLayoutSize(java.awt.Container var0) { return null; }
		public java.awt.Dimension preferredLayoutSize(java.awt.Container var0) { return null; }
		public void removeLayoutComponent(java.awt.Component var0) { }
		public void setCentersChildren(boolean var0) { }
		public void setPadding(int var0) { }
		public void setSyncAllWidths(boolean var0) { }
		protected boolean centersChildren;
		protected int padding;
		protected boolean syncAllWidths;
	}
	public class PropertyChangeHandler implements java.beans.PropertyChangeListener {
		public PropertyChangeHandler() { }
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	}
}

