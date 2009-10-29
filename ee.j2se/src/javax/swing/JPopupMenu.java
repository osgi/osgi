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

package javax.swing;
public class JPopupMenu extends javax.swing.JComponent implements javax.accessibility.Accessible, javax.swing.MenuElement {
	public JPopupMenu() { }
	public JPopupMenu(java.lang.String var0) { }
	public javax.swing.JMenuItem add(java.lang.String var0) { return null; }
	public javax.swing.JMenuItem add(javax.swing.Action var0) { return null; }
	public javax.swing.JMenuItem add(javax.swing.JMenuItem var0) { return null; }
	public void addPopupMenuListener(javax.swing.event.PopupMenuListener var0) { }
	public void addSeparator() { }
	protected java.beans.PropertyChangeListener createActionChangeListener(javax.swing.JMenuItem var0) { return null; }
	protected javax.swing.JMenuItem createActionComponent(javax.swing.Action var0) { return null; }
	protected void firePopupMenuCanceled() { }
	protected void firePopupMenuWillBecomeInvisible() { }
	protected void firePopupMenuWillBecomeVisible() { }
	public java.awt.Component getComponent() { return null; }
	/** @deprecated */ public java.awt.Component getComponentAtIndex(int var0) { return null; }
	public int getComponentIndex(java.awt.Component var0) { return 0; }
	public static boolean getDefaultLightWeightPopupEnabled() { return false; }
	public java.awt.Component getInvoker() { return null; }
	public java.lang.String getLabel() { return null; }
	public java.awt.Insets getMargin() { return null; }
	public javax.swing.event.PopupMenuListener[] getPopupMenuListeners() { return null; }
	public javax.swing.SingleSelectionModel getSelectionModel() { return null; }
	public javax.swing.MenuElement[] getSubElements() { return null; }
	public javax.swing.plaf.PopupMenuUI getUI() { return null; }
	public void insert(java.awt.Component var0, int var1) { }
	public void insert(javax.swing.Action var0, int var1) { }
	public boolean isBorderPainted() { return false; }
	public boolean isLightWeightPopupEnabled() { return false; }
	public boolean isPopupTrigger(java.awt.event.MouseEvent var0) { return false; }
	public void menuSelectionChanged(boolean var0) { }
	public void pack() { }
	public void processKeyEvent(java.awt.event.KeyEvent var0, javax.swing.MenuElement[] var1, javax.swing.MenuSelectionManager var2) { }
	public void processMouseEvent(java.awt.event.MouseEvent var0, javax.swing.MenuElement[] var1, javax.swing.MenuSelectionManager var2) { }
	public void removePopupMenuListener(javax.swing.event.PopupMenuListener var0) { }
	public void setBorderPainted(boolean var0) { }
	public static void setDefaultLightWeightPopupEnabled(boolean var0) { }
	public void setInvoker(java.awt.Component var0) { }
	public void setLabel(java.lang.String var0) { }
	public void setLightWeightPopupEnabled(boolean var0) { }
	public void setPopupSize(int var0, int var1) { }
	public void setPopupSize(java.awt.Dimension var0) { }
	public void setSelected(java.awt.Component var0) { }
	public void setSelectionModel(javax.swing.SingleSelectionModel var0) { }
	public void setUI(javax.swing.plaf.PopupMenuUI var0) { }
	public void show(java.awt.Component var0, int var1, int var2) { }
	protected class AccessibleJPopupMenu extends javax.swing.JComponent.AccessibleJComponent {
		protected AccessibleJPopupMenu() { }
	}
	public static class Separator extends javax.swing.JSeparator {
		public Separator() { }
	}
}

