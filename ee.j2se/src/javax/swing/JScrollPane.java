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

package javax.swing;
public class JScrollPane extends javax.swing.JComponent implements javax.accessibility.Accessible, javax.swing.ScrollPaneConstants {
	protected class AccessibleJScrollPane extends javax.swing.JComponent.AccessibleJComponent implements java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
		protected javax.swing.JViewport viewPort;
		public AccessibleJScrollPane() { } 
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
		public void resetViewPort() { }
		public void stateChanged(javax.swing.event.ChangeEvent var0) { }
	}
	protected class ScrollBar extends javax.swing.JScrollBar implements javax.swing.plaf.UIResource {
		public ScrollBar(int var0) { } 
	}
	protected javax.swing.JViewport columnHeader;
	protected javax.swing.JScrollBar horizontalScrollBar;
	protected int horizontalScrollBarPolicy;
	protected java.awt.Component lowerLeft;
	protected java.awt.Component lowerRight;
	protected javax.swing.JViewport rowHeader;
	protected java.awt.Component upperLeft;
	protected java.awt.Component upperRight;
	protected javax.swing.JScrollBar verticalScrollBar;
	protected int verticalScrollBarPolicy;
	protected javax.swing.JViewport viewport;
	public JScrollPane() { } 
	public JScrollPane(int var0, int var1) { } 
	public JScrollPane(java.awt.Component var0) { } 
	public JScrollPane(java.awt.Component var0, int var1, int var2) { } 
	public javax.swing.JScrollBar createHorizontalScrollBar() { return null; }
	public javax.swing.JScrollBar createVerticalScrollBar() { return null; }
	protected javax.swing.JViewport createViewport() { return null; }
	public javax.swing.JViewport getColumnHeader() { return null; }
	public java.awt.Component getCorner(java.lang.String var0) { return null; }
	public javax.swing.JScrollBar getHorizontalScrollBar() { return null; }
	public int getHorizontalScrollBarPolicy() { return 0; }
	public javax.swing.JViewport getRowHeader() { return null; }
	public javax.swing.plaf.ScrollPaneUI getUI() { return null; }
	public javax.swing.JScrollBar getVerticalScrollBar() { return null; }
	public int getVerticalScrollBarPolicy() { return 0; }
	public javax.swing.JViewport getViewport() { return null; }
	public javax.swing.border.Border getViewportBorder() { return null; }
	public java.awt.Rectangle getViewportBorderBounds() { return null; }
	public boolean isWheelScrollingEnabled() { return false; }
	public void setColumnHeader(javax.swing.JViewport var0) { }
	public void setColumnHeaderView(java.awt.Component var0) { }
	public void setCorner(java.lang.String var0, java.awt.Component var1) { }
	public void setHorizontalScrollBar(javax.swing.JScrollBar var0) { }
	public void setHorizontalScrollBarPolicy(int var0) { }
	public void setRowHeader(javax.swing.JViewport var0) { }
	public void setRowHeaderView(java.awt.Component var0) { }
	public void setUI(javax.swing.plaf.ScrollPaneUI var0) { }
	public void setVerticalScrollBar(javax.swing.JScrollBar var0) { }
	public void setVerticalScrollBarPolicy(int var0) { }
	public void setViewport(javax.swing.JViewport var0) { }
	public void setViewportBorder(javax.swing.border.Border var0) { }
	public void setViewportView(java.awt.Component var0) { }
	public void setWheelScrollingEnabled(boolean var0) { }
}

