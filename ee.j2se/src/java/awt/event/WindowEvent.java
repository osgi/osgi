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

package java.awt.event;
public class WindowEvent extends java.awt.event.ComponentEvent {
	public WindowEvent(java.awt.Window var0, int var1) { super((java.awt.Component) null, 0); }
	public WindowEvent(java.awt.Window var0, int var1, int var2, int var3) { super((java.awt.Component) null, 0); }
	public WindowEvent(java.awt.Window var0, int var1, java.awt.Window var2) { super((java.awt.Component) null, 0); }
	public WindowEvent(java.awt.Window var0, int var1, java.awt.Window var2, int var3, int var4) { super((java.awt.Component) null, 0); }
	public int getNewState() { return 0; }
	public int getOldState() { return 0; }
	public java.awt.Window getOppositeWindow() { return null; }
	public java.awt.Window getWindow() { return null; }
	public final static int WINDOW_ACTIVATED = 205;
	public final static int WINDOW_CLOSED = 202;
	public final static int WINDOW_CLOSING = 201;
	public final static int WINDOW_DEACTIVATED = 206;
	public final static int WINDOW_DEICONIFIED = 204;
	public final static int WINDOW_FIRST = 200;
	public final static int WINDOW_GAINED_FOCUS = 207;
	public final static int WINDOW_ICONIFIED = 203;
	public final static int WINDOW_LAST = 209;
	public final static int WINDOW_LOST_FOCUS = 208;
	public final static int WINDOW_OPENED = 200;
	public final static int WINDOW_STATE_CHANGED = 209;
}

