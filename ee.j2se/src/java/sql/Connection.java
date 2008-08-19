/*
 * $Date$
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
public abstract interface Connection {
	public abstract void clearWarnings() throws java.sql.SQLException;
	public abstract void close() throws java.sql.SQLException;
	public abstract void commit() throws java.sql.SQLException;
	public abstract java.sql.Statement createStatement() throws java.sql.SQLException;
	public abstract java.sql.Statement createStatement(int var0, int var1) throws java.sql.SQLException;
	public abstract java.sql.Statement createStatement(int var0, int var1, int var2) throws java.sql.SQLException;
	public abstract boolean getAutoCommit() throws java.sql.SQLException;
	public abstract java.lang.String getCatalog() throws java.sql.SQLException;
	public abstract int getHoldability() throws java.sql.SQLException;
	public abstract java.sql.DatabaseMetaData getMetaData() throws java.sql.SQLException;
	public abstract int getTransactionIsolation() throws java.sql.SQLException;
	public abstract java.util.Map getTypeMap() throws java.sql.SQLException;
	public abstract java.sql.SQLWarning getWarnings() throws java.sql.SQLException;
	public abstract boolean isClosed() throws java.sql.SQLException;
	public abstract boolean isReadOnly() throws java.sql.SQLException;
	public abstract java.lang.String nativeSQL(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.sql.CallableStatement prepareCall(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.sql.CallableStatement prepareCall(java.lang.String var0, int var1, int var2) throws java.sql.SQLException;
	public abstract java.sql.CallableStatement prepareCall(java.lang.String var0, int var1, int var2, int var3) throws java.sql.SQLException;
	public abstract java.sql.PreparedStatement prepareStatement(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.sql.PreparedStatement prepareStatement(java.lang.String var0, int var1) throws java.sql.SQLException;
	public abstract java.sql.PreparedStatement prepareStatement(java.lang.String var0, int var1, int var2) throws java.sql.SQLException;
	public abstract java.sql.PreparedStatement prepareStatement(java.lang.String var0, int var1, int var2, int var3) throws java.sql.SQLException;
	public abstract java.sql.PreparedStatement prepareStatement(java.lang.String var0, int[] var1) throws java.sql.SQLException;
	public abstract java.sql.PreparedStatement prepareStatement(java.lang.String var0, java.lang.String[] var1) throws java.sql.SQLException;
	public abstract void releaseSavepoint(java.sql.Savepoint var0) throws java.sql.SQLException;
	public abstract void rollback() throws java.sql.SQLException;
	public abstract void rollback(java.sql.Savepoint var0) throws java.sql.SQLException;
	public abstract void setAutoCommit(boolean var0) throws java.sql.SQLException;
	public abstract void setCatalog(java.lang.String var0) throws java.sql.SQLException;
	public abstract void setHoldability(int var0) throws java.sql.SQLException;
	public abstract void setReadOnly(boolean var0) throws java.sql.SQLException;
	public abstract java.sql.Savepoint setSavepoint() throws java.sql.SQLException;
	public abstract java.sql.Savepoint setSavepoint(java.lang.String var0) throws java.sql.SQLException;
	public abstract void setTransactionIsolation(int var0) throws java.sql.SQLException;
	public abstract void setTypeMap(java.util.Map var0) throws java.sql.SQLException;
	public final static int TRANSACTION_NONE = 0;
	public final static int TRANSACTION_READ_COMMITTED = 2;
	public final static int TRANSACTION_READ_UNCOMMITTED = 1;
	public final static int TRANSACTION_REPEATABLE_READ = 4;
	public final static int TRANSACTION_SERIALIZABLE = 8;
}

