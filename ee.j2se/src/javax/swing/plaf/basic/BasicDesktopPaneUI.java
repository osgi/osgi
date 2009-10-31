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
public class BasicDesktopPaneUI extends javax.swing.plaf.DesktopPaneUI {
	protected class CloseAction extends javax.swing.AbstractAction {
		protected CloseAction() { } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	protected class MaximizeAction extends javax.swing.AbstractAction {
		protected MaximizeAction() { } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	protected class MinimizeAction extends javax.swing.AbstractAction {
		protected MinimizeAction() { } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	protected class NavigateAction extends javax.swing.AbstractAction {
		protected NavigateAction() { } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	protected class OpenAction extends javax.swing.AbstractAction {
		protected OpenAction() { } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	/** @deprecated */
	@java.lang.Deprecated
	protected javax.swing.KeyStroke closeKey;
	protected javax.swing.JDesktopPane desktop;
	protected javax.swing.DesktopManager desktopManager;
	/** @deprecated */
	@java.lang.Deprecated
	protected javax.swing.KeyStroke maximizeKey;
	/** @deprecated */
	@java.lang.Deprecated
	protected javax.swing.KeyStroke minimizeKey;
	/** @deprecated */
	@java.lang.Deprecated
	protected javax.swing.KeyStroke navigateKey;
	/** @deprecated */
	@java.lang.Deprecated
	protected javax.swing.KeyStroke navigateKey2;
	public BasicDesktopPaneUI() { } 
	protected java.beans.PropertyChangeListener createPropertyChangeListener() { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected void installDefaults() { }
	protected void installDesktopManager() { }
	protected void installKeyboardActions() { }
	protected void installListeners() { }
	protected void registerKeyboardActions() { }
	protected void uninstallDefaults() { }
	protected void uninstallDesktopManager() { }
	protected void uninstallKeyboardActions() { }
	protected void uninstallListeners() { }
	protected void unregisterKeyboardActions() { }
}

