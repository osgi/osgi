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
public abstract interface ResultSetMetaData {
	public abstract java.lang.String getCatalogName(int var0) throws java.sql.SQLException;
	public abstract java.lang.String getColumnClassName(int var0) throws java.sql.SQLException;
	public abstract int getColumnCount() throws java.sql.SQLException;
	public abstract int getColumnDisplaySize(int var0) throws java.sql.SQLException;
	public abstract java.lang.String getColumnLabel(int var0) throws java.sql.SQLException;
	public abstract java.lang.String getColumnName(int var0) throws java.sql.SQLException;
	public abstract int getColumnType(int var0) throws java.sql.SQLException;
	public abstract java.lang.String getColumnTypeName(int var0) throws java.sql.SQLException;
	public abstract int getPrecision(int var0) throws java.sql.SQLException;
	public abstract int getScale(int var0) throws java.sql.SQLException;
	public abstract java.lang.String getSchemaName(int var0) throws java.sql.SQLException;
	public abstract java.lang.String getTableName(int var0) throws java.sql.SQLException;
	public abstract boolean isAutoIncrement(int var0) throws java.sql.SQLException;
	public abstract boolean isCaseSensitive(int var0) throws java.sql.SQLException;
	public abstract boolean isCurrency(int var0) throws java.sql.SQLException;
	public abstract boolean isDefinitelyWritable(int var0) throws java.sql.SQLException;
	public abstract int isNullable(int var0) throws java.sql.SQLException;
	public abstract boolean isReadOnly(int var0) throws java.sql.SQLException;
	public abstract boolean isSearchable(int var0) throws java.sql.SQLException;
	public abstract boolean isSigned(int var0) throws java.sql.SQLException;
	public abstract boolean isWritable(int var0) throws java.sql.SQLException;
	public final static int columnNoNulls = 0;
	public final static int columnNullable = 1;
	public final static int columnNullableUnknown = 2;
}

