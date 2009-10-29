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

package javax.swing.event;
public class TreeSelectionEvent extends java.util.EventObject {
	public TreeSelectionEvent(java.lang.Object var0, javax.swing.tree.TreePath var1, boolean var2, javax.swing.tree.TreePath var3, javax.swing.tree.TreePath var4) { super((java.lang.Object) null); }
	public TreeSelectionEvent(java.lang.Object var0, javax.swing.tree.TreePath[] var1, boolean[] var2, javax.swing.tree.TreePath var3, javax.swing.tree.TreePath var4) { super((java.lang.Object) null); }
	public java.lang.Object cloneWithSource(java.lang.Object var0) { return null; }
	public javax.swing.tree.TreePath getNewLeadSelectionPath() { return null; }
	public javax.swing.tree.TreePath getOldLeadSelectionPath() { return null; }
	public javax.swing.tree.TreePath getPath() { return null; }
	public javax.swing.tree.TreePath[] getPaths() { return null; }
	public boolean isAddedPath() { return false; }
	public boolean isAddedPath(int var0) { return false; }
	public boolean isAddedPath(javax.swing.tree.TreePath var0) { return false; }
	protected boolean[] areNew;
	protected javax.swing.tree.TreePath newLeadSelectionPath;
	protected javax.swing.tree.TreePath oldLeadSelectionPath;
	protected javax.swing.tree.TreePath[] paths;
}

