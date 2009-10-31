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

package javax.sql.rowset.serial;
public class SerialClob implements java.io.Serializable, java.lang.Cloneable, java.sql.Clob {
	public SerialClob(java.sql.Clob var0) throws java.sql.SQLException { } 
	public SerialClob(char[] var0) throws java.sql.SQLException { } 
	public void free() throws java.sql.SQLException { }
	public java.io.InputStream getAsciiStream() throws java.sql.SQLException { return null; }
	public java.io.Reader getCharacterStream() throws javax.sql.rowset.serial.SerialException { return null; }
	public java.io.Reader getCharacterStream(long var0, long var1) throws java.sql.SQLException { return null; }
	public java.lang.String getSubString(long var0, int var1) throws javax.sql.rowset.serial.SerialException { return null; }
	public long length() throws javax.sql.rowset.serial.SerialException { return 0l; }
	public long position(java.lang.String var0, long var1) throws java.sql.SQLException { return 0l; }
	public long position(java.sql.Clob var0, long var1) throws java.sql.SQLException { return 0l; }
	public java.io.OutputStream setAsciiStream(long var0) throws java.sql.SQLException { return null; }
	public java.io.Writer setCharacterStream(long var0) throws java.sql.SQLException { return null; }
	public int setString(long var0, java.lang.String var1) throws javax.sql.rowset.serial.SerialException { return 0; }
	public int setString(long var0, java.lang.String var1, int var2, int var3) throws javax.sql.rowset.serial.SerialException { return 0; }
	public void truncate(long var0) throws javax.sql.rowset.serial.SerialException { }
}

