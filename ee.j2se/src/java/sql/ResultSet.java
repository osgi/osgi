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
public abstract interface ResultSet {
	public abstract boolean absolute(int var0) throws java.sql.SQLException;
	public abstract void afterLast() throws java.sql.SQLException;
	public abstract void beforeFirst() throws java.sql.SQLException;
	public abstract void cancelRowUpdates() throws java.sql.SQLException;
	public abstract void clearWarnings() throws java.sql.SQLException;
	public abstract void close() throws java.sql.SQLException;
	public abstract void deleteRow() throws java.sql.SQLException;
	public abstract int findColumn(java.lang.String var0) throws java.sql.SQLException;
	public abstract boolean first() throws java.sql.SQLException;
	public abstract java.sql.Array getArray(int var0) throws java.sql.SQLException;
	public abstract java.sql.Array getArray(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.io.InputStream getAsciiStream(int var0) throws java.sql.SQLException;
	public abstract java.io.InputStream getAsciiStream(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.math.BigDecimal getBigDecimal(int var0) throws java.sql.SQLException;
	/** @deprecated */ public abstract java.math.BigDecimal getBigDecimal(int var0, int var1) throws java.sql.SQLException;
	public abstract java.math.BigDecimal getBigDecimal(java.lang.String var0) throws java.sql.SQLException;
	/** @deprecated */ public abstract java.math.BigDecimal getBigDecimal(java.lang.String var0, int var1) throws java.sql.SQLException;
	public abstract java.io.InputStream getBinaryStream(int var0) throws java.sql.SQLException;
	public abstract java.io.InputStream getBinaryStream(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.sql.Blob getBlob(int var0) throws java.sql.SQLException;
	public abstract java.sql.Blob getBlob(java.lang.String var0) throws java.sql.SQLException;
	public abstract boolean getBoolean(int var0) throws java.sql.SQLException;
	public abstract boolean getBoolean(java.lang.String var0) throws java.sql.SQLException;
	public abstract byte getByte(int var0) throws java.sql.SQLException;
	public abstract byte getByte(java.lang.String var0) throws java.sql.SQLException;
	public abstract byte[] getBytes(int var0) throws java.sql.SQLException;
	public abstract byte[] getBytes(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.io.Reader getCharacterStream(int var0) throws java.sql.SQLException;
	public abstract java.io.Reader getCharacterStream(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.sql.Clob getClob(int var0) throws java.sql.SQLException;
	public abstract java.sql.Clob getClob(java.lang.String var0) throws java.sql.SQLException;
	public abstract int getConcurrency() throws java.sql.SQLException;
	public abstract java.lang.String getCursorName() throws java.sql.SQLException;
	public abstract java.sql.Date getDate(int var0) throws java.sql.SQLException;
	public abstract java.sql.Date getDate(int var0, java.util.Calendar var1) throws java.sql.SQLException;
	public abstract java.sql.Date getDate(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.sql.Date getDate(java.lang.String var0, java.util.Calendar var1) throws java.sql.SQLException;
	public abstract double getDouble(int var0) throws java.sql.SQLException;
	public abstract double getDouble(java.lang.String var0) throws java.sql.SQLException;
	public abstract int getFetchDirection() throws java.sql.SQLException;
	public abstract int getFetchSize() throws java.sql.SQLException;
	public abstract float getFloat(int var0) throws java.sql.SQLException;
	public abstract float getFloat(java.lang.String var0) throws java.sql.SQLException;
	public abstract int getInt(int var0) throws java.sql.SQLException;
	public abstract int getInt(java.lang.String var0) throws java.sql.SQLException;
	public abstract long getLong(int var0) throws java.sql.SQLException;
	public abstract long getLong(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.sql.ResultSetMetaData getMetaData() throws java.sql.SQLException;
	public abstract java.lang.Object getObject(int var0) throws java.sql.SQLException;
	public abstract java.lang.Object getObject(int var0, java.util.Map var1) throws java.sql.SQLException;
	public abstract java.lang.Object getObject(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.lang.Object getObject(java.lang.String var0, java.util.Map var1) throws java.sql.SQLException;
	public abstract java.sql.Ref getRef(int var0) throws java.sql.SQLException;
	public abstract java.sql.Ref getRef(java.lang.String var0) throws java.sql.SQLException;
	public abstract int getRow() throws java.sql.SQLException;
	public abstract short getShort(int var0) throws java.sql.SQLException;
	public abstract short getShort(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.sql.Statement getStatement() throws java.sql.SQLException;
	public abstract java.lang.String getString(int var0) throws java.sql.SQLException;
	public abstract java.lang.String getString(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.sql.Time getTime(int var0) throws java.sql.SQLException;
	public abstract java.sql.Time getTime(int var0, java.util.Calendar var1) throws java.sql.SQLException;
	public abstract java.sql.Time getTime(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.sql.Time getTime(java.lang.String var0, java.util.Calendar var1) throws java.sql.SQLException;
	public abstract java.sql.Timestamp getTimestamp(int var0) throws java.sql.SQLException;
	public abstract java.sql.Timestamp getTimestamp(int var0, java.util.Calendar var1) throws java.sql.SQLException;
	public abstract java.sql.Timestamp getTimestamp(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.sql.Timestamp getTimestamp(java.lang.String var0, java.util.Calendar var1) throws java.sql.SQLException;
	public abstract int getType() throws java.sql.SQLException;
	public abstract java.net.URL getURL(int var0) throws java.sql.SQLException;
	public abstract java.net.URL getURL(java.lang.String var0) throws java.sql.SQLException;
	/** @deprecated */ public abstract java.io.InputStream getUnicodeStream(int var0) throws java.sql.SQLException;
	/** @deprecated */ public abstract java.io.InputStream getUnicodeStream(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.sql.SQLWarning getWarnings() throws java.sql.SQLException;
	public abstract void insertRow() throws java.sql.SQLException;
	public abstract boolean isAfterLast() throws java.sql.SQLException;
	public abstract boolean isBeforeFirst() throws java.sql.SQLException;
	public abstract boolean isFirst() throws java.sql.SQLException;
	public abstract boolean isLast() throws java.sql.SQLException;
	public abstract boolean last() throws java.sql.SQLException;
	public abstract void moveToCurrentRow() throws java.sql.SQLException;
	public abstract void moveToInsertRow() throws java.sql.SQLException;
	public abstract boolean next() throws java.sql.SQLException;
	public abstract boolean previous() throws java.sql.SQLException;
	public abstract void refreshRow() throws java.sql.SQLException;
	public abstract boolean relative(int var0) throws java.sql.SQLException;
	public abstract boolean rowDeleted() throws java.sql.SQLException;
	public abstract boolean rowInserted() throws java.sql.SQLException;
	public abstract boolean rowUpdated() throws java.sql.SQLException;
	public abstract void setFetchDirection(int var0) throws java.sql.SQLException;
	public abstract void setFetchSize(int var0) throws java.sql.SQLException;
	public abstract void updateArray(int var0, java.sql.Array var1) throws java.sql.SQLException;
	public abstract void updateArray(java.lang.String var0, java.sql.Array var1) throws java.sql.SQLException;
	public abstract void updateAsciiStream(int var0, java.io.InputStream var1, int var2) throws java.sql.SQLException;
	public abstract void updateAsciiStream(java.lang.String var0, java.io.InputStream var1, int var2) throws java.sql.SQLException;
	public abstract void updateBigDecimal(int var0, java.math.BigDecimal var1) throws java.sql.SQLException;
	public abstract void updateBigDecimal(java.lang.String var0, java.math.BigDecimal var1) throws java.sql.SQLException;
	public abstract void updateBinaryStream(int var0, java.io.InputStream var1, int var2) throws java.sql.SQLException;
	public abstract void updateBinaryStream(java.lang.String var0, java.io.InputStream var1, int var2) throws java.sql.SQLException;
	public abstract void updateBlob(int var0, java.sql.Blob var1) throws java.sql.SQLException;
	public abstract void updateBlob(java.lang.String var0, java.sql.Blob var1) throws java.sql.SQLException;
	public abstract void updateBoolean(int var0, boolean var1) throws java.sql.SQLException;
	public abstract void updateBoolean(java.lang.String var0, boolean var1) throws java.sql.SQLException;
	public abstract void updateByte(int var0, byte var1) throws java.sql.SQLException;
	public abstract void updateByte(java.lang.String var0, byte var1) throws java.sql.SQLException;
	public abstract void updateBytes(int var0, byte[] var1) throws java.sql.SQLException;
	public abstract void updateBytes(java.lang.String var0, byte[] var1) throws java.sql.SQLException;
	public abstract void updateCharacterStream(int var0, java.io.Reader var1, int var2) throws java.sql.SQLException;
	public abstract void updateCharacterStream(java.lang.String var0, java.io.Reader var1, int var2) throws java.sql.SQLException;
	public abstract void updateClob(int var0, java.sql.Clob var1) throws java.sql.SQLException;
	public abstract void updateClob(java.lang.String var0, java.sql.Clob var1) throws java.sql.SQLException;
	public abstract void updateDate(int var0, java.sql.Date var1) throws java.sql.SQLException;
	public abstract void updateDate(java.lang.String var0, java.sql.Date var1) throws java.sql.SQLException;
	public abstract void updateDouble(int var0, double var1) throws java.sql.SQLException;
	public abstract void updateDouble(java.lang.String var0, double var1) throws java.sql.SQLException;
	public abstract void updateFloat(int var0, float var1) throws java.sql.SQLException;
	public abstract void updateFloat(java.lang.String var0, float var1) throws java.sql.SQLException;
	public abstract void updateInt(int var0, int var1) throws java.sql.SQLException;
	public abstract void updateInt(java.lang.String var0, int var1) throws java.sql.SQLException;
	public abstract void updateLong(int var0, long var1) throws java.sql.SQLException;
	public abstract void updateLong(java.lang.String var0, long var1) throws java.sql.SQLException;
	public abstract void updateNull(int var0) throws java.sql.SQLException;
	public abstract void updateNull(java.lang.String var0) throws java.sql.SQLException;
	public abstract void updateObject(int var0, java.lang.Object var1) throws java.sql.SQLException;
	public abstract void updateObject(int var0, java.lang.Object var1, int var2) throws java.sql.SQLException;
	public abstract void updateObject(java.lang.String var0, java.lang.Object var1) throws java.sql.SQLException;
	public abstract void updateObject(java.lang.String var0, java.lang.Object var1, int var2) throws java.sql.SQLException;
	public abstract void updateRef(int var0, java.sql.Ref var1) throws java.sql.SQLException;
	public abstract void updateRef(java.lang.String var0, java.sql.Ref var1) throws java.sql.SQLException;
	public abstract void updateRow() throws java.sql.SQLException;
	public abstract void updateShort(int var0, short var1) throws java.sql.SQLException;
	public abstract void updateShort(java.lang.String var0, short var1) throws java.sql.SQLException;
	public abstract void updateString(int var0, java.lang.String var1) throws java.sql.SQLException;
	public abstract void updateString(java.lang.String var0, java.lang.String var1) throws java.sql.SQLException;
	public abstract void updateTime(int var0, java.sql.Time var1) throws java.sql.SQLException;
	public abstract void updateTime(java.lang.String var0, java.sql.Time var1) throws java.sql.SQLException;
	public abstract void updateTimestamp(int var0, java.sql.Timestamp var1) throws java.sql.SQLException;
	public abstract void updateTimestamp(java.lang.String var0, java.sql.Timestamp var1) throws java.sql.SQLException;
	public abstract boolean wasNull() throws java.sql.SQLException;
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
}

