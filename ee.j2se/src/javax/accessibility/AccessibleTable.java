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

package javax.accessibility;
public interface AccessibleTable {
	javax.accessibility.Accessible getAccessibleAt(int var0, int var1);
	javax.accessibility.Accessible getAccessibleCaption();
	int getAccessibleColumnCount();
	javax.accessibility.Accessible getAccessibleColumnDescription(int var0);
	int getAccessibleColumnExtentAt(int var0, int var1);
	javax.accessibility.AccessibleTable getAccessibleColumnHeader();
	int getAccessibleRowCount();
	javax.accessibility.Accessible getAccessibleRowDescription(int var0);
	int getAccessibleRowExtentAt(int var0, int var1);
	javax.accessibility.AccessibleTable getAccessibleRowHeader();
	javax.accessibility.Accessible getAccessibleSummary();
	int[] getSelectedAccessibleColumns();
	int[] getSelectedAccessibleRows();
	boolean isAccessibleColumnSelected(int var0);
	boolean isAccessibleRowSelected(int var0);
	boolean isAccessibleSelected(int var0, int var1);
	void setAccessibleCaption(javax.accessibility.Accessible var0);
	void setAccessibleColumnDescription(int var0, javax.accessibility.Accessible var1);
	void setAccessibleColumnHeader(javax.accessibility.AccessibleTable var0);
	void setAccessibleRowDescription(int var0, javax.accessibility.Accessible var1);
	void setAccessibleRowHeader(javax.accessibility.AccessibleTable var0);
	void setAccessibleSummary(javax.accessibility.Accessible var0);
}

