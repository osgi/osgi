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

package javax.swing.text;
public abstract class TableView extends javax.swing.text.BoxView {
	interface GridCell {
		int getColumnCount();
		int getGridColumn();
		int getGridRow();
		int getRowCount();
		void setGridLocation(int var0, int var1);
	}
	/** @deprecated */
	@java.lang.Deprecated
	public class TableCell extends javax.swing.text.BoxView implements javax.swing.text.TableView.GridCell {
		public TableCell(javax.swing.text.Element var0)  { super((javax.swing.text.Element) null, 0); } 
		public int getColumnCount() { return 0; }
		public int getGridColumn() { return 0; }
		public int getGridRow() { return 0; }
		public int getRowCount() { return 0; }
		public void setGridLocation(int var0, int var1) { }
	}
	public class TableRow extends javax.swing.text.BoxView {
		public TableRow(javax.swing.text.Element var0)  { super((javax.swing.text.Element) null, 0); } 
	}
	public TableView(javax.swing.text.Element var0)  { super((javax.swing.text.Element) null, 0); } 
	/** @deprecated */
	@java.lang.Deprecated
	protected javax.swing.text.TableView.TableCell createTableCell(javax.swing.text.Element var0) { return null; }
	protected javax.swing.text.TableView.TableRow createTableRow(javax.swing.text.Element var0) { return null; }
	protected void layoutColumns(int var0, int[] var1, int[] var2, javax.swing.SizeRequirements[] var3) { }
}

