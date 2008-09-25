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

package java.awt;
public class Cursor implements java.io.Serializable {
	public Cursor(int var0) { }
	protected Cursor(java.lang.String var0) { }
	public static java.awt.Cursor getDefaultCursor() { return null; }
	public java.lang.String getName() { return null; }
	public static java.awt.Cursor getPredefinedCursor(int var0) { return null; }
	public static java.awt.Cursor getSystemCustomCursor(java.lang.String var0) throws java.awt.AWTException { return null; }
	public int getType() { return 0; }
	public final static int CROSSHAIR_CURSOR = 1;
	public final static int CUSTOM_CURSOR = -1;
	public final static int DEFAULT_CURSOR = 0;
	public final static int E_RESIZE_CURSOR = 11;
	public final static int HAND_CURSOR = 12;
	public final static int MOVE_CURSOR = 13;
	public final static int NE_RESIZE_CURSOR = 7;
	public final static int NW_RESIZE_CURSOR = 6;
	public final static int N_RESIZE_CURSOR = 8;
	public final static int SE_RESIZE_CURSOR = 5;
	public final static int SW_RESIZE_CURSOR = 4;
	public final static int S_RESIZE_CURSOR = 9;
	public final static int TEXT_CURSOR = 2;
	public final static int WAIT_CURSOR = 3;
	public final static int W_RESIZE_CURSOR = 10;
	protected java.lang.String name;
	protected static java.awt.Cursor[] predefined;
}

