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
public class RelationNotification extends javax.management.Notification {
	public final static java.lang.String RELATION_BASIC_CREATION = "jmx.relation.creation.basic";
	public final static java.lang.String RELATION_BASIC_REMOVAL = "jmx.relation.removal.basic";
	public final static java.lang.String RELATION_BASIC_UPDATE = "jmx.relation.update.basic";
	public final static java.lang.String RELATION_MBEAN_CREATION = "jmx.relation.creation.mbean";
	public final static java.lang.String RELATION_MBEAN_REMOVAL = "jmx.relation.removal.mbean";
	public final static java.lang.String RELATION_MBEAN_UPDATE = "jmx.relation.update.mbean";
	public RelationNotification(java.lang.String var0, java.lang.Object var1, long var2, long var3, java.lang.String var4, java.lang.String var5, java.lang.String var6, javax.management.ObjectName var7, java.lang.String var8, java.util.List<javax.management.ObjectName> var9, java.util.List<javax.management.ObjectName> var10)  { super((java.lang.String) null, (java.lang.Object) null, 0l, (java.lang.String) null); } 
	public RelationNotification(java.lang.String var0, java.lang.Object var1, long var2, long var3, java.lang.String var4, java.lang.String var5, java.lang.String var6, javax.management.ObjectName var7, java.util.List<javax.management.ObjectName> var8)  { super((java.lang.String) null, (java.lang.Object) null, 0l, (java.lang.String) null); } 
	public java.util.List<javax.management.ObjectName> getMBeansToUnregister() { return null; }
	public java.util.List<javax.management.ObjectName> getNewRoleValue() { return null; }
	public javax.management.ObjectName getObjectName() { return null; }
	public java.util.List<javax.management.ObjectName> getOldRoleValue() { return null; }
	public java.lang.String getRelationId() { return null; }
	public java.lang.String getRelationTypeName() { return null; }
	public java.lang.String getRoleName() { return null; }
}

