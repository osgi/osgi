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

package java.awt;
public class TextField extends java.awt.TextComponent {
	public TextField() { }
	public TextField(int var0) { }
	public TextField(java.lang.String var0) { }
	public TextField(java.lang.String var0, int var1) { }
	public void addActionListener(java.awt.event.ActionListener var0) { }
	public boolean echoCharIsSet() { return false; }
	public java.awt.event.ActionListener[] getActionListeners() { return null; }
	public int getColumns() { return 0; }
	public char getEchoChar() { return '\0'; }
	public java.awt.Dimension getMinimumSize(int var0) { return null; }
	public java.awt.Dimension getPreferredSize(int var0) { return null; }
	/** @deprecated */ public java.awt.Dimension minimumSize(int var0) { return null; }
	/** @deprecated */ public java.awt.Dimension preferredSize(int var0) { return null; }
	protected void processActionEvent(java.awt.event.ActionEvent var0) { }
	public void removeActionListener(java.awt.event.ActionListener var0) { }
	public void setColumns(int var0) { }
	public void setEchoChar(char var0) { }
	/** @deprecated */ public void setEchoCharacter(char var0) { }
	public void setText(java.lang.String var0) { }
	protected class AccessibleAWTTextField extends java.awt.TextComponent.AccessibleAWTTextComponent {
		protected AccessibleAWTTextField() { }
	}
}

