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

package javax.swing.plaf.multi;
public class MultiTreeUI extends javax.swing.plaf.TreeUI {
	public MultiTreeUI() { }
	public void cancelEditing(javax.swing.JTree var0) { }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	public javax.swing.tree.TreePath getClosestPathForLocation(javax.swing.JTree var0, int var1, int var2) { return null; }
	public javax.swing.tree.TreePath getEditingPath(javax.swing.JTree var0) { return null; }
	public java.awt.Rectangle getPathBounds(javax.swing.JTree var0, javax.swing.tree.TreePath var1) { return null; }
	public javax.swing.tree.TreePath getPathForRow(javax.swing.JTree var0, int var1) { return null; }
	public int getRowCount(javax.swing.JTree var0) { return 0; }
	public int getRowForPath(javax.swing.JTree var0, javax.swing.tree.TreePath var1) { return 0; }
	public javax.swing.plaf.ComponentUI[] getUIs() { return null; }
	public boolean isEditing(javax.swing.JTree var0) { return false; }
	public void startEditingAtPath(javax.swing.JTree var0, javax.swing.tree.TreePath var1) { }
	public boolean stopEditing(javax.swing.JTree var0) { return false; }
	protected java.util.Vector uis;
}

