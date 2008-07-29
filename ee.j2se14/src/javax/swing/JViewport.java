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

package javax.swing;
public class JViewport extends javax.swing.JComponent implements javax.accessibility.Accessible {
	public JViewport() { }
	public void addChangeListener(javax.swing.event.ChangeListener var0) { }
	protected boolean computeBlit(int var0, int var1, java.awt.Point var2, java.awt.Point var3, java.awt.Dimension var4, java.awt.Rectangle var5) { return false; }
	protected java.awt.LayoutManager createLayoutManager() { return null; }
	protected javax.swing.JViewport.ViewListener createViewListener() { return null; }
	protected void fireStateChanged() { }
	public javax.swing.event.ChangeListener[] getChangeListeners() { return null; }
	public java.awt.Dimension getExtentSize() { return null; }
	public final java.awt.Insets getInsets() { return null; }
	public final java.awt.Insets getInsets(java.awt.Insets var0) { return null; }
	public int getScrollMode() { return 0; }
	public javax.swing.plaf.ViewportUI getUI() { return null; }
	public java.awt.Component getView() { return null; }
	public java.awt.Point getViewPosition() { return null; }
	public java.awt.Rectangle getViewRect() { return null; }
	public java.awt.Dimension getViewSize() { return null; }
	/** @deprecated */ public boolean isBackingStoreEnabled() { return false; }
	public void removeChangeListener(javax.swing.event.ChangeListener var0) { }
	/** @deprecated */ public void setBackingStoreEnabled(boolean var0) { }
	public final void setBorder(javax.swing.border.Border var0) { }
	public void setExtentSize(java.awt.Dimension var0) { }
	public void setScrollMode(int var0) { }
	public void setUI(javax.swing.plaf.ViewportUI var0) { }
	public void setView(java.awt.Component var0) { }
	public void setViewPosition(java.awt.Point var0) { }
	public void setViewSize(java.awt.Dimension var0) { }
	public java.awt.Dimension toViewCoordinates(java.awt.Dimension var0) { return null; }
	public java.awt.Point toViewCoordinates(java.awt.Point var0) { return null; }
	public final static int BACKINGSTORE_SCROLL_MODE = 2;
	public final static int BLIT_SCROLL_MODE = 1;
	public final static int SIMPLE_SCROLL_MODE = 0;
	/** @deprecated */ protected boolean backingStore;
	protected java.awt.Image backingStoreImage;
	protected boolean isViewSizeSet;
	protected java.awt.Point lastPaintPosition;
	protected boolean scrollUnderway;
	protected class AccessibleJViewport extends javax.swing.JComponent.AccessibleJComponent {
		protected AccessibleJViewport() { }
	}
	protected class ViewListener extends java.awt.event.ComponentAdapter implements java.io.Serializable {
		protected ViewListener() { }
	}
}

