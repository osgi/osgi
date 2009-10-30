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

package javax.sql.rowset.spi;
public interface SyncResolver extends javax.sql.RowSet {
	public final static int DELETE_ROW_CONFLICT = 1;
	public final static int INSERT_ROW_CONFLICT = 2;
	public final static int NO_ROW_CONFLICT = 3;
	public final static int UPDATE_ROW_CONFLICT = 0;
	java.lang.Object getConflictValue(int var0) throws java.sql.SQLException;
	java.lang.Object getConflictValue(java.lang.String var0) throws java.sql.SQLException;
	int getStatus();
	boolean nextConflict() throws java.sql.SQLException;
	boolean previousConflict() throws java.sql.SQLException;
	void setResolvedValue(int var0, java.lang.Object var1) throws java.sql.SQLException;
	void setResolvedValue(java.lang.String var0, java.lang.Object var1) throws java.sql.SQLException;
}

