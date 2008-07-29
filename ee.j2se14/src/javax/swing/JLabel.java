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

package javax.swing;
public class JLabel extends javax.swing.JComponent implements javax.accessibility.Accessible, javax.swing.SwingConstants {
	public JLabel() { }
	public JLabel(java.lang.String var0) { }
	public JLabel(java.lang.String var0, int var1) { }
	public JLabel(java.lang.String var0, javax.swing.Icon var1, int var2) { }
	public JLabel(javax.swing.Icon var0) { }
	public JLabel(javax.swing.Icon var0, int var1) { }
	protected int checkHorizontalKey(int var0, java.lang.String var1) { return 0; }
	protected int checkVerticalKey(int var0, java.lang.String var1) { return 0; }
	public javax.swing.Icon getDisabledIcon() { return null; }
	public int getDisplayedMnemonic() { return 0; }
	public int getDisplayedMnemonicIndex() { return 0; }
	public int getHorizontalAlignment() { return 0; }
	public int getHorizontalTextPosition() { return 0; }
	public javax.swing.Icon getIcon() { return null; }
	public int getIconTextGap() { return 0; }
	public java.awt.Component getLabelFor() { return null; }
	public java.lang.String getText() { return null; }
	public javax.swing.plaf.LabelUI getUI() { return null; }
	public int getVerticalAlignment() { return 0; }
	public int getVerticalTextPosition() { return 0; }
	public void setDisabledIcon(javax.swing.Icon var0) { }
	public void setDisplayedMnemonic(char var0) { }
	public void setDisplayedMnemonic(int var0) { }
	public void setDisplayedMnemonicIndex(int var0) { }
	public void setHorizontalAlignment(int var0) { }
	public void setHorizontalTextPosition(int var0) { }
	public void setIcon(javax.swing.Icon var0) { }
	public void setIconTextGap(int var0) { }
	public void setLabelFor(java.awt.Component var0) { }
	public void setText(java.lang.String var0) { }
	public void setUI(javax.swing.plaf.LabelUI var0) { }
	public void setVerticalAlignment(int var0) { }
	public void setVerticalTextPosition(int var0) { }
	protected java.awt.Component labelFor;
	protected class AccessibleJLabel extends javax.swing.JComponent.AccessibleJComponent implements javax.accessibility.AccessibleExtendedComponent, javax.accessibility.AccessibleText {
		protected AccessibleJLabel() { }
		public java.lang.String getAfterIndex(int var0, int var1) { return null; }
		public java.lang.String getAtIndex(int var0, int var1) { return null; }
		public java.lang.String getBeforeIndex(int var0, int var1) { return null; }
		public int getCaretPosition() { return 0; }
		public int getCharCount() { return 0; }
		public javax.swing.text.AttributeSet getCharacterAttribute(int var0) { return null; }
		public java.awt.Rectangle getCharacterBounds(int var0) { return null; }
		public int getIndexAtPoint(java.awt.Point var0) { return 0; }
		public java.lang.String getSelectedText() { return null; }
		public int getSelectionEnd() { return 0; }
		public int getSelectionStart() { return 0; }
	}
}

