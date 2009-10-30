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
public class DefaultKeyboardFocusManager extends java.awt.KeyboardFocusManager {
	public DefaultKeyboardFocusManager() { } 
	protected void dequeueKeyEvents(long var0, java.awt.Component var1) { }
	protected void discardKeyEvents(java.awt.Component var0) { }
	public boolean dispatchEvent(java.awt.AWTEvent var0) { return false; }
	public boolean dispatchKeyEvent(java.awt.event.KeyEvent var0) { return false; }
	public void downFocusCycle(java.awt.Container var0) { }
	protected void enqueueKeyEvents(long var0, java.awt.Component var1) { }
	public void focusNextComponent(java.awt.Component var0) { }
	public void focusPreviousComponent(java.awt.Component var0) { }
	public boolean postProcessKeyEvent(java.awt.event.KeyEvent var0) { return false; }
	public void processKeyEvent(java.awt.Component var0, java.awt.event.KeyEvent var1) { }
	public void upFocusCycle(java.awt.Component var0) { }
}

