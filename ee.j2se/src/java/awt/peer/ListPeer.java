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

package java.awt.peer;
public interface ListPeer extends java.awt.peer.ComponentPeer {
	void add(java.lang.String var0, int var1);
	void addItem(java.lang.String var0, int var1);
	void clear();
	void delItems(int var0, int var1);
	void deselect(int var0);
	java.awt.Dimension getMinimumSize(int var0);
	java.awt.Dimension getPreferredSize(int var0);
	int[] getSelectedIndexes();
	void makeVisible(int var0);
	java.awt.Dimension minimumSize(int var0);
	java.awt.Dimension preferredSize(int var0);
	void removeAll();
	void select(int var0);
	void setMultipleMode(boolean var0);
	void setMultipleSelections(boolean var0);
}

