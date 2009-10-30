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
public interface JoinRowSet extends javax.sql.rowset.WebRowSet {
	public final static int CROSS_JOIN = 0;
	public final static int FULL_JOIN = 4;
	public final static int INNER_JOIN = 1;
	public final static int LEFT_OUTER_JOIN = 2;
	public final static int RIGHT_OUTER_JOIN = 3;
	void addRowSet(javax.sql.RowSet var0, int var1) throws java.sql.SQLException;
	void addRowSet(javax.sql.RowSet var0, java.lang.String var1) throws java.sql.SQLException;
	void addRowSet(javax.sql.rowset.Joinable var0) throws java.sql.SQLException;
	void addRowSet(javax.sql.RowSet[] var0, int[] var1) throws java.sql.SQLException;
	void addRowSet(javax.sql.RowSet[] var0, java.lang.String[] var1) throws java.sql.SQLException;
	int getJoinType() throws java.sql.SQLException;
	java.lang.String[] getRowSetNames() throws java.sql.SQLException;
	java.util.Collection<?> getRowSets() throws java.sql.SQLException;
	java.lang.String getWhereClause() throws java.sql.SQLException;
	void setJoinType(int var0) throws java.sql.SQLException;
	boolean supportsCrossJoin();
	boolean supportsFullJoin();
	boolean supportsInnerJoin();
	boolean supportsLeftOuterJoin();
	boolean supportsRightOuterJoin();
	javax.sql.rowset.CachedRowSet toCachedRowSet() throws java.sql.SQLException;
}

