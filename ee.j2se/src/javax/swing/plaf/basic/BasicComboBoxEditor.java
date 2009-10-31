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
public class BasicComboBoxEditor implements java.awt.event.FocusListener, javax.swing.ComboBoxEditor {
	public static class UIResource extends javax.swing.plaf.basic.BasicComboBoxEditor implements javax.swing.plaf.UIResource {
		public UIResource() { } 
	}
	protected javax.swing.JTextField editor;
	public BasicComboBoxEditor() { } 
	public void addActionListener(java.awt.event.ActionListener var0) { }
	protected javax.swing.JTextField createEditorComponent() { return null; }
	public void focusGained(java.awt.event.FocusEvent var0) { }
	public void focusLost(java.awt.event.FocusEvent var0) { }
	public java.awt.Component getEditorComponent() { return null; }
	public java.lang.Object getItem() { return null; }
	public void removeActionListener(java.awt.event.ActionListener var0) { }
	public void selectAll() { }
	public void setItem(java.lang.Object var0) { }
}

