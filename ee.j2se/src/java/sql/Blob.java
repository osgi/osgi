/*
 * $Date$
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
public abstract interface Blob {
	public abstract java.io.InputStream getBinaryStream() throws java.sql.SQLException;
	public abstract byte[] getBytes(long var0, int var1) throws java.sql.SQLException;
	public abstract long length() throws java.sql.SQLException;
	public abstract long position(java.sql.Blob var0, long var1) throws java.sql.SQLException;
	public abstract long position(byte[] var0, long var1) throws java.sql.SQLException;
	public abstract java.io.OutputStream setBinaryStream(long var0) throws java.sql.SQLException;
	public abstract int setBytes(long var0, byte[] var1) throws java.sql.SQLException;
	public abstract int setBytes(long var0, byte[] var1, int var2, int var3) throws java.sql.SQLException;
	public abstract void truncate(long var0) throws java.sql.SQLException;
}

