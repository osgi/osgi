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

package java.awt;
public abstract class KeyboardFocusManager implements java.awt.KeyEventDispatcher, java.awt.KeyEventPostProcessor {
	public KeyboardFocusManager() { }
	public void addKeyEventDispatcher(java.awt.KeyEventDispatcher var0) { }
	public void addKeyEventPostProcessor(java.awt.KeyEventPostProcessor var0) { }
	public void addPropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public void addPropertyChangeListener(java.lang.String var0, java.beans.PropertyChangeListener var1) { }
	public void addVetoableChangeListener(java.beans.VetoableChangeListener var0) { }
	public void addVetoableChangeListener(java.lang.String var0, java.beans.VetoableChangeListener var1) { }
	public void clearGlobalFocusOwner() { }
	protected abstract void dequeueKeyEvents(long var0, java.awt.Component var1);
	protected abstract void discardKeyEvents(java.awt.Component var0);
	public abstract boolean dispatchEvent(java.awt.AWTEvent var0);
	public final void downFocusCycle() { }
	public abstract void downFocusCycle(java.awt.Container var0);
	protected abstract void enqueueKeyEvents(long var0, java.awt.Component var1);
	protected void firePropertyChange(java.lang.String var0, java.lang.Object var1, java.lang.Object var2) { }
	protected void fireVetoableChange(java.lang.String var0, java.lang.Object var1, java.lang.Object var2) throws java.beans.PropertyVetoException { }
	public final void focusNextComponent() { }
	public abstract void focusNextComponent(java.awt.Component var0);
	public final void focusPreviousComponent() { }
	public abstract void focusPreviousComponent(java.awt.Component var0);
	public java.awt.Window getActiveWindow() { return null; }
	public java.awt.Container getCurrentFocusCycleRoot() { return null; }
	public static java.awt.KeyboardFocusManager getCurrentKeyboardFocusManager() { return null; }
	public java.util.Set getDefaultFocusTraversalKeys(int var0) { return null; }
	public java.awt.FocusTraversalPolicy getDefaultFocusTraversalPolicy() { return null; }
	public java.awt.Component getFocusOwner() { return null; }
	public java.awt.Window getFocusedWindow() { return null; }
	protected java.awt.Window getGlobalActiveWindow() { return null; }
	protected java.awt.Container getGlobalCurrentFocusCycleRoot() { return null; }
	protected java.awt.Component getGlobalFocusOwner() { return null; }
	protected java.awt.Window getGlobalFocusedWindow() { return null; }
	protected java.awt.Component getGlobalPermanentFocusOwner() { return null; }
	protected java.util.List getKeyEventDispatchers() { return null; }
	protected java.util.List getKeyEventPostProcessors() { return null; }
	public java.awt.Component getPermanentFocusOwner() { return null; }
	public java.beans.PropertyChangeListener[] getPropertyChangeListeners() { return null; }
	public java.beans.PropertyChangeListener[] getPropertyChangeListeners(java.lang.String var0) { return null; }
	public java.beans.VetoableChangeListener[] getVetoableChangeListeners() { return null; }
	public java.beans.VetoableChangeListener[] getVetoableChangeListeners(java.lang.String var0) { return null; }
	public abstract void processKeyEvent(java.awt.Component var0, java.awt.event.KeyEvent var1);
	public final void redispatchEvent(java.awt.Component var0, java.awt.AWTEvent var1) { }
	public void removeKeyEventDispatcher(java.awt.KeyEventDispatcher var0) { }
	public void removeKeyEventPostProcessor(java.awt.KeyEventPostProcessor var0) { }
	public void removePropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public void removePropertyChangeListener(java.lang.String var0, java.beans.PropertyChangeListener var1) { }
	public void removeVetoableChangeListener(java.beans.VetoableChangeListener var0) { }
	public void removeVetoableChangeListener(java.lang.String var0, java.beans.VetoableChangeListener var1) { }
	public static void setCurrentKeyboardFocusManager(java.awt.KeyboardFocusManager var0) { }
	public void setDefaultFocusTraversalKeys(int var0, java.util.Set var1) { }
	public void setDefaultFocusTraversalPolicy(java.awt.FocusTraversalPolicy var0) { }
	protected void setGlobalActiveWindow(java.awt.Window var0) { }
	public void setGlobalCurrentFocusCycleRoot(java.awt.Container var0) { }
	protected void setGlobalFocusOwner(java.awt.Component var0) { }
	protected void setGlobalFocusedWindow(java.awt.Window var0) { }
	protected void setGlobalPermanentFocusOwner(java.awt.Component var0) { }
	public final void upFocusCycle() { }
	public abstract void upFocusCycle(java.awt.Component var0);
	public final static int BACKWARD_TRAVERSAL_KEYS = 1;
	public final static int DOWN_CYCLE_TRAVERSAL_KEYS = 3;
	public final static int FORWARD_TRAVERSAL_KEYS = 0;
	public final static int UP_CYCLE_TRAVERSAL_KEYS = 2;
}

