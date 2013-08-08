/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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
public class LayerUI<V extends java.awt.Component> extends javax.swing.plaf.ComponentUI implements java.io.Serializable {
	public LayerUI() { } 
	public void addPropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public void addPropertyChangeListener(java.lang.String var0, java.beans.PropertyChangeListener var1) { }
	public void applyPropertyChange(java.beans.PropertyChangeEvent var0, javax.swing.JLayer<? extends V> var1) { }
	public void doLayout(javax.swing.JLayer<? extends V> var0) { }
	public void eventDispatched(java.awt.AWTEvent var0, javax.swing.JLayer<? extends V> var1) { }
	protected void firePropertyChange(java.lang.String var0, java.lang.Object var1, java.lang.Object var2) { }
	public java.beans.PropertyChangeListener[] getPropertyChangeListeners() { return null; }
	public java.beans.PropertyChangeListener[] getPropertyChangeListeners(java.lang.String var0) { return null; }
	public void paintImmediately(int var0, int var1, int var2, int var3, javax.swing.JLayer<? extends V> var4) { }
	protected void processComponentEvent(java.awt.event.ComponentEvent var0, javax.swing.JLayer<? extends V> var1) { }
	protected void processFocusEvent(java.awt.event.FocusEvent var0, javax.swing.JLayer<? extends V> var1) { }
	protected void processHierarchyBoundsEvent(java.awt.event.HierarchyEvent var0, javax.swing.JLayer<? extends V> var1) { }
	protected void processHierarchyEvent(java.awt.event.HierarchyEvent var0, javax.swing.JLayer<? extends V> var1) { }
	protected void processInputMethodEvent(java.awt.event.InputMethodEvent var0, javax.swing.JLayer<? extends V> var1) { }
	protected void processKeyEvent(java.awt.event.KeyEvent var0, javax.swing.JLayer<? extends V> var1) { }
	protected void processMouseEvent(java.awt.event.MouseEvent var0, javax.swing.JLayer<? extends V> var1) { }
	protected void processMouseMotionEvent(java.awt.event.MouseEvent var0, javax.swing.JLayer<? extends V> var1) { }
	protected void processMouseWheelEvent(java.awt.event.MouseWheelEvent var0, javax.swing.JLayer<? extends V> var1) { }
	public void removePropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public void removePropertyChangeListener(java.lang.String var0, java.beans.PropertyChangeListener var1) { }
	public void updateUI(javax.swing.JLayer<? extends V> var0) { }
}

