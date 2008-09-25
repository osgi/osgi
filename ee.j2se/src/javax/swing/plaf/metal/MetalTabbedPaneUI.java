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

package javax.swing.plaf.metal;
public class MetalTabbedPaneUI extends javax.swing.plaf.basic.BasicTabbedPaneUI {
	public MetalTabbedPaneUI() { }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected java.awt.Color getColorForGap(int var0, int var1, int var2) { return null; }
	protected void paintBottomTabBorder(int var0, java.awt.Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) { }
	protected void paintHighlightBelowTab() { }
	protected void paintLeftTabBorder(int var0, java.awt.Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) { }
	protected void paintRightTabBorder(int var0, java.awt.Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) { }
	protected void paintTopTabBorder(int var0, java.awt.Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) { }
	protected boolean shouldFillGap(int var0, int var1, int var2, int var3) { return false; }
	protected boolean shouldRotateTabRuns(int var0, int var1) { return false; }
	protected int minTabWidth;
	protected java.awt.Color selectColor;
	protected java.awt.Color selectHighlight;
	protected java.awt.Color tabAreaBackground;
	public class TabbedPaneLayout extends javax.swing.plaf.basic.BasicTabbedPaneUI.TabbedPaneLayout {
		public TabbedPaneLayout() { }
	}
}

