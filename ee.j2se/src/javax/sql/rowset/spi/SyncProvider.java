/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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
public abstract class SyncProvider {
	public final static int DATASOURCE_DB_LOCK = 4;
	public final static int DATASOURCE_NO_LOCK = 1;
	public final static int DATASOURCE_ROW_LOCK = 2;
	public final static int DATASOURCE_TABLE_LOCK = 3;
	public final static int GRADE_CHECK_ALL_AT_COMMIT = 3;
	public final static int GRADE_CHECK_MODIFIED_AT_COMMIT = 2;
	public final static int GRADE_LOCK_WHEN_LOADED = 5;
	public final static int GRADE_LOCK_WHEN_MODIFIED = 4;
	public final static int GRADE_NONE = 1;
	public final static int NONUPDATABLE_VIEW_SYNC = 6;
	public final static int UPDATABLE_VIEW_SYNC = 5;
	public SyncProvider() { } 
	public abstract int getDataSourceLock() throws javax.sql.rowset.spi.SyncProviderException;
	public abstract int getProviderGrade();
	public abstract java.lang.String getProviderID();
	public abstract javax.sql.RowSetReader getRowSetReader();
	public abstract javax.sql.RowSetWriter getRowSetWriter();
	public abstract java.lang.String getVendor();
	public abstract java.lang.String getVersion();
	public abstract void setDataSourceLock(int var0) throws javax.sql.rowset.spi.SyncProviderException;
	public abstract int supportsUpdatableView();
}

