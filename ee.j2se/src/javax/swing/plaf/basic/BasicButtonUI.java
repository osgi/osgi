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

package javax.swing.plaf.basic;
public class BasicButtonUI extends javax.swing.plaf.ButtonUI {
	public BasicButtonUI() { }
	protected void clearTextShiftOffset() { }
	protected javax.swing.plaf.basic.BasicButtonListener createButtonListener(javax.swing.AbstractButton var0) { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	public int getDefaultTextIconGap(javax.swing.AbstractButton var0) { return 0; }
	protected java.lang.String getPropertyPrefix() { return null; }
	protected int getTextShiftOffset() { return 0; }
	protected void installDefaults(javax.swing.AbstractButton var0) { }
	protected void installKeyboardActions(javax.swing.AbstractButton var0) { }
	protected void installListeners(javax.swing.AbstractButton var0) { }
	protected void paintButtonPressed(java.awt.Graphics var0, javax.swing.AbstractButton var1) { }
	protected void paintFocus(java.awt.Graphics var0, javax.swing.AbstractButton var1, java.awt.Rectangle var2, java.awt.Rectangle var3, java.awt.Rectangle var4) { }
	protected void paintIcon(java.awt.Graphics var0, javax.swing.JComponent var1, java.awt.Rectangle var2) { }
	protected void paintText(java.awt.Graphics var0, javax.swing.AbstractButton var1, java.awt.Rectangle var2, java.lang.String var3) { }
	protected void paintText(java.awt.Graphics var0, javax.swing.JComponent var1, java.awt.Rectangle var2, java.lang.String var3) { }
	protected void setTextShiftOffset() { }
	protected void uninstallDefaults(javax.swing.AbstractButton var0) { }
	protected void uninstallKeyboardActions(javax.swing.AbstractButton var0) { }
	protected void uninstallListeners(javax.swing.AbstractButton var0) { }
	protected int defaultTextIconGap;
	protected int defaultTextShiftOffset;
}

