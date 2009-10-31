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
public interface RelationServiceMBean {
	void addRelation(javax.management.ObjectName var0) throws java.lang.NoSuchMethodException, javax.management.InstanceNotFoundException, javax.management.relation.InvalidRelationIdException, javax.management.relation.InvalidRelationServiceException, javax.management.relation.InvalidRoleValueException, javax.management.relation.RelationServiceNotRegisteredException, javax.management.relation.RelationTypeNotFoundException, javax.management.relation.RoleNotFoundException;
	void addRelationType(javax.management.relation.RelationType var0) throws javax.management.relation.InvalidRelationTypeException;
	java.lang.Integer checkRoleReading(java.lang.String var0, java.lang.String var1) throws javax.management.relation.RelationTypeNotFoundException;
	java.lang.Integer checkRoleWriting(javax.management.relation.Role var0, java.lang.String var1, java.lang.Boolean var2) throws javax.management.relation.RelationTypeNotFoundException;
	void createRelation(java.lang.String var0, java.lang.String var1, javax.management.relation.RoleList var2) throws javax.management.relation.InvalidRelationIdException, javax.management.relation.InvalidRoleValueException, javax.management.relation.RelationServiceNotRegisteredException, javax.management.relation.RelationTypeNotFoundException, javax.management.relation.RoleNotFoundException;
	void createRelationType(java.lang.String var0, javax.management.relation.RoleInfo[] var1) throws javax.management.relation.InvalidRelationTypeException;
	java.util.Map<javax.management.ObjectName,java.util.List<java.lang.String>> findAssociatedMBeans(javax.management.ObjectName var0, java.lang.String var1, java.lang.String var2);
	java.util.Map<java.lang.String,java.util.List<java.lang.String>> findReferencingRelations(javax.management.ObjectName var0, java.lang.String var1, java.lang.String var2);
	java.util.List<java.lang.String> findRelationsOfType(java.lang.String var0) throws javax.management.relation.RelationTypeNotFoundException;
	java.util.List<java.lang.String> getAllRelationIds();
	java.util.List<java.lang.String> getAllRelationTypeNames();
	javax.management.relation.RoleResult getAllRoles(java.lang.String var0) throws javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException;
	boolean getPurgeFlag();
	java.util.Map<javax.management.ObjectName,java.util.List<java.lang.String>> getReferencedMBeans(java.lang.String var0) throws javax.management.relation.RelationNotFoundException;
	java.lang.String getRelationTypeName(java.lang.String var0) throws javax.management.relation.RelationNotFoundException;
	java.util.List<javax.management.ObjectName> getRole(java.lang.String var0, java.lang.String var1) throws javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException, javax.management.relation.RoleNotFoundException;
	java.lang.Integer getRoleCardinality(java.lang.String var0, java.lang.String var1) throws javax.management.relation.RelationNotFoundException, javax.management.relation.RoleNotFoundException;
	javax.management.relation.RoleInfo getRoleInfo(java.lang.String var0, java.lang.String var1) throws javax.management.relation.RelationTypeNotFoundException, javax.management.relation.RoleInfoNotFoundException;
	java.util.List<javax.management.relation.RoleInfo> getRoleInfos(java.lang.String var0) throws javax.management.relation.RelationTypeNotFoundException;
	javax.management.relation.RoleResult getRoles(java.lang.String var0, java.lang.String[] var1) throws javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException;
	java.lang.Boolean hasRelation(java.lang.String var0);
	void isActive() throws javax.management.relation.RelationServiceNotRegisteredException;
	java.lang.String isRelation(javax.management.ObjectName var0);
	javax.management.ObjectName isRelationMBean(java.lang.String var0) throws javax.management.relation.RelationNotFoundException;
	void purgeRelations() throws javax.management.relation.RelationServiceNotRegisteredException;
	void removeRelation(java.lang.String var0) throws javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException;
	void removeRelationType(java.lang.String var0) throws javax.management.relation.RelationServiceNotRegisteredException, javax.management.relation.RelationTypeNotFoundException;
	void sendRelationCreationNotification(java.lang.String var0) throws javax.management.relation.RelationNotFoundException;
	void sendRelationRemovalNotification(java.lang.String var0, java.util.List<javax.management.ObjectName> var1) throws javax.management.relation.RelationNotFoundException;
	void sendRoleUpdateNotification(java.lang.String var0, javax.management.relation.Role var1, java.util.List<javax.management.ObjectName> var2) throws javax.management.relation.RelationNotFoundException;
	void setPurgeFlag(boolean var0);
	void setRole(java.lang.String var0, javax.management.relation.Role var1) throws javax.management.relation.InvalidRoleValueException, javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException, javax.management.relation.RelationTypeNotFoundException, javax.management.relation.RoleNotFoundException;
	javax.management.relation.RoleResult setRoles(java.lang.String var0, javax.management.relation.RoleList var1) throws javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException;
	void updateRoleMap(java.lang.String var0, javax.management.relation.Role var1, java.util.List<javax.management.ObjectName> var2) throws javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException;
}

