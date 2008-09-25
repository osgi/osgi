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

package java.awt;
public class CheckboxMenuItem extends java.awt.MenuItem implements java.awt.ItemSelectable, javax.accessibility.Accessible {
	public CheckboxMenuItem() { }
	public CheckboxMenuItem(java.lang.String var0) { }
	public CheckboxMenuItem(java.lang.String var0, boolean var1) { }
	public void addItemListener(java.awt.event.ItemListener var0) { }
	public java.awt.event.ItemListener[] getItemListeners() { return null; }
	public java.lang.Object[] getSelectedObjects() { return null; }
	public boolean getState() { return false; }
	protected void processItemEvent(java.awt.event.ItemEvent var0) { }
	public void removeItemListener(java.awt.event.ItemListener var0) { }
	public void setState(boolean var0) { }
	protected class AccessibleAWTCheckboxMenuItem extends java.awt.MenuItem.AccessibleAWTMenuItem implements javax.accessibility.AccessibleAction, javax.accessibility.AccessibleValue {
		protected AccessibleAWTCheckboxMenuItem() { }
	}
}

