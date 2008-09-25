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

package javax.sql;
public abstract interface RowSetMetaData extends java.sql.ResultSetMetaData {
	public abstract void setAutoIncrement(int var0, boolean var1) throws java.sql.SQLException;
	public abstract void setCaseSensitive(int var0, boolean var1) throws java.sql.SQLException;
	public abstract void setCatalogName(int var0, java.lang.String var1) throws java.sql.SQLException;
	public abstract void setColumnCount(int var0) throws java.sql.SQLException;
	public abstract void setColumnDisplaySize(int var0, int var1) throws java.sql.SQLException;
	public abstract void setColumnLabel(int var0, java.lang.String var1) throws java.sql.SQLException;
	public abstract void setColumnName(int var0, java.lang.String var1) throws java.sql.SQLException;
	public abstract void setColumnType(int var0, int var1) throws java.sql.SQLException;
	public abstract void setColumnTypeName(int var0, java.lang.String var1) throws java.sql.SQLException;
	public abstract void setCurrency(int var0, boolean var1) throws java.sql.SQLException;
	public abstract void setNullable(int var0, int var1) throws java.sql.SQLException;
	public abstract void setPrecision(int var0, int var1) throws java.sql.SQLException;
	public abstract void setScale(int var0, int var1) throws java.sql.SQLException;
	public abstract void setSchemaName(int var0, java.lang.String var1) throws java.sql.SQLException;
	public abstract void setSearchable(int var0, boolean var1) throws java.sql.SQLException;
	public abstract void setSigned(int var0, boolean var1) throws java.sql.SQLException;
	public abstract void setTableName(int var0, java.lang.String var1) throws java.sql.SQLException;
}

