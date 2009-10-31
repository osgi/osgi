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
public interface SQLOutput {
	void writeArray(java.sql.Array var0) throws java.sql.SQLException;
	void writeAsciiStream(java.io.InputStream var0) throws java.sql.SQLException;
	void writeBigDecimal(java.math.BigDecimal var0) throws java.sql.SQLException;
	void writeBinaryStream(java.io.InputStream var0) throws java.sql.SQLException;
	void writeBlob(java.sql.Blob var0) throws java.sql.SQLException;
	void writeBoolean(boolean var0) throws java.sql.SQLException;
	void writeByte(byte var0) throws java.sql.SQLException;
	void writeBytes(byte[] var0) throws java.sql.SQLException;
	void writeCharacterStream(java.io.Reader var0) throws java.sql.SQLException;
	void writeClob(java.sql.Clob var0) throws java.sql.SQLException;
	void writeDate(java.sql.Date var0) throws java.sql.SQLException;
	void writeDouble(double var0) throws java.sql.SQLException;
	void writeFloat(float var0) throws java.sql.SQLException;
	void writeInt(int var0) throws java.sql.SQLException;
	void writeLong(long var0) throws java.sql.SQLException;
	void writeNClob(java.sql.NClob var0) throws java.sql.SQLException;
	void writeNString(java.lang.String var0) throws java.sql.SQLException;
	void writeObject(java.sql.SQLData var0) throws java.sql.SQLException;
	void writeRef(java.sql.Ref var0) throws java.sql.SQLException;
	void writeRowId(java.sql.RowId var0) throws java.sql.SQLException;
	void writeSQLXML(java.sql.SQLXML var0) throws java.sql.SQLException;
	void writeShort(short var0) throws java.sql.SQLException;
	void writeString(java.lang.String var0) throws java.sql.SQLException;
	void writeStruct(java.sql.Struct var0) throws java.sql.SQLException;
	void writeTime(java.sql.Time var0) throws java.sql.SQLException;
	void writeTimestamp(java.sql.Timestamp var0) throws java.sql.SQLException;
	void writeURL(java.net.URL var0) throws java.sql.SQLException;
}

