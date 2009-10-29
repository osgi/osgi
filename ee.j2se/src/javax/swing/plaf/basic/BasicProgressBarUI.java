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

package javax.swing.plaf.basic;
public class BasicProgressBarUI extends javax.swing.plaf.ProgressBarUI {
	public BasicProgressBarUI() { }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected int getAmountFull(java.awt.Insets var0, int var1, int var2) { return 0; }
	protected int getAnimationIndex() { return 0; }
	protected java.awt.Rectangle getBox(java.awt.Rectangle var0) { return null; }
	protected int getCellLength() { return 0; }
	protected int getCellSpacing() { return 0; }
	protected java.awt.Dimension getPreferredInnerHorizontal() { return null; }
	protected java.awt.Dimension getPreferredInnerVertical() { return null; }
	protected java.awt.Color getSelectionBackground() { return null; }
	protected java.awt.Color getSelectionForeground() { return null; }
	protected java.awt.Point getStringPlacement(java.awt.Graphics var0, java.lang.String var1, int var2, int var3, int var4, int var5) { return null; }
	protected void incrementAnimationIndex() { }
	protected void installDefaults() { }
	protected void installListeners() { }
	protected void paintDeterminate(java.awt.Graphics var0, javax.swing.JComponent var1) { }
	protected void paintIndeterminate(java.awt.Graphics var0, javax.swing.JComponent var1) { }
	protected void paintString(java.awt.Graphics var0, int var1, int var2, int var3, int var4, int var5, java.awt.Insets var6) { }
	protected void setAnimationIndex(int var0) { }
	protected void setCellLength(int var0) { }
	protected void setCellSpacing(int var0) { }
	protected void startAnimationTimer() { }
	protected void stopAnimationTimer() { }
	protected void uninstallDefaults() { }
	protected void uninstallListeners() { }
	protected javax.swing.event.ChangeListener changeListener;
	protected javax.swing.JProgressBar progressBar;
	public class ChangeHandler implements javax.swing.event.ChangeListener {
		public ChangeHandler() { }
		public void stateChanged(javax.swing.event.ChangeEvent var0) { }
	}
}

