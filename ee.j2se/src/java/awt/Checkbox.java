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

package java.awt;
public class Checkbox extends java.awt.Component implements java.awt.ItemSelectable, javax.accessibility.Accessible {
	public Checkbox() { }
	public Checkbox(java.lang.String var0) { }
	public Checkbox(java.lang.String var0, java.awt.CheckboxGroup var1, boolean var2) { }
	public Checkbox(java.lang.String var0, boolean var1) { }
	public Checkbox(java.lang.String var0, boolean var1, java.awt.CheckboxGroup var2) { }
	public void addItemListener(java.awt.event.ItemListener var0) { }
	public java.awt.CheckboxGroup getCheckboxGroup() { return null; }
	public java.awt.event.ItemListener[] getItemListeners() { return null; }
	public java.lang.String getLabel() { return null; }
	public java.lang.Object[] getSelectedObjects() { return null; }
	public boolean getState() { return false; }
	protected void processItemEvent(java.awt.event.ItemEvent var0) { }
	public void removeItemListener(java.awt.event.ItemListener var0) { }
	public void setCheckboxGroup(java.awt.CheckboxGroup var0) { }
	public void setLabel(java.lang.String var0) { }
	public void setState(boolean var0) { }
	protected class AccessibleAWTCheckbox extends java.awt.Component.AccessibleAWTComponent implements java.awt.event.ItemListener, javax.accessibility.AccessibleAction, javax.accessibility.AccessibleValue {
		public AccessibleAWTCheckbox() { }
		public boolean doAccessibleAction(int var0) { return false; }
		public int getAccessibleActionCount() { return 0; }
		public java.lang.String getAccessibleActionDescription(int var0) { return null; }
		public java.lang.Number getCurrentAccessibleValue() { return null; }
		public java.lang.Number getMaximumAccessibleValue() { return null; }
		public java.lang.Number getMinimumAccessibleValue() { return null; }
		public void itemStateChanged(java.awt.event.ItemEvent var0) { }
		public boolean setCurrentAccessibleValue(java.lang.Number var0) { return false; }
	}
}

