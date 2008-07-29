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
public class Menu extends java.awt.MenuItem implements java.awt.MenuContainer, javax.accessibility.Accessible {
	public Menu() { }
	public Menu(java.lang.String var0) { }
	public Menu(java.lang.String var0, boolean var1) { }
	public java.awt.MenuItem add(java.awt.MenuItem var0) { return null; }
	public void add(java.lang.String var0) { }
	public void addSeparator() { }
	/** @deprecated */ public int countItems() { return 0; }
	public java.awt.MenuItem getItem(int var0) { return null; }
	public int getItemCount() { return 0; }
	public void insert(java.awt.MenuItem var0, int var1) { }
	public void insert(java.lang.String var0, int var1) { }
	public void insertSeparator(int var0) { }
	public boolean isTearOff() { return false; }
	public void remove(int var0) { }
	public void remove(java.awt.MenuComponent var0) { }
	public void removeAll() { }
	protected class AccessibleAWTMenu extends java.awt.MenuItem.AccessibleAWTMenuItem {
		protected AccessibleAWTMenu() { }
	}
}

