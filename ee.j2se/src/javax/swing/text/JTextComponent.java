/*
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

package javax.swing.text;
public abstract class JTextComponent extends javax.swing.JComponent implements javax.accessibility.Accessible, javax.swing.Scrollable {
	public JTextComponent() { }
	public void addCaretListener(javax.swing.event.CaretListener var0) { }
	public void addInputMethodListener(java.awt.event.InputMethodListener var0) { }
	public static javax.swing.text.Keymap addKeymap(java.lang.String var0, javax.swing.text.Keymap var1) { return null; }
	public void copy() { }
	public void cut() { }
	protected void fireCaretUpdate(javax.swing.event.CaretEvent var0) { }
	public javax.swing.Action[] getActions() { return null; }
	public javax.swing.text.Caret getCaret() { return null; }
	public java.awt.Color getCaretColor() { return null; }
	public javax.swing.event.CaretListener[] getCaretListeners() { return null; }
	public int getCaretPosition() { return 0; }
	public java.awt.Color getDisabledTextColor() { return null; }
	public javax.swing.text.Document getDocument() { return null; }
	public boolean getDragEnabled() { return false; }
	public char getFocusAccelerator() { return '\0'; }
	public javax.swing.text.Highlighter getHighlighter() { return null; }
	public javax.swing.text.Keymap getKeymap() { return null; }
	public static javax.swing.text.Keymap getKeymap(java.lang.String var0) { return null; }
	public java.awt.Insets getMargin() { return null; }
	public javax.swing.text.NavigationFilter getNavigationFilter() { return null; }
	public java.awt.Dimension getPreferredScrollableViewportSize() { return null; }
	public int getScrollableBlockIncrement(java.awt.Rectangle var0, int var1, int var2) { return 0; }
	public boolean getScrollableTracksViewportHeight() { return false; }
	public boolean getScrollableTracksViewportWidth() { return false; }
	public int getScrollableUnitIncrement(java.awt.Rectangle var0, int var1, int var2) { return 0; }
	public java.lang.String getSelectedText() { return null; }
	public java.awt.Color getSelectedTextColor() { return null; }
	public java.awt.Color getSelectionColor() { return null; }
	public int getSelectionEnd() { return 0; }
	public int getSelectionStart() { return 0; }
	public java.lang.String getText() { return null; }
	public java.lang.String getText(int var0, int var1) throws javax.swing.text.BadLocationException { return null; }
	public javax.swing.plaf.TextUI getUI() { return null; }
	public boolean isEditable() { return false; }
	public static void loadKeymap(javax.swing.text.Keymap var0, javax.swing.text.JTextComponent.KeyBinding[] var1, javax.swing.Action[] var2) { }
	public java.awt.Rectangle modelToView(int var0) throws javax.swing.text.BadLocationException { return null; }
	public void moveCaretPosition(int var0) { }
	public void paste() { }
	public void read(java.io.Reader var0, java.lang.Object var1) throws java.io.IOException { }
	public void removeCaretListener(javax.swing.event.CaretListener var0) { }
	public static javax.swing.text.Keymap removeKeymap(java.lang.String var0) { return null; }
	public void replaceSelection(java.lang.String var0) { }
	public void select(int var0, int var1) { }
	public void selectAll() { }
	public void setCaret(javax.swing.text.Caret var0) { }
	public void setCaretColor(java.awt.Color var0) { }
	public void setCaretPosition(int var0) { }
	public void setDisabledTextColor(java.awt.Color var0) { }
	public void setDocument(javax.swing.text.Document var0) { }
	public void setDragEnabled(boolean var0) { }
	public void setEditable(boolean var0) { }
	public void setFocusAccelerator(char var0) { }
	public void setHighlighter(javax.swing.text.Highlighter var0) { }
	public void setKeymap(javax.swing.text.Keymap var0) { }
	public void setMargin(java.awt.Insets var0) { }
	public void setNavigationFilter(javax.swing.text.NavigationFilter var0) { }
	public void setSelectedTextColor(java.awt.Color var0) { }
	public void setSelectionColor(java.awt.Color var0) { }
	public void setSelectionEnd(int var0) { }
	public void setSelectionStart(int var0) { }
	public void setText(java.lang.String var0) { }
	public void setUI(javax.swing.plaf.TextUI var0) { }
	public int viewToModel(java.awt.Point var0) { return 0; }
	public void write(java.io.Writer var0) throws java.io.IOException { }
	public final static java.lang.String DEFAULT_KEYMAP = "default";
	public final static java.lang.String FOCUS_ACCELERATOR_KEY = "focusAcceleratorKey";
	public class AccessibleJTextComponent extends javax.swing.JComponent.AccessibleJComponent implements javax.accessibility.AccessibleAction, javax.accessibility.AccessibleEditableText, javax.accessibility.AccessibleText, javax.swing.event.CaretListener, javax.swing.event.DocumentListener {
		public AccessibleJTextComponent() { }
		public void caretUpdate(javax.swing.event.CaretEvent var0) { }
		public void changedUpdate(javax.swing.event.DocumentEvent var0) { }
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
		public int getIndexAtPoint(java.awt.Point var0) { return 0; }
		public java.lang.String getSelectedText() { return null; }
		public int getSelectionEnd() { return 0; }
		public int getSelectionStart() { return 0; }
		public java.lang.String getTextRange(int var0, int var1) { return null; }
		public void insertTextAtIndex(int var0, java.lang.String var1) { }
		public void insertUpdate(javax.swing.event.DocumentEvent var0) { }
		public void paste(int var0) { }
		public void removeUpdate(javax.swing.event.DocumentEvent var0) { }
		public void replaceText(int var0, int var1, java.lang.String var2) { }
		public void selectText(int var0, int var1) { }
		public void setAttributes(int var0, int var1, javax.swing.text.AttributeSet var2) { }
		public void setTextContents(java.lang.String var0) { }
	}
	public static class KeyBinding {
		public KeyBinding(javax.swing.KeyStroke var0, java.lang.String var1) { }
		public java.lang.String actionName;
		public javax.swing.KeyStroke key;
	}
}

