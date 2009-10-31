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
public interface ResultSet {
	public final static int CLOSE_CURSORS_AT_COMMIT = 2;
	public final static int CONCUR_READ_ONLY = 1007;
	public final static int CONCUR_UPDATABLE = 1008;
	public final static int FETCH_FORWARD = 1000;
	public final static int FETCH_REVERSE = 1001;
	public final static int FETCH_UNKNOWN = 1002;
	public final static int HOLD_CURSORS_OVER_COMMIT = 1;
	public final static int TYPE_FORWARD_ONLY = 1003;
	public final static int TYPE_SCROLL_INSENSITIVE = 1004;
	public final static int TYPE_SCROLL_SENSITIVE = 1005;
	boolean absolute(int var0) throws java.sql.SQLException;
	void afterLast() throws java.sql.SQLException;
	void beforeFirst() throws java.sql.SQLException;
	void cancelRowUpdates() throws java.sql.SQLException;
	void clearWarnings() throws java.sql.SQLException;
	void close() throws java.sql.SQLException;
	void deleteRow() throws java.sql.SQLException;
	int findColumn(java.lang.String var0) throws java.sql.SQLException;
	boolean first() throws java.sql.SQLException;
	java.sql.Array getArray(int var0) throws java.sql.SQLException;
	java.sql.Array getArray(java.lang.String var0) throws java.sql.SQLException;
	java.io.InputStream getAsciiStream(int var0) throws java.sql.SQLException;
	java.io.InputStream getAsciiStream(java.lang.String var0) throws java.sql.SQLException;
	java.math.BigDecimal getBigDecimal(int var0) throws java.sql.SQLException;
	/** @deprecated */
	@java.lang.Deprecated
	java.math.BigDecimal getBigDecimal(int var0, int var1) throws java.sql.SQLException;
	java.math.BigDecimal getBigDecimal(java.lang.String var0) throws java.sql.SQLException;
	/** @deprecated */
	@java.lang.Deprecated
	java.math.BigDecimal getBigDecimal(java.lang.String var0, int var1) throws java.sql.SQLException;
	java.io.InputStream getBinaryStream(int var0) throws java.sql.SQLException;
	java.io.InputStream getBinaryStream(java.lang.String var0) throws java.sql.SQLException;
	java.sql.Blob getBlob(int var0) throws java.sql.SQLException;
	java.sql.Blob getBlob(java.lang.String var0) throws java.sql.SQLException;
	boolean getBoolean(int var0) throws java.sql.SQLException;
	boolean getBoolean(java.lang.String var0) throws java.sql.SQLException;
	byte getByte(int var0) throws java.sql.SQLException;
	byte getByte(java.lang.String var0) throws java.sql.SQLException;
	byte[] getBytes(int var0) throws java.sql.SQLException;
	byte[] getBytes(java.lang.String var0) throws java.sql.SQLException;
	java.io.Reader getCharacterStream(int var0) throws java.sql.SQLException;
	java.io.Reader getCharacterStream(java.lang.String var0) throws java.sql.SQLException;
	java.sql.Clob getClob(int var0) throws java.sql.SQLException;
	java.sql.Clob getClob(java.lang.String var0) throws java.sql.SQLException;
	int getConcurrency() throws java.sql.SQLException;
	java.lang.String getCursorName() throws java.sql.SQLException;
	java.sql.Date getDate(int var0) throws java.sql.SQLException;
	java.sql.Date getDate(int var0, java.util.Calendar var1) throws java.sql.SQLException;
	java.sql.Date getDate(java.lang.String var0) throws java.sql.SQLException;
	java.sql.Date getDate(java.lang.String var0, java.util.Calendar var1) throws java.sql.SQLException;
	double getDouble(int var0) throws java.sql.SQLException;
	double getDouble(java.lang.String var0) throws java.sql.SQLException;
	int getFetchDirection() throws java.sql.SQLException;
	int getFetchSize() throws java.sql.SQLException;
	float getFloat(int var0) throws java.sql.SQLException;
	float getFloat(java.lang.String var0) throws java.sql.SQLException;
	int getInt(int var0) throws java.sql.SQLException;
	int getInt(java.lang.String var0) throws java.sql.SQLException;
	long getLong(int var0) throws java.sql.SQLException;
	long getLong(java.lang.String var0) throws java.sql.SQLException;
	java.sql.ResultSetMetaData getMetaData() throws java.sql.SQLException;
	java.lang.Object getObject(int var0) throws java.sql.SQLException;
	java.lang.Object getObject(int var0, java.util.Map<java.lang.String,java.lang.Class<?>> var1) throws java.sql.SQLException;
	java.lang.Object getObject(java.lang.String var0) throws java.sql.SQLException;
	java.lang.Object getObject(java.lang.String var0, java.util.Map<java.lang.String,java.lang.Class<?>> var1) throws java.sql.SQLException;
	java.sql.Ref getRef(int var0) throws java.sql.SQLException;
	java.sql.Ref getRef(java.lang.String var0) throws java.sql.SQLException;
	int getRow() throws java.sql.SQLException;
	short getShort(int var0) throws java.sql.SQLException;
	short getShort(java.lang.String var0) throws java.sql.SQLException;
	java.sql.Statement getStatement() throws java.sql.SQLException;
	java.lang.String getString(int var0) throws java.sql.SQLException;
	java.lang.String getString(java.lang.String var0) throws java.sql.SQLException;
	java.sql.Time getTime(int var0) throws java.sql.SQLException;
	java.sql.Time getTime(int var0, java.util.Calendar var1) throws java.sql.SQLException;
	java.sql.Time getTime(java.lang.String var0) throws java.sql.SQLException;
	java.sql.Time getTime(java.lang.String var0, java.util.Calendar var1) throws java.sql.SQLException;
	java.sql.Timestamp getTimestamp(int var0) throws java.sql.SQLException;
	java.sql.Timestamp getTimestamp(int var0, java.util.Calendar var1) throws java.sql.SQLException;
	java.sql.Timestamp getTimestamp(java.lang.String var0) throws java.sql.SQLException;
	java.sql.Timestamp getTimestamp(java.lang.String var0, java.util.Calendar var1) throws java.sql.SQLException;
	int getType() throws java.sql.SQLException;
	java.net.URL getURL(int var0) throws java.sql.SQLException;
	java.net.URL getURL(java.lang.String var0) throws java.sql.SQLException;
	/** @deprecated */
	@java.lang.Deprecated
	java.io.InputStream getUnicodeStream(int var0) throws java.sql.SQLException;
	/** @deprecated */
	@java.lang.Deprecated
	java.io.InputStream getUnicodeStream(java.lang.String var0) throws java.sql.SQLException;
	java.sql.SQLWarning getWarnings() throws java.sql.SQLException;
	void insertRow() throws java.sql.SQLException;
	boolean isAfterLast() throws java.sql.SQLException;
	boolean isBeforeFirst() throws java.sql.SQLException;
	boolean isFirst() throws java.sql.SQLException;
	boolean isLast() throws java.sql.SQLException;
	boolean last() throws java.sql.SQLException;
	void moveToCurrentRow() throws java.sql.SQLException;
	void moveToInsertRow() throws java.sql.SQLException;
	boolean next() throws java.sql.SQLException;
	boolean previous() throws java.sql.SQLException;
	void refreshRow() throws java.sql.SQLException;
	boolean relative(int var0) throws java.sql.SQLException;
	boolean rowDeleted() throws java.sql.SQLException;
	boolean rowInserted() throws java.sql.SQLException;
	boolean rowUpdated() throws java.sql.SQLException;
	void setFetchDirection(int var0) throws java.sql.SQLException;
	void setFetchSize(int var0) throws java.sql.SQLException;
	void updateArray(int var0, java.sql.Array var1) throws java.sql.SQLException;
	void updateArray(java.lang.String var0, java.sql.Array var1) throws java.sql.SQLException;
	void updateAsciiStream(int var0, java.io.InputStream var1, int var2) throws java.sql.SQLException;
	void updateAsciiStream(java.lang.String var0, java.io.InputStream var1, int var2) throws java.sql.SQLException;
	void updateBigDecimal(int var0, java.math.BigDecimal var1) throws java.sql.SQLException;
	void updateBigDecimal(java.lang.String var0, java.math.BigDecimal var1) throws java.sql.SQLException;
	void updateBinaryStream(int var0, java.io.InputStream var1, int var2) throws java.sql.SQLException;
	void updateBinaryStream(java.lang.String var0, java.io.InputStream var1, int var2) throws java.sql.SQLException;
	void updateBlob(int var0, java.sql.Blob var1) throws java.sql.SQLException;
	void updateBlob(java.lang.String var0, java.sql.Blob var1) throws java.sql.SQLException;
	void updateBoolean(int var0, boolean var1) throws java.sql.SQLException;
	void updateBoolean(java.lang.String var0, boolean var1) throws java.sql.SQLException;
	void updateByte(int var0, byte var1) throws java.sql.SQLException;
	void updateByte(java.lang.String var0, byte var1) throws java.sql.SQLException;
	void updateBytes(int var0, byte[] var1) throws java.sql.SQLException;
	void updateBytes(java.lang.String var0, byte[] var1) throws java.sql.SQLException;
	void updateCharacterStream(int var0, java.io.Reader var1, int var2) throws java.sql.SQLException;
	void updateCharacterStream(java.lang.String var0, java.io.Reader var1, int var2) throws java.sql.SQLException;
	void updateClob(int var0, java.sql.Clob var1) throws java.sql.SQLException;
	void updateClob(java.lang.String var0, java.sql.Clob var1) throws java.sql.SQLException;
	void updateDate(int var0, java.sql.Date var1) throws java.sql.SQLException;
	void updateDate(java.lang.String var0, java.sql.Date var1) throws java.sql.SQLException;
	void updateDouble(int var0, double var1) throws java.sql.SQLException;
	void updateDouble(java.lang.String var0, double var1) throws java.sql.SQLException;
	void updateFloat(int var0, float var1) throws java.sql.SQLException;
	void updateFloat(java.lang.String var0, float var1) throws java.sql.SQLException;
	void updateInt(int var0, int var1) throws java.sql.SQLException;
	void updateInt(java.lang.String var0, int var1) throws java.sql.SQLException;
	void updateLong(int var0, long var1) throws java.sql.SQLException;
	void updateLong(java.lang.String var0, long var1) throws java.sql.SQLException;
	void updateNull(int var0) throws java.sql.SQLException;
	void updateNull(java.lang.String var0) throws java.sql.SQLException;
	void updateObject(int var0, java.lang.Object var1) throws java.sql.SQLException;
	void updateObject(int var0, java.lang.Object var1, int var2) throws java.sql.SQLException;
	void updateObject(java.lang.String var0, java.lang.Object var1) throws java.sql.SQLException;
	void updateObject(java.lang.String var0, java.lang.Object var1, int var2) throws java.sql.SQLException;
	void updateRef(int var0, java.sql.Ref var1) throws java.sql.SQLException;
	void updateRef(java.lang.String var0, java.sql.Ref var1) throws java.sql.SQLException;
	void updateRow() throws java.sql.SQLException;
	void updateShort(int var0, short var1) throws java.sql.SQLException;
	void updateShort(java.lang.String var0, short var1) throws java.sql.SQLException;
	void updateString(int var0, java.lang.String var1) throws java.sql.SQLException;
	void updateString(java.lang.String var0, java.lang.String var1) throws java.sql.SQLException;
	void updateTime(int var0, java.sql.Time var1) throws java.sql.SQLException;
	void updateTime(java.lang.String var0, java.sql.Time var1) throws java.sql.SQLException;
	void updateTimestamp(int var0, java.sql.Timestamp var1) throws java.sql.SQLException;
	void updateTimestamp(java.lang.String var0, java.sql.Timestamp var1) throws java.sql.SQLException;
	boolean wasNull() throws java.sql.SQLException;
}

