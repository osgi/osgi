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
public abstract class BaseRowSet implements java.io.Serializable, java.lang.Cloneable {
	public final static int ASCII_STREAM_PARAM = 2;
	public final static int BINARY_STREAM_PARAM = 1;
	public final static int UNICODE_STREAM_PARAM = 0;
	protected java.io.InputStream asciiStream;
	protected java.io.InputStream binaryStream;
	protected java.io.Reader charStream;
	protected java.io.InputStream unicodeStream;
	public BaseRowSet() { } 
	public void addRowSetListener(javax.sql.RowSetListener var0) { }
	public void clearParameters() throws java.sql.SQLException { }
	public java.lang.String getCommand() { return null; }
	public int getConcurrency() throws java.sql.SQLException { return 0; }
	public java.lang.String getDataSourceName() { return null; }
	public boolean getEscapeProcessing() throws java.sql.SQLException { return false; }
	public int getFetchDirection() throws java.sql.SQLException { return 0; }
	public int getFetchSize() throws java.sql.SQLException { return 0; }
	public int getMaxFieldSize() throws java.sql.SQLException { return 0; }
	public int getMaxRows() throws java.sql.SQLException { return 0; }
	public java.lang.Object[] getParams() throws java.sql.SQLException { return null; }
	public java.lang.String getPassword() { return null; }
	public int getQueryTimeout() throws java.sql.SQLException { return 0; }
	public boolean getShowDeleted() throws java.sql.SQLException { return false; }
	public int getTransactionIsolation() { return 0; }
	public int getType() throws java.sql.SQLException { return 0; }
	public java.util.Map<java.lang.String,java.lang.Class<?>> getTypeMap() { return null; }
	public java.lang.String getUrl() throws java.sql.SQLException { return null; }
	public java.lang.String getUsername() { return null; }
	protected void initParams() { }
	public boolean isReadOnly() { return false; }
	protected void notifyCursorMoved() throws java.sql.SQLException { }
	protected void notifyRowChanged() throws java.sql.SQLException { }
	protected void notifyRowSetChanged() throws java.sql.SQLException { }
	public void removeRowSetListener(javax.sql.RowSetListener var0) { }
	public void setArray(int var0, java.sql.Array var1) throws java.sql.SQLException { }
	public void setAsciiStream(int var0, java.io.InputStream var1, int var2) throws java.sql.SQLException { }
	public void setBigDecimal(int var0, java.math.BigDecimal var1) throws java.sql.SQLException { }
	public void setBinaryStream(int var0, java.io.InputStream var1, int var2) throws java.sql.SQLException { }
	public void setBlob(int var0, java.sql.Blob var1) throws java.sql.SQLException { }
	public void setBoolean(int var0, boolean var1) throws java.sql.SQLException { }
	public void setByte(int var0, byte var1) throws java.sql.SQLException { }
	public void setBytes(int var0, byte[] var1) throws java.sql.SQLException { }
	public void setCharacterStream(int var0, java.io.Reader var1, int var2) throws java.sql.SQLException { }
	public void setClob(int var0, java.sql.Clob var1) throws java.sql.SQLException { }
	public void setCommand(java.lang.String var0) throws java.sql.SQLException { }
	public void setConcurrency(int var0) throws java.sql.SQLException { }
	public void setDataSourceName(java.lang.String var0) throws java.sql.SQLException { }
	public void setDate(int var0, java.sql.Date var1) throws java.sql.SQLException { }
	public void setDate(int var0, java.sql.Date var1, java.util.Calendar var2) throws java.sql.SQLException { }
	public void setDouble(int var0, double var1) throws java.sql.SQLException { }
	public void setEscapeProcessing(boolean var0) throws java.sql.SQLException { }
	public void setFetchDirection(int var0) throws java.sql.SQLException { }
	public void setFetchSize(int var0) throws java.sql.SQLException { }
	public void setFloat(int var0, float var1) throws java.sql.SQLException { }
	public void setInt(int var0, int var1) throws java.sql.SQLException { }
	public void setLong(int var0, long var1) throws java.sql.SQLException { }
	public void setMaxFieldSize(int var0) throws java.sql.SQLException { }
	public void setMaxRows(int var0) throws java.sql.SQLException { }
	public void setNull(int var0, int var1) throws java.sql.SQLException { }
	public void setNull(int var0, int var1, java.lang.String var2) throws java.sql.SQLException { }
	public void setObject(int var0, java.lang.Object var1) throws java.sql.SQLException { }
	public void setObject(int var0, java.lang.Object var1, int var2) throws java.sql.SQLException { }
	public void setObject(int var0, java.lang.Object var1, int var2, int var3) throws java.sql.SQLException { }
	public void setPassword(java.lang.String var0) { }
	public void setQueryTimeout(int var0) throws java.sql.SQLException { }
	public void setReadOnly(boolean var0) { }
	public void setRef(int var0, java.sql.Ref var1) throws java.sql.SQLException { }
	public void setShort(int var0, short var1) throws java.sql.SQLException { }
	public void setShowDeleted(boolean var0) throws java.sql.SQLException { }
	public void setString(int var0, java.lang.String var1) throws java.sql.SQLException { }
	public void setTime(int var0, java.sql.Time var1) throws java.sql.SQLException { }
	public void setTime(int var0, java.sql.Time var1, java.util.Calendar var2) throws java.sql.SQLException { }
	public void setTimestamp(int var0, java.sql.Timestamp var1) throws java.sql.SQLException { }
	public void setTimestamp(int var0, java.sql.Timestamp var1, java.util.Calendar var2) throws java.sql.SQLException { }
	public void setTransactionIsolation(int var0) throws java.sql.SQLException { }
	public void setType(int var0) throws java.sql.SQLException { }
	public void setTypeMap(java.util.Map<java.lang.String,java.lang.Class<?>> var0) { }
	/** @deprecated */ public void setUnicodeStream(int var0, java.io.InputStream var1, int var2) throws java.sql.SQLException { }
	public void setUrl(java.lang.String var0) throws java.sql.SQLException { }
	public void setUsername(java.lang.String var0) { }
}

