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

package javax.swing;
public abstract class AbstractButton extends javax.swing.JComponent implements java.awt.ItemSelectable, javax.swing.SwingConstants {
	public AbstractButton() { }
	public void addActionListener(java.awt.event.ActionListener var0) { }
	public void addChangeListener(javax.swing.event.ChangeListener var0) { }
	public void addItemListener(java.awt.event.ItemListener var0) { }
	protected int checkHorizontalKey(int var0, java.lang.String var1) { return 0; }
	protected int checkVerticalKey(int var0, java.lang.String var1) { return 0; }
	protected void configurePropertiesFromAction(javax.swing.Action var0) { }
	protected java.awt.event.ActionListener createActionListener() { return null; }
	protected java.beans.PropertyChangeListener createActionPropertyChangeListener(javax.swing.Action var0) { return null; }
	protected javax.swing.event.ChangeListener createChangeListener() { return null; }
	protected java.awt.event.ItemListener createItemListener() { return null; }
	public void doClick() { }
	public void doClick(int var0) { }
	protected void fireActionPerformed(java.awt.event.ActionEvent var0) { }
	protected void fireItemStateChanged(java.awt.event.ItemEvent var0) { }
	protected void fireStateChanged() { }
	public javax.swing.Action getAction() { return null; }
	public java.lang.String getActionCommand() { return null; }
	public java.awt.event.ActionListener[] getActionListeners() { return null; }
	public javax.swing.event.ChangeListener[] getChangeListeners() { return null; }
	public javax.swing.Icon getDisabledIcon() { return null; }
	public javax.swing.Icon getDisabledSelectedIcon() { return null; }
	public int getDisplayedMnemonicIndex() { return 0; }
	public int getHorizontalAlignment() { return 0; }
	public int getHorizontalTextPosition() { return 0; }
	public javax.swing.Icon getIcon() { return null; }
	public int getIconTextGap() { return 0; }
	public java.awt.event.ItemListener[] getItemListeners() { return null; }
	/** @deprecated */ public java.lang.String getLabel() { return null; }
	public java.awt.Insets getMargin() { return null; }
	public int getMnemonic() { return 0; }
	public javax.swing.ButtonModel getModel() { return null; }
	public long getMultiClickThreshhold() { return 0l; }
	public javax.swing.Icon getPressedIcon() { return null; }
	public javax.swing.Icon getRolloverIcon() { return null; }
	public javax.swing.Icon getRolloverSelectedIcon() { return null; }
	public javax.swing.Icon getSelectedIcon() { return null; }
	public java.lang.Object[] getSelectedObjects() { return null; }
	public java.lang.String getText() { return null; }
	public javax.swing.plaf.ButtonUI getUI() { return null; }
	public int getVerticalAlignment() { return 0; }
	public int getVerticalTextPosition() { return 0; }
	protected void init(java.lang.String var0, javax.swing.Icon var1) { }
	public boolean isBorderPainted() { return false; }
	public boolean isContentAreaFilled() { return false; }
	public boolean isFocusPainted() { return false; }
	public boolean isRolloverEnabled() { return false; }
	public boolean isSelected() { return false; }
	public void removeActionListener(java.awt.event.ActionListener var0) { }
	public void removeChangeListener(javax.swing.event.ChangeListener var0) { }
	public void removeItemListener(java.awt.event.ItemListener var0) { }
	public void setAction(javax.swing.Action var0) { }
	public void setActionCommand(java.lang.String var0) { }
	public void setBorderPainted(boolean var0) { }
	public void setContentAreaFilled(boolean var0) { }
	public void setDisabledIcon(javax.swing.Icon var0) { }
	public void setDisabledSelectedIcon(javax.swing.Icon var0) { }
	public void setDisplayedMnemonicIndex(int var0) { }
	public void setFocusPainted(boolean var0) { }
	public void setHorizontalAlignment(int var0) { }
	public void setHorizontalTextPosition(int var0) { }
	public void setIcon(javax.swing.Icon var0) { }
	public void setIconTextGap(int var0) { }
	/** @deprecated */ public void setLabel(java.lang.String var0) { }
	public void setMargin(java.awt.Insets var0) { }
	public void setMnemonic(char var0) { }
	public void setMnemonic(int var0) { }
	public void setModel(javax.swing.ButtonModel var0) { }
	public void setMultiClickThreshhold(long var0) { }
	public void setPressedIcon(javax.swing.Icon var0) { }
	public void setRolloverEnabled(boolean var0) { }
	public void setRolloverIcon(javax.swing.Icon var0) { }
	public void setRolloverSelectedIcon(javax.swing.Icon var0) { }
	public void setSelected(boolean var0) { }
	public void setSelectedIcon(javax.swing.Icon var0) { }
	public void setText(java.lang.String var0) { }
	public void setUI(javax.swing.plaf.ButtonUI var0) { }
	public void setVerticalAlignment(int var0) { }
	public void setVerticalTextPosition(int var0) { }
	public final static java.lang.String BORDER_PAINTED_CHANGED_PROPERTY = "borderPainted";
	public final static java.lang.String CONTENT_AREA_FILLED_CHANGED_PROPERTY = "contentAreaFilled";
	public final static java.lang.String DISABLED_ICON_CHANGED_PROPERTY = "disabledIcon";
	public final static java.lang.String DISABLED_SELECTED_ICON_CHANGED_PROPERTY = "disabledSelectedIcon";
	public final static java.lang.String FOCUS_PAINTED_CHANGED_PROPERTY = "focusPainted";
	public final static java.lang.String HORIZONTAL_ALIGNMENT_CHANGED_PROPERTY = "horizontalAlignment";
	public final static java.lang.String HORIZONTAL_TEXT_POSITION_CHANGED_PROPERTY = "horizontalTextPosition";
	public final static java.lang.String ICON_CHANGED_PROPERTY = "icon";
	public final static java.lang.String MARGIN_CHANGED_PROPERTY = "margin";
	public final static java.lang.String MNEMONIC_CHANGED_PROPERTY = "mnemonic";
	public final static java.lang.String MODEL_CHANGED_PROPERTY = "model";
	public final static java.lang.String PRESSED_ICON_CHANGED_PROPERTY = "pressedIcon";
	public final static java.lang.String ROLLOVER_ENABLED_CHANGED_PROPERTY = "rolloverEnabled";
	public final static java.lang.String ROLLOVER_ICON_CHANGED_PROPERTY = "rolloverIcon";
	public final static java.lang.String ROLLOVER_SELECTED_ICON_CHANGED_PROPERTY = "rolloverSelectedIcon";
	public final static java.lang.String SELECTED_ICON_CHANGED_PROPERTY = "selectedIcon";
	public final static java.lang.String TEXT_CHANGED_PROPERTY = "text";
	public final static java.lang.String VERTICAL_ALIGNMENT_CHANGED_PROPERTY = "verticalAlignment";
	public final static java.lang.String VERTICAL_TEXT_POSITION_CHANGED_PROPERTY = "verticalTextPosition";
	protected java.awt.event.ActionListener actionListener;
	protected javax.swing.event.ChangeEvent changeEvent;
	protected javax.swing.event.ChangeListener changeListener;
	protected java.awt.event.ItemListener itemListener;
	protected javax.swing.ButtonModel model;
	protected abstract class AccessibleAbstractButton extends javax.swing.JComponent.AccessibleJComponent implements javax.accessibility.AccessibleAction, javax.accessibility.AccessibleExtendedComponent, javax.accessibility.AccessibleText, javax.accessibility.AccessibleValue {
		protected AccessibleAbstractButton() { }
		public boolean doAccessibleAction(int var0) { return false; }
		public int getAccessibleActionCount() { return 0; }
		public java.lang.String getAccessibleActionDescription(int var0) { return null; }
		public java.lang.String getAfterIndex(int var0, int var1) { return null; }
		public java.lang.String getAtIndex(int var0, int var1) { return null; }
		public java.lang.String getBeforeIndex(int var0, int var1) { return null; }
		public int getCaretPosition() { return 0; }
		public int getCharCount() { return 0; }
		public javax.swing.text.AttributeSet getCharacterAttribute(int var0) { return null; }
		public java.awt.Rectangle getCharacterBounds(int var0) { return null; }
		public java.lang.Number getCurrentAccessibleValue() { return null; }
		public int getIndexAtPoint(java.awt.Point var0) { return 0; }
		public java.lang.Number getMaximumAccessibleValue() { return null; }
		public java.lang.Number getMinimumAccessibleValue() { return null; }
		public java.lang.String getSelectedText() { return null; }
		public int getSelectionEnd() { return 0; }
		public int getSelectionStart() { return 0; }
		public boolean setCurrentAccessibleValue(java.lang.Number var0) { return false; }
	}
	protected class ButtonChangeListener implements java.io.Serializable, javax.swing.event.ChangeListener {
		public void stateChanged(javax.swing.event.ChangeEvent var0) { }
		private ButtonChangeListener() { } /* generated constructor to prevent compiler adding default public constructor */
	}
}

