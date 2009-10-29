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

package java.awt.event;
public abstract class InputEvent extends java.awt.event.ComponentEvent {
	public void consume() { }
	public int getModifiers() { return 0; }
	public int getModifiersEx() { return 0; }
	public static java.lang.String getModifiersExText(int var0) { return null; }
	public long getWhen() { return 0l; }
	public boolean isAltDown() { return false; }
	public boolean isAltGraphDown() { return false; }
	public boolean isConsumed() { return false; }
	public boolean isControlDown() { return false; }
	public boolean isMetaDown() { return false; }
	public boolean isShiftDown() { return false; }
	public final static int ALT_DOWN_MASK = 512;
	public final static int ALT_GRAPH_DOWN_MASK = 8192;
	public final static int ALT_GRAPH_MASK = 32;
	public final static int ALT_MASK = 8;
	public final static int BUTTON1_DOWN_MASK = 1024;
	public final static int BUTTON1_MASK = 16;
	public final static int BUTTON2_DOWN_MASK = 2048;
	public final static int BUTTON2_MASK = 8;
	public final static int BUTTON3_DOWN_MASK = 4096;
	public final static int BUTTON3_MASK = 4;
	public final static int CTRL_DOWN_MASK = 128;
	public final static int CTRL_MASK = 2;
	public final static int META_DOWN_MASK = 256;
	public final static int META_MASK = 4;
	public final static int SHIFT_DOWN_MASK = 64;
	public final static int SHIFT_MASK = 1;
	InputEvent() { super((java.awt.Component) null, 0); } /* generated constructor to prevent compiler adding default public constructor */
}

