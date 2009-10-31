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

package javax.management.relation;
public class RoleInfo implements java.io.Serializable {
	public final static int ROLE_CARDINALITY_INFINITY = -1;
	public RoleInfo(java.lang.String var0, java.lang.String var1) throws java.lang.ClassNotFoundException, javax.management.NotCompliantMBeanException { } 
	public RoleInfo(java.lang.String var0, java.lang.String var1, boolean var2, boolean var3) throws java.lang.ClassNotFoundException, javax.management.NotCompliantMBeanException { } 
	public RoleInfo(java.lang.String var0, java.lang.String var1, boolean var2, boolean var3, int var4, int var5, java.lang.String var6) throws java.lang.ClassNotFoundException, javax.management.NotCompliantMBeanException, javax.management.relation.InvalidRoleInfoException { } 
	public RoleInfo(javax.management.relation.RoleInfo var0) { } 
	public boolean checkMaxDegree(int var0) { return false; }
	public boolean checkMinDegree(int var0) { return false; }
	public java.lang.String getDescription() { return null; }
	public int getMaxDegree() { return 0; }
	public int getMinDegree() { return 0; }
	public java.lang.String getName() { return null; }
	public java.lang.String getRefMBeanClassName() { return null; }
	public boolean isReadable() { return false; }
	public boolean isWritable() { return false; }
}

