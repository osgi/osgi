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

package javax.swing.plaf.basic;
public class BasicLabelUI extends javax.swing.plaf.LabelUI implements java.beans.PropertyChangeListener {
	protected static javax.swing.plaf.basic.BasicLabelUI labelUI;
	public BasicLabelUI() { } 
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected void installComponents(javax.swing.JLabel var0) { }
	protected void installDefaults(javax.swing.JLabel var0) { }
	protected void installKeyboardActions(javax.swing.JLabel var0) { }
	protected void installListeners(javax.swing.JLabel var0) { }
	protected java.lang.String layoutCL(javax.swing.JLabel var0, java.awt.FontMetrics var1, java.lang.String var2, javax.swing.Icon var3, java.awt.Rectangle var4, java.awt.Rectangle var5, java.awt.Rectangle var6) { return null; }
	protected void paintDisabledText(javax.swing.JLabel var0, java.awt.Graphics var1, java.lang.String var2, int var3, int var4) { }
	protected void paintEnabledText(javax.swing.JLabel var0, java.awt.Graphics var1, java.lang.String var2, int var3, int var4) { }
	public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	protected void uninstallComponents(javax.swing.JLabel var0) { }
	protected void uninstallDefaults(javax.swing.JLabel var0) { }
	protected void uninstallKeyboardActions(javax.swing.JLabel var0) { }
	protected void uninstallListeners(javax.swing.JLabel var0) { }
}

