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
public class BasicDesktopIconUI extends javax.swing.plaf.DesktopIconUI {
	public class MouseInputHandler extends javax.swing.event.MouseInputAdapter {
		public MouseInputHandler() { } 
		public void moveAndRepaint(javax.swing.JComponent var0, int var1, int var2, int var3, int var4) { }
	}
	protected javax.swing.JInternalFrame.JDesktopIcon desktopIcon;
	protected javax.swing.JInternalFrame frame;
	protected javax.swing.JComponent iconPane;
	public BasicDesktopIconUI() { } 
	protected javax.swing.event.MouseInputListener createMouseInputListener() { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	public void deiconize() { }
	public java.awt.Insets getInsets(javax.swing.JComponent var0) { return null; }
	protected void installComponents() { }
	protected void installDefaults() { }
	protected void installListeners() { }
	protected void uninstallComponents() { }
	protected void uninstallDefaults() { }
	protected void uninstallListeners() { }
}

