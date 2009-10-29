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
public abstract class LookAndFeel {
	public LookAndFeel() { }
	public javax.swing.UIDefaults getDefaults() { return null; }
	public abstract java.lang.String getDescription();
	public static java.lang.Object getDesktopPropertyValue(java.lang.String var0, java.lang.Object var1) { return null; }
	public abstract java.lang.String getID();
	public abstract java.lang.String getName();
	public boolean getSupportsWindowDecorations() { return false; }
	public void initialize() { }
	public static void installBorder(javax.swing.JComponent var0, java.lang.String var1) { }
	public static void installColors(javax.swing.JComponent var0, java.lang.String var1, java.lang.String var2) { }
	public static void installColorsAndFont(javax.swing.JComponent var0, java.lang.String var1, java.lang.String var2, java.lang.String var3) { }
	public abstract boolean isNativeLookAndFeel();
	public abstract boolean isSupportedLookAndFeel();
	public static void loadKeyBindings(javax.swing.InputMap var0, java.lang.Object[] var1) { }
	public static javax.swing.ComponentInputMap makeComponentInputMap(javax.swing.JComponent var0, java.lang.Object[] var1) { return null; }
	public static java.lang.Object makeIcon(java.lang.Class var0, java.lang.String var1) { return null; }
	public static javax.swing.InputMap makeInputMap(java.lang.Object[] var0) { return null; }
	public static javax.swing.text.JTextComponent.KeyBinding[] makeKeyBindings(java.lang.Object[] var0) { return null; }
	public void provideErrorFeedback(java.awt.Component var0) { }
	public void uninitialize() { }
	public static void uninstallBorder(javax.swing.JComponent var0) { }
}

