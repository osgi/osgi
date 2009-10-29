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

package javax.swing;
public class JOptionPane extends javax.swing.JComponent implements javax.accessibility.Accessible {
	public JOptionPane() { }
	public JOptionPane(java.lang.Object var0) { }
	public JOptionPane(java.lang.Object var0, int var1) { }
	public JOptionPane(java.lang.Object var0, int var1, int var2) { }
	public JOptionPane(java.lang.Object var0, int var1, int var2, javax.swing.Icon var3) { }
	public JOptionPane(java.lang.Object var0, int var1, int var2, javax.swing.Icon var3, java.lang.Object[] var4) { }
	public JOptionPane(java.lang.Object var0, int var1, int var2, javax.swing.Icon var3, java.lang.Object[] var4, java.lang.Object var5) { }
	public javax.swing.JDialog createDialog(java.awt.Component var0, java.lang.String var1) { return null; }
	public javax.swing.JInternalFrame createInternalFrame(java.awt.Component var0, java.lang.String var1) { return null; }
	public static javax.swing.JDesktopPane getDesktopPaneForComponent(java.awt.Component var0) { return null; }
	public static java.awt.Frame getFrameForComponent(java.awt.Component var0) { return null; }
	public javax.swing.Icon getIcon() { return null; }
	public java.lang.Object getInitialSelectionValue() { return null; }
	public java.lang.Object getInitialValue() { return null; }
	public java.lang.Object getInputValue() { return null; }
	public int getMaxCharactersPerLineCount() { return 0; }
	public java.lang.Object getMessage() { return null; }
	public int getMessageType() { return 0; }
	public int getOptionType() { return 0; }
	public java.lang.Object[] getOptions() { return null; }
	public static java.awt.Frame getRootFrame() { return null; }
	public java.lang.Object[] getSelectionValues() { return null; }
	public javax.swing.plaf.OptionPaneUI getUI() { return null; }
	public java.lang.Object getValue() { return null; }
	public boolean getWantsInput() { return false; }
	public void selectInitialValue() { }
	public void setIcon(javax.swing.Icon var0) { }
	public void setInitialSelectionValue(java.lang.Object var0) { }
	public void setInitialValue(java.lang.Object var0) { }
	public void setInputValue(java.lang.Object var0) { }
	public void setMessage(java.lang.Object var0) { }
	public void setMessageType(int var0) { }
	public void setOptionType(int var0) { }
	public void setOptions(java.lang.Object[] var0) { }
	public static void setRootFrame(java.awt.Frame var0) { }
	public void setSelectionValues(java.lang.Object[] var0) { }
	public void setUI(javax.swing.plaf.OptionPaneUI var0) { }
	public void setValue(java.lang.Object var0) { }
	public void setWantsInput(boolean var0) { }
	public static int showConfirmDialog(java.awt.Component var0, java.lang.Object var1) { return 0; }
	public static int showConfirmDialog(java.awt.Component var0, java.lang.Object var1, java.lang.String var2, int var3) { return 0; }
	public static int showConfirmDialog(java.awt.Component var0, java.lang.Object var1, java.lang.String var2, int var3, int var4) { return 0; }
	public static int showConfirmDialog(java.awt.Component var0, java.lang.Object var1, java.lang.String var2, int var3, int var4, javax.swing.Icon var5) { return 0; }
	public static java.lang.String showInputDialog(java.awt.Component var0, java.lang.Object var1) { return null; }
	public static java.lang.String showInputDialog(java.awt.Component var0, java.lang.Object var1, java.lang.Object var2) { return null; }
	public static java.lang.String showInputDialog(java.awt.Component var0, java.lang.Object var1, java.lang.String var2, int var3) { return null; }
	public static java.lang.Object showInputDialog(java.awt.Component var0, java.lang.Object var1, java.lang.String var2, int var3, javax.swing.Icon var4, java.lang.Object[] var5, java.lang.Object var6) { return null; }
	public static java.lang.String showInputDialog(java.lang.Object var0) { return null; }
	public static java.lang.String showInputDialog(java.lang.Object var0, java.lang.Object var1) { return null; }
	public static int showInternalConfirmDialog(java.awt.Component var0, java.lang.Object var1) { return 0; }
	public static int showInternalConfirmDialog(java.awt.Component var0, java.lang.Object var1, java.lang.String var2, int var3) { return 0; }
	public static int showInternalConfirmDialog(java.awt.Component var0, java.lang.Object var1, java.lang.String var2, int var3, int var4) { return 0; }
	public static int showInternalConfirmDialog(java.awt.Component var0, java.lang.Object var1, java.lang.String var2, int var3, int var4, javax.swing.Icon var5) { return 0; }
	public static java.lang.String showInternalInputDialog(java.awt.Component var0, java.lang.Object var1) { return null; }
	public static java.lang.String showInternalInputDialog(java.awt.Component var0, java.lang.Object var1, java.lang.String var2, int var3) { return null; }
	public static java.lang.Object showInternalInputDialog(java.awt.Component var0, java.lang.Object var1, java.lang.String var2, int var3, javax.swing.Icon var4, java.lang.Object[] var5, java.lang.Object var6) { return null; }
	public static void showInternalMessageDialog(java.awt.Component var0, java.lang.Object var1) { }
	public static void showInternalMessageDialog(java.awt.Component var0, java.lang.Object var1, java.lang.String var2, int var3) { }
	public static void showInternalMessageDialog(java.awt.Component var0, java.lang.Object var1, java.lang.String var2, int var3, javax.swing.Icon var4) { }
	public static int showInternalOptionDialog(java.awt.Component var0, java.lang.Object var1, java.lang.String var2, int var3, int var4, javax.swing.Icon var5, java.lang.Object[] var6, java.lang.Object var7) { return 0; }
	public static void showMessageDialog(java.awt.Component var0, java.lang.Object var1) { }
	public static void showMessageDialog(java.awt.Component var0, java.lang.Object var1, java.lang.String var2, int var3) { }
	public static void showMessageDialog(java.awt.Component var0, java.lang.Object var1, java.lang.String var2, int var3, javax.swing.Icon var4) { }
	public static int showOptionDialog(java.awt.Component var0, java.lang.Object var1, java.lang.String var2, int var3, int var4, javax.swing.Icon var5, java.lang.Object[] var6, java.lang.Object var7) { return 0; }
	public final static int CANCEL_OPTION = 2;
	public final static int CLOSED_OPTION = -1;
	public final static int DEFAULT_OPTION = -1;
	public final static int ERROR_MESSAGE = 0;
	public final static java.lang.String ICON_PROPERTY = "icon";
	public final static int INFORMATION_MESSAGE = 1;
	public final static java.lang.String INITIAL_SELECTION_VALUE_PROPERTY = "initialSelectionValue";
	public final static java.lang.String INITIAL_VALUE_PROPERTY = "initialValue";
	public final static java.lang.String INPUT_VALUE_PROPERTY = "inputValue";
	public final static java.lang.String MESSAGE_PROPERTY = "message";
	public final static java.lang.String MESSAGE_TYPE_PROPERTY = "messageType";
	public final static int NO_OPTION = 1;
	public final static int OK_CANCEL_OPTION = 2;
	public final static int OK_OPTION = 0;
	public final static java.lang.String OPTIONS_PROPERTY = "options";
	public final static java.lang.String OPTION_TYPE_PROPERTY = "optionType";
	public final static int PLAIN_MESSAGE = -1;
	public final static int QUESTION_MESSAGE = 3;
	public final static java.lang.String SELECTION_VALUES_PROPERTY = "selectionValues";
	public final static java.lang.Object UNINITIALIZED_VALUE; static { UNINITIALIZED_VALUE = null; }
	public final static java.lang.String VALUE_PROPERTY = "value";
	public final static java.lang.String WANTS_INPUT_PROPERTY = "wantsInput";
	public final static int WARNING_MESSAGE = 2;
	public final static int YES_NO_CANCEL_OPTION = 1;
	public final static int YES_NO_OPTION = 0;
	public final static int YES_OPTION = 0;
	protected javax.swing.Icon icon;
	protected java.lang.Object initialSelectionValue;
	protected java.lang.Object initialValue;
	protected java.lang.Object inputValue;
	protected java.lang.Object message;
	protected int messageType;
	protected int optionType;
	protected java.lang.Object[] options;
	protected java.lang.Object[] selectionValues;
	protected java.lang.Object value;
	protected boolean wantsInput;
	protected class AccessibleJOptionPane extends javax.swing.JComponent.AccessibleJComponent {
		protected AccessibleJOptionPane() { }
	}
}

