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
public abstract class RowSorter<M> {
	public static class SortKey {
		public SortKey(int var0, javax.swing.SortOrder var1) { } 
		public final int getColumn() { return 0; }
		public final javax.swing.SortOrder getSortOrder() { return null; }
	}
	public RowSorter() { } 
	public void addRowSorterListener(javax.swing.event.RowSorterListener var0) { }
	public abstract void allRowsChanged();
	public abstract int convertRowIndexToModel(int var0);
	public abstract int convertRowIndexToView(int var0);
	protected void fireRowSorterChanged(int[] var0) { }
	protected void fireSortOrderChanged() { }
	public abstract M getModel();
	public abstract int getModelRowCount();
	public abstract java.util.List<? extends javax.swing.RowSorter.SortKey> getSortKeys();
	public abstract int getViewRowCount();
	public abstract void modelStructureChanged();
	public void removeRowSorterListener(javax.swing.event.RowSorterListener var0) { }
	public abstract void rowsDeleted(int var0, int var1);
	public abstract void rowsInserted(int var0, int var1);
	public abstract void rowsUpdated(int var0, int var1);
	public abstract void rowsUpdated(int var0, int var1, int var2);
	public abstract void setSortKeys(java.util.List<? extends javax.swing.RowSorter.SortKey> var0);
	public abstract void toggleSortOrder(int var0);
}

