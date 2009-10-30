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
public class RelationSupport implements javax.management.MBeanRegistration, javax.management.relation.RelationSupportMBean {
	public RelationSupport(java.lang.String var0, javax.management.ObjectName var1, java.lang.String var2, javax.management.relation.RoleList var3) throws javax.management.relation.InvalidRoleValueException { } 
	public RelationSupport(java.lang.String var0, javax.management.ObjectName var1, javax.management.MBeanServer var2, java.lang.String var3, javax.management.relation.RoleList var4) throws javax.management.relation.InvalidRoleValueException { } 
	public javax.management.relation.RoleResult getAllRoles() throws javax.management.relation.RelationServiceNotRegisteredException { return null; }
	public java.util.Map getReferencedMBeans() { return null; }
	public java.lang.String getRelationId() { return null; }
	public javax.management.ObjectName getRelationServiceName() { return null; }
	public java.lang.String getRelationTypeName() { return null; }
	public java.util.List getRole(java.lang.String var0) throws javax.management.relation.RelationServiceNotRegisteredException, javax.management.relation.RoleNotFoundException { return null; }
	public java.lang.Integer getRoleCardinality(java.lang.String var0) throws javax.management.relation.RoleNotFoundException { return null; }
	public javax.management.relation.RoleResult getRoles(java.lang.String[] var0) throws javax.management.relation.RelationServiceNotRegisteredException { return null; }
	public void handleMBeanUnregistration(javax.management.ObjectName var0, java.lang.String var1) throws javax.management.relation.InvalidRoleValueException, javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException, javax.management.relation.RelationTypeNotFoundException, javax.management.relation.RoleNotFoundException { }
	public java.lang.Boolean isInRelationService() { return null; }
	public void postDeregister() { }
	public void postRegister(java.lang.Boolean var0) { }
	public void preDeregister() throws java.lang.Exception { }
	public javax.management.ObjectName preRegister(javax.management.MBeanServer var0, javax.management.ObjectName var1) throws java.lang.Exception { return null; }
	public javax.management.relation.RoleList retrieveAllRoles() { return null; }
	public void setRelationServiceManagementFlag(java.lang.Boolean var0) { }
	public void setRole(javax.management.relation.Role var0) throws javax.management.relation.InvalidRoleValueException, javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException, javax.management.relation.RelationTypeNotFoundException, javax.management.relation.RoleNotFoundException { }
	public javax.management.relation.RoleResult setRoles(javax.management.relation.RoleList var0) throws javax.management.relation.RelationNotFoundException, javax.management.relation.RelationServiceNotRegisteredException, javax.management.relation.RelationTypeNotFoundException { return null; }
}

