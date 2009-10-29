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

package javax.swing.table;
public class TableColumn implements java.io.Serializable {
	public TableColumn() { }
	public TableColumn(int var0) { }
	public TableColumn(int var0, int var1) { }
	public TableColumn(int var0, int var1, javax.swing.table.TableCellRenderer var2, javax.swing.table.TableCellEditor var3) { }
	public void addPropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	protected javax.swing.table.TableCellRenderer createDefaultHeaderRenderer() { return null; }
	/** @deprecated */ public void disableResizedPosting() { }
	/** @deprecated */ public void enableResizedPosting() { }
	public javax.swing.table.TableCellEditor getCellEditor() { return null; }
	public javax.swing.table.TableCellRenderer getCellRenderer() { return null; }
	public javax.swing.table.TableCellRenderer getHeaderRenderer() { return null; }
	public java.lang.Object getHeaderValue() { return null; }
	public java.lang.Object getIdentifier() { return null; }
	public int getMaxWidth() { return 0; }
	public int getMinWidth() { return 0; }
	public int getModelIndex() { return 0; }
	public int getPreferredWidth() { return 0; }
	public java.beans.PropertyChangeListener[] getPropertyChangeListeners() { return null; }
	public boolean getResizable() { return false; }
	public int getWidth() { return 0; }
	public void removePropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public void setCellEditor(javax.swing.table.TableCellEditor var0) { }
	public void setCellRenderer(javax.swing.table.TableCellRenderer var0) { }
	public void setHeaderRenderer(javax.swing.table.TableCellRenderer var0) { }
	public void setHeaderValue(java.lang.Object var0) { }
	public void setIdentifier(java.lang.Object var0) { }
	public void setMaxWidth(int var0) { }
	public void setMinWidth(int var0) { }
	public void setModelIndex(int var0) { }
	public void setPreferredWidth(int var0) { }
	public void setResizable(boolean var0) { }
	public void setWidth(int var0) { }
	public void sizeWidthToFit() { }
	public final static java.lang.String CELL_RENDERER_PROPERTY = "cellRenderer";
	public final static java.lang.String COLUMN_WIDTH_PROPERTY = "columWidth";
	public final static java.lang.String HEADER_RENDERER_PROPERTY = "headerRenderer";
	public final static java.lang.String HEADER_VALUE_PROPERTY = "headerValue";
	protected javax.swing.table.TableCellEditor cellEditor;
	protected javax.swing.table.TableCellRenderer cellRenderer;
	protected javax.swing.table.TableCellRenderer headerRenderer;
	protected java.lang.Object headerValue;
	protected java.lang.Object identifier;
	protected boolean isResizable;
	protected int maxWidth;
	protected int minWidth;
	protected int modelIndex;
	/** @deprecated */ protected int resizedPostingDisableCount;
	protected int width;
}

