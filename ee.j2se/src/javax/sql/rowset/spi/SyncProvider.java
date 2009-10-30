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
public abstract class SyncProvider {
	public static int DATASOURCE_DB_LOCK;
	public static int DATASOURCE_NO_LOCK;
	public static int DATASOURCE_ROW_LOCK;
	public static int DATASOURCE_TABLE_LOCK;
	public static int GRADE_CHECK_ALL_AT_COMMIT;
	public static int GRADE_CHECK_MODIFIED_AT_COMMIT;
	public static int GRADE_LOCK_WHEN_LOADED;
	public static int GRADE_LOCK_WHEN_MODIFIED;
	public static int GRADE_NONE;
	public static int NONUPDATABLE_VIEW_SYNC;
	public static int UPDATABLE_VIEW_SYNC;
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

