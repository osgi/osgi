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
public interface Relation {
	javax.management.relation.RoleResult getAllRoles() throws javax.management.relation.RelationServiceNotRegisteredException;
	java.util.Map<javax.management.ObjectName,java.util.List<java.lang.String>> getReferencedMBeans();
	java.lang.String getRelationId();
	javax.management.ObjectName getRelationServiceName();
	java.lang.String getRelationTypeName();
	java.util.List<javax.management.ObjectName> getRole(java.lang.String var0) throws javax.management.relation.RelationServiceNotRegisteredException, javax.management.relation.RoleNotFoundException;
	java.lang.Integer getRoleCardinality(java.lang.String var0) throws javax.management.relation.RoleNotFoundException;
	javax.management.relation.RoleResult getRoles(java.lang.String[] var0) throws javax.management.relation.RelationServiceNotRegisteredException;
	void handleMBeanUnregistration(javax.management.ObjectName var0, java.lang.String var1) throws javax.management.relation.InvalidRoleValueException, javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException, javax.management.relation.RelationTypeNotFoundException, javax.management.relation.RoleNotFoundException;
	javax.management.relation.RoleList retrieveAllRoles();
	void setRole(javax.management.relation.Role var0) throws javax.management.relation.InvalidRoleValueException, javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException, javax.management.relation.RelationTypeNotFoundException, javax.management.relation.RoleNotFoundException;
	javax.management.relation.RoleResult setRoles(javax.management.relation.RoleList var0) throws javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException, javax.management.relation.RelationTypeNotFoundException;
}

