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

package javax.swing.event;
public class RowSorterEvent extends java.util.EventObject {
	public enum Type {
		SORTED,
		SORT_ORDER_CHANGED;
	}
	public RowSorterEvent(javax.swing.RowSorter var0)  { super((java.lang.Object) null); } 
	public RowSorterEvent(javax.swing.RowSorter var0, javax.swing.event.RowSorterEvent.Type var1, int[] var2)  { super((java.lang.Object) null); } 
	public int convertPreviousRowIndexToModel(int var0) { return 0; }
	public int getPreviousRowCount() { return 0; }
	public javax.swing.RowSorter getSource() { return null; }
	public javax.swing.event.RowSorterEvent.Type getType() { return null; }
}

