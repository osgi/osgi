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

package java.awt.event;
public class MouseEvent extends java.awt.event.InputEvent {
	public final static int BUTTON1 = 1;
	public final static int BUTTON2 = 2;
	public final static int BUTTON3 = 3;
	public final static int MOUSE_CLICKED = 500;
	public final static int MOUSE_DRAGGED = 506;
	public final static int MOUSE_ENTERED = 504;
	public final static int MOUSE_EXITED = 505;
	public final static int MOUSE_FIRST = 500;
	public final static int MOUSE_LAST = 507;
	public final static int MOUSE_MOVED = 503;
	public final static int MOUSE_PRESSED = 501;
	public final static int MOUSE_RELEASED = 502;
	public final static int MOUSE_WHEEL = 507;
	public final static int NOBUTTON = 0;
	public MouseEvent(java.awt.Component var0, int var1, long var2, int var3, int var4, int var5, int var6, boolean var7) { } 
	public MouseEvent(java.awt.Component var0, int var1, long var2, int var3, int var4, int var5, int var6, boolean var7, int var8) { } 
	public int getButton() { return 0; }
	public int getClickCount() { return 0; }
	public static java.lang.String getMouseModifiersText(int var0) { return null; }
	public java.awt.Point getPoint() { return null; }
	public int getX() { return 0; }
	public int getY() { return 0; }
	public boolean isPopupTrigger() { return false; }
	public void translatePoint(int var0, int var1) { }
}

