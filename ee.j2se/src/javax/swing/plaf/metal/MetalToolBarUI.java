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

package javax.swing.plaf.metal;
public class MetalToolBarUI extends javax.swing.plaf.basic.BasicToolBarUI {
	public MetalToolBarUI() { }
	protected java.awt.event.ContainerListener createContainerListener() { return null; }
	protected java.beans.PropertyChangeListener createRolloverListener() { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected void setDragOffset(java.awt.Point var0) { }
	protected java.awt.event.ContainerListener contListener;
	protected java.beans.PropertyChangeListener rolloverListener;
	protected class MetalContainerListener extends javax.swing.plaf.basic.BasicToolBarUI.ToolBarContListener {
		protected MetalContainerListener() { }
	}
	protected class MetalDockingListener extends javax.swing.plaf.basic.BasicToolBarUI.DockingListener {
		public MetalDockingListener(javax.swing.JToolBar var0) { super((javax.swing.JToolBar) null); }
	}
	protected class MetalRolloverListener extends javax.swing.plaf.basic.BasicToolBarUI.PropertyListener {
		protected MetalRolloverListener() { }
	}
}

