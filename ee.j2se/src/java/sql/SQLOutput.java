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
public abstract interface SQLOutput {
	public abstract void writeArray(java.sql.Array var0) throws java.sql.SQLException;
	public abstract void writeAsciiStream(java.io.InputStream var0) throws java.sql.SQLException;
	public abstract void writeBigDecimal(java.math.BigDecimal var0) throws java.sql.SQLException;
	public abstract void writeBinaryStream(java.io.InputStream var0) throws java.sql.SQLException;
	public abstract void writeBlob(java.sql.Blob var0) throws java.sql.SQLException;
	public abstract void writeBoolean(boolean var0) throws java.sql.SQLException;
	public abstract void writeByte(byte var0) throws java.sql.SQLException;
	public abstract void writeBytes(byte[] var0) throws java.sql.SQLException;
	public abstract void writeCharacterStream(java.io.Reader var0) throws java.sql.SQLException;
	public abstract void writeClob(java.sql.Clob var0) throws java.sql.SQLException;
	public abstract void writeDate(java.sql.Date var0) throws java.sql.SQLException;
	public abstract void writeDouble(double var0) throws java.sql.SQLException;
	public abstract void writeFloat(float var0) throws java.sql.SQLException;
	public abstract void writeInt(int var0) throws java.sql.SQLException;
	public abstract void writeLong(long var0) throws java.sql.SQLException;
	public abstract void writeObject(java.sql.SQLData var0) throws java.sql.SQLException;
	public abstract void writeRef(java.sql.Ref var0) throws java.sql.SQLException;
	public abstract void writeShort(short var0) throws java.sql.SQLException;
	public abstract void writeString(java.lang.String var0) throws java.sql.SQLException;
	public abstract void writeStruct(java.sql.Struct var0) throws java.sql.SQLException;
	public abstract void writeTime(java.sql.Time var0) throws java.sql.SQLException;
	public abstract void writeTimestamp(java.sql.Timestamp var0) throws java.sql.SQLException;
	public abstract void writeURL(java.net.URL var0) throws java.sql.SQLException;
}

