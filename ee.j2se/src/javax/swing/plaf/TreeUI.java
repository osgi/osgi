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

package javax.swing.plaf;
public abstract class TreeUI extends javax.swing.plaf.ComponentUI {
	public TreeUI() { }
	public abstract void cancelEditing(javax.swing.JTree var0);
	public abstract javax.swing.tree.TreePath getClosestPathForLocation(javax.swing.JTree var0, int var1, int var2);
	public abstract javax.swing.tree.TreePath getEditingPath(javax.swing.JTree var0);
	public abstract java.awt.Rectangle getPathBounds(javax.swing.JTree var0, javax.swing.tree.TreePath var1);
	public abstract javax.swing.tree.TreePath getPathForRow(javax.swing.JTree var0, int var1);
	public abstract int getRowCount(javax.swing.JTree var0);
	public abstract int getRowForPath(javax.swing.JTree var0, javax.swing.tree.TreePath var1);
	public abstract boolean isEditing(javax.swing.JTree var0);
	public abstract void startEditingAtPath(javax.swing.JTree var0, javax.swing.tree.TreePath var1);
	public abstract boolean stopEditing(javax.swing.JTree var0);
}

