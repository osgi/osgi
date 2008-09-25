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
public class Choice extends java.awt.Component implements java.awt.ItemSelectable, javax.accessibility.Accessible {
	public Choice() { }
	public void add(java.lang.String var0) { }
	public void addItem(java.lang.String var0) { }
	public void addItemListener(java.awt.event.ItemListener var0) { }
	/** @deprecated */ public int countItems() { return 0; }
	public java.lang.String getItem(int var0) { return null; }
	public int getItemCount() { return 0; }
	public java.awt.event.ItemListener[] getItemListeners() { return null; }
	public int getSelectedIndex() { return 0; }
	public java.lang.String getSelectedItem() { return null; }
	public java.lang.Object[] getSelectedObjects() { return null; }
	public void insert(java.lang.String var0, int var1) { }
	protected void processItemEvent(java.awt.event.ItemEvent var0) { }
	public void remove(int var0) { }
	public void remove(java.lang.String var0) { }
	public void removeAll() { }
	public void removeItemListener(java.awt.event.ItemListener var0) { }
	public void select(int var0) { }
	public void select(java.lang.String var0) { }
	protected class AccessibleAWTChoice extends java.awt.Component.AccessibleAWTComponent implements javax.accessibility.AccessibleAction {
		public AccessibleAWTChoice() { }
		public boolean doAccessibleAction(int var0) { return false; }
		public int getAccessibleActionCount() { return 0; }
		public java.lang.String getAccessibleActionDescription(int var0) { return null; }
	}
}

