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
public class JDesktopPane extends javax.swing.JLayeredPane implements javax.accessibility.Accessible {
	public JDesktopPane() { }
	public javax.swing.JInternalFrame[] getAllFrames() { return null; }
	public javax.swing.JInternalFrame[] getAllFramesInLayer(int var0) { return null; }
	public javax.swing.DesktopManager getDesktopManager() { return null; }
	public int getDragMode() { return 0; }
	public javax.swing.JInternalFrame getSelectedFrame() { return null; }
	public javax.swing.plaf.DesktopPaneUI getUI() { return null; }
	public void setDesktopManager(javax.swing.DesktopManager var0) { }
	public void setDragMode(int var0) { }
	public void setSelectedFrame(javax.swing.JInternalFrame var0) { }
	public void setUI(javax.swing.plaf.DesktopPaneUI var0) { }
	public static int LIVE_DRAG_MODE;
	public static int OUTLINE_DRAG_MODE;
	protected class AccessibleJDesktopPane extends javax.swing.JComponent.AccessibleJComponent {
		protected AccessibleJDesktopPane() { }
	}
}

