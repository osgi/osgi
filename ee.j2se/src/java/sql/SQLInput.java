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
public interface SQLInput {
	java.sql.Array readArray() throws java.sql.SQLException;
	java.io.InputStream readAsciiStream() throws java.sql.SQLException;
	java.math.BigDecimal readBigDecimal() throws java.sql.SQLException;
	java.io.InputStream readBinaryStream() throws java.sql.SQLException;
	java.sql.Blob readBlob() throws java.sql.SQLException;
	boolean readBoolean() throws java.sql.SQLException;
	byte readByte() throws java.sql.SQLException;
	byte[] readBytes() throws java.sql.SQLException;
	java.io.Reader readCharacterStream() throws java.sql.SQLException;
	java.sql.Clob readClob() throws java.sql.SQLException;
	java.sql.Date readDate() throws java.sql.SQLException;
	double readDouble() throws java.sql.SQLException;
	float readFloat() throws java.sql.SQLException;
	int readInt() throws java.sql.SQLException;
	long readLong() throws java.sql.SQLException;
	java.lang.Object readObject() throws java.sql.SQLException;
	java.sql.Ref readRef() throws java.sql.SQLException;
	short readShort() throws java.sql.SQLException;
	java.lang.String readString() throws java.sql.SQLException;
	java.sql.Time readTime() throws java.sql.SQLException;
	java.sql.Timestamp readTimestamp() throws java.sql.SQLException;
	java.net.URL readURL() throws java.sql.SQLException;
	boolean wasNull() throws java.sql.SQLException;
}

