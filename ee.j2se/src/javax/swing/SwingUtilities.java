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
public class SwingUtilities implements javax.swing.SwingConstants {
	public static java.awt.Rectangle calculateInnerArea(javax.swing.JComponent var0, java.awt.Rectangle var1) { return null; }
	public static java.awt.Rectangle[] computeDifference(java.awt.Rectangle var0, java.awt.Rectangle var1) { return null; }
	public static java.awt.Rectangle computeIntersection(int var0, int var1, int var2, int var3, java.awt.Rectangle var4) { return null; }
	public static int computeStringWidth(java.awt.FontMetrics var0, java.lang.String var1) { return 0; }
	public static java.awt.Rectangle computeUnion(int var0, int var1, int var2, int var3, java.awt.Rectangle var4) { return null; }
	public static java.awt.event.MouseEvent convertMouseEvent(java.awt.Component var0, java.awt.event.MouseEvent var1, java.awt.Component var2) { return null; }
	public static java.awt.Point convertPoint(java.awt.Component var0, int var1, int var2, java.awt.Component var3) { return null; }
	public static java.awt.Point convertPoint(java.awt.Component var0, java.awt.Point var1, java.awt.Component var2) { return null; }
	public static void convertPointFromScreen(java.awt.Point var0, java.awt.Component var1) { }
	public static void convertPointToScreen(java.awt.Point var0, java.awt.Component var1) { }
	public static java.awt.Rectangle convertRectangle(java.awt.Component var0, java.awt.Rectangle var1, java.awt.Component var2) { return null; }
	/** @deprecated */ public static java.awt.Component findFocusOwner(java.awt.Component var0) { return null; }
	public static javax.accessibility.Accessible getAccessibleAt(java.awt.Component var0, java.awt.Point var1) { return null; }
	public static javax.accessibility.Accessible getAccessibleChild(java.awt.Component var0, int var1) { return null; }
	public static int getAccessibleChildrenCount(java.awt.Component var0) { return 0; }
	public static int getAccessibleIndexInParent(java.awt.Component var0) { return 0; }
	public static javax.accessibility.AccessibleStateSet getAccessibleStateSet(java.awt.Component var0) { return null; }
	public static java.awt.Container getAncestorNamed(java.lang.String var0, java.awt.Component var1) { return null; }
	public static java.awt.Container getAncestorOfClass(java.lang.Class var0, java.awt.Component var1) { return null; }
	public static java.awt.Component getDeepestComponentAt(java.awt.Component var0, int var1, int var2) { return null; }
	public static java.awt.Rectangle getLocalBounds(java.awt.Component var0) { return null; }
	public static java.awt.Component getRoot(java.awt.Component var0) { return null; }
	public static javax.swing.JRootPane getRootPane(java.awt.Component var0) { return null; }
	public static javax.swing.ActionMap getUIActionMap(javax.swing.JComponent var0) { return null; }
	public static javax.swing.InputMap getUIInputMap(javax.swing.JComponent var0, int var1) { return null; }
	public static java.awt.Window getWindowAncestor(java.awt.Component var0) { return null; }
	public static void invokeAndWait(java.lang.Runnable var0) throws java.lang.InterruptedException, java.lang.reflect.InvocationTargetException { }
	public static void invokeLater(java.lang.Runnable var0) { }
	public static boolean isDescendingFrom(java.awt.Component var0, java.awt.Component var1) { return false; }
	public static boolean isEventDispatchThread() { return false; }
	public static boolean isLeftMouseButton(java.awt.event.MouseEvent var0) { return false; }
	public static boolean isMiddleMouseButton(java.awt.event.MouseEvent var0) { return false; }
	public final static boolean isRectangleContainingRectangle(java.awt.Rectangle var0, java.awt.Rectangle var1) { return false; }
	public static boolean isRightMouseButton(java.awt.event.MouseEvent var0) { return false; }
	public static java.lang.String layoutCompoundLabel(java.awt.FontMetrics var0, java.lang.String var1, javax.swing.Icon var2, int var3, int var4, int var5, int var6, java.awt.Rectangle var7, java.awt.Rectangle var8, java.awt.Rectangle var9, int var10) { return null; }
	public static java.lang.String layoutCompoundLabel(javax.swing.JComponent var0, java.awt.FontMetrics var1, java.lang.String var2, javax.swing.Icon var3, int var4, int var5, int var6, int var7, java.awt.Rectangle var8, java.awt.Rectangle var9, java.awt.Rectangle var10, int var11) { return null; }
	public static boolean notifyAction(javax.swing.Action var0, javax.swing.KeyStroke var1, java.awt.event.KeyEvent var2, java.lang.Object var3, int var4) { return false; }
	public static void paintComponent(java.awt.Graphics var0, java.awt.Component var1, java.awt.Container var2, int var3, int var4, int var5, int var6) { }
	public static void paintComponent(java.awt.Graphics var0, java.awt.Component var1, java.awt.Container var2, java.awt.Rectangle var3) { }
	public static boolean processKeyBindings(java.awt.event.KeyEvent var0) { return false; }
	public static void replaceUIActionMap(javax.swing.JComponent var0, javax.swing.ActionMap var1) { }
	public static void replaceUIInputMap(javax.swing.JComponent var0, int var1, javax.swing.InputMap var2) { }
	public static void updateComponentTreeUI(java.awt.Component var0) { }
	public static java.awt.Window windowForComponent(java.awt.Component var0) { return null; }
	private SwingUtilities() { } /* generated constructor to prevent compiler adding default public constructor */
}

