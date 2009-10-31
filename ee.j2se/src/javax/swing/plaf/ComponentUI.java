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

package javax.swing.plaf;
public abstract class ComponentUI {
	public ComponentUI() { } 
	public boolean contains(javax.swing.JComponent var0, int var1, int var2) { return false; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	public javax.accessibility.Accessible getAccessibleChild(javax.swing.JComponent var0, int var1) { return null; }
	public int getAccessibleChildrenCount(javax.swing.JComponent var0) { return 0; }
	public int getBaseline(javax.swing.JComponent var0, int var1, int var2) { return 0; }
	public java.awt.Component.BaselineResizeBehavior getBaselineResizeBehavior(javax.swing.JComponent var0) { return null; }
	public java.awt.Dimension getMaximumSize(javax.swing.JComponent var0) { return null; }
	public java.awt.Dimension getMinimumSize(javax.swing.JComponent var0) { return null; }
	public java.awt.Dimension getPreferredSize(javax.swing.JComponent var0) { return null; }
	public void installUI(javax.swing.JComponent var0) { }
	public void paint(java.awt.Graphics var0, javax.swing.JComponent var1) { }
	public void uninstallUI(javax.swing.JComponent var0) { }
	public void update(java.awt.Graphics var0, javax.swing.JComponent var1) { }
}

