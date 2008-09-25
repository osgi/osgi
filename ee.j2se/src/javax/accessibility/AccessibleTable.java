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

package javax.accessibility;
public abstract interface AccessibleTable {
	public abstract javax.accessibility.Accessible getAccessibleAt(int var0, int var1);
	public abstract javax.accessibility.Accessible getAccessibleCaption();
	public abstract int getAccessibleColumnCount();
	public abstract javax.accessibility.Accessible getAccessibleColumnDescription(int var0);
	public abstract int getAccessibleColumnExtentAt(int var0, int var1);
	public abstract javax.accessibility.AccessibleTable getAccessibleColumnHeader();
	public abstract int getAccessibleRowCount();
	public abstract javax.accessibility.Accessible getAccessibleRowDescription(int var0);
	public abstract int getAccessibleRowExtentAt(int var0, int var1);
	public abstract javax.accessibility.AccessibleTable getAccessibleRowHeader();
	public abstract javax.accessibility.Accessible getAccessibleSummary();
	public abstract int[] getSelectedAccessibleColumns();
	public abstract int[] getSelectedAccessibleRows();
	public abstract boolean isAccessibleColumnSelected(int var0);
	public abstract boolean isAccessibleRowSelected(int var0);
	public abstract boolean isAccessibleSelected(int var0, int var1);
	public abstract void setAccessibleCaption(javax.accessibility.Accessible var0);
	public abstract void setAccessibleColumnDescription(int var0, javax.accessibility.Accessible var1);
	public abstract void setAccessibleColumnHeader(javax.accessibility.AccessibleTable var0);
	public abstract void setAccessibleRowDescription(int var0, javax.accessibility.Accessible var1);
	public abstract void setAccessibleRowHeader(javax.accessibility.AccessibleTable var0);
	public abstract void setAccessibleSummary(javax.accessibility.Accessible var0);
}

