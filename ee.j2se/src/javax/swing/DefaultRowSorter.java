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
public abstract class DefaultRowSorter<M,I> extends javax.swing.RowSorter<M> {
	protected static abstract class ModelWrapper<M,I> {
		protected ModelWrapper() { } 
		public abstract int getColumnCount();
		public abstract I getIdentifier(int var0);
		public abstract M getModel();
		public abstract int getRowCount();
		public java.lang.String getStringValueAt(int var0, int var1) { return null; }
		public abstract java.lang.Object getValueAt(int var0, int var1);
	}
	public DefaultRowSorter() { } 
	public void allRowsChanged() { }
	public int convertRowIndexToModel(int var0) { return 0; }
	public int convertRowIndexToView(int var0) { return 0; }
	public java.util.Comparator<?> getComparator(int var0) { return null; }
	public int getMaxSortKeys() { return 0; }
	public final M getModel() { return null; }
	public int getModelRowCount() { return 0; }
	protected final javax.swing.DefaultRowSorter.ModelWrapper<M,I> getModelWrapper() { return null; }
	public javax.swing.RowFilter<? super M,? super I> getRowFilter() { return null; }
	public java.util.List<? extends javax.swing.RowSorter.SortKey> getSortKeys() { return null; }
	public boolean getSortsOnUpdates() { return false; }
	public int getViewRowCount() { return 0; }
	public boolean isSortable(int var0) { return false; }
	public void modelStructureChanged() { }
	public void rowsDeleted(int var0, int var1) { }
	public void rowsInserted(int var0, int var1) { }
	public void rowsUpdated(int var0, int var1) { }
	public void rowsUpdated(int var0, int var1, int var2) { }
	public void setComparator(int var0, java.util.Comparator<?> var1) { }
	public void setMaxSortKeys(int var0) { }
	protected final void setModelWrapper(javax.swing.DefaultRowSorter.ModelWrapper<M,I> var0) { }
	public void setRowFilter(javax.swing.RowFilter<? super M,? super I> var0) { }
	public void setSortKeys(java.util.List<? extends javax.swing.RowSorter.SortKey> var0) { }
	public void setSortable(int var0, boolean var1) { }
	public void setSortsOnUpdates(boolean var0) { }
	public void sort() { }
	public void toggleSortOrder(int var0) { }
	protected boolean useToString(int var0) { return false; }
}

