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
public class JFormattedTextField extends javax.swing.JTextField {
	public JFormattedTextField() { }
	public JFormattedTextField(java.lang.Object var0) { }
	public JFormattedTextField(java.text.Format var0) { }
	public JFormattedTextField(javax.swing.JFormattedTextField.AbstractFormatter var0) { }
	public JFormattedTextField(javax.swing.JFormattedTextField.AbstractFormatterFactory var0) { }
	public JFormattedTextField(javax.swing.JFormattedTextField.AbstractFormatterFactory var0, java.lang.Object var1) { }
	public void commitEdit() throws java.text.ParseException { }
	public int getFocusLostBehavior() { return 0; }
	public javax.swing.JFormattedTextField.AbstractFormatter getFormatter() { return null; }
	public javax.swing.JFormattedTextField.AbstractFormatterFactory getFormatterFactory() { return null; }
	public java.lang.Object getValue() { return null; }
	protected void invalidEdit() { }
	public boolean isEditValid() { return false; }
	public void setFocusLostBehavior(int var0) { }
	protected void setFormatter(javax.swing.JFormattedTextField.AbstractFormatter var0) { }
	public void setFormatterFactory(javax.swing.JFormattedTextField.AbstractFormatterFactory var0) { }
	public void setValue(java.lang.Object var0) { }
	public final static int COMMIT = 0;
	public final static int COMMIT_OR_REVERT = 1;
	public final static int PERSIST = 3;
	public final static int REVERT = 2;
	public static abstract class AbstractFormatter implements java.io.Serializable {
		public AbstractFormatter() { }
		protected java.lang.Object clone() throws java.lang.CloneNotSupportedException { return null; }
		protected javax.swing.Action[] getActions() { return null; }
		protected javax.swing.text.DocumentFilter getDocumentFilter() { return null; }
		protected javax.swing.JFormattedTextField getFormattedTextField() { return null; }
		protected javax.swing.text.NavigationFilter getNavigationFilter() { return null; }
		public void install(javax.swing.JFormattedTextField var0) { }
		protected void invalidEdit() { }
		protected void setEditValid(boolean var0) { }
		public abstract java.lang.Object stringToValue(java.lang.String var0) throws java.text.ParseException;
		public void uninstall() { }
		public abstract java.lang.String valueToString(java.lang.Object var0) throws java.text.ParseException;
	}
	public static abstract class AbstractFormatterFactory {
		public AbstractFormatterFactory() { }
		public abstract javax.swing.JFormattedTextField.AbstractFormatter getFormatter(javax.swing.JFormattedTextField var0);
	}
}

