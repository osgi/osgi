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
public class JToolBar extends javax.swing.JComponent implements javax.accessibility.Accessible, javax.swing.SwingConstants {
	protected class AccessibleJToolBar extends javax.swing.JComponent.AccessibleJComponent {
		protected AccessibleJToolBar() { } 
	}
	public static class Separator extends javax.swing.JSeparator {
		public Separator() { } 
		public Separator(java.awt.Dimension var0) { } 
		public java.awt.Dimension getSeparatorSize() { return null; }
		public void setSeparatorSize(java.awt.Dimension var0) { }
	}
	public JToolBar() { } 
	public JToolBar(int var0) { } 
	public JToolBar(java.lang.String var0) { } 
	public JToolBar(java.lang.String var0, int var1) { } 
	public javax.swing.JButton add(javax.swing.Action var0) { return null; }
	public void addSeparator() { }
	public void addSeparator(java.awt.Dimension var0) { }
	protected java.beans.PropertyChangeListener createActionChangeListener(javax.swing.JButton var0) { return null; }
	protected javax.swing.JButton createActionComponent(javax.swing.Action var0) { return null; }
	public java.awt.Component getComponentAtIndex(int var0) { return null; }
	public int getComponentIndex(java.awt.Component var0) { return 0; }
	public java.awt.Insets getMargin() { return null; }
	public int getOrientation() { return 0; }
	public javax.swing.plaf.ToolBarUI getUI() { return null; }
	public boolean isBorderPainted() { return false; }
	public boolean isFloatable() { return false; }
	public boolean isRollover() { return false; }
	public void setBorderPainted(boolean var0) { }
	public void setFloatable(boolean var0) { }
	public void setMargin(java.awt.Insets var0) { }
	public void setOrientation(int var0) { }
	public void setRollover(boolean var0) { }
	public void setUI(javax.swing.plaf.ToolBarUI var0) { }
}

