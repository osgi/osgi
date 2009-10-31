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

package java.awt;
public class Container extends java.awt.Component {
	protected class AccessibleAWTContainer extends java.awt.Component.AccessibleAWTComponent {
		protected class AccessibleContainerHandler implements java.awt.event.ContainerListener {
			protected AccessibleContainerHandler() { } 
			public void componentAdded(java.awt.event.ContainerEvent var0) { }
			public void componentRemoved(java.awt.event.ContainerEvent var0) { }
		}
		protected java.awt.event.ContainerListener accessibleContainerHandler;
		protected AccessibleAWTContainer() { } 
	}
	public Container() { } 
	public java.awt.Component add(java.awt.Component var0) { return null; }
	public java.awt.Component add(java.awt.Component var0, int var1) { return null; }
	public void add(java.awt.Component var0, java.lang.Object var1) { }
	public void add(java.awt.Component var0, java.lang.Object var1, int var2) { }
	public java.awt.Component add(java.lang.String var0, java.awt.Component var1) { return null; }
	public void addContainerListener(java.awt.event.ContainerListener var0) { }
	protected void addImpl(java.awt.Component var0, java.lang.Object var1, int var2) { }
	/** @deprecated */
	@java.lang.Deprecated
	public int countComponents() { return 0; }
	public java.awt.Component findComponentAt(int var0, int var1) { return null; }
	public java.awt.Component findComponentAt(java.awt.Point var0) { return null; }
	public java.awt.Component getComponent(int var0) { return null; }
	public int getComponentCount() { return 0; }
	public final int getComponentZOrder(java.awt.Component var0) { return 0; }
	public java.awt.Component[] getComponents() { return null; }
	public java.awt.event.ContainerListener[] getContainerListeners() { return null; }
	public java.awt.FocusTraversalPolicy getFocusTraversalPolicy() { return null; }
	public java.awt.Insets getInsets() { return null; }
	public java.awt.LayoutManager getLayout() { return null; }
	public java.awt.Point getMousePosition(boolean var0) { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	public java.awt.Insets insets() { return null; }
	public boolean isAncestorOf(java.awt.Component var0) { return false; }
	public boolean isFocusCycleRoot() { return false; }
	public final boolean isFocusTraversalPolicyProvider() { return false; }
	public boolean isFocusTraversalPolicySet() { return false; }
	public void paintComponents(java.awt.Graphics var0) { }
	public void printComponents(java.awt.Graphics var0) { }
	protected void processContainerEvent(java.awt.event.ContainerEvent var0) { }
	public void remove(int var0) { }
	public void remove(java.awt.Component var0) { }
	public void removeAll() { }
	public void removeContainerListener(java.awt.event.ContainerListener var0) { }
	public final void setComponentZOrder(java.awt.Component var0, int var1) { }
	public void setFocusCycleRoot(boolean var0) { }
	public void setFocusTraversalPolicy(java.awt.FocusTraversalPolicy var0) { }
	public final void setFocusTraversalPolicyProvider(boolean var0) { }
	public void setLayout(java.awt.LayoutManager var0) { }
	public void transferFocusDownCycle() { }
	protected void validateTree() { }
}

