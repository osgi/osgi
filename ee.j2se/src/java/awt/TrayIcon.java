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
public class TrayIcon {
	public enum MessageType {
		ERROR,
		INFO,
		NONE,
		WARNING;
	}
	public TrayIcon(java.awt.Image var0) { } 
	public TrayIcon(java.awt.Image var0, java.lang.String var1) { } 
	public TrayIcon(java.awt.Image var0, java.lang.String var1, java.awt.PopupMenu var2) { } 
	public void addActionListener(java.awt.event.ActionListener var0) { }
	public void addMouseListener(java.awt.event.MouseListener var0) { }
	public void addMouseMotionListener(java.awt.event.MouseMotionListener var0) { }
	public void displayMessage(java.lang.String var0, java.lang.String var1, java.awt.TrayIcon.MessageType var2) { }
	public java.lang.String getActionCommand() { return null; }
	public java.awt.event.ActionListener[] getActionListeners() { return null; }
	public java.awt.Image getImage() { return null; }
	public java.awt.event.MouseListener[] getMouseListeners() { return null; }
	public java.awt.event.MouseMotionListener[] getMouseMotionListeners() { return null; }
	public java.awt.PopupMenu getPopupMenu() { return null; }
	public java.awt.Dimension getSize() { return null; }
	public java.lang.String getToolTip() { return null; }
	public boolean isImageAutoSize() { return false; }
	public void removeActionListener(java.awt.event.ActionListener var0) { }
	public void removeMouseListener(java.awt.event.MouseListener var0) { }
	public void removeMouseMotionListener(java.awt.event.MouseMotionListener var0) { }
	public void setActionCommand(java.lang.String var0) { }
	public void setImage(java.awt.Image var0) { }
	public void setImageAutoSize(boolean var0) { }
	public void setPopupMenu(java.awt.PopupMenu var0) { }
	public void setToolTip(java.lang.String var0) { }
}

