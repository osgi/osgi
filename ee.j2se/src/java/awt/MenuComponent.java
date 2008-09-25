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
public abstract class MenuComponent implements java.io.Serializable {
	public MenuComponent() { }
	public final void dispatchEvent(java.awt.AWTEvent var0) { }
	public javax.accessibility.AccessibleContext getAccessibleContext() { return null; }
	public java.awt.Font getFont() { return null; }
	public java.lang.String getName() { return null; }
	public java.awt.MenuContainer getParent() { return null; }
	/** @deprecated */ public java.awt.peer.MenuComponentPeer getPeer() { return null; }
	protected final java.lang.Object getTreeLock() { return null; }
	protected java.lang.String paramString() { return null; }
	/** @deprecated */ public boolean postEvent(java.awt.Event var0) { return false; }
	protected void processEvent(java.awt.AWTEvent var0) { }
	public void removeNotify() { }
	public void setFont(java.awt.Font var0) { }
	public void setName(java.lang.String var0) { }
	protected abstract class AccessibleAWTMenuComponent extends javax.accessibility.AccessibleContext implements java.io.Serializable, javax.accessibility.AccessibleComponent, javax.accessibility.AccessibleSelection {
		protected AccessibleAWTMenuComponent() { }
		public void addAccessibleSelection(int var0) { }
		public void addFocusListener(java.awt.event.FocusListener var0) { }
		public void clearAccessibleSelection() { }
		public boolean contains(java.awt.Point var0) { return false; }
		public javax.accessibility.Accessible getAccessibleAt(java.awt.Point var0) { return null; }
		public javax.accessibility.Accessible getAccessibleChild(int var0) { return null; }
		public int getAccessibleChildrenCount() { return 0; }
		public int getAccessibleIndexInParent() { return 0; }
		public javax.accessibility.AccessibleRole getAccessibleRole() { return null; }
		public javax.accessibility.Accessible getAccessibleSelection(int var0) { return null; }
		public int getAccessibleSelectionCount() { return 0; }
		public javax.accessibility.AccessibleStateSet getAccessibleStateSet() { return null; }
		public java.awt.Color getBackground() { return null; }
		public java.awt.Rectangle getBounds() { return null; }
		public java.awt.Cursor getCursor() { return null; }
		public java.awt.Font getFont() { return null; }
		public java.awt.FontMetrics getFontMetrics(java.awt.Font var0) { return null; }
		public java.awt.Color getForeground() { return null; }
		public java.util.Locale getLocale() { return null; }
		public java.awt.Point getLocation() { return null; }
		public java.awt.Point getLocationOnScreen() { return null; }
		public java.awt.Dimension getSize() { return null; }
		public boolean isAccessibleChildSelected(int var0) { return false; }
		public boolean isEnabled() { return false; }
		public boolean isFocusTraversable() { return false; }
		public boolean isShowing() { return false; }
		public boolean isVisible() { return false; }
		public void removeAccessibleSelection(int var0) { }
		public void removeFocusListener(java.awt.event.FocusListener var0) { }
		public void requestFocus() { }
		public void selectAllAccessibleSelection() { }
		public void setBackground(java.awt.Color var0) { }
		public void setBounds(java.awt.Rectangle var0) { }
		public void setCursor(java.awt.Cursor var0) { }
		public void setEnabled(boolean var0) { }
		public void setFont(java.awt.Font var0) { }
		public void setForeground(java.awt.Color var0) { }
		public void setLocation(java.awt.Point var0) { }
		public void setSize(java.awt.Dimension var0) { }
		public void setVisible(boolean var0) { }
	}
}

