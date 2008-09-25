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

package java.sql;
public abstract interface Statement {
	public abstract void addBatch(java.lang.String var0) throws java.sql.SQLException;
	public abstract void cancel() throws java.sql.SQLException;
	public abstract void clearBatch() throws java.sql.SQLException;
	public abstract void clearWarnings() throws java.sql.SQLException;
	public abstract void close() throws java.sql.SQLException;
	public abstract boolean execute(java.lang.String var0) throws java.sql.SQLException;
	public abstract boolean execute(java.lang.String var0, int var1) throws java.sql.SQLException;
	public abstract boolean execute(java.lang.String var0, int[] var1) throws java.sql.SQLException;
	public abstract boolean execute(java.lang.String var0, java.lang.String[] var1) throws java.sql.SQLException;
	public abstract int[] executeBatch() throws java.sql.SQLException;
	public abstract java.sql.ResultSet executeQuery(java.lang.String var0) throws java.sql.SQLException;
	public abstract int executeUpdate(java.lang.String var0) throws java.sql.SQLException;
	public abstract int executeUpdate(java.lang.String var0, int var1) throws java.sql.SQLException;
	public abstract int executeUpdate(java.lang.String var0, int[] var1) throws java.sql.SQLException;
	public abstract int executeUpdate(java.lang.String var0, java.lang.String[] var1) throws java.sql.SQLException;
	public abstract java.sql.Connection getConnection() throws java.sql.SQLException;
	public abstract int getFetchDirection() throws java.sql.SQLException;
	public abstract int getFetchSize() throws java.sql.SQLException;
	public abstract java.sql.ResultSet getGeneratedKeys() throws java.sql.SQLException;
	public abstract int getMaxFieldSize() throws java.sql.SQLException;
	public abstract int getMaxRows() throws java.sql.SQLException;
	public abstract boolean getMoreResults() throws java.sql.SQLException;
	public abstract boolean getMoreResults(int var0) throws java.sql.SQLException;
	public abstract int getQueryTimeout() throws java.sql.SQLException;
	public abstract java.sql.ResultSet getResultSet() throws java.sql.SQLException;
	public abstract int getResultSetConcurrency() throws java.sql.SQLException;
	public abstract int getResultSetHoldability() throws java.sql.SQLException;
	public abstract int getResultSetType() throws java.sql.SQLException;
	public abstract int getUpdateCount() throws java.sql.SQLException;
	public abstract java.sql.SQLWarning getWarnings() throws java.sql.SQLException;
	public abstract void setCursorName(java.lang.String var0) throws java.sql.SQLException;
	public abstract void setEscapeProcessing(boolean var0) throws java.sql.SQLException;
	public abstract void setFetchDirection(int var0) throws java.sql.SQLException;
	public abstract void setFetchSize(int var0) throws java.sql.SQLException;
	public abstract void setMaxFieldSize(int var0) throws java.sql.SQLException;
	public abstract void setMaxRows(int var0) throws java.sql.SQLException;
	public abstract void setQueryTimeout(int var0) throws java.sql.SQLException;
	public final static int CLOSE_ALL_RESULTS = 3;
	public final static int CLOSE_CURRENT_RESULT = 1;
	public final static int EXECUTE_FAILED = -3;
	public final static int KEEP_CURRENT_RESULT = 2;
	public final static int NO_GENERATED_KEYS = 2;
	public final static int RETURN_GENERATED_KEYS = 1;
	public final static int SUCCESS_NO_INFO = -2;
}

