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
public abstract class AWTEvent extends java.util.EventObject {
	public AWTEvent(java.awt.Event var0) { super((java.lang.Object) null); }
	public AWTEvent(java.lang.Object var0, int var1) { super((java.lang.Object) null); }
	protected void consume() { }
	public int getID() { return 0; }
	protected boolean isConsumed() { return false; }
	public java.lang.String paramString() { return null; }
	public void setSource(java.lang.Object var0) { }
	public final static long ACTION_EVENT_MASK = 128l;
	public final static long ADJUSTMENT_EVENT_MASK = 256l;
	public final static long COMPONENT_EVENT_MASK = 1l;
	public final static long CONTAINER_EVENT_MASK = 2l;
	public final static long FOCUS_EVENT_MASK = 4l;
	public final static long HIERARCHY_BOUNDS_EVENT_MASK = 65536l;
	public final static long HIERARCHY_EVENT_MASK = 32768l;
	public final static long INPUT_METHOD_EVENT_MASK = 2048l;
	public final static long INVOCATION_EVENT_MASK = 16384l;
	public final static long ITEM_EVENT_MASK = 512l;
	public final static long KEY_EVENT_MASK = 8l;
	public final static long MOUSE_EVENT_MASK = 16l;
	public final static long MOUSE_MOTION_EVENT_MASK = 32l;
	public final static long MOUSE_WHEEL_EVENT_MASK = 131072l;
	public final static long PAINT_EVENT_MASK = 8192l;
	public final static int RESERVED_ID_MAX = 1999;
	public final static long TEXT_EVENT_MASK = 1024l;
	public final static long WINDOW_EVENT_MASK = 64l;
	public final static long WINDOW_FOCUS_EVENT_MASK = 524288l;
	public final static long WINDOW_STATE_EVENT_MASK = 262144l;
	protected boolean consumed;
	protected int id;
}

