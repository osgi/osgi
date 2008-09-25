/*
 * $Revision$
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

package javax.swing.event;
public class TableModelEvent extends java.util.EventObject {
	public TableModelEvent(javax.swing.table.TableModel var0) { super((java.lang.Object) null); }
	public TableModelEvent(javax.swing.table.TableModel var0, int var1) { super((java.lang.Object) null); }
	public TableModelEvent(javax.swing.table.TableModel var0, int var1, int var2) { super((java.lang.Object) null); }
	public TableModelEvent(javax.swing.table.TableModel var0, int var1, int var2, int var3) { super((java.lang.Object) null); }
	public TableModelEvent(javax.swing.table.TableModel var0, int var1, int var2, int var3, int var4) { super((java.lang.Object) null); }
	public int getColumn() { return 0; }
	public int getFirstRow() { return 0; }
	public int getLastRow() { return 0; }
	public int getType() { return 0; }
	public final static int ALL_COLUMNS = -1;
	public final static int DELETE = -1;
	public final static int HEADER_ROW = -1;
	public final static int INSERT = 1;
	public final static int UPDATE = 0;
	protected int column;
	protected int firstRow;
	protected int lastRow;
	protected int type;
}

