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
public class JRootPane extends javax.swing.JComponent implements javax.accessibility.Accessible {
	protected class AccessibleJRootPane extends javax.swing.JComponent.AccessibleJComponent {
		protected AccessibleJRootPane() { } 
	}
	class DefaultAction extends javax.swing.AbstractAction {
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
		public void setOwner(javax.swing.JButton var0) { }
		private DefaultAction() { } /* generated constructor to prevent compiler adding default public constructor */
	}
	protected class RootLayout implements java.awt.LayoutManager2, java.io.Serializable {
		protected RootLayout() { } 
		public void addLayoutComponent(java.awt.Component var0, java.lang.Object var1) { }
		public void addLayoutComponent(java.lang.String var0, java.awt.Component var1) { }
		public float getLayoutAlignmentX(java.awt.Container var0) { return 0.0f; }
		public float getLayoutAlignmentY(java.awt.Container var0) { return 0.0f; }
		public void invalidateLayout(java.awt.Container var0) { }
		public void layoutContainer(java.awt.Container var0) { }
		public java.awt.Dimension maximumLayoutSize(java.awt.Container var0) { return null; }
		public java.awt.Dimension minimumLayoutSize(java.awt.Container var0) { return null; }
		public java.awt.Dimension preferredLayoutSize(java.awt.Container var0) { return null; }
		public void removeLayoutComponent(java.awt.Component var0) { }
	}
	public final static int COLOR_CHOOSER_DIALOG = 5;
	public final static int ERROR_DIALOG = 4;
	public final static int FILE_CHOOSER_DIALOG = 6;
	public final static int FRAME = 1;
	public final static int INFORMATION_DIALOG = 3;
	public final static int NONE = 0;
	public final static int PLAIN_DIALOG = 2;
	public final static int QUESTION_DIALOG = 7;
	public final static int WARNING_DIALOG = 8;
	protected java.awt.Container contentPane;
	protected javax.swing.JButton defaultButton;
	/** @deprecated */
	@java.lang.Deprecated
	protected javax.swing.JRootPane.DefaultAction defaultPressAction;
	/** @deprecated */
	@java.lang.Deprecated
	protected javax.swing.JRootPane.DefaultAction defaultReleaseAction;
	protected java.awt.Component glassPane;
	protected javax.swing.JLayeredPane layeredPane;
	protected javax.swing.JMenuBar menuBar;
	public JRootPane() { } 
	protected java.awt.Container createContentPane() { return null; }
	protected java.awt.Component createGlassPane() { return null; }
	protected javax.swing.JLayeredPane createLayeredPane() { return null; }
	protected java.awt.LayoutManager createRootLayout() { return null; }
	public java.awt.Container getContentPane() { return null; }
	public javax.swing.JButton getDefaultButton() { return null; }
	public java.awt.Component getGlassPane() { return null; }
	public javax.swing.JMenuBar getJMenuBar() { return null; }
	public javax.swing.JLayeredPane getLayeredPane() { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	public javax.swing.JMenuBar getMenuBar() { return null; }
	public javax.swing.plaf.RootPaneUI getUI() { return null; }
	public int getWindowDecorationStyle() { return 0; }
	public void setContentPane(java.awt.Container var0) { }
	public void setDefaultButton(javax.swing.JButton var0) { }
	public void setGlassPane(java.awt.Component var0) { }
	public void setJMenuBar(javax.swing.JMenuBar var0) { }
	public void setLayeredPane(javax.swing.JLayeredPane var0) { }
	/** @deprecated */
	@java.lang.Deprecated
	public void setMenuBar(javax.swing.JMenuBar var0) { }
	public void setUI(javax.swing.plaf.RootPaneUI var0) { }
	public void setWindowDecorationStyle(int var0) { }
}

