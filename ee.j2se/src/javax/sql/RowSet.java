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

package javax.sql;
public interface RowSet extends java.sql.ResultSet {
	void addRowSetListener(javax.sql.RowSetListener var0);
	void clearParameters() throws java.sql.SQLException;
	void execute() throws java.sql.SQLException;
	java.lang.String getCommand();
	java.lang.String getDataSourceName();
	boolean getEscapeProcessing() throws java.sql.SQLException;
	int getMaxFieldSize() throws java.sql.SQLException;
	int getMaxRows() throws java.sql.SQLException;
	java.lang.String getPassword();
	int getQueryTimeout() throws java.sql.SQLException;
	int getTransactionIsolation();
	java.util.Map<java.lang.String,java.lang.Class<?>> getTypeMap() throws java.sql.SQLException;
	java.lang.String getUrl() throws java.sql.SQLException;
	java.lang.String getUsername();
	boolean isReadOnly();
	void removeRowSetListener(javax.sql.RowSetListener var0);
	void setArray(int var0, java.sql.Array var1) throws java.sql.SQLException;
	void setAsciiStream(int var0, java.io.InputStream var1, int var2) throws java.sql.SQLException;
	void setBigDecimal(int var0, java.math.BigDecimal var1) throws java.sql.SQLException;
	void setBinaryStream(int var0, java.io.InputStream var1, int var2) throws java.sql.SQLException;
	void setBlob(int var0, java.sql.Blob var1) throws java.sql.SQLException;
	void setBoolean(int var0, boolean var1) throws java.sql.SQLException;
	void setByte(int var0, byte var1) throws java.sql.SQLException;
	void setBytes(int var0, byte[] var1) throws java.sql.SQLException;
	void setCharacterStream(int var0, java.io.Reader var1, int var2) throws java.sql.SQLException;
	void setClob(int var0, java.sql.Clob var1) throws java.sql.SQLException;
	void setCommand(java.lang.String var0) throws java.sql.SQLException;
	void setConcurrency(int var0) throws java.sql.SQLException;
	void setDataSourceName(java.lang.String var0) throws java.sql.SQLException;
	void setDate(int var0, java.sql.Date var1) throws java.sql.SQLException;
	void setDate(int var0, java.sql.Date var1, java.util.Calendar var2) throws java.sql.SQLException;
	void setDouble(int var0, double var1) throws java.sql.SQLException;
	void setEscapeProcessing(boolean var0) throws java.sql.SQLException;
	void setFloat(int var0, float var1) throws java.sql.SQLException;
	void setInt(int var0, int var1) throws java.sql.SQLException;
	void setLong(int var0, long var1) throws java.sql.SQLException;
	void setMaxFieldSize(int var0) throws java.sql.SQLException;
	void setMaxRows(int var0) throws java.sql.SQLException;
	void setNull(int var0, int var1) throws java.sql.SQLException;
	void setNull(int var0, int var1, java.lang.String var2) throws java.sql.SQLException;
	void setObject(int var0, java.lang.Object var1) throws java.sql.SQLException;
	void setObject(int var0, java.lang.Object var1, int var2) throws java.sql.SQLException;
	void setObject(int var0, java.lang.Object var1, int var2, int var3) throws java.sql.SQLException;
	void setPassword(java.lang.String var0) throws java.sql.SQLException;
	void setQueryTimeout(int var0) throws java.sql.SQLException;
	void setReadOnly(boolean var0) throws java.sql.SQLException;
	void setRef(int var0, java.sql.Ref var1) throws java.sql.SQLException;
	void setShort(int var0, short var1) throws java.sql.SQLException;
	void setString(int var0, java.lang.String var1) throws java.sql.SQLException;
	void setTime(int var0, java.sql.Time var1) throws java.sql.SQLException;
	void setTime(int var0, java.sql.Time var1, java.util.Calendar var2) throws java.sql.SQLException;
	void setTimestamp(int var0, java.sql.Timestamp var1) throws java.sql.SQLException;
	void setTimestamp(int var0, java.sql.Timestamp var1, java.util.Calendar var2) throws java.sql.SQLException;
	void setTransactionIsolation(int var0) throws java.sql.SQLException;
	void setType(int var0) throws java.sql.SQLException;
	void setTypeMap(java.util.Map<java.lang.String,java.lang.Class<?>> var0) throws java.sql.SQLException;
	void setUrl(java.lang.String var0) throws java.sql.SQLException;
	void setUsername(java.lang.String var0) throws java.sql.SQLException;
}

