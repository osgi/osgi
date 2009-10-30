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

package javax.swing.plaf.basic;
public abstract class BasicTextUI extends javax.swing.plaf.TextUI implements javax.swing.text.ViewFactory {
	public static class BasicCaret extends javax.swing.text.DefaultCaret implements javax.swing.plaf.UIResource {
		public BasicCaret() { } 
	}
	public static class BasicHighlighter extends javax.swing.text.DefaultHighlighter implements javax.swing.plaf.UIResource {
		public BasicHighlighter() { } 
	}
	public BasicTextUI() { } 
	public javax.swing.text.View create(javax.swing.text.Element var0) { return null; }
	public javax.swing.text.View create(javax.swing.text.Element var0, int var1, int var2) { return null; }
	protected javax.swing.text.Caret createCaret() { return null; }
	protected javax.swing.text.Highlighter createHighlighter() { return null; }
	protected javax.swing.text.Keymap createKeymap() { return null; }
	public void damageRange(javax.swing.text.JTextComponent var0, int var1, int var2) { }
	public void damageRange(javax.swing.text.JTextComponent var0, int var1, int var2, javax.swing.text.Position.Bias var3, javax.swing.text.Position.Bias var4) { }
	protected final javax.swing.text.JTextComponent getComponent() { return null; }
	public javax.swing.text.EditorKit getEditorKit(javax.swing.text.JTextComponent var0) { return null; }
	protected java.lang.String getKeymapName() { return null; }
	public int getNextVisualPositionFrom(javax.swing.text.JTextComponent var0, int var1, javax.swing.text.Position.Bias var2, int var3, javax.swing.text.Position.Bias[] var4) throws javax.swing.text.BadLocationException { return 0; }
	protected abstract java.lang.String getPropertyPrefix();
	public javax.swing.text.View getRootView(javax.swing.text.JTextComponent var0) { return null; }
	protected java.awt.Rectangle getVisibleEditorRect() { return null; }
	protected void installDefaults() { }
	protected void installKeyboardActions() { }
	protected void installListeners() { }
	protected void modelChanged() { }
	public java.awt.Rectangle modelToView(javax.swing.text.JTextComponent var0, int var1) throws javax.swing.text.BadLocationException { return null; }
	public java.awt.Rectangle modelToView(javax.swing.text.JTextComponent var0, int var1, javax.swing.text.Position.Bias var2) throws javax.swing.text.BadLocationException { return null; }
	public final void paint(java.awt.Graphics var0, javax.swing.JComponent var1) { }
	protected void paintBackground(java.awt.Graphics var0) { }
	protected void paintSafely(java.awt.Graphics var0) { }
	protected void propertyChange(java.beans.PropertyChangeEvent var0) { }
	protected final void setView(javax.swing.text.View var0) { }
	protected void uninstallDefaults() { }
	protected void uninstallKeyboardActions() { }
	protected void uninstallListeners() { }
	public int viewToModel(javax.swing.text.JTextComponent var0, java.awt.Point var1) { return 0; }
	public int viewToModel(javax.swing.text.JTextComponent var0, java.awt.Point var1, javax.swing.text.Position.Bias[] var2) { return 0; }
}

