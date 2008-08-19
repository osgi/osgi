/*
 * $Date$
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

package javax.swing;
public class SortingFocusTraversalPolicy extends javax.swing.InternalFrameFocusTraversalPolicy {
	protected SortingFocusTraversalPolicy() { }
	public SortingFocusTraversalPolicy(java.util.Comparator var0) { }
	protected boolean accept(java.awt.Component var0) { return false; }
	protected java.util.Comparator getComparator() { return null; }
	public java.awt.Component getComponentAfter(java.awt.Container var0, java.awt.Component var1) { return null; }
	public java.awt.Component getComponentBefore(java.awt.Container var0, java.awt.Component var1) { return null; }
	public java.awt.Component getDefaultComponent(java.awt.Container var0) { return null; }
	public java.awt.Component getFirstComponent(java.awt.Container var0) { return null; }
	public boolean getImplicitDownCycleTraversal() { return false; }
	public java.awt.Component getLastComponent(java.awt.Container var0) { return null; }
	protected void setComparator(java.util.Comparator var0) { }
	public void setImplicitDownCycleTraversal(boolean var0) { }
}

