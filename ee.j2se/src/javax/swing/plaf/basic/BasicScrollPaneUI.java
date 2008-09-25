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
public class BasicScrollPaneUI extends javax.swing.plaf.ScrollPaneUI implements javax.swing.ScrollPaneConstants {
	public BasicScrollPaneUI() { }
	protected javax.swing.event.ChangeListener createHSBChangeListener() { return null; }
	protected java.awt.event.MouseWheelListener createMouseWheelListener() { return null; }
	protected java.beans.PropertyChangeListener createPropertyChangeListener() { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected javax.swing.event.ChangeListener createVSBChangeListener() { return null; }
	protected javax.swing.event.ChangeListener createViewportChangeListener() { return null; }
	protected void installDefaults(javax.swing.JScrollPane var0) { }
	protected void installKeyboardActions(javax.swing.JScrollPane var0) { }
	protected void installListeners(javax.swing.JScrollPane var0) { }
	protected void syncScrollPaneWithViewport() { }
	protected void uninstallDefaults(javax.swing.JScrollPane var0) { }
	protected void uninstallKeyboardActions(javax.swing.JScrollPane var0) { }
	protected void uninstallListeners(javax.swing.JComponent var0) { }
	protected void updateColumnHeader(java.beans.PropertyChangeEvent var0) { }
	protected void updateRowHeader(java.beans.PropertyChangeEvent var0) { }
	protected void updateScrollBarDisplayPolicy(java.beans.PropertyChangeEvent var0) { }
	protected void updateViewport(java.beans.PropertyChangeEvent var0) { }
	protected javax.swing.event.ChangeListener hsbChangeListener;
	protected javax.swing.JScrollPane scrollpane;
	protected java.beans.PropertyChangeListener spPropertyChangeListener;
	protected javax.swing.event.ChangeListener viewportChangeListener;
	protected javax.swing.event.ChangeListener vsbChangeListener;
	public class HSBChangeListener implements javax.swing.event.ChangeListener {
		public HSBChangeListener() { }
		public void stateChanged(javax.swing.event.ChangeEvent var0) { }
	}
	protected class MouseWheelHandler implements java.awt.event.MouseWheelListener {
		protected MouseWheelHandler() { }
		public void mouseWheelMoved(java.awt.event.MouseWheelEvent var0) { }
	}
	public class PropertyChangeHandler implements java.beans.PropertyChangeListener {
		public PropertyChangeHandler() { }
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	}
	public class VSBChangeListener implements javax.swing.event.ChangeListener {
		public VSBChangeListener() { }
		public void stateChanged(javax.swing.event.ChangeEvent var0) { }
	}
	public class ViewportChangeHandler implements javax.swing.event.ChangeListener {
		public ViewportChangeHandler() { }
		public void stateChanged(javax.swing.event.ChangeEvent var0) { }
	}
}

