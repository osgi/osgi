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
public interface SQLXML {
	void free() throws java.sql.SQLException;
	java.io.InputStream getBinaryStream() throws java.sql.SQLException;
	java.io.Reader getCharacterStream() throws java.sql.SQLException;
	<T extends javax.xml.transform.Source> T getSource(java.lang.Class<T> var0) throws java.sql.SQLException;
	java.lang.String getString() throws java.sql.SQLException;
	java.io.OutputStream setBinaryStream() throws java.sql.SQLException;
	java.io.Writer setCharacterStream() throws java.sql.SQLException;
	<T extends javax.xml.transform.Result> T setResult(java.lang.Class<T> var0) throws java.sql.SQLException;
	void setString(java.lang.String var0) throws java.sql.SQLException;
}

