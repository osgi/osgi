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

package javax.swing.event;
public class TreeModelEvent extends java.util.EventObject {
	protected int[] childIndices;
	protected java.lang.Object[] children;
	protected javax.swing.tree.TreePath path;
	public TreeModelEvent(java.lang.Object var0, javax.swing.tree.TreePath var1)  { super((java.lang.Object) null); } 
	public TreeModelEvent(java.lang.Object var0, javax.swing.tree.TreePath var1, int[] var2, java.lang.Object[] var3)  { super((java.lang.Object) null); } 
	public TreeModelEvent(java.lang.Object var0, java.lang.Object[] var1)  { super((java.lang.Object) null); } 
	public TreeModelEvent(java.lang.Object var0, java.lang.Object[] var1, int[] var2, java.lang.Object[] var3)  { super((java.lang.Object) null); } 
	public int[] getChildIndices() { return null; }
	public java.lang.Object[] getChildren() { return null; }
	public java.lang.Object[] getPath() { return null; }
	public javax.swing.tree.TreePath getTreePath() { return null; }
}

