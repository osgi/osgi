/*
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
public abstract interface CallableStatement extends java.sql.PreparedStatement {
	public abstract java.sql.Array getArray(int var0) throws java.sql.SQLException;
	public abstract java.sql.Array getArray(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.math.BigDecimal getBigDecimal(int var0) throws java.sql.SQLException;
	/** @deprecated */ public abstract java.math.BigDecimal getBigDecimal(int var0, int var1) throws java.sql.SQLException;
	public abstract java.math.BigDecimal getBigDecimal(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.sql.Blob getBlob(int var0) throws java.sql.SQLException;
	public abstract java.sql.Blob getBlob(java.lang.String var0) throws java.sql.SQLException;
	public abstract boolean getBoolean(int var0) throws java.sql.SQLException;
	public abstract boolean getBoolean(java.lang.String var0) throws java.sql.SQLException;
	public abstract byte getByte(int var0) throws java.sql.SQLException;
	public abstract byte getByte(java.lang.String var0) throws java.sql.SQLException;
	public abstract byte[] getBytes(int var0) throws java.sql.SQLException;
	public abstract byte[] getBytes(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.sql.Clob getClob(int var0) throws java.sql.SQLException;
	public abstract java.sql.Clob getClob(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.sql.Date getDate(int var0) throws java.sql.SQLException;
	public abstract java.sql.Date getDate(int var0, java.util.Calendar var1) throws java.sql.SQLException;
	public abstract java.sql.Date getDate(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.sql.Date getDate(java.lang.String var0, java.util.Calendar var1) throws java.sql.SQLException;
	public abstract double getDouble(int var0) throws java.sql.SQLException;
	public abstract double getDouble(java.lang.String var0) throws java.sql.SQLException;
	public abstract float getFloat(int var0) throws java.sql.SQLException;
	public abstract float getFloat(java.lang.String var0) throws java.sql.SQLException;
	public abstract int getInt(int var0) throws java.sql.SQLException;
	public abstract int getInt(java.lang.String var0) throws java.sql.SQLException;
	public abstract long getLong(int var0) throws java.sql.SQLException;
	public abstract long getLong(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.lang.Object getObject(int var0) throws java.sql.SQLException;
	public abstract java.lang.Object getObject(int var0, java.util.Map var1) throws java.sql.SQLException;
	public abstract java.lang.Object getObject(java.lang.String var0) throws java.sql.SQLException;
	public abstract java.lang.Object getObject(java.lang.String var0, java.util.Map var1) throws java.sql.SQLException;
	public abstract java.sql.Ref getRef(int var0) throws java.sql.SQLException;
	public abstract java.sql.Ref getRef(java.lang.String var0) throws java.sql.SQLException;
	public abstract short getShort(int var0) throws java.sql.SQLException;
	public abstract short getShort(java.lang.String var0) throws java.sql.SQLException;
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
	public abstract java.net.URL getURL(int var0) throws java.sql.SQLException;
	public abstract java.net.URL getURL(java.lang.String var0) throws java.sql.SQLException;
	public abstract void registerOutParameter(int var0, int var1) throws java.sql.SQLException;
	public abstract void registerOutParameter(int var0, int var1, int var2) throws java.sql.SQLException;
	public abstract void registerOutParameter(int var0, int var1, java.lang.String var2) throws java.sql.SQLException;
	public abstract void registerOutParameter(java.lang.String var0, int var1) throws java.sql.SQLException;
	public abstract void registerOutParameter(java.lang.String var0, int var1, int var2) throws java.sql.SQLException;
	public abstract void registerOutParameter(java.lang.String var0, int var1, java.lang.String var2) throws java.sql.SQLException;
	public abstract void setAsciiStream(java.lang.String var0, java.io.InputStream var1, int var2) throws java.sql.SQLException;
	public abstract void setBigDecimal(java.lang.String var0, java.math.BigDecimal var1) throws java.sql.SQLException;
	public abstract void setBinaryStream(java.lang.String var0, java.io.InputStream var1, int var2) throws java.sql.SQLException;
	public abstract void setBoolean(java.lang.String var0, boolean var1) throws java.sql.SQLException;
	public abstract void setByte(java.lang.String var0, byte var1) throws java.sql.SQLException;
	public abstract void setBytes(java.lang.String var0, byte[] var1) throws java.sql.SQLException;
	public abstract void setCharacterStream(java.lang.String var0, java.io.Reader var1, int var2) throws java.sql.SQLException;
	public abstract void setDate(java.lang.String var0, java.sql.Date var1) throws java.sql.SQLException;
	public abstract void setDate(java.lang.String var0, java.sql.Date var1, java.util.Calendar var2) throws java.sql.SQLException;
	public abstract void setDouble(java.lang.String var0, double var1) throws java.sql.SQLException;
	public abstract void setFloat(java.lang.String var0, float var1) throws java.sql.SQLException;
	public abstract void setInt(java.lang.String var0, int var1) throws java.sql.SQLException;
	public abstract void setLong(java.lang.String var0, long var1) throws java.sql.SQLException;
	public abstract void setNull(java.lang.String var0, int var1) throws java.sql.SQLException;
	public abstract void setNull(java.lang.String var0, int var1, java.lang.String var2) throws java.sql.SQLException;
	public abstract void setObject(java.lang.String var0, java.lang.Object var1) throws java.sql.SQLException;
	public abstract void setObject(java.lang.String var0, java.lang.Object var1, int var2) throws java.sql.SQLException;
	public abstract void setObject(java.lang.String var0, java.lang.Object var1, int var2, int var3) throws java.sql.SQLException;
	public abstract void setShort(java.lang.String var0, short var1) throws java.sql.SQLException;
	public abstract void setString(java.lang.String var0, java.lang.String var1) throws java.sql.SQLException;
	public abstract void setTime(java.lang.String var0, java.sql.Time var1) throws java.sql.SQLException;
	public abstract void setTime(java.lang.String var0, java.sql.Time var1, java.util.Calendar var2) throws java.sql.SQLException;
	public abstract void setTimestamp(java.lang.String var0, java.sql.Timestamp var1) throws java.sql.SQLException;
	public abstract void setTimestamp(java.lang.String var0, java.sql.Timestamp var1, java.util.Calendar var2) throws java.sql.SQLException;
	public abstract void setURL(java.lang.String var0, java.net.URL var1) throws java.sql.SQLException;
	public abstract boolean wasNull() throws java.sql.SQLException;
}

