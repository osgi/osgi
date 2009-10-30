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

package javax.swing;
public class JSpinner extends javax.swing.JComponent implements javax.accessibility.Accessible {
	protected class AccessibleJSpinner extends javax.swing.JComponent.AccessibleJComponent implements javax.accessibility.AccessibleAction, javax.accessibility.AccessibleEditableText, javax.accessibility.AccessibleText, javax.accessibility.AccessibleValue, javax.swing.event.ChangeListener {
		protected AccessibleJSpinner() { } 
		public void cut(int var0, int var1) { }
		public void delete(int var0, int var1) { }
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
		public java.lang.String getTextRange(int var0, int var1) { return null; }
		public void insertTextAtIndex(int var0, java.lang.String var1) { }
		public void paste(int var0) { }
		public void replaceText(int var0, int var1, java.lang.String var2) { }
		public void selectText(int var0, int var1) { }
		public void setAttributes(int var0, int var1, javax.swing.text.AttributeSet var2) { }
		public boolean setCurrentAccessibleValue(java.lang.Number var0) { return false; }
		public void setTextContents(java.lang.String var0) { }
		public void stateChanged(javax.swing.event.ChangeEvent var0) { }
	}
	public static class DateEditor extends javax.swing.JSpinner.DefaultEditor {
		public DateEditor(javax.swing.JSpinner var0)  { super((javax.swing.JSpinner) null); } 
		public DateEditor(javax.swing.JSpinner var0, java.lang.String var1)  { super((javax.swing.JSpinner) null); } 
		public java.text.SimpleDateFormat getFormat() { return null; }
		public javax.swing.SpinnerDateModel getModel() { return null; }
	}
	public static class DefaultEditor extends javax.swing.JPanel implements java.awt.LayoutManager, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
		public DefaultEditor(javax.swing.JSpinner var0) { } 
		public void addLayoutComponent(java.lang.String var0, java.awt.Component var1) { }
		public void commitEdit() throws java.text.ParseException { }
		public void dismiss(javax.swing.JSpinner var0) { }
		public javax.swing.JSpinner getSpinner() { return null; }
		public javax.swing.JFormattedTextField getTextField() { return null; }
		public void layoutContainer(java.awt.Container var0) { }
		public java.awt.Dimension minimumLayoutSize(java.awt.Container var0) { return null; }
		public java.awt.Dimension preferredLayoutSize(java.awt.Container var0) { return null; }
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
		public void removeLayoutComponent(java.awt.Component var0) { }
		public void stateChanged(javax.swing.event.ChangeEvent var0) { }
	}
	public static class ListEditor extends javax.swing.JSpinner.DefaultEditor {
		public ListEditor(javax.swing.JSpinner var0)  { super((javax.swing.JSpinner) null); } 
		public javax.swing.SpinnerListModel getModel() { return null; }
	}
	public static class NumberEditor extends javax.swing.JSpinner.DefaultEditor {
		public NumberEditor(javax.swing.JSpinner var0)  { super((javax.swing.JSpinner) null); } 
		public NumberEditor(javax.swing.JSpinner var0, java.lang.String var1)  { super((javax.swing.JSpinner) null); } 
		public java.text.DecimalFormat getFormat() { return null; }
		public javax.swing.SpinnerNumberModel getModel() { return null; }
	}
	public JSpinner() { } 
	public JSpinner(javax.swing.SpinnerModel var0) { } 
	public void addChangeListener(javax.swing.event.ChangeListener var0) { }
	public void commitEdit() throws java.text.ParseException { }
	protected javax.swing.JComponent createEditor(javax.swing.SpinnerModel var0) { return null; }
	protected void fireStateChanged() { }
	public javax.swing.event.ChangeListener[] getChangeListeners() { return null; }
	public javax.swing.JComponent getEditor() { return null; }
	public javax.swing.SpinnerModel getModel() { return null; }
	public java.lang.Object getNextValue() { return null; }
	public java.lang.Object getPreviousValue() { return null; }
	public javax.swing.plaf.SpinnerUI getUI() { return null; }
	public java.lang.Object getValue() { return null; }
	public void removeChangeListener(javax.swing.event.ChangeListener var0) { }
	public void setEditor(javax.swing.JComponent var0) { }
	public void setModel(javax.swing.SpinnerModel var0) { }
	public void setUI(javax.swing.plaf.SpinnerUI var0) { }
	public void setValue(java.lang.Object var0) { }
}

