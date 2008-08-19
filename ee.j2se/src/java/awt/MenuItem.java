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
public class MenuItem extends java.awt.MenuComponent implements javax.accessibility.Accessible {
	public MenuItem() { }
	public MenuItem(java.lang.String var0) { }
	public MenuItem(java.lang.String var0, java.awt.MenuShortcut var1) { }
	public void addActionListener(java.awt.event.ActionListener var0) { }
	public void addNotify() { }
	public void deleteShortcut() { }
	/** @deprecated */ public void disable() { }
	protected final void disableEvents(long var0) { }
	/** @deprecated */ public void enable() { }
	/** @deprecated */ public void enable(boolean var0) { }
	protected final void enableEvents(long var0) { }
	public java.lang.String getActionCommand() { return null; }
	public java.awt.event.ActionListener[] getActionListeners() { return null; }
	public java.lang.String getLabel() { return null; }
	public java.util.EventListener[] getListeners(java.lang.Class var0) { return null; }
	public java.awt.MenuShortcut getShortcut() { return null; }
	public boolean isEnabled() { return false; }
	public java.lang.String paramString() { return null; }
	protected void processActionEvent(java.awt.event.ActionEvent var0) { }
	public void removeActionListener(java.awt.event.ActionListener var0) { }
	public void setActionCommand(java.lang.String var0) { }
	public void setEnabled(boolean var0) { }
	public void setLabel(java.lang.String var0) { }
	public void setShortcut(java.awt.MenuShortcut var0) { }
	protected class AccessibleAWTMenuItem extends java.awt.MenuComponent.AccessibleAWTMenuComponent implements javax.accessibility.AccessibleAction, javax.accessibility.AccessibleValue {
		protected AccessibleAWTMenuItem() { }
		public boolean doAccessibleAction(int var0) { return false; }
		public int getAccessibleActionCount() { return 0; }
		public java.lang.String getAccessibleActionDescription(int var0) { return null; }
		public java.lang.Number getCurrentAccessibleValue() { return null; }
		public java.lang.Number getMaximumAccessibleValue() { return null; }
		public java.lang.Number getMinimumAccessibleValue() { return null; }
		public boolean setCurrentAccessibleValue(java.lang.Number var0) { return false; }
	}
}

