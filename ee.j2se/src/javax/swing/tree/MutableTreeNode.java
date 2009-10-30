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

package javax.swing.tree;
public interface MutableTreeNode extends javax.swing.tree.TreeNode {
	void insert(javax.swing.tree.MutableTreeNode var0, int var1);
	void remove(int var0);
	void remove(javax.swing.tree.MutableTreeNode var0);
	void removeFromParent();
	void setParent(javax.swing.tree.MutableTreeNode var0);
	void setUserObject(java.lang.Object var0);
}

