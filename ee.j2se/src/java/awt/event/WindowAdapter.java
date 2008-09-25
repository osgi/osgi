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

package java.awt.event;
public abstract class WindowAdapter implements java.awt.event.WindowFocusListener, java.awt.event.WindowListener, java.awt.event.WindowStateListener {
	public WindowAdapter() { }
	public void windowActivated(java.awt.event.WindowEvent var0) { }
	public void windowClosed(java.awt.event.WindowEvent var0) { }
	public void windowClosing(java.awt.event.WindowEvent var0) { }
	public void windowDeactivated(java.awt.event.WindowEvent var0) { }
	public void windowDeiconified(java.awt.event.WindowEvent var0) { }
	public void windowGainedFocus(java.awt.event.WindowEvent var0) { }
	public void windowIconified(java.awt.event.WindowEvent var0) { }
	public void windowLostFocus(java.awt.event.WindowEvent var0) { }
	public void windowOpened(java.awt.event.WindowEvent var0) { }
	public void windowStateChanged(java.awt.event.WindowEvent var0) { }
}

