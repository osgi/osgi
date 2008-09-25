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
public class JFrame extends java.awt.Frame implements javax.accessibility.Accessible, javax.swing.RootPaneContainer, javax.swing.WindowConstants {
	public JFrame() { }
	public JFrame(java.awt.GraphicsConfiguration var0) { }
	public JFrame(java.lang.String var0) { }
	public JFrame(java.lang.String var0, java.awt.GraphicsConfiguration var1) { }
	protected javax.swing.JRootPane createRootPane() { return null; }
	protected void frameInit() { }
	public java.awt.Container getContentPane() { return null; }
	public int getDefaultCloseOperation() { return 0; }
	public java.awt.Component getGlassPane() { return null; }
	public javax.swing.JMenuBar getJMenuBar() { return null; }
	public javax.swing.JLayeredPane getLayeredPane() { return null; }
	public javax.swing.JRootPane getRootPane() { return null; }
	public static boolean isDefaultLookAndFeelDecorated() { return false; }
	protected boolean isRootPaneCheckingEnabled() { return false; }
	public void setContentPane(java.awt.Container var0) { }
	public void setDefaultCloseOperation(int var0) { }
	public static void setDefaultLookAndFeelDecorated(boolean var0) { }
	public void setGlassPane(java.awt.Component var0) { }
	public void setJMenuBar(javax.swing.JMenuBar var0) { }
	public void setLayeredPane(javax.swing.JLayeredPane var0) { }
	protected void setRootPane(javax.swing.JRootPane var0) { }
	protected void setRootPaneCheckingEnabled(boolean var0) { }
	public final static int EXIT_ON_CLOSE = 3;
	protected javax.accessibility.AccessibleContext accessibleContext;
	protected javax.swing.JRootPane rootPane;
	protected boolean rootPaneCheckingEnabled;
	protected class AccessibleJFrame extends java.awt.Frame.AccessibleAWTFrame {
		protected AccessibleJFrame() { }
	}
}

