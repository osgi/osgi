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
public abstract interface ListSelectionModel {
	public abstract void addListSelectionListener(javax.swing.event.ListSelectionListener var0);
	public abstract void addSelectionInterval(int var0, int var1);
	public abstract void clearSelection();
	public abstract int getAnchorSelectionIndex();
	public abstract int getLeadSelectionIndex();
	public abstract int getMaxSelectionIndex();
	public abstract int getMinSelectionIndex();
	public abstract int getSelectionMode();
	public abstract boolean getValueIsAdjusting();
	public abstract void insertIndexInterval(int var0, int var1, boolean var2);
	public abstract boolean isSelectedIndex(int var0);
	public abstract boolean isSelectionEmpty();
	public abstract void removeIndexInterval(int var0, int var1);
	public abstract void removeListSelectionListener(javax.swing.event.ListSelectionListener var0);
	public abstract void removeSelectionInterval(int var0, int var1);
	public abstract void setAnchorSelectionIndex(int var0);
	public abstract void setLeadSelectionIndex(int var0);
	public abstract void setSelectionInterval(int var0, int var1);
	public abstract void setSelectionMode(int var0);
	public abstract void setValueIsAdjusting(boolean var0);
	public final static int MULTIPLE_INTERVAL_SELECTION = 2;
	public final static int SINGLE_INTERVAL_SELECTION = 1;
	public final static int SINGLE_SELECTION = 0;
}

