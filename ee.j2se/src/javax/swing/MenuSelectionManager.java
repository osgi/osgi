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

package javax.swing;
public class MenuSelectionManager {
	protected javax.swing.event.ChangeEvent changeEvent;
	protected javax.swing.event.EventListenerList listenerList;
	public MenuSelectionManager() { } 
	public void addChangeListener(javax.swing.event.ChangeListener var0) { }
	public void clearSelectedPath() { }
	public java.awt.Component componentForPoint(java.awt.Component var0, java.awt.Point var1) { return null; }
	public static javax.swing.MenuSelectionManager defaultManager() { return null; }
	protected void fireStateChanged() { }
	public javax.swing.event.ChangeListener[] getChangeListeners() { return null; }
	public javax.swing.MenuElement[] getSelectedPath() { return null; }
	public boolean isComponentPartOfCurrentMenu(java.awt.Component var0) { return false; }
	public void processKeyEvent(java.awt.event.KeyEvent var0) { }
	public void processMouseEvent(java.awt.event.MouseEvent var0) { }
	public void removeChangeListener(javax.swing.event.ChangeListener var0) { }
	public void setSelectedPath(javax.swing.MenuElement[] var0) { }
}

