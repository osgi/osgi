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
public abstract interface SQLInput {
	public abstract java.sql.Array readArray() throws java.sql.SQLException;
	public abstract java.io.InputStream readAsciiStream() throws java.sql.SQLException;
	public abstract java.math.BigDecimal readBigDecimal() throws java.sql.SQLException;
	public abstract java.io.InputStream readBinaryStream() throws java.sql.SQLException;
	public abstract java.sql.Blob readBlob() throws java.sql.SQLException;
	public abstract boolean readBoolean() throws java.sql.SQLException;
	public abstract byte readByte() throws java.sql.SQLException;
	public abstract byte[] readBytes() throws java.sql.SQLException;
	public abstract java.io.Reader readCharacterStream() throws java.sql.SQLException;
	public abstract java.sql.Clob readClob() throws java.sql.SQLException;
	public abstract java.sql.Date readDate() throws java.sql.SQLException;
	public abstract double readDouble() throws java.sql.SQLException;
	public abstract float readFloat() throws java.sql.SQLException;
	public abstract int readInt() throws java.sql.SQLException;
	public abstract long readLong() throws java.sql.SQLException;
	public abstract java.lang.Object readObject() throws java.sql.SQLException;
	public abstract java.sql.Ref readRef() throws java.sql.SQLException;
	public abstract short readShort() throws java.sql.SQLException;
	public abstract java.lang.String readString() throws java.sql.SQLException;
	public abstract java.sql.Time readTime() throws java.sql.SQLException;
	public abstract java.sql.Timestamp readTimestamp() throws java.sql.SQLException;
	public abstract java.net.URL readURL() throws java.sql.SQLException;
	public abstract boolean wasNull() throws java.sql.SQLException;
}

