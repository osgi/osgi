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
public class ScrollPane extends java.awt.Container implements javax.accessibility.Accessible {
	public ScrollPane() { }
	public ScrollPane(int var0) { }
	protected final void addImpl(java.awt.Component var0, java.lang.Object var1, int var2) { }
	protected boolean eventTypeEnabled(int var0) { return false; }
	public java.awt.Adjustable getHAdjustable() { return null; }
	public int getHScrollbarHeight() { return 0; }
	public java.awt.Point getScrollPosition() { return null; }
	public int getScrollbarDisplayPolicy() { return 0; }
	public java.awt.Adjustable getVAdjustable() { return null; }
	public int getVScrollbarWidth() { return 0; }
	public java.awt.Dimension getViewportSize() { return null; }
	public boolean isWheelScrollingEnabled() { return false; }
	public java.lang.String paramString() { return null; }
	public final void setLayout(java.awt.LayoutManager var0) { }
	public void setScrollPosition(int var0, int var1) { }
	public void setScrollPosition(java.awt.Point var0) { }
	public void setWheelScrollingEnabled(boolean var0) { }
	public final static int SCROLLBARS_ALWAYS = 1;
	public final static int SCROLLBARS_AS_NEEDED = 0;
	public final static int SCROLLBARS_NEVER = 2;
	protected class AccessibleAWTScrollPane extends java.awt.Container.AccessibleAWTContainer {
		protected AccessibleAWTScrollPane() { }
	}
}

