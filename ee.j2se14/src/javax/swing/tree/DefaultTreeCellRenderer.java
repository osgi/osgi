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

package javax.swing.tree;
public class DefaultTreeCellRenderer extends javax.swing.JLabel implements javax.swing.tree.TreeCellRenderer {
	public DefaultTreeCellRenderer() { }
	public java.awt.Color getBackgroundNonSelectionColor() { return null; }
	public java.awt.Color getBackgroundSelectionColor() { return null; }
	public java.awt.Color getBorderSelectionColor() { return null; }
	public javax.swing.Icon getClosedIcon() { return null; }
	public javax.swing.Icon getDefaultClosedIcon() { return null; }
	public javax.swing.Icon getDefaultLeafIcon() { return null; }
	public javax.swing.Icon getDefaultOpenIcon() { return null; }
	public javax.swing.Icon getLeafIcon() { return null; }
	public javax.swing.Icon getOpenIcon() { return null; }
	public java.awt.Color getTextNonSelectionColor() { return null; }
	public java.awt.Color getTextSelectionColor() { return null; }
	public java.awt.Component getTreeCellRendererComponent(javax.swing.JTree var0, java.lang.Object var1, boolean var2, boolean var3, boolean var4, int var5, boolean var6) { return null; }
	public void setBackgroundNonSelectionColor(java.awt.Color var0) { }
	public void setBackgroundSelectionColor(java.awt.Color var0) { }
	public void setBorderSelectionColor(java.awt.Color var0) { }
	public void setClosedIcon(javax.swing.Icon var0) { }
	public void setLeafIcon(javax.swing.Icon var0) { }
	public void setOpenIcon(javax.swing.Icon var0) { }
	public void setTextNonSelectionColor(java.awt.Color var0) { }
	public void setTextSelectionColor(java.awt.Color var0) { }
	protected java.awt.Color backgroundNonSelectionColor;
	protected java.awt.Color backgroundSelectionColor;
	protected java.awt.Color borderSelectionColor;
	protected javax.swing.Icon closedIcon;
	protected boolean hasFocus;
	protected javax.swing.Icon leafIcon;
	protected javax.swing.Icon openIcon;
	protected boolean selected;
	protected java.awt.Color textNonSelectionColor;
	protected java.awt.Color textSelectionColor;
}

