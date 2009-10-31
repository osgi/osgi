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
public abstract class RowFilter<M,I> {
	public enum ComparisonType {
		AFTER,
		BEFORE,
		EQUAL,
		NOT_EQUAL;
	}
	public static abstract class Entry<M,I> {
		public Entry() { } 
		public abstract I getIdentifier();
		public abstract M getModel();
		public java.lang.String getStringValue(int var0) { return null; }
		public abstract java.lang.Object getValue(int var0);
		public abstract int getValueCount();
	}
	public RowFilter() { } 
	public static <M,I> javax.swing.RowFilter<M,I> andFilter(java.lang.Iterable<? extends javax.swing.RowFilter<? super M,? super I>> var0) { return null; }
	public static <M,I> javax.swing.RowFilter<M,I> dateFilter(javax.swing.RowFilter.ComparisonType var0, java.util.Date var1, int... var2) { return null; }
	public abstract boolean include(javax.swing.RowFilter.Entry<? extends M,? extends I> var0);
	public static <M,I> javax.swing.RowFilter<M,I> notFilter(javax.swing.RowFilter<M,I> var0) { return null; }
	public static <M,I> javax.swing.RowFilter<M,I> numberFilter(javax.swing.RowFilter.ComparisonType var0, java.lang.Number var1, int... var2) { return null; }
	public static <M,I> javax.swing.RowFilter<M,I> orFilter(java.lang.Iterable<? extends javax.swing.RowFilter<? super M,? super I>> var0) { return null; }
	public static <M,I> javax.swing.RowFilter<M,I> regexFilter(java.lang.String var0, int... var1) { return null; }
}

