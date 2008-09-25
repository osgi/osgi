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

package javax.swing;
public class ScrollPaneLayout implements java.awt.LayoutManager, java.io.Serializable, javax.swing.ScrollPaneConstants {
	public ScrollPaneLayout() { }
	public void addLayoutComponent(java.lang.String var0, java.awt.Component var1) { }
	protected java.awt.Component addSingletonComponent(java.awt.Component var0, java.awt.Component var1) { return null; }
	public javax.swing.JViewport getColumnHeader() { return null; }
	public java.awt.Component getCorner(java.lang.String var0) { return null; }
	public javax.swing.JScrollBar getHorizontalScrollBar() { return null; }
	public int getHorizontalScrollBarPolicy() { return 0; }
	public javax.swing.JViewport getRowHeader() { return null; }
	public javax.swing.JScrollBar getVerticalScrollBar() { return null; }
	public int getVerticalScrollBarPolicy() { return 0; }
	public javax.swing.JViewport getViewport() { return null; }
	/** @deprecated */ public java.awt.Rectangle getViewportBorderBounds(javax.swing.JScrollPane var0) { return null; }
	public void layoutContainer(java.awt.Container var0) { }
	public java.awt.Dimension minimumLayoutSize(java.awt.Container var0) { return null; }
	public java.awt.Dimension preferredLayoutSize(java.awt.Container var0) { return null; }
	public void removeLayoutComponent(java.awt.Component var0) { }
	public void setHorizontalScrollBarPolicy(int var0) { }
	public void setVerticalScrollBarPolicy(int var0) { }
	public void syncWithScrollPane(javax.swing.JScrollPane var0) { }
	protected javax.swing.JViewport colHead;
	protected javax.swing.JScrollBar hsb;
	protected int hsbPolicy;
	protected java.awt.Component lowerLeft;
	protected java.awt.Component lowerRight;
	protected javax.swing.JViewport rowHead;
	protected java.awt.Component upperLeft;
	protected java.awt.Component upperRight;
	protected javax.swing.JViewport viewport;
	protected javax.swing.JScrollBar vsb;
	protected int vsbPolicy;
	public static class UIResource extends javax.swing.ScrollPaneLayout implements javax.swing.plaf.UIResource {
		public UIResource() { }
	}
}

