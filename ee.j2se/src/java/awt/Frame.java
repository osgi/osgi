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
public class Frame extends java.awt.Window implements java.awt.MenuContainer {
	public Frame() { super((java.awt.Window) null, (java.awt.GraphicsConfiguration) null); }
	public Frame(java.awt.GraphicsConfiguration var0) { super((java.awt.Window) null, (java.awt.GraphicsConfiguration) null); }
	public Frame(java.lang.String var0) { super((java.awt.Window) null, (java.awt.GraphicsConfiguration) null); }
	public Frame(java.lang.String var0, java.awt.GraphicsConfiguration var1) { super((java.awt.Window) null, (java.awt.GraphicsConfiguration) null); }
	/** @deprecated */ public int getCursorType() { return 0; }
	public int getExtendedState() { return 0; }
	public static java.awt.Frame[] getFrames() { return null; }
	public java.awt.Image getIconImage() { return null; }
	public java.awt.Rectangle getMaximizedBounds() { return null; }
	public java.awt.MenuBar getMenuBar() { return null; }
	public int getState() { return 0; }
	public java.lang.String getTitle() { return null; }
	public boolean isResizable() { return false; }
	public boolean isUndecorated() { return false; }
	public void remove(java.awt.MenuComponent var0) { }
	/** @deprecated */ public void setCursor(int var0) { }
	public void setExtendedState(int var0) { }
	public void setIconImage(java.awt.Image var0) { }
	public void setMaximizedBounds(java.awt.Rectangle var0) { }
	public void setMenuBar(java.awt.MenuBar var0) { }
	public void setResizable(boolean var0) { }
	public void setState(int var0) { }
	public void setTitle(java.lang.String var0) { }
	public void setUndecorated(boolean var0) { }
	/** @deprecated */ public final static int CROSSHAIR_CURSOR = 1;
	/** @deprecated */ public final static int DEFAULT_CURSOR = 0;
	/** @deprecated */ public final static int E_RESIZE_CURSOR = 11;
	/** @deprecated */ public final static int HAND_CURSOR = 12;
	public final static int ICONIFIED = 1;
	public final static int MAXIMIZED_BOTH = 6;
	public final static int MAXIMIZED_HORIZ = 2;
	public final static int MAXIMIZED_VERT = 4;
	/** @deprecated */ public final static int MOVE_CURSOR = 13;
	/** @deprecated */ public final static int NE_RESIZE_CURSOR = 7;
	public final static int NORMAL = 0;
	/** @deprecated */ public final static int NW_RESIZE_CURSOR = 6;
	/** @deprecated */ public final static int N_RESIZE_CURSOR = 8;
	/** @deprecated */ public final static int SE_RESIZE_CURSOR = 5;
	/** @deprecated */ public final static int SW_RESIZE_CURSOR = 4;
	/** @deprecated */ public final static int S_RESIZE_CURSOR = 9;
	/** @deprecated */ public final static int TEXT_CURSOR = 2;
	/** @deprecated */ public final static int WAIT_CURSOR = 3;
	/** @deprecated */ public final static int W_RESIZE_CURSOR = 10;
	protected class AccessibleAWTFrame extends java.awt.Window.AccessibleAWTWindow {
		protected AccessibleAWTFrame() { }
	}
}

