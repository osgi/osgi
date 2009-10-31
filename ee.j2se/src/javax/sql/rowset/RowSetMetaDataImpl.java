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
public class RowSetMetaDataImpl implements java.io.Serializable, javax.sql.RowSetMetaData {
	public RowSetMetaDataImpl() { } 
	public java.lang.String getCatalogName(int var0) throws java.sql.SQLException { return null; }
	public java.lang.String getColumnClassName(int var0) throws java.sql.SQLException { return null; }
	public int getColumnCount() throws java.sql.SQLException { return 0; }
	public int getColumnDisplaySize(int var0) throws java.sql.SQLException { return 0; }
	public java.lang.String getColumnLabel(int var0) throws java.sql.SQLException { return null; }
	public java.lang.String getColumnName(int var0) throws java.sql.SQLException { return null; }
	public int getColumnType(int var0) throws java.sql.SQLException { return 0; }
	public java.lang.String getColumnTypeName(int var0) throws java.sql.SQLException { return null; }
	public int getPrecision(int var0) throws java.sql.SQLException { return 0; }
	public int getScale(int var0) throws java.sql.SQLException { return 0; }
	public java.lang.String getSchemaName(int var0) throws java.sql.SQLException { return null; }
	public java.lang.String getTableName(int var0) throws java.sql.SQLException { return null; }
	public boolean isAutoIncrement(int var0) throws java.sql.SQLException { return false; }
	public boolean isCaseSensitive(int var0) throws java.sql.SQLException { return false; }
	public boolean isCurrency(int var0) throws java.sql.SQLException { return false; }
	public boolean isDefinitelyWritable(int var0) throws java.sql.SQLException { return false; }
	public int isNullable(int var0) throws java.sql.SQLException { return 0; }
	public boolean isReadOnly(int var0) throws java.sql.SQLException { return false; }
	public boolean isSearchable(int var0) throws java.sql.SQLException { return false; }
	public boolean isSigned(int var0) throws java.sql.SQLException { return false; }
	public boolean isWrapperFor(java.lang.Class<?> var0) throws java.sql.SQLException { return false; }
	public boolean isWritable(int var0) throws java.sql.SQLException { return false; }
	public void setAutoIncrement(int var0, boolean var1) throws java.sql.SQLException { }
	public void setCaseSensitive(int var0, boolean var1) throws java.sql.SQLException { }
	public void setCatalogName(int var0, java.lang.String var1) throws java.sql.SQLException { }
	public void setColumnCount(int var0) throws java.sql.SQLException { }
	public void setColumnDisplaySize(int var0, int var1) throws java.sql.SQLException { }
	public void setColumnLabel(int var0, java.lang.String var1) throws java.sql.SQLException { }
	public void setColumnName(int var0, java.lang.String var1) throws java.sql.SQLException { }
	public void setColumnType(int var0, int var1) throws java.sql.SQLException { }
	public void setColumnTypeName(int var0, java.lang.String var1) throws java.sql.SQLException { }
	public void setCurrency(int var0, boolean var1) throws java.sql.SQLException { }
	public void setNullable(int var0, int var1) throws java.sql.SQLException { }
	public void setPrecision(int var0, int var1) throws java.sql.SQLException { }
	public void setScale(int var0, int var1) throws java.sql.SQLException { }
	public void setSchemaName(int var0, java.lang.String var1) throws java.sql.SQLException { }
	public void setSearchable(int var0, boolean var1) throws java.sql.SQLException { }
	public void setSigned(int var0, boolean var1) throws java.sql.SQLException { }
	public void setTableName(int var0, java.lang.String var1) throws java.sql.SQLException { }
	public <T> T unwrap(java.lang.Class<T> var0) throws java.sql.SQLException { return null; }
}

