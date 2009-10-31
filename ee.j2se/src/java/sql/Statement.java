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

package java.sql;
public interface Statement extends java.sql.Wrapper {
	public final static int CLOSE_ALL_RESULTS = 3;
	public final static int CLOSE_CURRENT_RESULT = 1;
	public final static int EXECUTE_FAILED = -3;
	public final static int KEEP_CURRENT_RESULT = 2;
	public final static int NO_GENERATED_KEYS = 2;
	public final static int RETURN_GENERATED_KEYS = 1;
	public final static int SUCCESS_NO_INFO = -2;
	void addBatch(java.lang.String var0) throws java.sql.SQLException;
	void cancel() throws java.sql.SQLException;
	void clearBatch() throws java.sql.SQLException;
	void clearWarnings() throws java.sql.SQLException;
	void close() throws java.sql.SQLException;
	boolean execute(java.lang.String var0) throws java.sql.SQLException;
	boolean execute(java.lang.String var0, int var1) throws java.sql.SQLException;
	boolean execute(java.lang.String var0, int[] var1) throws java.sql.SQLException;
	boolean execute(java.lang.String var0, java.lang.String[] var1) throws java.sql.SQLException;
	int[] executeBatch() throws java.sql.SQLException;
	java.sql.ResultSet executeQuery(java.lang.String var0) throws java.sql.SQLException;
	int executeUpdate(java.lang.String var0) throws java.sql.SQLException;
	int executeUpdate(java.lang.String var0, int var1) throws java.sql.SQLException;
	int executeUpdate(java.lang.String var0, int[] var1) throws java.sql.SQLException;
	int executeUpdate(java.lang.String var0, java.lang.String[] var1) throws java.sql.SQLException;
	java.sql.Connection getConnection() throws java.sql.SQLException;
	int getFetchDirection() throws java.sql.SQLException;
	int getFetchSize() throws java.sql.SQLException;
	java.sql.ResultSet getGeneratedKeys() throws java.sql.SQLException;
	int getMaxFieldSize() throws java.sql.SQLException;
	int getMaxRows() throws java.sql.SQLException;
	boolean getMoreResults() throws java.sql.SQLException;
	boolean getMoreResults(int var0) throws java.sql.SQLException;
	int getQueryTimeout() throws java.sql.SQLException;
	java.sql.ResultSet getResultSet() throws java.sql.SQLException;
	int getResultSetConcurrency() throws java.sql.SQLException;
	int getResultSetHoldability() throws java.sql.SQLException;
	int getResultSetType() throws java.sql.SQLException;
	int getUpdateCount() throws java.sql.SQLException;
	java.sql.SQLWarning getWarnings() throws java.sql.SQLException;
	boolean isClosed() throws java.sql.SQLException;
	boolean isPoolable() throws java.sql.SQLException;
	void setCursorName(java.lang.String var0) throws java.sql.SQLException;
	void setEscapeProcessing(boolean var0) throws java.sql.SQLException;
	void setFetchDirection(int var0) throws java.sql.SQLException;
	void setFetchSize(int var0) throws java.sql.SQLException;
	void setMaxFieldSize(int var0) throws java.sql.SQLException;
	void setMaxRows(int var0) throws java.sql.SQLException;
	void setPoolable(boolean var0) throws java.sql.SQLException;
	void setQueryTimeout(int var0) throws java.sql.SQLException;
}

