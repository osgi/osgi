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

package javax.swing.event;
public class MenuKeyEvent extends java.awt.event.KeyEvent {
	public MenuKeyEvent(java.awt.Component var0, int var1, long var2, int var3, int var4, char var5, javax.swing.MenuElement[] var6, javax.swing.MenuSelectionManager var7) { super((java.awt.Component) null, 0, 0l, 0, 0, '\0', 0); }
	public javax.swing.MenuSelectionManager getMenuSelectionManager() { return null; }
	public javax.swing.MenuElement[] getPath() { return null; }
}

