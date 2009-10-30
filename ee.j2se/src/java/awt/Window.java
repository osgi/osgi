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
public class Window extends java.awt.Container implements javax.accessibility.Accessible {
	protected class AccessibleAWTWindow extends java.awt.Container.AccessibleAWTContainer {
		protected AccessibleAWTWindow() { } 
	}
	public Window(java.awt.Frame var0) { } 
	public Window(java.awt.Window var0) { } 
	public Window(java.awt.Window var0, java.awt.GraphicsConfiguration var1) { } 
	public void addWindowFocusListener(java.awt.event.WindowFocusListener var0) { }
	public void addWindowListener(java.awt.event.WindowListener var0) { }
	public void addWindowStateListener(java.awt.event.WindowStateListener var0) { }
	/** @deprecated */ public void applyResourceBundle(java.lang.String var0) { }
	/** @deprecated */ public void applyResourceBundle(java.util.ResourceBundle var0) { }
	public void createBufferStrategy(int var0) { }
	public void createBufferStrategy(int var0, java.awt.BufferCapabilities var1) throws java.awt.AWTException { }
	public void dispose() { }
	public java.awt.image.BufferStrategy getBufferStrategy() { return null; }
	public final java.awt.Container getFocusCycleRootAncestor() { return null; }
	public java.awt.Component getFocusOwner() { return null; }
	public boolean getFocusableWindowState() { return false; }
	public java.awt.Component getMostRecentFocusOwner() { return null; }
	public java.awt.Window[] getOwnedWindows() { return null; }
	public java.awt.Window getOwner() { return null; }
	public final java.lang.String getWarningString() { return null; }
	public java.awt.event.WindowFocusListener[] getWindowFocusListeners() { return null; }
	public java.awt.event.WindowListener[] getWindowListeners() { return null; }
	public java.awt.event.WindowStateListener[] getWindowStateListeners() { return null; }
	public boolean isActive() { return false; }
	public final boolean isAlwaysOnTop() { return false; }
	public final boolean isFocusCycleRoot() { return false; }
	public final boolean isFocusableWindow() { return false; }
	public boolean isFocused() { return false; }
	public boolean isLocationByPlatform() { return false; }
	public void pack() { }
	protected void processWindowEvent(java.awt.event.WindowEvent var0) { }
	protected void processWindowFocusEvent(java.awt.event.WindowEvent var0) { }
	protected void processWindowStateEvent(java.awt.event.WindowEvent var0) { }
	public void removeWindowFocusListener(java.awt.event.WindowFocusListener var0) { }
	public void removeWindowListener(java.awt.event.WindowListener var0) { }
	public void removeWindowStateListener(java.awt.event.WindowStateListener var0) { }
	public final void setAlwaysOnTop(boolean var0) { }
	public final void setFocusCycleRoot(boolean var0) { }
	public void setFocusableWindowState(boolean var0) { }
	public void setLocationByPlatform(boolean var0) { }
	public void setLocationRelativeTo(java.awt.Component var0) { }
	public void toBack() { }
	public void toFront() { }
}

