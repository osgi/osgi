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

package javax.swing.tree;
public class DefaultMutableTreeNode implements java.io.Serializable, java.lang.Cloneable, javax.swing.tree.MutableTreeNode {
	public DefaultMutableTreeNode() { }
	public DefaultMutableTreeNode(java.lang.Object var0) { }
	public DefaultMutableTreeNode(java.lang.Object var0, boolean var1) { }
	public void add(javax.swing.tree.MutableTreeNode var0) { }
	public java.util.Enumeration breadthFirstEnumeration() { return null; }
	public java.util.Enumeration children() { return null; }
	public java.lang.Object clone() { return null; }
	public java.util.Enumeration depthFirstEnumeration() { return null; }
	public boolean getAllowsChildren() { return false; }
	public javax.swing.tree.TreeNode getChildAfter(javax.swing.tree.TreeNode var0) { return null; }
	public javax.swing.tree.TreeNode getChildAt(int var0) { return null; }
	public javax.swing.tree.TreeNode getChildBefore(javax.swing.tree.TreeNode var0) { return null; }
	public int getChildCount() { return 0; }
	public int getDepth() { return 0; }
	public javax.swing.tree.TreeNode getFirstChild() { return null; }
	public javax.swing.tree.DefaultMutableTreeNode getFirstLeaf() { return null; }
	public int getIndex(javax.swing.tree.TreeNode var0) { return 0; }
	public javax.swing.tree.TreeNode getLastChild() { return null; }
	public javax.swing.tree.DefaultMutableTreeNode getLastLeaf() { return null; }
	public int getLeafCount() { return 0; }
	public int getLevel() { return 0; }
	public javax.swing.tree.DefaultMutableTreeNode getNextLeaf() { return null; }
	public javax.swing.tree.DefaultMutableTreeNode getNextNode() { return null; }
	public javax.swing.tree.DefaultMutableTreeNode getNextSibling() { return null; }
	public javax.swing.tree.TreeNode getParent() { return null; }
	public javax.swing.tree.TreeNode[] getPath() { return null; }
	protected javax.swing.tree.TreeNode[] getPathToRoot(javax.swing.tree.TreeNode var0, int var1) { return null; }
	public javax.swing.tree.DefaultMutableTreeNode getPreviousLeaf() { return null; }
	public javax.swing.tree.DefaultMutableTreeNode getPreviousNode() { return null; }
	public javax.swing.tree.DefaultMutableTreeNode getPreviousSibling() { return null; }
	public javax.swing.tree.TreeNode getRoot() { return null; }
	public javax.swing.tree.TreeNode getSharedAncestor(javax.swing.tree.DefaultMutableTreeNode var0) { return null; }
	public int getSiblingCount() { return 0; }
	public java.lang.Object getUserObject() { return null; }
	public java.lang.Object[] getUserObjectPath() { return null; }
	public void insert(javax.swing.tree.MutableTreeNode var0, int var1) { }
	public boolean isLeaf() { return false; }
	public boolean isNodeAncestor(javax.swing.tree.TreeNode var0) { return false; }
	public boolean isNodeChild(javax.swing.tree.TreeNode var0) { return false; }
	public boolean isNodeDescendant(javax.swing.tree.DefaultMutableTreeNode var0) { return false; }
	public boolean isNodeRelated(javax.swing.tree.DefaultMutableTreeNode var0) { return false; }
	public boolean isNodeSibling(javax.swing.tree.TreeNode var0) { return false; }
	public boolean isRoot() { return false; }
	public java.util.Enumeration pathFromAncestorEnumeration(javax.swing.tree.TreeNode var0) { return null; }
	public java.util.Enumeration postorderEnumeration() { return null; }
	public java.util.Enumeration preorderEnumeration() { return null; }
	public void remove(int var0) { }
	public void remove(javax.swing.tree.MutableTreeNode var0) { }
	public void removeAllChildren() { }
	public void removeFromParent() { }
	public void setAllowsChildren(boolean var0) { }
	public void setParent(javax.swing.tree.MutableTreeNode var0) { }
	public void setUserObject(java.lang.Object var0) { }
	public final static java.util.Enumeration EMPTY_ENUMERATION; static { EMPTY_ENUMERATION = null; }
	protected boolean allowsChildren;
	protected java.util.Vector children;
	protected javax.swing.tree.MutableTreeNode parent;
	protected java.lang.Object userObject;
}

