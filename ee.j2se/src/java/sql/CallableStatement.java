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
public interface CallableStatement extends java.sql.PreparedStatement {
	java.sql.Array getArray(int var0) throws java.sql.SQLException;
	java.sql.Array getArray(java.lang.String var0) throws java.sql.SQLException;
	java.math.BigDecimal getBigDecimal(int var0) throws java.sql.SQLException;
	/** @deprecated */ java.math.BigDecimal getBigDecimal(int var0, int var1) throws java.sql.SQLException;
	java.math.BigDecimal getBigDecimal(java.lang.String var0) throws java.sql.SQLException;
	java.sql.Blob getBlob(int var0) throws java.sql.SQLException;
	java.sql.Blob getBlob(java.lang.String var0) throws java.sql.SQLException;
	boolean getBoolean(int var0) throws java.sql.SQLException;
	boolean getBoolean(java.lang.String var0) throws java.sql.SQLException;
	byte getByte(int var0) throws java.sql.SQLException;
	byte getByte(java.lang.String var0) throws java.sql.SQLException;
	byte[] getBytes(int var0) throws java.sql.SQLException;
	byte[] getBytes(java.lang.String var0) throws java.sql.SQLException;
	java.sql.Clob getClob(int var0) throws java.sql.SQLException;
	java.sql.Clob getClob(java.lang.String var0) throws java.sql.SQLException;
	java.sql.Date getDate(int var0) throws java.sql.SQLException;
	java.sql.Date getDate(int var0, java.util.Calendar var1) throws java.sql.SQLException;
	java.sql.Date getDate(java.lang.String var0) throws java.sql.SQLException;
	java.sql.Date getDate(java.lang.String var0, java.util.Calendar var1) throws java.sql.SQLException;
	double getDouble(int var0) throws java.sql.SQLException;
	double getDouble(java.lang.String var0) throws java.sql.SQLException;
	float getFloat(int var0) throws java.sql.SQLException;
	float getFloat(java.lang.String var0) throws java.sql.SQLException;
	int getInt(int var0) throws java.sql.SQLException;
	int getInt(java.lang.String var0) throws java.sql.SQLException;
	long getLong(int var0) throws java.sql.SQLException;
	long getLong(java.lang.String var0) throws java.sql.SQLException;
	java.lang.Object getObject(int var0) throws java.sql.SQLException;
	java.lang.Object getObject(int var0, java.util.Map<java.lang.String,java.lang.Class<?>> var1) throws java.sql.SQLException;
	java.lang.Object getObject(java.lang.String var0) throws java.sql.SQLException;
	java.lang.Object getObject(java.lang.String var0, java.util.Map<java.lang.String,java.lang.Class<?>> var1) throws java.sql.SQLException;
	java.sql.Ref getRef(int var0) throws java.sql.SQLException;
	java.sql.Ref getRef(java.lang.String var0) throws java.sql.SQLException;
	short getShort(int var0) throws java.sql.SQLException;
	short getShort(java.lang.String var0) throws java.sql.SQLException;
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
	java.net.URL getURL(int var0) throws java.sql.SQLException;
	java.net.URL getURL(java.lang.String var0) throws java.sql.SQLException;
	void registerOutParameter(int var0, int var1) throws java.sql.SQLException;
	void registerOutParameter(int var0, int var1, int var2) throws java.sql.SQLException;
	void registerOutParameter(int var0, int var1, java.lang.String var2) throws java.sql.SQLException;
	void registerOutParameter(java.lang.String var0, int var1) throws java.sql.SQLException;
	void registerOutParameter(java.lang.String var0, int var1, int var2) throws java.sql.SQLException;
	void registerOutParameter(java.lang.String var0, int var1, java.lang.String var2) throws java.sql.SQLException;
	void setAsciiStream(java.lang.String var0, java.io.InputStream var1, int var2) throws java.sql.SQLException;
	void setBigDecimal(java.lang.String var0, java.math.BigDecimal var1) throws java.sql.SQLException;
	void setBinaryStream(java.lang.String var0, java.io.InputStream var1, int var2) throws java.sql.SQLException;
	void setBoolean(java.lang.String var0, boolean var1) throws java.sql.SQLException;
	void setByte(java.lang.String var0, byte var1) throws java.sql.SQLException;
	void setBytes(java.lang.String var0, byte[] var1) throws java.sql.SQLException;
	void setCharacterStream(java.lang.String var0, java.io.Reader var1, int var2) throws java.sql.SQLException;
	void setDate(java.lang.String var0, java.sql.Date var1) throws java.sql.SQLException;
	void setDate(java.lang.String var0, java.sql.Date var1, java.util.Calendar var2) throws java.sql.SQLException;
	void setDouble(java.lang.String var0, double var1) throws java.sql.SQLException;
	void setFloat(java.lang.String var0, float var1) throws java.sql.SQLException;
	void setInt(java.lang.String var0, int var1) throws java.sql.SQLException;
	void setLong(java.lang.String var0, long var1) throws java.sql.SQLException;
	void setNull(java.lang.String var0, int var1) throws java.sql.SQLException;
	void setNull(java.lang.String var0, int var1, java.lang.String var2) throws java.sql.SQLException;
	void setObject(java.lang.String var0, java.lang.Object var1) throws java.sql.SQLException;
	void setObject(java.lang.String var0, java.lang.Object var1, int var2) throws java.sql.SQLException;
	void setObject(java.lang.String var0, java.lang.Object var1, int var2, int var3) throws java.sql.SQLException;
	void setShort(java.lang.String var0, short var1) throws java.sql.SQLException;
	void setString(java.lang.String var0, java.lang.String var1) throws java.sql.SQLException;
	void setTime(java.lang.String var0, java.sql.Time var1) throws java.sql.SQLException;
	void setTime(java.lang.String var0, java.sql.Time var1, java.util.Calendar var2) throws java.sql.SQLException;
	void setTimestamp(java.lang.String var0, java.sql.Timestamp var1) throws java.sql.SQLException;
	void setTimestamp(java.lang.String var0, java.sql.Timestamp var1, java.util.Calendar var2) throws java.sql.SQLException;
	void setURL(java.lang.String var0, java.net.URL var1) throws java.sql.SQLException;
	boolean wasNull() throws java.sql.SQLException;
}

