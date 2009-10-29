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
public class UIManager implements java.io.Serializable {
	public UIManager() { }
	public static void addAuxiliaryLookAndFeel(javax.swing.LookAndFeel var0) { }
	public static void addPropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public static java.lang.Object get(java.lang.Object var0) { return null; }
	public static java.lang.Object get(java.lang.Object var0, java.util.Locale var1) { return null; }
	public static javax.swing.LookAndFeel[] getAuxiliaryLookAndFeels() { return null; }
	public static boolean getBoolean(java.lang.Object var0) { return false; }
	public static boolean getBoolean(java.lang.Object var0, java.util.Locale var1) { return false; }
	public static javax.swing.border.Border getBorder(java.lang.Object var0) { return null; }
	public static javax.swing.border.Border getBorder(java.lang.Object var0, java.util.Locale var1) { return null; }
	public static java.awt.Color getColor(java.lang.Object var0) { return null; }
	public static java.awt.Color getColor(java.lang.Object var0, java.util.Locale var1) { return null; }
	public static java.lang.String getCrossPlatformLookAndFeelClassName() { return null; }
	public static javax.swing.UIDefaults getDefaults() { return null; }
	public static java.awt.Dimension getDimension(java.lang.Object var0) { return null; }
	public static java.awt.Dimension getDimension(java.lang.Object var0, java.util.Locale var1) { return null; }
	public static java.awt.Font getFont(java.lang.Object var0) { return null; }
	public static java.awt.Font getFont(java.lang.Object var0, java.util.Locale var1) { return null; }
	public static javax.swing.Icon getIcon(java.lang.Object var0) { return null; }
	public static javax.swing.Icon getIcon(java.lang.Object var0, java.util.Locale var1) { return null; }
	public static java.awt.Insets getInsets(java.lang.Object var0) { return null; }
	public static java.awt.Insets getInsets(java.lang.Object var0, java.util.Locale var1) { return null; }
	public static javax.swing.UIManager.LookAndFeelInfo[] getInstalledLookAndFeels() { return null; }
	public static int getInt(java.lang.Object var0) { return 0; }
	public static int getInt(java.lang.Object var0, java.util.Locale var1) { return 0; }
	public static javax.swing.LookAndFeel getLookAndFeel() { return null; }
	public static javax.swing.UIDefaults getLookAndFeelDefaults() { return null; }
	public static java.beans.PropertyChangeListener[] getPropertyChangeListeners() { return null; }
	public static java.lang.String getString(java.lang.Object var0) { return null; }
	public static java.lang.String getString(java.lang.Object var0, java.util.Locale var1) { return null; }
	public static java.lang.String getSystemLookAndFeelClassName() { return null; }
	public static javax.swing.plaf.ComponentUI getUI(javax.swing.JComponent var0) { return null; }
	public static void installLookAndFeel(java.lang.String var0, java.lang.String var1) { }
	public static void installLookAndFeel(javax.swing.UIManager.LookAndFeelInfo var0) { }
	public static java.lang.Object put(java.lang.Object var0, java.lang.Object var1) { return null; }
	public static boolean removeAuxiliaryLookAndFeel(javax.swing.LookAndFeel var0) { return false; }
	public static void removePropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public static void setInstalledLookAndFeels(javax.swing.UIManager.LookAndFeelInfo[] var0) { }
	public static void setLookAndFeel(java.lang.String var0) throws java.lang.ClassNotFoundException, java.lang.IllegalAccessException, java.lang.InstantiationException, javax.swing.UnsupportedLookAndFeelException { }
	public static void setLookAndFeel(javax.swing.LookAndFeel var0) throws javax.swing.UnsupportedLookAndFeelException { }
	public static class LookAndFeelInfo {
		public LookAndFeelInfo(java.lang.String var0, java.lang.String var1) { }
		public java.lang.String getClassName() { return null; }
		public java.lang.String getName() { return null; }
	}
}

