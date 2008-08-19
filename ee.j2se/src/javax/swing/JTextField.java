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
public class JTextField extends javax.swing.text.JTextComponent implements javax.swing.SwingConstants {
	public JTextField() { }
	public JTextField(int var0) { }
	public JTextField(java.lang.String var0) { }
	public JTextField(java.lang.String var0, int var1) { }
	public JTextField(javax.swing.text.Document var0, java.lang.String var1, int var2) { }
	public void addActionListener(java.awt.event.ActionListener var0) { }
	protected void configurePropertiesFromAction(javax.swing.Action var0) { }
	protected java.beans.PropertyChangeListener createActionPropertyChangeListener(javax.swing.Action var0) { return null; }
	protected javax.swing.text.Document createDefaultModel() { return null; }
	protected void fireActionPerformed() { }
	public javax.swing.Action getAction() { return null; }
	public java.awt.event.ActionListener[] getActionListeners() { return null; }
	protected int getColumnWidth() { return 0; }
	public int getColumns() { return 0; }
	public int getHorizontalAlignment() { return 0; }
	public javax.swing.BoundedRangeModel getHorizontalVisibility() { return null; }
	public int getScrollOffset() { return 0; }
	public void postActionEvent() { }
	public void removeActionListener(java.awt.event.ActionListener var0) { }
	public void setAction(javax.swing.Action var0) { }
	public void setActionCommand(java.lang.String var0) { }
	public void setColumns(int var0) { }
	public void setHorizontalAlignment(int var0) { }
	public void setScrollOffset(int var0) { }
	public final static java.lang.String notifyAction = "notify-field-accept";
	protected class AccessibleJTextField extends javax.swing.text.JTextComponent.AccessibleJTextComponent {
		protected AccessibleJTextField() { }
	}
}

