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
public class JTextArea extends javax.swing.text.JTextComponent {
	protected class AccessibleJTextArea extends javax.swing.text.JTextComponent.AccessibleJTextComponent {
		protected AccessibleJTextArea() { } 
	}
	public JTextArea() { } 
	public JTextArea(int var0, int var1) { } 
	public JTextArea(java.lang.String var0) { } 
	public JTextArea(java.lang.String var0, int var1, int var2) { } 
	public JTextArea(javax.swing.text.Document var0) { } 
	public JTextArea(javax.swing.text.Document var0, java.lang.String var1, int var2, int var3) { } 
	public void append(java.lang.String var0) { }
	protected javax.swing.text.Document createDefaultModel() { return null; }
	protected int getColumnWidth() { return 0; }
	public int getColumns() { return 0; }
	public int getLineCount() { return 0; }
	public int getLineEndOffset(int var0) throws javax.swing.text.BadLocationException { return 0; }
	public int getLineOfOffset(int var0) throws javax.swing.text.BadLocationException { return 0; }
	public int getLineStartOffset(int var0) throws javax.swing.text.BadLocationException { return 0; }
	public boolean getLineWrap() { return false; }
	protected int getRowHeight() { return 0; }
	public int getRows() { return 0; }
	public int getTabSize() { return 0; }
	public boolean getWrapStyleWord() { return false; }
	public void insert(java.lang.String var0, int var1) { }
	public void replaceRange(java.lang.String var0, int var1, int var2) { }
	public void setColumns(int var0) { }
	public void setLineWrap(boolean var0) { }
	public void setRows(int var0) { }
	public void setTabSize(int var0) { }
	public void setWrapStyleWord(boolean var0) { }
}

