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
public class RelationTypeSupport implements javax.management.relation.RelationType {
	protected RelationTypeSupport(java.lang.String var0) { } 
	public RelationTypeSupport(java.lang.String var0, javax.management.relation.RoleInfo[] var1) throws javax.management.relation.InvalidRelationTypeException { } 
	protected void addRoleInfo(javax.management.relation.RoleInfo var0) throws javax.management.relation.InvalidRelationTypeException { }
	public java.lang.String getRelationTypeName() { return null; }
	public javax.management.relation.RoleInfo getRoleInfo(java.lang.String var0) throws javax.management.relation.RoleInfoNotFoundException { return null; }
	public java.util.List getRoleInfos() { return null; }
}

