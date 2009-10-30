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

package javax.swing;
public interface ListSelectionModel {
	public final static int MULTIPLE_INTERVAL_SELECTION = 2;
	public final static int SINGLE_INTERVAL_SELECTION = 1;
	public final static int SINGLE_SELECTION = 0;
	void addListSelectionListener(javax.swing.event.ListSelectionListener var0);
	void addSelectionInterval(int var0, int var1);
	void clearSelection();
	int getAnchorSelectionIndex();
	int getLeadSelectionIndex();
	int getMaxSelectionIndex();
	int getMinSelectionIndex();
	int getSelectionMode();
	boolean getValueIsAdjusting();
	void insertIndexInterval(int var0, int var1, boolean var2);
	boolean isSelectedIndex(int var0);
	boolean isSelectionEmpty();
	void removeIndexInterval(int var0, int var1);
	void removeListSelectionListener(javax.swing.event.ListSelectionListener var0);
	void removeSelectionInterval(int var0, int var1);
	void setAnchorSelectionIndex(int var0);
	void setLeadSelectionIndex(int var0);
	void setSelectionInterval(int var0, int var1);
	void setSelectionMode(int var0);
	void setValueIsAdjusting(boolean var0);
}

