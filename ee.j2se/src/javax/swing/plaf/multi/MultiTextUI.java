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

package javax.swing.plaf.multi;
public class MultiTextUI extends javax.swing.plaf.TextUI {
	protected java.util.Vector uis;
	public MultiTextUI() { } 
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	public void damageRange(javax.swing.text.JTextComponent var0, int var1, int var2) { }
	public void damageRange(javax.swing.text.JTextComponent var0, int var1, int var2, javax.swing.text.Position.Bias var3, javax.swing.text.Position.Bias var4) { }
	public javax.swing.text.EditorKit getEditorKit(javax.swing.text.JTextComponent var0) { return null; }
	public int getNextVisualPositionFrom(javax.swing.text.JTextComponent var0, int var1, javax.swing.text.Position.Bias var2, int var3, javax.swing.text.Position.Bias[] var4) throws javax.swing.text.BadLocationException { return 0; }
	public javax.swing.text.View getRootView(javax.swing.text.JTextComponent var0) { return null; }
	public javax.swing.plaf.ComponentUI[] getUIs() { return null; }
	public java.awt.Rectangle modelToView(javax.swing.text.JTextComponent var0, int var1) throws javax.swing.text.BadLocationException { return null; }
	public java.awt.Rectangle modelToView(javax.swing.text.JTextComponent var0, int var1, javax.swing.text.Position.Bias var2) throws javax.swing.text.BadLocationException { return null; }
	public int viewToModel(javax.swing.text.JTextComponent var0, java.awt.Point var1) { return 0; }
	public int viewToModel(javax.swing.text.JTextComponent var0, java.awt.Point var1, javax.swing.text.Position.Bias[] var2) { return 0; }
}

