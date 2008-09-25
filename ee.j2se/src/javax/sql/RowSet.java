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

package javax.sql;
public abstract interface RowSet extends java.sql.ResultSet {
	public abstract void addRowSetListener(javax.sql.RowSetListener var0);
	public abstract void clearParameters() throws java.sql.SQLException;
	public abstract void execute() throws java.sql.SQLException;
	public abstract java.lang.String getCommand();
	public abstract java.lang.String getDataSourceName();
	public abstract boolean getEscapeProcessing() throws java.sql.SQLException;
	public abstract int getMaxFieldSize() throws java.sql.SQLException;
	public abstract int getMaxRows() throws java.sql.SQLException;
	public abstract java.lang.String getPassword();
	public abstract int getQueryTimeout() throws java.sql.SQLException;
	public abstract int getTransactionIsolation();
	public abstract java.util.Map getTypeMap() throws java.sql.SQLException;
	public abstract java.lang.String getUrl() throws java.sql.SQLException;
	public abstract java.lang.String getUsername();
	public abstract boolean isReadOnly();
	public abstract void removeRowSetListener(javax.sql.RowSetListener var0);
	public abstract void setArray(int var0, java.sql.Array var1) throws java.sql.SQLException;
	public abstract void setAsciiStream(int var0, java.io.InputStream var1, int var2) throws java.sql.SQLException;
	public abstract void setBigDecimal(int var0, java.math.BigDecimal var1) throws java.sql.SQLException;
	public abstract void setBinaryStream(int var0, java.io.InputStream var1, int var2) throws java.sql.SQLException;
	public abstract void setBlob(int var0, java.sql.Blob var1) throws java.sql.SQLException;
	public abstract void setBoolean(int var0, boolean var1) throws java.sql.SQLException;
	public abstract void setByte(int var0, byte var1) throws java.sql.SQLException;
	public abstract void setBytes(int var0, byte[] var1) throws java.sql.SQLException;
	public abstract void setCharacterStream(int var0, java.io.Reader var1, int var2) throws java.sql.SQLException;
	public abstract void setClob(int var0, java.sql.Clob var1) throws java.sql.SQLException;
	public abstract void setCommand(java.lang.String var0) throws java.sql.SQLException;
	public abstract void setConcurrency(int var0) throws java.sql.SQLException;
	public abstract void setDataSourceName(java.lang.String var0) throws java.sql.SQLException;
	public abstract void setDate(int var0, java.sql.Date var1) throws java.sql.SQLException;
	public abstract void setDate(int var0, java.sql.Date var1, java.util.Calendar var2) throws java.sql.SQLException;
	public abstract void setDouble(int var0, double var1) throws java.sql.SQLException;
	public abstract void setEscapeProcessing(boolean var0) throws java.sql.SQLException;
	public abstract void setFloat(int var0, float var1) throws java.sql.SQLException;
	public abstract void setInt(int var0, int var1) throws java.sql.SQLException;
	public abstract void setLong(int var0, long var1) throws java.sql.SQLException;
	public abstract void setMaxFieldSize(int var0) throws java.sql.SQLException;
	public abstract void setMaxRows(int var0) throws java.sql.SQLException;
	public abstract void setNull(int var0, int var1) throws java.sql.SQLException;
	public abstract void setNull(int var0, int var1, java.lang.String var2) throws java.sql.SQLException;
	public abstract void setObject(int var0, java.lang.Object var1) throws java.sql.SQLException;
	public abstract void setObject(int var0, java.lang.Object var1, int var2) throws java.sql.SQLException;
	public abstract void setObject(int var0, java.lang.Object var1, int var2, int var3) throws java.sql.SQLException;
	public abstract void setPassword(java.lang.String var0) throws java.sql.SQLException;
	public abstract void setQueryTimeout(int var0) throws java.sql.SQLException;
	public abstract void setReadOnly(boolean var0) throws java.sql.SQLException;
	public abstract void setRef(int var0, java.sql.Ref var1) throws java.sql.SQLException;
	public abstract void setShort(int var0, short var1) throws java.sql.SQLException;
	public abstract void setString(int var0, java.lang.String var1) throws java.sql.SQLException;
	public abstract void setTime(int var0, java.sql.Time var1) throws java.sql.SQLException;
	public abstract void setTime(int var0, java.sql.Time var1, java.util.Calendar var2) throws java.sql.SQLException;
	public abstract void setTimestamp(int var0, java.sql.Timestamp var1) throws java.sql.SQLException;
	public abstract void setTimestamp(int var0, java.sql.Timestamp var1, java.util.Calendar var2) throws java.sql.SQLException;
	public abstract void setTransactionIsolation(int var0) throws java.sql.SQLException;
	public abstract void setType(int var0) throws java.sql.SQLException;
	public abstract void setTypeMap(java.util.Map var0) throws java.sql.SQLException;
	public abstract void setUrl(java.lang.String var0) throws java.sql.SQLException;
	public abstract void setUsername(java.lang.String var0) throws java.sql.SQLException;
}

