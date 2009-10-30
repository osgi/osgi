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
public class RelationService extends javax.management.NotificationBroadcasterSupport implements javax.management.MBeanRegistration, javax.management.NotificationListener, javax.management.relation.RelationServiceMBean {
	public RelationService(boolean var0) { } 
	public void addRelation(javax.management.ObjectName var0) throws java.lang.NoSuchMethodException, javax.management.InstanceNotFoundException, javax.management.relation.InvalidRelationIdException, javax.management.relation.InvalidRelationServiceException, javax.management.relation.InvalidRoleValueException, javax.management.relation.RelationServiceNotRegisteredException, javax.management.relation.RelationTypeNotFoundException, javax.management.relation.RoleNotFoundException { }
	public void addRelationType(javax.management.relation.RelationType var0) throws javax.management.relation.InvalidRelationTypeException { }
	public java.lang.Integer checkRoleReading(java.lang.String var0, java.lang.String var1) throws javax.management.relation.RelationTypeNotFoundException { return null; }
	public java.lang.Integer checkRoleWriting(javax.management.relation.Role var0, java.lang.String var1, java.lang.Boolean var2) throws javax.management.relation.RelationTypeNotFoundException { return null; }
	public void createRelation(java.lang.String var0, java.lang.String var1, javax.management.relation.RoleList var2) throws javax.management.relation.InvalidRelationIdException, javax.management.relation.InvalidRoleValueException, javax.management.relation.RelationServiceNotRegisteredException, javax.management.relation.RelationTypeNotFoundException, javax.management.relation.RoleNotFoundException { }
	public void createRelationType(java.lang.String var0, javax.management.relation.RoleInfo[] var1) throws javax.management.relation.InvalidRelationTypeException { }
	public java.util.Map findAssociatedMBeans(javax.management.ObjectName var0, java.lang.String var1, java.lang.String var2) { return null; }
	public java.util.Map findReferencingRelations(javax.management.ObjectName var0, java.lang.String var1, java.lang.String var2) { return null; }
	public java.util.List findRelationsOfType(java.lang.String var0) throws javax.management.relation.RelationTypeNotFoundException { return null; }
	public java.util.List getAllRelationIds() { return null; }
	public java.util.List getAllRelationTypeNames() { return null; }
	public javax.management.relation.RoleResult getAllRoles(java.lang.String var0) throws javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException { return null; }
	public boolean getPurgeFlag() { return false; }
	public java.util.Map getReferencedMBeans(java.lang.String var0) throws javax.management.relation.RelationNotFoundException { return null; }
	public java.lang.String getRelationTypeName(java.lang.String var0) throws javax.management.relation.RelationNotFoundException { return null; }
	public java.util.List getRole(java.lang.String var0, java.lang.String var1) throws javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException, javax.management.relation.RoleNotFoundException { return null; }
	public java.lang.Integer getRoleCardinality(java.lang.String var0, java.lang.String var1) throws javax.management.relation.RelationNotFoundException, javax.management.relation.RoleNotFoundException { return null; }
	public javax.management.relation.RoleInfo getRoleInfo(java.lang.String var0, java.lang.String var1) throws javax.management.relation.RelationTypeNotFoundException, javax.management.relation.RoleInfoNotFoundException { return null; }
	public java.util.List getRoleInfos(java.lang.String var0) throws javax.management.relation.RelationTypeNotFoundException { return null; }
	public javax.management.relation.RoleResult getRoles(java.lang.String var0, java.lang.String[] var1) throws javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException { return null; }
	public void handleNotification(javax.management.Notification var0, java.lang.Object var1) { }
	public java.lang.Boolean hasRelation(java.lang.String var0) { return null; }
	public void isActive() throws javax.management.relation.RelationServiceNotRegisteredException { }
	public java.lang.String isRelation(javax.management.ObjectName var0) { return null; }
	public javax.management.ObjectName isRelationMBean(java.lang.String var0) throws javax.management.relation.RelationNotFoundException { return null; }
	public void postDeregister() { }
	public void postRegister(java.lang.Boolean var0) { }
	public void preDeregister() throws java.lang.Exception { }
	public javax.management.ObjectName preRegister(javax.management.MBeanServer var0, javax.management.ObjectName var1) throws java.lang.Exception { return null; }
	public void purgeRelations() throws javax.management.relation.RelationServiceNotRegisteredException { }
	public void removeRelation(java.lang.String var0) throws javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException { }
	public void removeRelationType(java.lang.String var0) throws javax.management.relation.RelationServiceNotRegisteredException, javax.management.relation.RelationTypeNotFoundException { }
	public void sendRelationCreationNotification(java.lang.String var0) throws javax.management.relation.RelationNotFoundException { }
	public void sendRelationRemovalNotification(java.lang.String var0, java.util.List var1) throws javax.management.relation.RelationNotFoundException { }
	public void sendRoleUpdateNotification(java.lang.String var0, javax.management.relation.Role var1, java.util.List var2) throws javax.management.relation.RelationNotFoundException { }
	public void setPurgeFlag(boolean var0) { }
	public void setRole(java.lang.String var0, javax.management.relation.Role var1) throws javax.management.relation.InvalidRoleValueException, javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException, javax.management.relation.RoleNotFoundException { }
	public javax.management.relation.RoleResult setRoles(java.lang.String var0, javax.management.relation.RoleList var1) throws javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException { return null; }
	public void updateRoleMap(java.lang.String var0, javax.management.relation.Role var1, java.util.List var2) throws javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException { }
}

