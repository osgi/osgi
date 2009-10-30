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

package javax.sql.rowset;
public interface JdbcRowSet extends javax.sql.RowSet, javax.sql.rowset.Joinable {
	void commit() throws java.sql.SQLException;
	boolean getAutoCommit() throws java.sql.SQLException;
	javax.sql.rowset.RowSetWarning getRowSetWarnings() throws java.sql.SQLException;
	boolean getShowDeleted() throws java.sql.SQLException;
	void rollback() throws java.sql.SQLException;
	void rollback(java.sql.Savepoint var0) throws java.sql.SQLException;
	void setAutoCommit(boolean var0) throws java.sql.SQLException;
	void setShowDeleted(boolean var0) throws java.sql.SQLException;
}

