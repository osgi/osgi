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

package java.awt;
public class Button extends java.awt.Component implements javax.accessibility.Accessible {
	protected class AccessibleAWTButton extends java.awt.Component.AccessibleAWTComponent implements javax.accessibility.AccessibleAction, javax.accessibility.AccessibleValue {
		protected AccessibleAWTButton() { } 
		public boolean doAccessibleAction(int var0) { return false; }
		public int getAccessibleActionCount() { return 0; }
		public java.lang.String getAccessibleActionDescription(int var0) { return null; }
		public java.lang.Number getCurrentAccessibleValue() { return null; }
		public java.lang.Number getMaximumAccessibleValue() { return null; }
		public java.lang.Number getMinimumAccessibleValue() { return null; }
		public boolean setCurrentAccessibleValue(java.lang.Number var0) { return false; }
	}
	public Button() { } 
	public Button(java.lang.String var0) { } 
	public void addActionListener(java.awt.event.ActionListener var0) { }
	public java.lang.String getActionCommand() { return null; }
	public java.awt.event.ActionListener[] getActionListeners() { return null; }
	public java.lang.String getLabel() { return null; }
	protected void processActionEvent(java.awt.event.ActionEvent var0) { }
	public void removeActionListener(java.awt.event.ActionListener var0) { }
	public void setActionCommand(java.lang.String var0) { }
	public void setLabel(java.lang.String var0) { }
}

