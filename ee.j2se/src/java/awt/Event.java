/*
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
public class Event implements java.io.Serializable {
	public Event(java.lang.Object var0, int var1, java.lang.Object var2) { }
	public Event(java.lang.Object var0, long var1, int var2, int var3, int var4, int var5, int var6) { }
	public Event(java.lang.Object var0, long var1, int var2, int var3, int var4, int var5, int var6, java.lang.Object var7) { }
	public boolean controlDown() { return false; }
	public boolean metaDown() { return false; }
	protected java.lang.String paramString() { return null; }
	public boolean shiftDown() { return false; }
	public void translate(int var0, int var1) { }
	public final static int ACTION_EVENT = 1001;
	public final static int ALT_MASK = 8;
	public final static int BACK_SPACE = 8;
	public final static int CAPS_LOCK = 1022;
	public final static int CTRL_MASK = 2;
	public final static int DELETE = 127;
	public final static int DOWN = 1005;
	public final static int END = 1001;
	public final static int ENTER = 10;
	public final static int ESCAPE = 27;
	public final static int F1 = 1008;
	public final static int F10 = 1017;
	public final static int F11 = 1018;
	public final static int F12 = 1019;
	public final static int F2 = 1009;
	public final static int F3 = 1010;
	public final static int F4 = 1011;
	public final static int F5 = 1012;
	public final static int F6 = 1013;
	public final static int F7 = 1014;
	public final static int F8 = 1015;
	public final static int F9 = 1016;
	public final static int GOT_FOCUS = 1004;
	public final static int HOME = 1000;
	public final static int INSERT = 1025;
	public final static int KEY_ACTION = 403;
	public final static int KEY_ACTION_RELEASE = 404;
	public final static int KEY_PRESS = 401;
	public final static int KEY_RELEASE = 402;
	public final static int LEFT = 1006;
	public final static int LIST_DESELECT = 702;
	public final static int LIST_SELECT = 701;
	public final static int LOAD_FILE = 1002;
	public final static int LOST_FOCUS = 1005;
	public final static int META_MASK = 4;
	public final static int MOUSE_DOWN = 501;
	public final static int MOUSE_DRAG = 506;
	public final static int MOUSE_ENTER = 504;
	public final static int MOUSE_EXIT = 505;
	public final static int MOUSE_MOVE = 503;
	public final static int MOUSE_UP = 502;
	public final static int NUM_LOCK = 1023;
	public final static int PAUSE = 1024;
	public final static int PGDN = 1003;
	public final static int PGUP = 1002;
	public final static int PRINT_SCREEN = 1020;
	public final static int RIGHT = 1007;
	public final static int SAVE_FILE = 1003;
	public final static int SCROLL_ABSOLUTE = 605;
	public final static int SCROLL_BEGIN = 606;
	public final static int SCROLL_END = 607;
	public final static int SCROLL_LINE_DOWN = 602;
	public final static int SCROLL_LINE_UP = 601;
	public final static int SCROLL_LOCK = 1021;
	public final static int SCROLL_PAGE_DOWN = 604;
	public final static int SCROLL_PAGE_UP = 603;
	public final static int SHIFT_MASK = 1;
	public final static int TAB = 9;
	public final static int UP = 1004;
	public final static int WINDOW_DEICONIFY = 204;
	public final static int WINDOW_DESTROY = 201;
	public final static int WINDOW_EXPOSE = 202;
	public final static int WINDOW_ICONIFY = 203;
	public final static int WINDOW_MOVED = 205;
	public java.lang.Object arg;
	public int clickCount;
	public java.awt.Event evt;
	public int id;
	public int key;
	public int modifiers;
	public java.lang.Object target;
	public long when;
	public int x;
	public int y;
}

