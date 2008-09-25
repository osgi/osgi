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
public class MenuBar extends java.awt.MenuComponent implements java.awt.MenuContainer, javax.accessibility.Accessible {
	public MenuBar() { }
	public java.awt.Menu add(java.awt.Menu var0) { return null; }
	public void addNotify() { }
	/** @deprecated */ public int countMenus() { return 0; }
	public void deleteShortcut(java.awt.MenuShortcut var0) { }
	public java.awt.Menu getHelpMenu() { return null; }
	public java.awt.Menu getMenu(int var0) { return null; }
	public int getMenuCount() { return 0; }
	public java.awt.MenuItem getShortcutMenuItem(java.awt.MenuShortcut var0) { return null; }
	public void remove(int var0) { }
	public void remove(java.awt.MenuComponent var0) { }
	public void setHelpMenu(java.awt.Menu var0) { }
	public java.util.Enumeration shortcuts() { return null; }
	protected class AccessibleAWTMenuBar extends java.awt.MenuComponent.AccessibleAWTMenuComponent {
		protected AccessibleAWTMenuBar() { }
	}
}

