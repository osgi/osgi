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

package javax.sql.rowset;
public interface CachedRowSet extends javax.sql.RowSet, javax.sql.rowset.Joinable {
	public final static boolean COMMIT_ON_ACCEPT_CHANGES = true;
	void acceptChanges() throws javax.sql.rowset.spi.SyncProviderException;
	void acceptChanges(java.sql.Connection var0) throws javax.sql.rowset.spi.SyncProviderException;
	boolean columnUpdated(int var0) throws java.sql.SQLException;
	boolean columnUpdated(java.lang.String var0) throws java.sql.SQLException;
	void commit() throws java.sql.SQLException;
	javax.sql.rowset.CachedRowSet createCopy() throws java.sql.SQLException;
	javax.sql.rowset.CachedRowSet createCopyNoConstraints() throws java.sql.SQLException;
	javax.sql.rowset.CachedRowSet createCopySchema() throws java.sql.SQLException;
	javax.sql.RowSet createShared() throws java.sql.SQLException;
	void execute(java.sql.Connection var0) throws java.sql.SQLException;
	int[] getKeyColumns() throws java.sql.SQLException;
	java.sql.ResultSet getOriginal() throws java.sql.SQLException;
	java.sql.ResultSet getOriginalRow() throws java.sql.SQLException;
	int getPageSize();
	javax.sql.rowset.RowSetWarning getRowSetWarnings() throws java.sql.SQLException;
	boolean getShowDeleted() throws java.sql.SQLException;
	javax.sql.rowset.spi.SyncProvider getSyncProvider() throws java.sql.SQLException;
	java.lang.String getTableName() throws java.sql.SQLException;
	boolean nextPage() throws java.sql.SQLException;
	void populate(java.sql.ResultSet var0) throws java.sql.SQLException;
	void populate(java.sql.ResultSet var0, int var1) throws java.sql.SQLException;
	boolean previousPage() throws java.sql.SQLException;
	void release() throws java.sql.SQLException;
	void restoreOriginal() throws java.sql.SQLException;
	void rollback() throws java.sql.SQLException;
	void rollback(java.sql.Savepoint var0) throws java.sql.SQLException;
	void rowSetPopulated(javax.sql.RowSetEvent var0, int var1) throws java.sql.SQLException;
	void setKeyColumns(int[] var0) throws java.sql.SQLException;
	void setMetaData(javax.sql.RowSetMetaData var0) throws java.sql.SQLException;
	void setOriginalRow() throws java.sql.SQLException;
	void setPageSize(int var0) throws java.sql.SQLException;
	void setShowDeleted(boolean var0) throws java.sql.SQLException;
	void setSyncProvider(java.lang.String var0) throws java.sql.SQLException;
	void setTableName(java.lang.String var0) throws java.sql.SQLException;
	int size();
	java.util.Collection<?> toCollection() throws java.sql.SQLException;
	java.util.Collection<?> toCollection(int var0) throws java.sql.SQLException;
	java.util.Collection<?> toCollection(java.lang.String var0) throws java.sql.SQLException;
	void undoDelete() throws java.sql.SQLException;
	void undoInsert() throws java.sql.SQLException;
	void undoUpdate() throws java.sql.SQLException;
}

