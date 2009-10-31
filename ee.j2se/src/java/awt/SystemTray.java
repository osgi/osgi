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
public class SystemTray {
	public void add(java.awt.TrayIcon var0) throws java.awt.AWTException { }
	public void addPropertyChangeListener(java.lang.String var0, java.beans.PropertyChangeListener var1) { }
	public java.beans.PropertyChangeListener[] getPropertyChangeListeners(java.lang.String var0) { return null; }
	public static java.awt.SystemTray getSystemTray() { return null; }
	public java.awt.Dimension getTrayIconSize() { return null; }
	public java.awt.TrayIcon[] getTrayIcons() { return null; }
	public static boolean isSupported() { return false; }
	public void remove(java.awt.TrayIcon var0) { }
	public void removePropertyChangeListener(java.lang.String var0, java.beans.PropertyChangeListener var1) { }
	private SystemTray() { } /* generated constructor to prevent compiler adding default public constructor */
}

